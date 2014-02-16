/*
* This file is part of Multi Core Control.
*
* Copyright Shaun Simpson <shauns2029@gmail.com>
*
*/

package uk.co.immutablefix.multicorecontrol;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

public class VoltageControl extends SysfsInterface {
	final static int MIN_DEFAULT_VOLTAGE = 700;
	static int MIN_VOLTAGE = 700;
	static int MAX_VOLTAGE = 1400;	

    private String output;
    private int []frequencies = null;
    
    final String uvTablePath = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";
    final String vddLevelsPath = "/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels";

	public boolean isSupported() {
		return ((RootTools.exists(uvTablePath)) || (RootTools.exists(vddLevelsPath))); 
	}
	
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
						if (MAX_VOLTAGE < voltages[i/2]) {
							MAX_VOLTAGE = voltages[i/2];
						} else if (MIN_VOLTAGE > voltages[i/2]) {
							MIN_VOLTAGE = voltages[i/2];
						}
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
							frequencies[i/2] = Integer.parseInt(table[i]) * 1000;				
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
							frequencies[i/2] = Integer.parseInt(table[i]);				
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
		
		if (RootTools.exists(uvTablePath)) {
			tablePath = uvTablePath;
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
		
		if (RootTools.exists(vddLevelsPath)) {
			tablePath = vddLevelsPath;
		} else {
			throw new Exception("Failed to find voltage table, unsupported kernel.");
		}
		
		output = getTable(tablePath);
		output = output.replaceAll("[ ]", "");
		table = output.split("[:;]+");
		return table;
	}

	protected String getTable(String tablePath) throws Exception {
		Shell shell = RootTools.getShell(true);
		output = "";

		Command command = new Command(0, "cat " + tablePath)
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
		
		setFilePermissions(shell, "777", tablePath);
		shell.add(command);
		commandWait(command);
		setFilePermissions(shell, "444", tablePath);

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
			if ((voltages[i] < MIN_VOLTAGE) || (voltages[i] > MAX_VOLTAGE)){
				throw new Exception("Failed to set voltages, incorrect parameters.");
			}
		}
		
		if (RootTools.exists(vddLevelsPath)) {
			vdd = true;
			tablePath = vddLevelsPath;
		} else if (RootTools.exists(uvTablePath)) {
			tablePath = uvTablePath;
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
					commands[i] = new CommandCapture(0,	"echo " + freqs[i]  + " " 
							+ voltages[i] * 1000 + " > " + tablePath);
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
}
