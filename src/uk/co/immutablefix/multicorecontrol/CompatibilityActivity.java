package uk.co.immutablefix.multicorecontrol;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CompatibilityActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compatibility);

		((RelativeLayout) findViewById(R.id.relativeLayout)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
	    });
		
		int idImageGreen = getResources().getIdentifier("@drawable/round_dot_green", "drawable", getPackageName());
		int idImageRed = getResources().getIdentifier("@drawable/round_dot_red", "drawable", getPackageName());

		if (new CpuControl().isSupported()) {
			((ImageView) (findViewById(R.id.ivCpuControl))).setImageResource(idImageGreen);
		} else {
			((ImageView) (findViewById(R.id.ivCpuControl))).setImageResource(idImageRed);
		}

		if (new VoltageControl().isSupported()) {
			((ImageView) (findViewById(R.id.ivVoltageControl))).setImageResource(idImageGreen);
		} else {
			((ImageView) (findViewById(R.id.ivVoltageControl))).setImageResource(idImageRed);
		}
		
		if (new MPDecision().isSupported()) {
			((ImageView) (findViewById(R.id.ivMpdControl))).setImageResource(idImageGreen);
		} else {
			((ImageView) (findViewById(R.id.ivMpdControl))).setImageResource(idImageRed);
		}

		if (new ColourControl().isSupported()) {
			((ImageView) (findViewById(R.id.ivColourControl))).setImageResource(idImageGreen);
		} else {
			((ImageView) (findViewById(R.id.ivColourControl))).setImageResource(idImageRed);
		}
	}
}
