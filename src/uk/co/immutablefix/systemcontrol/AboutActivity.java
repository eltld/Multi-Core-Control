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
import android.widget.ImageView;
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
				String compatibility = "Comtatibility Report: ";
				try {
					if (new CpuControl().isSupported()) {
						compatibility += "CPU yes, ";
					} else {
						compatibility += "CPU no, ";
					}

					if (new VoltageControl().isSupported()) {
						compatibility += "voltage yes, ";
					} else {
						compatibility += "voltage no, ";
					}
					
					if (new MPDecision().isSupported()) {
						compatibility += "MPDecision yes, ";
					} else {
						compatibility += "MPDecision no, ";
					}

					if (new ColourControl().isSupported()) {
						compatibility += "colour yes.";
					} else {
						compatibility += "colour no.";
					}
					
					SendMail("Kernel: " + sys.getSetting("/proc/sys/kernel/osrelease") + "\n\n" + compatibility + "\n\n");
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
	   String subject="Control Your System";
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
