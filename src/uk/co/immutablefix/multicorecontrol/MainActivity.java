package uk.co.immutablefix.multicorecontrol;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;

import uk.co.immutablefix.multicorecontrol.R;


public class MainActivity extends Activity {
	TextView [] tvVoltage;
	SeekBar [] sbVoltage;
	int MIN_VOLTAGE = 700;
	int MAX_VOLTAGE = 1150;

	CheckBox cbxBoot;
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		VoltageControl vc = new VoltageControl();

	    try {
			addVoltageUI(vc.getFrequencies());
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"Error getting CPU frequencies. " + e.getMessage(),
			  		Toast.LENGTH_LONG).show();
		}
	    
	    try {
			setUiVoltages(vc.getVoltages());
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"Error getting CPU voltages. " + e.getMessage(),
			  		Toast.LENGTH_LONG).show();
		}
	}

    //Creates menus
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
    
    //Handles menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.mitmAbout:
    		Intent about = new Intent(this, AboutActivity.class);
    		startActivity(about);
    		return true;
    	case R.id.mitmQuit:
    		finish();
    		break;
    	}
    	
    	return false;
    }  
    
    public void addVoltageUI(int freqs[]){
    	LinearLayout [] llSettings;    	

    	llSettings = new LinearLayout[freqs.length]; 
    	tvVoltage = new TextView[freqs.length];
    	sbVoltage = new SeekBar[freqs.length];
    	
    	ScrollView sv = new ScrollView(this);
    	LinearLayout ll = new LinearLayout(this);
    	ll.setOrientation(LinearLayout.VERTICAL);
    	sv.addView(ll);

    	for (int i=0; i < llSettings.length; i++){

    		llSettings[i] = new LinearLayout(this);
    		llSettings[i].setPadding(20, 20, 20, 20);
    		llSettings[i].setOrientation(LinearLayout.HORIZONTAL);
    		llSettings[i].setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 
					LayoutParams.WRAP_CONTENT, 
					1f));
    		
    		// Add frequency textview
    		TextView tv = new TextView(this);
			tv.setText(freqs[i] + "MHz");
			tv.setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 
					LayoutParams.WRAP_CONTENT, 
					2f));
			llSettings[i].addView(tv);
			
    		// Add voltage textview
    		sbVoltage[i] = new SeekBar(this);
    		sbVoltage[i].setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 
					LayoutParams.WRAP_CONTENT, 
					1f));
    		sbVoltage[i].setMax(MAX_VOLTAGE - MIN_VOLTAGE); //ToDo: Add max value
    		sbVoltage[i].setProgress(0);
    		llSettings[i].addView(sbVoltage[i]);
    		
    		sbVoltage[i].setId(i);
    		
    		sbVoltage[i].setOnSeekBarChangeListener(new OnSeekBarChangeListener() {       
    		    @Override       
    		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {     
    		    	tvVoltage[seekBar.getId()].setText(progress + MIN_VOLTAGE + "mV");
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

			// Add voltage textview
    		tvVoltage[i] = new TextView(this);
    		tvVoltage[i].setText(MIN_VOLTAGE + "mV");
    		tvVoltage[i].setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 
					LayoutParams.WRAP_CONTENT, 
					2f));
    		llSettings[i].addView(tvVoltage[i]);
			ll.addView(llSettings[i]);
		}
    	
    	LinearLayout llbtn = new LinearLayout(this);
		llbtn.setPadding(20, 20, 20, 20);
		llbtn.setOrientation(LinearLayout.HORIZONTAL);
		llbtn.setLayoutParams(new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT, 
				1f));

    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);

    	Button btnDown = new Button(this);
        params.gravity=Gravity.LEFT;
        btnDown.setLayoutParams(params);
        btnDown.setText("-10 mV");
        btnDown.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				for (int i=0; i<sbVoltage.length; i++)
				{
					sbVoltage[i].setProgress(sbVoltage[i].getProgress() - 10);
				}
			}
    	});
    	llbtn.addView(btnDown);

    	Button btnUp = new Button(this);
        params.gravity=Gravity.RIGHT;
        btnUp.setLayoutParams(params);
        btnUp.setText("+ 10 mV");
        btnUp.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				for (int i=0; i<sbVoltage.length; i++)
				{
					sbVoltage[i].setProgress(sbVoltage[i].getProgress() + 10);
				}
			}
    	});
    	llbtn.addView(btnUp);
    	
    	ll.addView(llbtn);

    	LinearLayout llbtn2 = new LinearLayout(this);
		llbtn2.setPadding(20, 20, 20, 20);
		llbtn2.setOrientation(LinearLayout.HORIZONTAL);
		llbtn2.setLayoutParams(new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT, 
				1f));

    	Button btnApply = new Button(this);
        params.gravity=Gravity.LEFT;
    	btnApply.setLayoutParams(params);
    	btnApply.setText("Apply");
    	btnApply.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				VoltageControl vc = new VoltageControl();
 				vc.setVoltages(getApplicationContext(), getUiVoltages());
			}
    	});
    	llbtn2.addView(btnApply);

    	Button btnSave = new Button(this);
        params.gravity=Gravity.CENTER;
    	btnSave.setLayoutParams(params);
    	btnSave.setText("Save");
    	btnSave.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor e = prefs.edit();
				e.putString("CustomVoltages", getUiVoltages());
				e.commit(); // this saves to disk and notifies observers
			}
    	});
    	llbtn2.addView(btnSave);

    	Button btnLoad = new Button(this);
        params.gravity=Gravity.RIGHT;
    	btnLoad.setLayoutParams(params);
    	btnLoad.setText("Load");
    	btnLoad.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String volts = prefs.getString("CustomVoltages", null);
				if (volts != null){
					setUiVoltages(volts);
				}
					
			}
    	});
    	llbtn2.addView(btnLoad);
    	
    	ll.addView(llbtn2);

    	cbxBoot = new CheckBox(this);
	    cbxBoot.setChecked(prefs.getBoolean("ApplyOnBoot", false));
	    cbxBoot.setText("Apply custom voltages on boot");
	    
	    cbxBoot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean applyOnBoot = cbxBoot.isChecked();
				
				SharedPreferences.Editor e = prefs.edit();
				e.putBoolean("ApplyOnBoot", applyOnBoot);
				e.commit(); // this saves to disk and notifies observers
			}
		});		
    	
	    ll.addView(cbxBoot);
    	
    	this.setContentView(sv);
    }
    
    public void setUiVoltages(int [] voltages){
    	for(int i=0; i<voltages.length; i++){
    		sbVoltage[i].setProgress(voltages[i] - MIN_VOLTAGE);
    	}
    }
    
    public void setUiVoltages(String volts){
    	String[] voltages = volts.split("[ ]+");
    	//ToDo: Error if voltages count is wrong.
    	if (sbVoltage.length == sbVoltage.length){
	    	for(int i=0; i<voltages.length; i++){
	    		sbVoltage[i].setProgress(Integer.parseInt(voltages[i]) - MIN_VOLTAGE);
	    	}
    	}
    }

    public String getUiVoltages(){
		String volts = "";
		for(int i=0; i<sbVoltage.length; i++)
		{
			volts += String.valueOf(sbVoltage[i].getProgress() + MIN_VOLTAGE);
			if (i<sbVoltage.length-1) volts += " ";
		}
		return volts;
    }
}

