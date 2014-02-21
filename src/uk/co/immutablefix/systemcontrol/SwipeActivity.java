package uk.co.immutablefix.systemcontrol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SwipeActivity extends FragmentActivity {
	
	static ViewPager mViewPager;
	
	static long backPressed = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if (!RootTools.isRootAvailable()) {
        	showExitMessage(R.string.no_root_title, R.string.no_root_message);
	        return;
        } else {
        	try {
				RootTools.getShell(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				showExitMessage(R.string.no_root_title, R.string.timeout_root_message);
		        return;
			} catch (RootDeniedException e) {
				showExitMessage(R.string.no_root_title, R.string.denied_root_message);
		        return;
			}
        }

        Fragment tabOneFragment = new CpuControlFragment();
        Fragment tabTwoFragment = new VoltageControlFragment();
        Fragment tabThreeFragment = new MPDecisionFragment();
        Fragment tabFourFragment = new ColourControlFragment();

        // Remove unsupported tabs.
        if (!((VoltageControlFragment) tabTwoFragment).isSupported()) tabTwoFragment = null;
		if (!((MPDecisionFragment) tabThreeFragment).isSupported()) tabThreeFragment = null;
		if (!((ColourControlFragment) tabFourFragment).isSupported()) tabFourFragment = null;
        
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(tabOneFragment);
		if (tabTwoFragment != null)	mPagerAdapter.addFragment(tabTwoFragment);
		if (tabThreeFragment != null) mPagerAdapter.addFragment(tabThreeFragment);
		if (tabFourFragment != null) mPagerAdapter.addFragment(tabFourFragment);
        
        mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOffscreenPageLimit(4);
	    mViewPager.setCurrentItem(0);
		
		mViewPager.setOnPageChangeListener(
	            new ViewPager.SimpleOnPageChangeListener() {
	                @Override
	                public void onPageSelected(int position) {
	                    // When swiping between pages, select the
	                    // corresponding tab.
	                    getActionBar().setSelectedNavigationItem(position);
	                }
	            });
        
        ActionBar ab = getActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tab1 = ab.newTab().setText("CPU Control")
				.setTabListener(new TabListener<CpuControlFragment>(
                        this, "cpucontrol", CpuControlFragment.class));
		ab.addTab(tab1);

		if (tabTwoFragment != null){
			Tab tab2 = ab.newTab().setText("Voltage Control")
					.setTabListener(new TabListener<VoltageControlFragment>(
	                        this, "voltagecontrol", VoltageControlFragment.class));
			ab.addTab(tab2);
        }

		if (tabThreeFragment != null){
			Tab tab3 = ab.newTab().setText("MPD Control")
	        		.setTabListener(new TabListener<MPDecisionFragment>(
	                        this, "mpdecision", MPDecisionFragment.class));
			ab.addTab(tab3);
		}

		if (tabFourFragment != null){
			Tab tab4 = ab.newTab().setText("Colour Control")
					.setTabListener(new TabListener<ColourControlFragment>(
	                        this, "colourcontrol", ColourControlFragment.class));
			ab.addTab(tab4);
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if (prefs.getInt("FirstRunVer", 0) < 1) {
			Intent compatibility = new Intent(this, CompatibilityActivity.class);
			startActivity(compatibility);
			Intent about = new Intent(this, AboutActivity.class);
			startActivity(about);
			
			SharedPreferences.Editor e = prefs.edit();
			e.putInt("FirstRunVer", 1);
			e.commit(); // this saves to disk and notifies observers
		}
    }
    
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        /** Constructor used each time a new tab is created.
          * @param activity  The host Activity, used to instantiate the fragment
          * @param tag  The identifier tag for the fragment
          * @param clz  The fragment's Class, used to instantiate the fragment
          */
        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        /* The following are each of the ActionBar.TabListener callbacks */

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }

		public void onTabReselected(Tab arg0,
				android.app.FragmentTransaction arg1)
		{
			// TODO Auto-generated method stub
			
		}

		public void onTabSelected(Tab arg0, android.app.FragmentTransaction arg1)
		{
			// TODO Auto-generated method stub
			mViewPager.setCurrentItem(arg0.getPosition());
		}

		public void onTabUnselected(Tab arg0,
				android.app.FragmentTransaction arg1)
		{
			// TODO Auto-generated method stub
			
		}
    }
    
    public class PagerAdapter extends FragmentPagerAdapter {

        private final ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

        public PagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
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
    	case R.id.mitmCompatibility:
    		Intent compatibility = new Intent(this, CompatibilityActivity.class);
    		startActivity(compatibility);
    		return true;
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
    
	@Override
	public void onBackPressed()
	{
		if (backPressed < java.lang.System.currentTimeMillis()) {
			Toast.makeText(getApplicationContext(),
					"Press back again to exit", 
	    			Toast.LENGTH_SHORT).show();
			backPressed = java.lang.System.currentTimeMillis() + 5000;
		} else {
			finish();
		}
	}
    
	private void showExitMessage(int title, int message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               finish();
	           }
	       });

		builder.setMessage(message).setTitle(title);
		builder.create().show();
	}
}



