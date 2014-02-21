/*
* This file is part of Multi Core Control.
*
* Copyright Shaun Simpson <shauns2029@gmail.com>
*
*/

package uk.co.immutablefix.systemcontrol;


import com.stericson.RootTools.RootTools;

import uk.co.immutablefix.systemcontrol.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

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
		
		((TextView) findViewById(R.id.tvSendEmail)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				SysfsInterface sys = new SysfsInterface();
				try {
					SendMail("Kernel: " + sys.getSetting("/proc/sys/kernel/osrelease") + "\n\n");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					SendMail("");
				}
			}
	    });
		
		
	}
	
	void SendMail(String body)
	{
	   String[] recepient =  new String[1];
	   recepient[0] = "immutablefix@gmail.com";
	   String subject="System Control";
	   Intent i = new Intent(Intent.ACTION_SEND);
	   i.setType("message/rfc822");
	   i.putExtra(Intent.EXTRA_EMAIL, recepient);
	   i.putExtra(Intent.EXTRA_SUBJECT, subject);
	   i.putExtra(Intent.EXTRA_TEXT   , body);
	   try {
	      startActivity(Intent.createChooser(i, "Send mail..."));
	    } catch (android.content.ActivityNotFoundException ex) {
	    Toast.makeText(getApplicationContext(), 
	    		"There are no email clients installed.", Toast.LENGTH_SHORT).show();
	    }
	}
	
}
