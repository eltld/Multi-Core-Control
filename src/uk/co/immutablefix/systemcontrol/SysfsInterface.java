package uk.co.immutablefix.systemcontrol;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import android.util.Log;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

public class SysfsInterface {
    private String output;

	public synchronized String getSetting(String path) throws Exception {
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

	public synchronized void setSetting(String path, String value) throws Exception {
		if (!RootTools.exists(path)) {
			throw new Exception("Error: Kernel does not support setting.");
		}

		Shell shell = RootTools.getShell(true);
		
		if (setFilePermissions(shell, "777", path) != 0) {
			throw new Exception("Error: Failed to configure setting.");
		}

		try {
			CommandCapture command2 = new CommandCapture(0, "echo " + value + " > " + path);
			shell.add(command2);
			commandWait(command2);
		} catch (IOException e) {
			throw new Exception("Error setting Sysfs value, IO exception.");
		} catch (TimeoutException e) {
			throw new Exception("Error setting Sysfs value, timeout.");
		} catch (RootDeniedException e) {
			throw new Exception("Error setting Sysfs value, failed to get root permissions.");
		} catch (Exception e) {
			throw new Exception("Error setting Sysfs value, general exception.");
		}

		setFilePermissions(shell, "444", path);
	}

	public void setSettingForce(String path, String value) throws Exception {
		Shell shell = RootTools.getShell(true);
		
		CommandCapture command = new CommandCapture(0, "chmod 777 " + path);
		shell.add(command);
		commandWait(command);

		try {
			CommandCapture command2 = new CommandCapture(0, "echo " + value + " > " + path);
			shell.add(command2);
			commandWait(command2);
		} catch (Exception e) {
		}
	}
	
	protected int setFilePermissions(Shell shell, String permissions, String file) throws Exception{
		if (shell == null) shell = RootTools.getShell(true);

		CommandCapture command = new CommandCapture(0, "chmod " + permissions + " " + file);
		shell.add(command);
		commandWait(command);
		
		return command.getExitCode();
	}
	
	protected void commandWait(Command cmd) throws Exception {
        while (!cmd.isFinished()) {
            synchronized (cmd) {
                try {
                    if (!cmd.isFinished()) {
                        cmd.wait(10);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
