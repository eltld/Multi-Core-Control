/*
* This file is part of Multi Core Control.
*
* Copyright Shaun Simpson <shauns2029@gmail.com>
*
*/

package uk.co.immutablefix.multicorecontrol;

import com.stericson.RootTools.RootTools;

public class MPDecision extends SysfsInterface {
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

}
