package uk.co.immutablefix.multicorecontrol;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import uk.co.immutablefix.multicorecontrol.R;

public class MPDecisionActivity extends Activity {
	private SeekBar sbMaxCPUs = null;
	private TextView tvMaxCPUs = null;
	private SeekBar sbMinCPUs = null;
	private TextView tvMinCPUs = null;
	
	SharedPreferences prefs;
	CheckBox cbxBoot;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mpdecision);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		tvMinCPUs = (TextView) findViewById(R.id.tvMinCPUs);
		tvMaxCPUs = (TextView) findViewById(R.id.tvMaxCPUs);
		
        sbMinCPUs = (SeekBar) findViewById(R.id.sbMinCPUs);
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

        sbMaxCPUs = (SeekBar) findViewById(R.id.sbMaxCPUs);
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
        
    	Button btnApply = (Button) findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				MPDecision mpd = new MPDecision();
 				
				try {
 			        mpd.setMinCPUs(sbMinCPUs.getProgress() + 1);
 			        mpd.setMaxCPUs(sbMaxCPUs.getProgress() + 1);

 			        Toast.makeText(getApplicationContext(),
							"Successfully configured MPD.", 
			    			Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"Error configuring MPD. " + e.getMessage(),
					  		Toast.LENGTH_LONG).show();
				}
			}
    	});        
        
    	Button btnSave = (Button) findViewById(R.id.btnSave);
    	btnSave.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor e = prefs.edit();
				e.putInt("MinCPUs", sbMinCPUs.getProgress() + 1);
				e.putInt("MaxCPUs", sbMaxCPUs.getProgress() + 1);
				e.commit(); // this saves to disk and notifies observers
			}
    	});        

        cbxBoot = (CheckBox) findViewById(R.id.cbxBoot);
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
        
        MPDecision mpd = new MPDecision();
        try {
			sbMinCPUs.setProgress(mpd.getMinCPUs() - 1);
		} catch (Exception e) {
			sbMinCPUs.setEnabled(false);
		}

        try {
			sbMaxCPUs.setProgress(mpd.getMaxCPUs() - 1);
		} catch (Exception e) {
			sbMaxCPUs.setEnabled(false);
		}
	}
}
