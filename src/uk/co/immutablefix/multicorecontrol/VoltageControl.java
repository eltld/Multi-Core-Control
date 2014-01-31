package uk.co.immutablefix.multicorecontrol;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;

public class VoltageControl {
	static String defaultVoltages = "850 875 900 925 975 1000 1025 1075 1100 1125 1137 1150";
	static String defaultCustomVoltages = "700 725 750 775 825 850 875 925 950 975 990 1000";
    private String output;

    //ToDo: ensure returnimg in mV
	public int[] getVoltages() throws Exception {
		int [] voltages = null;
		String [] uvTable = getUVTable();
		voltages = new int[uvTable.length/3];
				
		for(int i=0; i<uvTable.length; i++){
			if (i%3 == 1) 
			{
				if (uvTable[i+1].endsWith("mV")){
					uvTable[i] = uvTable[i].replaceAll("[:^A-Za-z]", ""); 
					voltages[i/3] = Integer.parseInt(uvTable[i]);				
				}
			}				
		}
		
		return voltages;
	}

    //ToDo: ensure returnimg in MHz
	public int[] getFrequencies() throws Exception {
		int [] frequencies = null;
		String [] uvTable = getUVTable();
		frequencies = new int[uvTable.length/3];
				
		for(int i=0; i<uvTable.length; i++){
			if (i%3 == 0) 
			{
				if (uvTable[i].endsWith("mhz")){
					uvTable[i] = uvTable[i].replaceAll("[:^A-Za-z]", ""); 
					frequencies[i/3] = Integer.parseInt(uvTable[i]);				
				}
			}				
			
		}
		
		return frequencies;
	}

	protected String[] getUVTable() throws Exception {
		String [] uvTable = null;
		output = "";
		
		CommandCapture command = new CommandCapture(0, 
				"chmod 777 /sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table");

		Command command2 = new Command(0, "cat /sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table")
		{
		        @Override
		        public void output(int id, String line)
		        {
		        	output += line + " ";
		        }

				@Override
				public void commandCompleted(int arg0, int arg1) {}

				@Override
				public void commandOutput(int arg0, String arg1) {}

				@Override
				public void commandTerminated(int arg0, String arg1) {}
		};
		
		RootTools.getShell(true).add(command);
		commandWait(command);
		RootTools.getShell(true).add(command2);
		commandWait(command2);
		output = output.replaceAll("[:]", "");
		uvTable = output.split("[ ]+");
		
		return uvTable;
	}
	
	public int setVoltages(Context context, int[] voltages) {
		String volts = "";
		for(int i=0; i<voltages.length; i++)
		{
			volts += String.valueOf(voltages[i]);
			if (i<voltages.length-1) volts += " ";
		}
		
		return setVoltages(context, volts);
	}

	public int setVoltages(Context context, String voltages) {
		int err = -1;

		CommandCapture command = new CommandCapture(0, 
				"chmod 777 /sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table",
				"echo \"" + voltages + "\" > /sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table\n");

		try {
			RootTools.getShell(true).add(command);
			commandWait(command);
			err = command.getExitCode();
			if (err != 0) err = 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			err=2;
			Toast.makeText(context,
					"Error setting CPU voltages, IO exception.",
			  		Toast.LENGTH_LONG).show();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			err=2;
			Toast.makeText(context,
					"Error setting CPU voltages, timed out.",
			  		Toast.LENGTH_LONG).show();
		} catch (RootDeniedException e) {
			// TODO Auto-generated catch block
			err=2;
			Toast.makeText(context,
					"Error setting CPU voltages, failed to get root permissions.",
			  		Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			err=2;
			Toast.makeText(context,
					"Error setting CPU voltages, general  exception.",
			  		Toast.LENGTH_LONG).show();
		}

		if (err == 0) {
			Toast.makeText(context,
					"Successfully set CPU voltages.", 
	    			Toast.LENGTH_LONG).show();
		} else if (err == 1){
	    	  Toast.makeText(context,
	    			"Error setting CPU voltages.",
	    			Toast.LENGTH_LONG).show();  
		}
		
		return err;
	}

	protected void commandWait(Command cmd) throws Exception {

        while (!cmd.isFinished()) {

            synchronized (cmd) {
                try {
                    if (!cmd.isFinished()) {
                        cmd.wait(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
