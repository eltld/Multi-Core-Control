package uk.co.immutablefix.multicorecontrol;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import uk.co.immutablefix.multicorecontrol.R;


public class MainActivity extends Activity {
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

}
