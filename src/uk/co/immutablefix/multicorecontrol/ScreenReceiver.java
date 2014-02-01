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

public class ScreenReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				int time = 5000;
				
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
		if (prefs.getBoolean("ApplyOnBoot", false))
		{
			VoltageControl vc = new VoltageControl();
	
			String voltages = prefs.getString("CustomVoltages", VoltageControl.defaultCustomVoltages);
			
			try {
				vc.setVoltages(voltages);
				Toast.makeText(context,
						"Successfully set CPU voltages.", 
		    			Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(context,
						"Error setting CPU voltages. " + e.getMessage(),
				  		Toast.LENGTH_LONG).show();
			}
		}
	}
}
