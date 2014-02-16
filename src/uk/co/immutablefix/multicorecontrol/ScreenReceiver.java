/*
* This file is part of Multi Core Control.
*
* Copyright Shaun Simpson <shauns2029@gmail.com>
*
*/

package uk.co.immutablefix.multicorecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;
import uk.co.immutablefix.multicorecontrol.CpuControlFragment;

public class ScreenReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				int time = 10000;
				
				try {
            		Thread.sleep(time);
                } catch (InterruptedException e1) {
                	// TODO Auto-generated catch block
                	e1.printStackTrace();
                }
				return;
			}
		};
		
		runnable.run();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor prefEdit = prefs.edit();

		if (prefs.getBoolean("VoltagesApplyOnBoot", false))
		{
			VoltageControl vc = new VoltageControl();
	
			String voltages = prefs.getString("CustomVoltages", null);
			
			if (voltages != null) {
				try {
					vc.setVoltages(voltages);
					Toast.makeText(context,
							"Successfully set CPU voltages.", 
			    			Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					prefEdit.putBoolean("VoltagesApplyOnBoot", false);
					prefEdit.commit(); // this saves to disk and notifies observers

					Toast.makeText(context,
							"Error setting CPU voltages. " + e.getMessage(),
					  		Toast.LENGTH_LONG).show();
				}
			}
		}
		
		if (prefs.getBoolean("MPDApplyOnBoot", false))
		{
	        MPDecision mpd = new MPDecision();
	
			int min = prefs.getInt("MinCPUs", 0);
			int max = prefs.getInt("MaxCPUs", 0);

			if ((min > 0) && (max > 0)) {
				try {
				        mpd.setMinCPUs(min);
				        mpd.setMaxCPUs(max);
						Toast.makeText(context,
								"Successfully configured MPD.", 
				    			Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					prefEdit.putBoolean("MPDApplyOnBoot", false);
					prefEdit.commit(); // this saves to disk and notifies observers
					
					Toast.makeText(context,
							"Error configuring MPD. " + e.getMessage(),
					  		Toast.LENGTH_LONG).show();
				}
			}
		}
		
		if (prefs.getBoolean("ColourApplyOnBoot", false))
		{
			ColourControl ctl = new ColourControl();
	
			int red = prefs.getInt("RedMultiplier", 0);
			int green = prefs.getInt("GreenMultiplier", 1);
			int blue = prefs.getInt("BlueMultiplier", -1);
			
			try {
				ctl.setColours(red, green, blue);
				Toast.makeText(context,
						"Successfully set colours.", 
			    		Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				prefEdit.putBoolean("ColourApplyOnBoot", false);
				prefEdit.commit(); // this saves to disk and notifies observers

				Toast.makeText(context,
						"Error configuring colours. " + e.getMessage(),
				  		Toast.LENGTH_LONG).show();
			}
		}

		if (prefs.getBoolean("CPUApplyOnBoot", false))
		{
			CpuControl cpu = new CpuControl();
	
			int min = prefs.getInt("GovMin", 0);
			int max = prefs.getInt("GovMax", 0);
			
			if ((min > 0) && (max > 0)) {
				try {
					cpu.SetScalingFrequencies(cpu.getCpusPresent(), min, max);
					Toast.makeText(context,
							"Successfully set governor frequencies.", 
				    		Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					prefEdit.putBoolean("CPUApplyOnBoot", false);
					prefEdit.commit(); // this saves to disk and notifies observers
					
					Toast.makeText(context,
							"Error configuring governor frequencies. " + e.getMessage(),
					  		Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
