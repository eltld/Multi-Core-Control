package uk.co.immutablefix.multicorecontrol;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CpuFragment extends Fragment {
	final static int CPU_TOTAL = 4;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
		for (int i=0; i < CPU_TOTAL; i++) {
//			tvCpuLable[i].setText("CPU" + i + 1);
		}
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.cpu_fragment, container, false);

		if (savedInstanceState == null) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	
			for (int i=0; i < CPU_TOTAL; i++) {
				CpuItemFragment fragment = new CpuItemFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("cpuId", i);
				fragment.setArguments(bundle);
				fragmentTransaction.add(R.id.cpu_fragment, fragment);
			}
	
			fragmentTransaction.commit();
		}

		return view;
	}
	
}
