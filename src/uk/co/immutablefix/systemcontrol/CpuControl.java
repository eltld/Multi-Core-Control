package uk.co.immutablefix.systemcontrol;

import com.stericson.RootTools.RootTools;

public class CpuControl extends SysfsInterface{

	public boolean isSupported() {
		boolean pathsExist = ((RootTools.exists("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq")) &&
				(RootTools.exists("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq")) &&
				(RootTools.exists("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")) &&
				(RootTools.exists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq")) &&
				(RootTools.exists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq")) &&
				(RootTools.exists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies")));
		
		if (pathsExist) {
			try {
				getAvaliableFrequencies(0);
				return true; 
			} catch (Exception e) {
			}
		} 
		
		return false;
	}
	
	public int getCpusPresent() {
		String path = "/sys/devices/system/cpu/present";
		int cpuTotal = 1;
		
		if (RootTools.exists(path)) {
			String[] present;
			try {
				present = getSetting(path).split("[ -]+");
				if (present.length == 2) {
					cpuTotal =  Integer.parseInt(present[1]) - Integer.parseInt(present[0]) + 1; 
				}
			} catch (Exception e) {
			}
		}
		
		return cpuTotal;
	}
	
	public int getCpuFrequency(int cpuNo) throws Exception {
		String path = "/sys/devices/system/cpu/cpu" + cpuNo + "/cpufreq/cpuinfo_cur_freq";
				
		if (!RootTools.exists(path)) {
			return -1; //Cpu not avaliable
		}
		
		return Integer.parseInt(getSetting(path));
	}
	
	public int getCpuMinFrequency(int cpuNo) throws Exception {
		String path = "/sys/devices/system/cpu/cpu" + cpuNo + "/cpufreq/cpuinfo_min_freq";
				
		if (!RootTools.exists(path)) {
			return -1; //Cpu not avaliable
		}
		
		return Integer.parseInt(getSetting(path));
	}

	public int getCpuMaxFrequency(int cpuNo) throws Exception {
		String path = "/sys/devices/system/cpu/cpu" + cpuNo + "/cpufreq/cpuinfo_max_freq";
				
		if (!RootTools.exists(path)) {
			return -1; //Cpu not avaliable
		}
		
		return Integer.parseInt(getSetting(path));
	}

	public int[] getAvaliableFrequencies(int cpuNo) throws Exception {
		String path = "/sys/devices/system/cpu/cpu" + cpuNo + "/cpufreq/scaling_available_frequencies";
				
		if (!RootTools.exists(path)) {
			throw new Exception("Error: Unsupported kernel.");
		}
		
		String[] freqs = getSetting(path).split("[ ]+");
		
		int[] frequencies = new int[freqs.length];
		for (int i=0; i<frequencies.length; i++){
			frequencies[i] = Integer.parseInt(freqs[i]);
		}
		
		return frequencies;
	}
	
	public void setScalingMin(int cpuNo, int freq) throws Exception {
		String path = "/sys/devices/system/cpu/cpu" + cpuNo + "/cpufreq/scaling_min_freq";

		setSettingForce(path, String.valueOf(freq));
	}
	
	public void setScalingMax(int cpuNo, int freq) throws Exception {
		String path = "/sys/devices/system/cpu/cpu" + cpuNo + "/cpufreq/scaling_max_freq";
		
		setSettingForce(path, String.valueOf(freq));
	}

	public int getScalingMin(int cpuNo) throws Exception {
		String path = "/sys/devices/system/cpu/cpu" + cpuNo + "/cpufreq/scaling_min_freq";

		return Integer.parseInt(getSetting(path));
	}

	public int getScalingMax(int cpuNo) throws Exception {
		String path = "/sys/devices/system/cpu/cpu" + cpuNo + "/cpufreq/scaling_max_freq";

		return Integer.parseInt(getSetting(path));
	}

	// Try to get all cores enabled by using MPD or busy threads, then set scaling frequencies.
	// Settings only stick for enabled CPUs.
	public void SetScalingFrequencies(int cores, int min, int max) throws Exception {
		MPDecision mpd = new MPDecision();
		int maxCPUs = 1, minCPUs = 1;
		
		try {
			minCPUs = mpd.getMinCPUs();
			maxCPUs = mpd.getMaxCPUs();
			
			mpd.setMaxCPUs(cores);
			mpd.setMinCPUs(cores);
		} catch (Exception e) {
			// Create threads to get cores hot plugged-in.
			for (int i=0; i<cores; i++){
		        new Thread(new Runnable() { 
		            public void run(){
		            	long timeout = java.lang.System.currentTimeMillis() + 1000;
		    			while (java.lang.System.currentTimeMillis() < timeout);
		            }
		        }).start();
			}
        
			// Give cores time to plug-in.
			Thread.sleep(500);
		}
		
   		for (int i=0; i<cores; i++){
			setScalingMin(i, min);
        	setScalingMax(i, max);
   		}

		try {
			mpd.setMaxCPUs(maxCPUs);
			mpd.setMinCPUs(minCPUs);
		} catch (Exception e) {
		}
	}
}
