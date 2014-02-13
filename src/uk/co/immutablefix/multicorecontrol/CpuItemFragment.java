package uk.co.immutablefix.multicorecontrol;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CpuItemFragment extends Fragment {
	int cpuId = 0;
	boolean running = false;
	TextView tvCpuLable;
	TextView tvCpuFrequency;
	Thread thread = null;

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
		cpuId = extras.getInt("cpuId", 0);

		tvCpuLable = (TextView) view.findViewById(R.id.tvCpuNo);
		tvCpuLable.setText("CPU" + String.valueOf(cpuId + 1));

		tvCpuFrequency = (TextView) view.findViewById(R.id.tvCpuFrequency);
		tvCpuFrequency.setText("off");
		
	    final Handler handler = new Handler();
	    Runnable runnable = new Runnable() {
			@Override
			public void run() {
				int time = 2000;
				
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
								freq = cpu.getCpuFrequency(cpuId);
								if (freq < 0) {
									tvCpuFrequency.setText("off");
								} else {
									tvCpuFrequency.setText(String.valueOf(freq) + "MHz");
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
