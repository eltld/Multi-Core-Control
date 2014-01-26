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
	
			int err = vc.setVoltages(context, prefs.getString("CustomVoltages", VoltageControl.defaultCustomVoltages));
			
			if (err == 0) {
		    	  Toast.makeText(context,
		    			"Successfully set CPU voltages.", 
		    			Toast.LENGTH_LONG).show();
			} else if (err == 1){
		    	  Toast.makeText(context,
		    			"Error setting CPU voltages.",
		    			Toast.LENGTH_LONG).show();  
			} else {
	    	  Toast.makeText(context,
	    			"Error setting CPU voltages, failed to get root permissions.",
	    			Toast.LENGTH_LONG).show();
			}
		}

//		BackgroundService.launchService(context);		
		
	}
}
