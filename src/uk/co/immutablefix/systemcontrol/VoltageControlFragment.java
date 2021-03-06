/*
* This file is part of Multi Core Control.
*
* Copyright Shaun Simpson <shauns2029@gmail.com>
*
*/

package uk.co.immutablefix.systemcontrol;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;

public class VoltageControlFragment extends Fragment {
	static private int[] savedUIVoltages = null;
	private boolean uiActive = false;

	TextView[] tvVoltages = null;
	TextView[] tvFrequencies = null;
	SeekBar[] sbVoltages = null;
	ImageView[] imgMinus = null;
	ImageView[] imgPlus = null;

	VoltageControl vc = null;

	int idImageMinus = 0;
	int idImagePlus = 0;
	int idImageMinusRed = 0;
	int idImagePlusRed = 0;
	
	int [] appliedVoltages = null;

	CheckBox cbxBoot;
	SharedPreferences prefs;

	public boolean isSupported(){
        return new VoltageControl().isSupported();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroyView () {
		if (uiActive) savedUIVoltages = getUiVoltages();
	    super.onDestroyView();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = null;
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
		vc = new VoltageControl();
		int max, min;
		
	    try {
	    	appliedVoltages = vc.getVoltages();
	    	
	    	max = prefs.getInt("MaxVoltage", 0);
	    	
	    	if (max < VoltageControl.MAX_VOLTAGE){
				SharedPreferences.Editor e = prefs.edit();
				e.putInt("MaxVoltage", VoltageControl.MAX_VOLTAGE);
				e.commit(); // this saves to disk and notifies observers
	    	} else {
	    		VoltageControl.MAX_VOLTAGE = max;
	    	}

	    	min = prefs.getInt("MinVoltage", VoltageControl.MIN_DEFAULT_VOLTAGE);

	    	if (min > VoltageControl.MIN_VOLTAGE){
				SharedPreferences.Editor e = prefs.edit();
				e.putInt("MinVoltage", VoltageControl.MIN_VOLTAGE);
				e.commit(); // this saves to disk and notifies observers
	    	} else {
	    		VoltageControl.MIN_VOLTAGE = min;
	    	}

		    try {
		    	view = getVoltageUI(vc.getFrequencies());
				setUiVoltages(appliedVoltages);
				if (savedUIVoltages != null)
				{
					setUiVoltages(savedUIVoltages);
				}
			} catch (Exception e) {
				Toast.makeText(getActivity().getApplicationContext(),
						"Error getting CPU frequencies. " + e.getMessage(),
				  		Toast.LENGTH_LONG).show();
			}
	    } catch (Exception e) {
		}
		
	    if (view == null) {
	        uiActive = false;
	    	view = inflater.inflate(R.layout.voltage_control_unsupported, container, false);
	    } else {
	        uiActive = true;
	    }
	    
	    return view;
	}

	public View getVoltageUI(int freqs[]){
    		LinearLayout[] llSettings = new LinearLayout[freqs.length];
	    	tvVoltages = new TextView[freqs.length];
	    	TextView[] tvFrequencies = new TextView[freqs.length];
	    	sbVoltages = new SeekBar[freqs.length];
	    	imgMinus = new ImageView[freqs.length];
	    	imgPlus = new ImageView[freqs.length];
	    	LinearLayout ll = new LinearLayout(getActivity().getApplicationContext());
	    	ll.setOrientation(LinearLayout.VERTICAL);

	    	ScrollView sv = new ScrollView(getActivity().getApplicationContext());
	    	sv.addView(ll);

			idImageMinus = getResources().getIdentifier("drawable/round_minus", "drawable", getActivity().getPackageName());
			idImagePlus = getResources().getIdentifier("drawable/round_plus", "drawable", getActivity().getPackageName());
			idImageMinusRed = getResources().getIdentifier("drawable/round_minus_red", "drawable", getActivity().getPackageName());
			idImagePlusRed = getResources().getIdentifier("drawable/round_plus_red", "drawable", getActivity().getPackageName());

			TableLayout.LayoutParams params = new TableLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 
					LayoutParams.WRAP_CONTENT, 
					2f);
			
			LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(90, 90);

			for (int i=0; i < llSettings.length; i++){
				llSettings[i] = new LinearLayout(getActivity().getApplicationContext());
	    		llSettings[i].setPadding(20, 20, 20, 20);
	    		llSettings[i].setOrientation(LinearLayout.HORIZONTAL);
	    		llSettings[i].setLayoutParams(new TableLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 
						LayoutParams.WRAP_CONTENT, 
						1f));
    		
	    		// Add frequency textview
	    		tvFrequencies[i] = new TextView(getActivity().getApplicationContext());
	    		tvFrequencies[i].setText(freqs[i] / 1000 + " MHz");
	    		tvFrequencies[i].setLayoutParams(params);
			
	    		llSettings[i].addView(tvFrequencies[i]);
			
				imgMinus[i] = new ImageView(getActivity().getApplicationContext());
				imgMinus[i].setId(i);
				imgMinus[i].setPadding(5, 0, 10, 0);
				imgMinus[i].setLayoutParams(imgParams);
				imgMinus[i].setImageResource(idImageMinus);
				imgMinus[i].setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						int current = sbVoltages[v.getId()].getProgress();
						int applied = appliedVoltages[v.getId()] - VoltageControl.MIN_VOLTAGE;
						
						if ((current > applied) && (current - applied < 5)) {
							current = applied;
						} else {
							current -= 5;
						}
						sbVoltages[v.getId()].setProgress(current);
					}
				});

				llSettings[i].addView(imgMinus[i]);
			
	    		// Add voltage textview
	    		sbVoltages[i] = new SeekBar(getActivity().getApplicationContext());
	    		sbVoltages[i].setLayoutParams(new TableLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 
						LayoutParams.WRAP_CONTENT, 
						1f));
	    		sbVoltages[i].setMax(VoltageControl.MAX_VOLTAGE - VoltageControl.MIN_VOLTAGE); //ToDo: Add max value
	    		sbVoltages[i].setProgress(0);
	    		sbVoltages[i].setTag(i);
	    		sbVoltages[i].setOnSeekBarChangeListener(new OnSeekBarChangeListener() {       
	    		    @Override       
	    		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
	    		    	int i = (Integer) seekBar.getTag();
	    		    	tvVoltages[i].setText(progress + VoltageControl.MIN_VOLTAGE + " mV");
	    		    	updatePlusMinusImage(i, progress);
	    		    }
	
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
	
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
	    		});

	    		llSettings[i].addView(sbVoltages[i]);

				imgPlus[i] = new ImageView(getActivity().getApplicationContext());
				imgPlus[i].setId(i);
				imgPlus[i].setPadding(5, 0, 10, 0);
				imgPlus[i].setLayoutParams(imgParams);
				imgPlus[i].setImageResource(idImagePlus);
				imgPlus[i].setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						int current = sbVoltages[v.getId()].getProgress();
						int applied = appliedVoltages[v.getId()] - VoltageControl.MIN_VOLTAGE;
						
						if ((applied > current) && (applied - current < 5)) {
							current = applied;
						} else {
							current += 5;
						}
						
						sbVoltages[v.getId()].setProgress(current);
					}
				});
    		
				llSettings[i].addView(imgPlus[i]);
    		
				// Add voltage textview
	    		tvVoltages[i] = new TextView(getActivity().getApplicationContext());
	    		tvVoltages[i].setText(VoltageControl.MIN_VOLTAGE + " mV");
	    		tvVoltages[i].setLayoutParams(params);
    		
	    		llSettings[i].addView(tvVoltages[i]);
	    		ll.addView(llSettings[i]);
			}
	   	
	    	LinearLayout llbtn = new LinearLayout(getActivity().getApplicationContext());
			llbtn.setPadding(20, 20, 20, 20);
			llbtn.setOrientation(LinearLayout.HORIZONTAL);
			llbtn.setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 
					LayoutParams.MATCH_PARENT, 
					1f));
	
	    	Button btnDown5 = new Button(getActivity().getApplicationContext());
	        params.gravity=Gravity.LEFT;
	        btnDown5.setLayoutParams(params);
	        btnDown5.setText("-5mV");
	        btnDown5.setOnClickListener(new OnClickListener (){
				@Override
				public void onClick(View v) {
					for (int i=0; i<sbVoltages.length; i++)
					{
						sbVoltages[i].setProgress(sbVoltages[i].getProgress() - 5);
					}
				}
	    	});
	    	llbtn.addView(btnDown5);
	
	    	Button btnDown = new Button(getActivity().getApplicationContext());
	        params.gravity=Gravity.LEFT;
	        btnDown.setLayoutParams(params);
	        btnDown.setText("-25mV");
	        btnDown.setOnClickListener(new OnClickListener (){
				@Override
				public void onClick(View v) {
					for (int i=0; i<sbVoltages.length; i++)
					{
						sbVoltages[i].setProgress(sbVoltages[i].getProgress() - 25);
					}
				}
	    	});
	    	llbtn.addView(btnDown);
	    	
	    	Button btnUp5 = new Button(getActivity().getApplicationContext());
	        params.gravity=Gravity.RIGHT;
	        btnUp5.setLayoutParams(params);
	        btnUp5.setText("+5mV");
	        btnUp5.setOnClickListener(new OnClickListener (){
				@Override
				public void onClick(View v) {
					for (int i=0; i<sbVoltages.length; i++)
					{
						sbVoltages[i].setProgress(sbVoltages[i].getProgress() + 5);
					}
				}
	    	});
	    	llbtn.addView(btnUp5);
	
	    	Button btnUp = new Button(getActivity().getApplicationContext());
	        params.gravity=Gravity.RIGHT;
	        btnUp.setLayoutParams(params);
	        btnUp.setText("+25mV");
	        btnUp.setOnClickListener(new OnClickListener (){
				@Override
				public void onClick(View v) {
					for (int i=0; i<sbVoltages.length; i++)
					{
						sbVoltages[i].setProgress(sbVoltages[i].getProgress() + 25);
					}
				}
	    	});
	    	llbtn.addView(btnUp);
	    	
	    	ll.addView(llbtn);
	
	    	LinearLayout llbtn2 = new LinearLayout(getActivity().getApplicationContext());
			llbtn2.setPadding(20, 20, 20, 20);
			llbtn2.setOrientation(LinearLayout.HORIZONTAL);
			llbtn2.setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 
					LayoutParams.MATCH_PARENT, 
					1f));
	
	    	Button btnApply = new Button(getActivity().getApplicationContext());
	        params.gravity=Gravity.LEFT;
	    	btnApply.setLayoutParams(params);
	    	btnApply.setText("Apply");
	    	btnApply.setOnClickListener(new OnClickListener (){
				@Override
				public void onClick(View v) {
	 				try {
						vc.setVoltages(getUiVoltages());
				    	appliedVoltages = vc.getVoltages();
				    	updateUI();
				    	
						Toast.makeText(getActivity().getApplicationContext(),
								"Successfully set CPU voltages.", 
				    			Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Toast.makeText(getActivity().getApplicationContext(),
								"Error setting CPU voltages. " + e.getMessage(),
						  		Toast.LENGTH_LONG).show();
					}
				}
	    	});
	    	llbtn2.addView(btnApply);
	
	    	Button btnSave = new Button(getActivity().getApplicationContext());
	        params.gravity=Gravity.CENTER;
	    	btnSave.setLayoutParams(params);
	    	btnSave.setText("Save");
	    	btnSave.setOnClickListener(new OnClickListener (){
				@Override
				public void onClick(View v) {
					SharedPreferences.Editor e = prefs.edit();
					e.putString("CustomVoltages", getVoltages());
					e.commit(); // this saves to disk and notifies observers

					// Enable apply on boot check box if needed.
					if (!cbxBoot.isEnabled()) cbxBoot.setEnabled(true);

					Toast.makeText(getActivity().getApplicationContext(),
							"Saved",
					  		Toast.LENGTH_SHORT).show();
				}
	    	});
	    	llbtn2.addView(btnSave);
	
	    	Button btnLoad = new Button(getActivity().getApplicationContext());
	        params.gravity=Gravity.RIGHT;
	    	btnLoad.setLayoutParams(params);
	    	btnLoad.setText("Load");
	    	btnLoad.setOnClickListener(new OnClickListener (){
				@Override
				public void onClick(View v) {
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
					String volts = prefs.getString("CustomVoltages", null);
					if (volts != null){
						try {
							setUiVoltages(volts);

							Toast.makeText(getActivity().getApplicationContext(),
									"Loaded",
							  		Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							Toast.makeText(getActivity().getApplicationContext(),
									"No settings saved.",
							  		Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(getActivity().getApplicationContext(),
								"No settings saved.",
						  		Toast.LENGTH_LONG).show();
					}
				}
	    	});
	    	llbtn2.addView(btnLoad);

	    	Button btnReset = new Button(getActivity().getApplicationContext());
	        params.gravity=Gravity.RIGHT;
	        btnReset.setLayoutParams(params);
	        btnReset.setText("Reset");
	        btnReset.setOnClickListener(new OnClickListener (){
				@Override
				public void onClick(View v) {
				    try {
				    	appliedVoltages = vc.getVoltages(); 
						setUiVoltages(appliedVoltages);

						Toast.makeText(getActivity().getApplicationContext(),
								"Reset",
						  		Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Toast.makeText(getActivity().getApplicationContext(),
								"Error getting CPU voltages. " + e.getMessage(),
						  		Toast.LENGTH_LONG).show();
					}
				}
	    	});
	    	llbtn2.addView(btnReset);
	    	
	    	
	    	ll.addView(llbtn2);
	
	    	cbxBoot = new CheckBox(getActivity().getApplicationContext());
		    cbxBoot.setChecked(prefs.getBoolean("VoltagesApplyOnBoot", false));
		    cbxBoot.setText("Apply saved settings on boot");
		    // Test if there are settings to apply on boot. If not disable the check box.
			cbxBoot.setEnabled(prefs.getString("CustomVoltages", null) != null);
		    cbxBoot.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Boolean applyOnBoot = cbxBoot.isChecked();
					
					SharedPreferences.Editor e = prefs.edit();
					e.putBoolean("VoltagesApplyOnBoot", applyOnBoot);
					e.commit(); // this saves to disk and notifies observers
				}
			});		
	    	
		    ll.addView(cbxBoot);
    	
    	return sv;
    }
    
    public void updateUI() {
    	for (int i = 0; i < sbVoltages.length; i++){
    		updatePlusMinusImage(i, sbVoltages[i].getProgress());
    	}
    }

    public void updatePlusMinusImage(int i, int progress){
    	imgMinus[i].setImageResource(idImageMinus);
		imgPlus[i].setImageResource(idImagePlus);
    	
    	if (appliedVoltages.length == tvVoltages.length){
	    	if (appliedVoltages[i] > progress + VoltageControl.MIN_VOLTAGE) { 
	    		imgMinus[i].setImageResource(idImageMinusRed);
    		} else if (appliedVoltages[i] < progress + VoltageControl.MIN_VOLTAGE) { 
	    		imgPlus[i].setImageResource(idImagePlusRed);
	    	}
    	}
	
    }
    
    public void setUiVoltages(int [] voltages) throws Exception{
    	if (voltages.length != sbVoltages.length){
    		throw new Exception("Failed to set voltages, incorrect number of voltages.");
    	}
    	
    	for(int i=0; i<voltages.length; i++){
    		sbVoltages[i].setProgress(voltages[i] - VoltageControl.MIN_VOLTAGE);
    	}
    }
    
    public void setUiVoltages(String volts) throws Exception{
    	String[] voltages = volts.split("[ ]+");

    	if (voltages.length != sbVoltages.length){
    		throw new Exception("Failed to update UI voltages.");
    	}
    	
    	//ToDo: Error if voltages count is wrong.
    	if (sbVoltages.length == sbVoltages.length){
	    	for(int i=0; i<voltages.length; i++){
	    		sbVoltages[i].setProgress(Integer.parseInt(voltages[i]) - VoltageControl.MIN_VOLTAGE);
	    	}
    	}
    }

    public int[] getUiVoltages(){
		int[] volts = new int[sbVoltages.length];

		for(int i=0; i<sbVoltages.length; i++)
		{
			volts[i] = sbVoltages[i].getProgress() + VoltageControl.MIN_VOLTAGE;
		}
		return volts;
    }

    public String getVoltages(){
		String volts = "";

		for(int i=0; i<sbVoltages.length; i++)
		{
			volts += sbVoltages[i].getProgress() + VoltageControl.MIN_VOLTAGE; 
			if (i < sbVoltages.length - 1) volts += " ";
		}
		return volts;
    }
}

