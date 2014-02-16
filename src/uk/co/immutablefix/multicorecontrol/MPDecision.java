/*
* This file is part of Multi Core Control.
*
* Copyright Shaun Simpson <shauns2029@gmail.com>
*
*/

package uk.co.immutablefix.multicorecontrol;

import com.stericson.RootTools.RootTools;

public class MPDecision extends SysfsInterface {
	final String minCpuPath = "/sys/kernel/msm_mpdecision/conf/min_cpus";
	final String maxCpuPath = "/sys/kernel/msm_mpdecision/conf/max_cpus";
	
	public boolean isSupported() {
		return ((RootTools.exists(minCpuPath)) && (RootTools.exists(maxCpuPath))); 
	}

	public int getMinCPUs() throws Exception {
		return Integer.parseInt(getSetting(minCpuPath));
	}

	public int getMaxCPUs() throws Exception {
		return Integer.parseInt(getSetting(maxCpuPath));
	}

	public void setMinCPUs(int cpus) throws Exception {
		setSetting(minCpuPath, String.valueOf(cpus));
	}
	
	public void setMaxCPUs(int cpus) throws Exception {
		setSetting(maxCpuPath, String.valueOf(cpus));
	}
}