/*
 * 		setContentView(R.layout.activity_main);

		editDefault = (EditText) findViewById(R.id.editDefault);
		editCustom = (EditText) findViewById(R.id.editCustom);
		
		editDefault.setText(VoltageControl.defaultVoltages); 
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		editCustom.setText(prefs.getString("CustomVoltages", VoltageControl.defaultCustomVoltages));
		
	    Button btnApplyCustom = (Button) findViewById(R.id.buttonApplyCustom);
	    btnApplyCustom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				VoltageControl vc = new VoltageControl();

 				if (vc.setVoltages(getApplicationContext(), editCustom.getText().toString()) == 0) {
					SharedPreferences.Editor e = prefs.edit();
					e.putString("CustomVoltages", editCustom.getText().toString());
					e.commit(); // this saves to disk and notifies observers
 				}
			}
		});		

	    Button btnApplyDefault = (Button) findViewById(R.id.buttonApplyDefault);
	    btnApplyDefault.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				VoltageControl vc = new VoltageControl();

 				vc.setVoltages(getApplicationContext(), editDefault.getText().toString());
			}
		});		

	    cbxBoot = (CheckBox) findViewById(R.id.checkBoxBoot);
	    cbxBoot.setChecked(prefs.getBoolean("ApplyOnBoot", false)); 
	    cbxBoot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean applyOnBoot = cbxBoot.isChecked();
				
				SharedPreferences.Editor e = prefs.edit();
				e.putBoolean("ApplyOnBoot", applyOnBoot);
				e.commit(); // this saves to disk and notifies observers
			}
		});		

	    Button btnReset = (Button) findViewById(R.id.buttonReset);
	    btnReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editDefault.setText(VoltageControl.defaultVoltages); 
				editCustom.setText(VoltageControl.defaultCustomVoltages); 
			}
		});		
 */
