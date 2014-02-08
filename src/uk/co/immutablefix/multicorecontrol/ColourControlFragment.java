// Copyright 2013 Shaun Simpson shauns2029@gmail.com

package uk.co.immutablefix.multicorecontrol;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class ColourControlFragment extends Fragment {
	SeekBar sbarRed, sbarGreen, sbarBlue;
	SharedPreferences prefs;
	static int[] appliedColours = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.colour_control, container, false);
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

		sbarRed = (SeekBar) view.findViewById(R.id.seekBarRed);
		sbarGreen = (SeekBar) view.findViewById(R.id.seekBarGreen);
		sbarBlue = (SeekBar) view.findViewById(R.id.seekBarBlue);

		ColourControl ctl = new ColourControl();
		try {
			appliedColours = ctl.getColours();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			view = inflater.inflate(R.layout.colour_control_unsupported, container, false);
		}

		sbarRed.setProgress(appliedColours[0]);
		sbarGreen.setProgress(appliedColours[1]);
		sbarBlue.setProgress(appliedColours[2]);
/*		
		sbarRed.setProgress(prefs.getInt("RedMultiplier", sbarRed.getMax()));
		sbarGreen.setProgress(prefs.getInt("GreenMultiplier", sbarGreen.getMax()));
		sbarBlue.setProgress(prefs.getInt("BlueMultiplier", sbarBlue.getMax()));
*/
	    Button btnApply = (Button) view.findViewById(R.id.buttonApply);
	    btnApply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int red, green, blue;

				red = sbarRed.getProgress();
				green = sbarGreen.getProgress();
				blue = sbarBlue.getProgress();

				SharedPreferences.Editor e = prefs.edit();
				e.putInt("redMultiplier", red);
				e.putInt("greenMultiplier", green);
				e.putInt("blueMultiplier", blue);
				e.commit(); // this saves to disk and notifies observers
				
				int[] colours = new int[3];
				colours[0] = red;
				colours[1] = green;
				colours[2] = blue;
						
				ColourControl ctl = new ColourControl();
				try {
					ctl.setColours(colours);
				} catch (Exception e1) {
					Toast.makeText(getActivity().getApplicationContext(),
							"Error setting colours. " + e1.getMessage(),
					  		Toast.LENGTH_LONG).show();
				}
			}
		});	

	    
	    return view;
	}
}
