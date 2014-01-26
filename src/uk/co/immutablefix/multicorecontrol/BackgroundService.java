package uk.co.immutablefix.multicorecontrol;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class BackgroundService extends IntentService {
	public static void launchService(Context context) {
		if (context == null) return;
		context.startService(new Intent(context, BackgroundService.class));
	}

	public BackgroundService() {
		super("BackgroundService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {   
			   // Preform su to get root privledges  
			   Process p = Runtime.getRuntime().exec("su");   
			      
			   // Attempt to write a file to a root-only   
			   DataOutputStream os = new DataOutputStream(p.getOutputStream());
			   // Set permissions
			   os.writeBytes("chmod 777 /sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table\n");
			   // write voltages
			   os.writeBytes("echo \"700 725 750 775 825 850 875 925 950 975 990 1000\" > /sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table\n");
			   
			   // Close the terminal  
			   os.writeBytes("exit\n");   
			   os.flush();   
			   try {   
				  p.waitFor();   

  			      if (p.exitValue() != 255) {   
			          // TODO Code to run on success  
  			    	  Toast.makeText(getApplicationContext(),
			    			"Successfully set CPU voltages", 
			    			Toast.LENGTH_LONG).show();  
			      }   
			      else {   
			          // TODO Code to run on unsuccessful  
  			    	  Toast.makeText(getApplicationContext(),
			    			"Error setting CPU voltages",
			    			Toast.LENGTH_LONG).show();  
			      }   
			   } catch (InterruptedException e) {   
			      // TODO Code to run in interrupted exception  
			   }   
			} catch (IOException e) {   
			   // TODO Code to run in input/output exception  
		    	  Toast.makeText(getApplicationContext(),
		    			"Error setting colour, is the franco.Kernel installed?",
		    			Toast.LENGTH_LONG).show();  
			}  		
		
	}
}