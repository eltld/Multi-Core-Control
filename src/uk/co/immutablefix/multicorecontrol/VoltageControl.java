package uk.co.immutablefix.multicorecontrol;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.Context;
import android.widget.Toast;

public class VoltageControl {
	static String defaultVoltages = "850 875 900 925 975 1000 1025 1075 1100 1125 1137 1150";
	static String defaultCustomVoltages = "700 725 750 775 825 850 875 925 950 975 990 1000";
	
	Process shell = null;
	
	public int setVoltages(Context context, String voltages) {
		int err = -1;
		
		if (getRootShell()) {
			err = executeShellCommand("chmod 777 /sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table\n");
			
			if (err == 0) {
				err = executeShellCommand("echo \"" + voltages + "\" > /sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table\n");
			}
			
			if (err == 0) {
				executeShellCommand("exit");
			}
			
			if (err == 0) {
				try {
					shell.waitFor();
					err = shell.exitValue();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (err == 0) {
				err = 0;
			} else {
				err = 1;			
			}
		} else {
			err = 2;
		}
		
		if (shell != null) {
			shell.destroy();
			shell = null;
		}
		
		if (context != null)
		{
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
		
		return err;
	}
	
	protected boolean getRootShell(){
		boolean exitValue = false;

		if (shell == null) {
			try {
				shell = Runtime.getRuntime().exec("su");
				exitValue = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else exitValue = true;

		return exitValue;
	}
	
	protected int executeShellCommand(String command) {
		int err = -1;
		
		if (shell != null) {
			DataOutputStream os = new DataOutputStream(shell.getOutputStream());
			try {
				os.writeBytes(command + "\n");
				err = 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return err;
	}
}
