package uk.co.immutablefix.multicorecontrol;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;

import uk.co.immutablefix.multicorecontrol.R;


public class MainActivity extends Activity {
	TextView [] tvVoltage;
	SeekBar [] sbVoltage;

	EditText editDefault, editCustom;
	CheckBox cbxBoot;
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
	    
	    String [] frequencies;
	    frequencies = new String[12];
	    
	    for (int i=0; i < frequencies.length; i++){
	    	frequencies[i] = i*100 + "MHz";
	    }
	    
	    addVoltageUI(frequencies);
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
    
    public void addVoltageUI(String freqs[]){
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
			tv.setText(freqs[i]);
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
    		sbVoltage[i].setMax(1400); //ToDo: Add max value
    		sbVoltage[i].setProgress(0);
    		llSettings[i].addView(sbVoltage[i]);
    		
    		sbVoltage[i].setId(i);
    		
    		sbVoltage[i].setOnSeekBarChangeListener(new OnSeekBarChangeListener() {       
    		    @Override       
    		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {     
    		    	tvVoltage[seekBar.getId()].setText(progress + "mV");
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
    		tvVoltage[i].setText("0mv");
    		tvVoltage[i].setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 
					LayoutParams.WRAP_CONTENT, 
					2f));
    		llSettings[i].addView(tvVoltage[i]);
   		
			ll.addView(llSettings[i]);
		}
    	
    	this.setContentView(sv);
    }

}
