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

public class MPDecision {
    private String output;

	public int getMinCPUs() throws Exception {
		String path = "/sys/kernel/msm_mpdecision/conf/min_cpus";
				
		if (!RootTools.exists(path)) {
			throw new Exception("Error: Unsupported kernel.");
		}
		
		return Integer.parseInt(getSetting(path));
	}

	public int getMaxCPUs() throws Exception {
		String path = "/sys/kernel/msm_mpdecision/conf/max_cpus";
		
		return Integer.parseInt(getSetting(path));
	}

	public void setMinCPUs(int cpus) throws Exception {
		String path = "/sys/kernel/msm_mpdecision/conf/min_cpus";
		
		setSetting(path, String.valueOf(cpus));
	}
	
	public void setMaxCPUs(int cpus) throws Exception {
		String path = "/sys/kernel/msm_mpdecision/conf/max_cpus";
		
		setSetting(path, String.valueOf(cpus));
	}
	
	public String getSetting(String path) throws Exception {
		output = "";
		
		if (!RootTools.exists(path)) {
			throw new Exception("Error: Kernel does not support setting.");
		}

		Command command = new Command(0, "cat " + path)
		{
		        @Override
		        public void output(int id, String line)
		        {
		        	// Only returns last line of output.
		        	output = line;
		        }
		        
				@Override
				public void commandCompleted(int arg0, int arg1) {
					
				}
				
				@Override
				public void commandOutput(int arg0, String arg1) {
					
				}
				
				@Override
				public void commandTerminated(int arg0, String arg1) {
					
				}
		};
		
		RootTools.getShell(true).add(command);
		commandWait(command);
		
		if (command.getExitCode() != 0) {
			throw new Exception("Error: Failed to retrieve setting. " + output);
		}
		
		return output;
	}

	public void setSetting(String path, String value) throws Exception {
		if (!RootTools.exists(path)) {
			throw new Exception("Error: Kernel does not support setting.");
		}

		Shell shell = RootTools.getShell(true);
		
		CommandCapture command = new CommandCapture(0, "chmod 777 " + path);
		shell.add(command);
		commandWait(command);

		if (command.getExitCode() != 0) {
			throw new Exception("Error: Failed to configure setting.");
		}

		try {
			CommandCapture command2 = new CommandCapture(0, "echo " + value + " > " + path);
			shell.add(command2);
			commandWait(command2);
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
