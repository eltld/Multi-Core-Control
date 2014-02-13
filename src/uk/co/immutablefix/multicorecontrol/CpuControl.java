package uk.co.immutablefix.multicorecontrol;

import com.stericson.RootTools.RootTools;

public class CpuControl extends SysfsInterface{
	public int getCpuFrequency(int cpuNo) throws Exception {
		String path = "/sys/devices/system/cpu/cpu" + cpuNo + "/cpufreq/cpuinfo_cur_freq";
				
		if (!RootTools.exists(path)) {
			return -1; //Cpu not avaliable
		}
		
		return Integer.parseInt(getSetting(path)) / 1000;
	}
}
