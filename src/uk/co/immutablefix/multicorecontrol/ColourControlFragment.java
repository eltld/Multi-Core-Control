// Copyright 2013 Shaun Simpson shauns2029@gmail.com

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

public class ColourControlFragment extends Fragment {
	SeekBar sbarRed, sbarGreen, sbarBlue = null;
	CheckBox cbxBoot = null;
	SharedPreferences prefs = null;
	static int[] appliedColours = null;
	
	public boolean isSupported(){
		return new ColourControl().isSupported();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = null;
		
		ColourControl ctl = new ColourControl();
		try {
			appliedColours = ctl.getColours();
		} catch (Exception e2) {
			view = inflater.inflate(R.layout.colour_control_unsupported, container, false);
			return view;
		}

		view = inflater.inflate(R.layout.colour_control, container, false);
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

		sbarRed = (SeekBar) view.findViewById(R.id.seekBarRed);
		sbarGreen = (SeekBar) view.findViewById(R.id.seekBarGreen);
		sbarBlue = (SeekBar) view.findViewById(R.id.seekBarBlue);
		
		sbarRed.setProgress(appliedColours[0]);
		sbarGreen.setProgress(appliedColours[1]);
		sbarBlue.setProgress(appliedColours[2]);

		Button btnApply = (Button) view.findViewById(R.id.btnApply);
	    btnApply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int red, green, blue;

				red = sbarRed.getProgress();
				green = sbarGreen.getProgress();
				blue = sbarBlue.getProgress();

				ColourControl ctl = new ColourControl();
				try {
					ctl.setColours(red, green, blue);
 			        Toast.makeText(getActivity().getApplicationContext(),
							"Successfully set colours.", 
			    			Toast.LENGTH_SHORT).show();
 			    } catch (Exception e1) {
					Toast.makeText(getActivity().getApplicationContext(),
							"Error setting colours. " + e1.getMessage(),
					  		Toast.LENGTH_LONG).show();
				}
			}
		});	

		Button btnSave = (Button) view.findViewById(R.id.btnSave);
	    btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int red, green, blue;

				red = sbarRed.getProgress();
				green = sbarGreen.getProgress();
				blue = sbarBlue.getProgress();

				SharedPreferences.Editor e = prefs.edit();
				e.putInt("RedMultiplier", red);
				e.putInt("GreenMultiplier", green);
				e.putInt("BlueMultiplier", blue);
				e.commit(); // this saves to disk and notifies observers

				// Enable apply on boot check box if needed.
				if (!cbxBoot.isEnabled()) cbxBoot.setEnabled(true);

				Toast.makeText(getActivity().getApplicationContext(),
						"Saved", 
		    			Toast.LENGTH_SHORT).show();
			}
		});	

		Button btnLoad = (Button) view.findViewById(R.id.btnLoad);
		btnLoad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int red, green, blue;

				red = prefs.getInt("RedMultiplier", -1);
				green = prefs.getInt("GreenMultiplier", -1);
				blue = prefs.getInt("BlueMultiplier", -1);
				
				if ((red >= 0) && (green >= 0) && (blue >= 0)) {
					sbarRed.setProgress(red);
					sbarGreen.setProgress(green);
					sbarBlue.setProgress(blue);
					
					Toast.makeText(getActivity().getApplicationContext(),
							"Loaded", 
			    			Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity().getApplicationContext(),
							"No settings saved.",
					  		Toast.LENGTH_LONG).show();
				}				
			}
		});	
	    
	    cbxBoot = (CheckBox) view.findViewById(R.id.cbxBoot);
	    cbxBoot.setChecked(prefs.getBoolean("ColourApplyOnBoot", false));        
		// Test if there are settings to apply on boot. If disable not the check box.
	    cbxBoot.setEnabled((prefs.getInt("RedMultiplier", -1) > -1));
	    cbxBoot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean applyOnBoot = cbxBoot.isChecked();
				
				SharedPreferences.Editor e = prefs.edit();
				e.putBoolean("ColourApplyOnBoot", applyOnBoot);
				e.commit(); // this saves to disk and notifies observers
			}
		});		

	    
	    return view;
	}
}
