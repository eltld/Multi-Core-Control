package uk.co.immutablefix.multicorecontrol;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import uk.co.immutablefix.multicorecontrol.R;

public class MPDecisionFragment extends Fragment {
	private SeekBar sbMaxCPUs = null;
	private TextView tvMaxCPUs = null;
	private SeekBar sbMinCPUs = null;
	private TextView tvMinCPUs = null;
	
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
		View view = inflater.inflate(R.layout.activity_mpdecision, container, false);
			
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
		
		tvMinCPUs = (TextView) view.findViewById(R.id.tvMinCPUs);
		tvMaxCPUs = (TextView) view.findViewById(R.id.tvMaxCPUs);
		
        sbMinCPUs = (SeekBar) view.findViewById(R.id.sbMinCPUs);
        sbMinCPUs.setMax(3);
        sbMinCPUs.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tvMinCPUs.setText(String.valueOf(progress + 1));
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});	

        sbMaxCPUs = (SeekBar) view.findViewById(R.id.sbMaxCPUs);
        sbMaxCPUs.setMax(3);
        sbMaxCPUs.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tvMaxCPUs.setText(String.valueOf(progress + 1));
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
				MPDecision mpd = new MPDecision();
 				
				try {
 			        mpd.setMinCPUs(sbMinCPUs.getProgress() + 1);
 			        mpd.setMaxCPUs(sbMaxCPUs.getProgress() + 1);

 			        Toast.makeText(getActivity().getApplicationContext(),
							"Successfully configured cores.", 
			    			Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(getActivity().getApplicationContext(),
							"Error configuring cores. " + e.getMessage(),
					  		Toast.LENGTH_LONG).show();
				}
			}
    	});        
        
    	Button btnSave = (Button) view.findViewById(R.id.btnSave);
    	btnSave.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor e = prefs.edit();
				e.putInt("MinCPUs", sbMinCPUs.getProgress() + 1);
				e.putInt("MaxCPUs", sbMaxCPUs.getProgress() + 1);
				e.commit(); // this saves to disk and notifies observers

				Toast.makeText(getActivity().getApplicationContext(),
						"Saved",
				  		Toast.LENGTH_SHORT).show();
			}
    	});        

        cbxBoot = (CheckBox) view.findViewById(R.id.cbxBoot);
	    cbxBoot.setChecked(prefs.getBoolean("MPDApplyOnBoot", false));        
	    cbxBoot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean applyOnBoot = cbxBoot.isChecked();
				
				SharedPreferences.Editor e = prefs.edit();
				e.putBoolean("MPDApplyOnBoot", applyOnBoot);
				e.commit(); // this saves to disk and notifies observers
			}
		});		
        
	    TextView tvUnsupported = (TextView) view.findViewById(R.id.tvUnsupported);
	    
        MPDecision mpd = new MPDecision();
        try {
			sbMinCPUs.setProgress(mpd.getMinCPUs() - 1);
			sbMaxCPUs.setProgress(mpd.getMaxCPUs() - 1);
			tvUnsupported.setVisibility(View.GONE);
		} catch (Exception e) {
			sbMinCPUs.setEnabled(false);
			sbMaxCPUs.setEnabled(false);

			Toast.makeText(getActivity().getApplicationContext(),
					e.getMessage(),
			  		Toast.LENGTH_LONG).show();
		}

        return view;
	}
}
