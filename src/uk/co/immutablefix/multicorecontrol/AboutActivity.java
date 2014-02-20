/*
* This file is part of Multi Core Control.
*
* Copyright Shaun Simpson <shauns2029@gmail.com>
*
*/

package uk.co.immutablefix.multicorecontrol;


import uk.co.immutablefix.multicorecontrol.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		((LinearLayout) findViewById(R.id.LinearLayout)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
	    });
	}
	
}
