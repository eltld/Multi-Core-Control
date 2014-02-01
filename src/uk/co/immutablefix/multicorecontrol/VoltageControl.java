/*
* This file is part of Multi Core Control.
*
* Copyright Shaun Simpson <shauns2029@gmail.com>
*
*/

package uk.co.immutablefix.multicorecontrol;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

public class VoltageControl {
	static String defaultVoltages = "850 875 900 925 975 1000 1025 1075 1100 1125 1137 1150";
	static String defaultCustomVoltages = "700 725 750 775 825 850 875 925 950 975 990 1000";
    private String output;
    private int []frequencies = null;

	public int[] getVoltages() throws Exception {
		int [] voltages = null;
		String [] table = null;
				
		try {
			table = getUVTable();
			voltages = new int[table.length/2];
			
			for(int i=0; i<table.length; i++){
				if (i%2 == 1) 
				{
					if (table[i].endsWith("mV")){
						table[i] = table[i].replaceAll("[:^A-Za-z]", ""); 
						voltages[i/2] = Integer.parseInt(table[i]);				
					}
				}				
			}
		} catch (Exception e) {
			// Indicated that no UV table can be found.
		}
		
		// Try getting a VDD table.
		if (table == null){
			table = getVddTable();
			voltages = new int[table.length/2];
					
			for(int i=0; i<table.length; i++){
				if (i%2 == 1) 
				{
					if (table[i].endsWith("00")){
						voltages[i/2] = Integer.parseInt(table[i])/1000;				
					}
				}				
				
			}
		}
		
		return voltages;
	}

	public int[] getFrequencies() throws Exception {
		String [] table = null;
		
		if (frequencies == null)
		{
			try {
				table = getUVTable();
				frequencies = new int[table.length/2];
						
				for(int i=0; i<table.length; i++){
					if (i%2 == 0) 
					{
						if (table[i].endsWith("mhz")){
							table[i] = table[i].replaceAll("[:^A-Za-z]", ""); 
							frequencies[i/2] = Integer.parseInt(table[i]);				
						}
					}				
					
				}
			} catch (Exception e) {
				// Indicated that no UV table can be found.
			}
			
			// Try getting a VDD table.
			if (table == null){
				table = getVddTable();
				frequencies = new int[table.length/2];
						
				for(int i=0; i<table.length; i++){
					if (i%2 == 0) 
					{
						if (table[i].endsWith("000")){
							frequencies[i/2] = Integer.parseInt(table[i])/1000;				
						}
					}				
					
				}
			}
		}
		
		return frequencies;
	}

	protected String[] getUVTable() throws Exception {
		output = "";
		String tablePath = "";
		String [] table = null;
		
		if (RootTools.exists("/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table")) {
			tablePath = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";
		} else {
			throw new Exception("Failed to find voltage table, unsupported kernel.");
		}
			
		output = getTable(tablePath);
		output = output.replaceAll("[ ]", "");
		table = output.split("[:;]+");
		return table;
	}

	protected String[] getVddTable() throws Exception {
		output = "";
		String tablePath = "";
		String [] table = null;
		
		if (RootTools.exists("/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels")) {
			tablePath = "/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels";
		} else {
			throw new Exception("Failed to find voltage table, unsupported kernel.");
		}
		
		output = getTable(tablePath);
		output = output.replaceAll("[ ]", "");
		table = output.split("[:;]+");
		return table;
	}

	protected String getTable(String tablePath) throws Exception {
		output = "";

		CommandCapture command = new CommandCapture(0, 
				"chmod 777 " + tablePath);

		Command command2 = new Command(0, "cat " + tablePath)
		{
		        @Override
		        public void output(int id, String line)
		        {
		        	output += line + ";";
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

		return output;
	}

	// Voltages in mV.
	public void setVoltages(int[] voltages) throws Exception {
		String tablePath = null;
		boolean vdd = false;
		
		int[] freqs = getFrequencies();
		
		// Check number of parameters
		if (voltages.length != frequencies.length){
			throw new Exception("Failed to set voltages, incorrect number of parameters.");
		}

		// Check value of parameters
		for(int i=0; i<voltages.length; i++)
		{
			if ((voltages[i] < 700) || (voltages[i] > 1400)){
				throw new Exception("Failed to set voltages, incorrect parameters.");
			}
		}
		
		if (RootTools.exists("/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels")) {
			vdd = true;
			tablePath = "/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels";
			// VDD table uses uV and KHz.  
			for(int i=0; i<voltages.length; i++)
			{
				voltages[i] = voltages[i] * 1000;
				freqs[i] = freqs[i] * 1000;
			}
		} else if (RootTools.exists("/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table")) {
			tablePath = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";
		} else {
			throw new Exception("Failed to find voltage table, unsupported kernel.");
		}

		Shell shell = RootTools.getShell(true);
		
		CommandCapture command = new CommandCapture(0, "chmod 777 " + tablePath);
		shell.add(command);
		commandWait(command);

		try {
			if (vdd) {
				CommandCapture []commands = new CommandCapture[voltages.length];

				for(int i=0; i<voltages.length; i++)
				{
					commands[i] = new CommandCapture(0,	"echo " + freqs[i] + " " + voltages[i] + " > " + tablePath);
					shell.add(commands[i]);
					commandWait(commands[i]);
				}
			} else {
				StringBuilder v = new StringBuilder();
				
				for(int i=0; i<voltages.length; i++)
				{
					v.append(voltages[i] + " ");
				}
				
				CommandCapture command2 = new CommandCapture(0, "echo " + v.toString() + " > " + tablePath);

				shell.add(command2);
				commandWait(command2);
			}
		} catch (IOException e) {
			throw new Exception("Error setting CPU voltages, IO exception.");
		} catch (TimeoutException e) {
			throw new Exception("Error setting CPU voltages, timeout.");
		} catch (RootDeniedException e) {
			throw new Exception("Error setting CPU voltages, failed to get root permissions.");
		} catch (Exception e) {
			throw new Exception("Error setting CPU voltages, general  exception.");
		}
	}

	public void setVoltages(String voltages) throws Exception {
		String[] v = voltages.split("[ ]+");
		int[] volts = new int[v.length];
		
		for (int i=0; i<volts.length; i++){
			volts[i] = Integer.parseInt(v[i]);
		}
			
		setVoltages(volts);
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
