package uk.co.immutablefix.systemcontrol;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CpuItemFragment extends Fragment {
	int cpuNo = 0;
	boolean running = false;
	TextView tvCpuLable;
	TextView tvCpuFrequency;
	ProgressBar pbCpuFrequency;
	Thread thread = null;
	static int minFrequency = 0;
	static int maxFrequency = 0;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.cpu_indicator_fragment, null);
			
		Bundle extras = getArguments();
		cpuNo = extras.getInt("cpuNo", 0);

		tvCpuLable = (TextView) view.findViewById(R.id.tvCpuNo);
		tvCpuLable.setText("CPU" + String.valueOf(cpuNo + 1));

		tvCpuFrequency = (TextView) view.findViewById(R.id.tvCpuFrequency);
		tvCpuFrequency.setText("off");

		pbCpuFrequency = (ProgressBar) view.findViewById(R.id.pbCpuFrequency);
		pbCpuFrequency.setProgress(0);
		
		CpuControl cpu = new CpuControl();
		try {
			minFrequency = cpu.getCpuMinFrequency(0);
			maxFrequency = cpu.getCpuMaxFrequency(0);
		} catch (Exception e2) {
		}
		
		
	    final Handler handler = new Handler();
	    Runnable runnable = new Runnable() {
			@Override
			public void run() {
				int time = 1000;
				
				while (running) {
					try {
	            		Thread.sleep(time);
	                } catch (InterruptedException e1) {
	                	// TODO Auto-generated catch block
	                	e1.printStackTrace();
	                }	
			    	
					handler.post(new Runnable() {
						@Override
						public void run() {
							int freq;
							
							CpuControl cpu = new CpuControl();

							try {
								freq = cpu.getCpuFrequency(cpuNo);
								if (freq < 0) {
									tvCpuFrequency.setText("off");
									pbCpuFrequency.setProgress(0);
								} else {
									tvCpuFrequency.setText(String.valueOf(freq/1000) + "MHz");
									pbCpuFrequency.setProgress((freq * 100)/maxFrequency);
								}
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					});
					
				}

				//Log.d("THREAD", "Ended.");		
				return;
			}
		};
		
		running = true;
		thread = new Thread(runnable);
		thread.start();

		return view;
	}


	@Override
	public void onDestroyView(){
	    super.onDestroyView();		
		running = false;
		thread.interrupt();
	}
}
