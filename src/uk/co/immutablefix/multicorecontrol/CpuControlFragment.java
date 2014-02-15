package uk.co.immutablefix.multicorecontrol;

import android.support.v4.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class CpuControlFragment extends Fragment {
	int cpuTotal = 1;
	private SeekBar sbGovMin = null;
	private TextView tvGovMax = null;
	private SeekBar sbGovMax = null;
	private TextView tvGovMin = null;
	int[] frequencies = null;
	
	SharedPreferences prefs;
	CheckBox cbxBoot;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
    	int min = 0, max = 0;
    	boolean governorEnabled = true;

    	View view = inflater.inflate(R.layout.cpu_fragment, container, false);
		
		CpuControl cpu = new CpuControl();
		
		try {
			cpuTotal = cpu.getCpusPresent();
        	max = cpu.getScalingMax(0);

        	// Delay to let scaling minimum frequency to settle. 
        	// Some kernels boost minimum frequency shortly after touches.
        	long timeout = java.lang.System.currentTimeMillis() + 1000;
			while (java.lang.System.currentTimeMillis() < timeout);
			
        	min = cpu.getScalingMin(0);
		} catch (Exception e1) {
			view.findViewById(R.id.govLayout).setVisibility(LinearLayout.GONE);
			governorEnabled = false;
		}
		
		if (governorEnabled) {
			prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
			
			try {
				frequencies = cpu.getAvaliableFrequencies(0);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			tvGovMin = (TextView) view.findViewById(R.id.tvGovMin);
			tvGovMax = (TextView) view.findViewById(R.id.tvGovMax);
			
	        sbGovMin = (SeekBar) view.findViewById(R.id.sbGovMin);
	        sbGovMin.setMax(frequencies.length - 1);
	        sbGovMin.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					tvGovMin.setText(String.valueOf(frequencies[progress]/1000) + "MHz");
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});	

	        sbGovMax = (SeekBar) view.findViewById(R.id.sbGovMax);
	        sbGovMax.setMax(frequencies.length - 1);
	        sbGovMax.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					tvGovMax.setText(String.valueOf(frequencies[progress]/1000) + "MHz");
					
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});	
	        
	    	Button btnApply = (Button) view.findViewById(R.id.btnApply);
	        btnApply.setOnClickListener(new OnClickListener (){
				@Override
				public void onClick(View v) {
					try {
						new CpuControl().SetScalingFrequencies(cpuTotal,frequencies[sbGovMin.getProgress()], frequencies[sbGovMax.getProgress()]);

	 			        Toast.makeText(getActivity().getApplicationContext(),
								"Successfully configured governor.", 
				    			Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Toast.makeText(getActivity().getApplicationContext(),
								"Error configuring governor. " + e.getMessage(),
						  		Toast.LENGTH_LONG).show();
					}
				}
	    	});        
	        
	    	Button btnSave = (Button) view.findViewById(R.id.btnSave);
	    	btnSave.setOnClickListener(new OnClickListener (){
				@Override
				public void onClick(View v) {
					SharedPreferences.Editor e = prefs.edit();
					e.putInt("GovMin", frequencies[sbGovMin.getProgress()]);
					e.putInt("GovMax", frequencies[sbGovMax.getProgress()]);
					e.commit(); // this saves to disk and notifies observers

					Toast.makeText(getActivity().getApplicationContext(),
							"Saved",
					  		Toast.LENGTH_SHORT).show();
				}
	    	});        

	        cbxBoot = (CheckBox) view.findViewById(R.id.cbxBoot);
		    cbxBoot.setChecked(prefs.getBoolean("CPUApplyOnBoot", false));        
		    cbxBoot.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Boolean applyOnBoot = cbxBoot.isChecked();
					
					SharedPreferences.Editor e = prefs.edit();
					e.putBoolean("CPUApplyOnBoot", applyOnBoot);
					e.commit(); // this saves to disk and notifies observers
				}
			});		
	       
			tvGovMin.setText(String.valueOf(min/1000) + "MHz");
			tvGovMax.setText(String.valueOf(max/1000) + "MHz");

			for (int i=0; i<frequencies.length; i++) {
	    		if (min == frequencies[i]) {
	    			sbGovMin.setProgress(i);
	    			break;
	    		}
	    	}
		        	
	    	for (int i=0; i<frequencies.length; i++) {
	    		if (max == frequencies[i]) {
	    			sbGovMax.setProgress(i);
	    			break;
	    		}
	    	}
		}
		
		if (savedInstanceState == null) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	
			for (int i=0; i < cpuTotal; i++) {
				CpuItemFragment fragment = new CpuItemFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("cpuNo", i);
				fragment.setArguments(bundle);
				fragmentTransaction.add(R.id.cpu_fragment, fragment);
			}
	
			fragmentTransaction.commit();
		}

		return view;
	}
}
