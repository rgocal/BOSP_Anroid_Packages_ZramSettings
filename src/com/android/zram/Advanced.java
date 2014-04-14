package com.android.zram;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.Utils;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.NxIndustries.Zram.R;
import com.android.zram.helpers.CMDProcessor;
import com.android.zram.helpers.Constants;
import com.android.zram.helpers.Helpers;

public class Advanced extends PreferenceFragment implements OnSharedPreferenceChangeListener, Constants {

    private Preference mDirtyRatio;
	private Preference mDirtyBackground;
	private Preference mDirtyExpireCentisecs;
	private Preference mDirtyWriteback;
	private Preference mMinFreeK;
	private Preference mOvercommit;
	private Preference mSwappiness;
//--------
	private CheckBoxPreference mDsync;
	
	private Preference mBltimeout;
	private CheckBoxPreference mBltouch;

    private CheckBoxPreference mBln;
    //--------
	private CheckBoxPreference mHomeOn;
	private CheckBoxPreference mMenuBackOn;
	
	private Preference mHomeAllowedIrqs;
	private Preference mHomeReportWait;

	private Preference mMenuBackIrqChecks;
	private Preference mMenuBackFirstErrWait;
	private Preference mMenuBackLastErrWait;	
//--------
	private Preference mVfs;
	private CheckBoxPreference mDynamicWriteBackOn;
	private Preference mDynamicWriteBackActive;
	private Preference mDynamicWriteBackSuspend;

	private ListPreference mReadAhead;
	SharedPreferences mPreferences;
	
	private int mSeekbarProgress;
	private EditText settingText;
	private String sreadahead;
    private final String BLN_PATH=Helpers.bln_path();
    
    public static final String KSM_RUN_FILE = "/sys/kernel/mm/ksm/run";
    public static final String KSM_PREF = "pref_ksm";
    public static final String KSM_PREF_DISABLED = "0";
    public static final String KSM_PREF_ENABLED = "1";
    private static final String PURGEABLE_ASSETS_PREF = "pref_purgeable_assets";
    private static final String PURGEABLE_ASSETS_PERSIST_PROP = "persist.sys.purgeable_assets";
    private static final String PURGEABLE_ASSETS_DEFAULT = "0";
    private CheckBoxPreference mPurgeableAssetsPref;
    private CheckBoxPreference mKSMPref;
    private int swapAvailable = -1;
    
    private static final String APPS2SD_PREF = "apps2sd_opt";
	private CheckBoxPreference mApps2SDPref;
	private static final String DC_CACHE_PREF = "dccache_opt";
	private CheckBoxPreference mDCCachePref;
	private static final String DC_SDCARD_PREF = "dcsdcard_opt";
	private CheckBoxPreference mDCSDCardPref;
	private static final String COMPCACHE_PREF = "compcache_opt";
	private CheckBoxPreference mCompcachePref;
	private static final String LINUXSWAP_PREF = "linuxswap_opt";
	private CheckBoxPreference mLinuxSWAPPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(this);
        addPreferencesFromResource(R.layout.advanced);
        
	    sreadahead=getResources().getString(R.string.ps_read_ahead,"");
	    
	    mPurgeableAssetsPref = (CheckBoxPreference) findPreference(PURGEABLE_ASSETS_PREF);
        mKSMPref = (CheckBoxPreference) findPreference(KSM_PREF);
        
        mApps2SDPref = (CheckBoxPreference) findPreference(APPS2SD_PREF);
     	mDCCachePref = (CheckBoxPreference) findPreference(DC_CACHE_PREF);
     	mDCSDCardPref = (CheckBoxPreference) findPreference(DC_SDCARD_PREF);
     	mCompcachePref = (CheckBoxPreference) findPreference(COMPCACHE_PREF);
     	mLinuxSWAPPref = (CheckBoxPreference) findPreference(LINUXSWAP_PREF);
     	mLinuxSWAPPref.setEnabled(false);
        mLinuxSWAPPref.setSummaryOff("no linuxswap partition");

        mReadAhead = (ListPreference) findPreference(PREF_READ_AHEAD);
        mBltimeout= findPreference(PREF_BLTIMEOUT);
        mBltouch=(CheckBoxPreference) findPreference(PREF_BLTOUCH);
        mBln=(CheckBoxPreference) findPreference(PREF_BLN);
        mDsync=(CheckBoxPreference) findPreference(PREF_DSYNC);
        mHomeOn=(CheckBoxPreference) findPreference(PFK_HOME_ON);
        mHomeAllowedIrqs = findPreference(PREF_HOME_ALLOWED_IRQ);
        mHomeReportWait = findPreference(PREF_HOME_REPORT_WAIT);
        mMenuBackOn= (CheckBoxPreference) findPreference(PFK_MENUBACK_ON);
        mMenuBackIrqChecks= findPreference(PREF_MENUBACK_INTERRUPT_CHECKS);
        mMenuBackFirstErrWait= findPreference(PREF_MENUBACK_FIRST_ERR_WAIT);
        mMenuBackLastErrWait= findPreference(PREF_MENUBACK_LAST_ERR_WAIT);
        mDirtyRatio = findPreference(PREF_DIRTY_RATIO);
        mDirtyBackground = findPreference(PREF_DIRTY_BACKGROUND);
        mDirtyExpireCentisecs = findPreference(PREF_DIRTY_EXPIRE);
        mDirtyWriteback = findPreference(PREF_DIRTY_WRITEBACK);
        mMinFreeK =  findPreference(PREF_MIN_FREE_KB);
        mOvercommit = findPreference(PREF_OVERCOMMIT);
        mSwappiness =  findPreference(PREF_SWAPPINESS);
        mVfs = findPreference(PREF_VFS);
        mDynamicWriteBackOn = (CheckBoxPreference) findPreference(PREF_DYNAMIC_DIRTY_WRITEBACK);
        mDynamicWriteBackActive = findPreference(PREF_DIRTY_WRITEBACK_ACTIVE);
        mDynamicWriteBackSuspend = findPreference(PREF_DIRTY_WRITEBACK_SUSPEND);
        
        if (Utils.fileExists(KSM_RUN_FILE)) {
            mKSMPref.setChecked(KSM_PREF_ENABLED.equals(Utils.fileReadOneLine(KSM_RUN_FILE)));
        }

        String purgeableAssets = SystemProperties.get(PURGEABLE_ASSETS_PERSIST_PROP,
                PURGEABLE_ASSETS_DEFAULT);
        mPurgeableAssetsPref.setChecked("1".equals(purgeableAssets));
		

        if (!new File(DSYNC_PATH).exists()) {
            PreferenceCategory hideCat = (PreferenceCategory) findPreference("dsync");
            getPreferenceScreen().removePreference(hideCat);
            }
        else{
            mDsync.setChecked(Helpers.readOneLine(DSYNC_PATH).equals("1"));
        }
        if (!new File(PFK_HOME_ENABLED).exists() || !new File(PFK_MENUBACK_ENABLED).exists()) {
            PreferenceCategory hideCat = (PreferenceCategory) findPreference("pfk");
            getPreferenceScreen().removePreference(hideCat);
            }
        else{
            mHomeOn.setChecked(Helpers.readOneLine(PFK_HOME_ENABLED).equals("1"));
            mHomeOn.setSummary(getString(R.string.ps_home_enabled,Helpers.readOneLine(PFK_HOME_IGNORED_KP)));
            mHomeAllowedIrqs.setSummary(Helpers.readOneLine(PFK_HOME_ALLOWED_IRQ));
            mHomeReportWait.setSummary(Helpers.readOneLine(PFK_HOME_REPORT_WAIT) +" ms");

            mMenuBackOn.setChecked(Helpers.readOneLine(PFK_MENUBACK_ENABLED).equals("1"));
            mMenuBackOn.setSummary(getString(R.string.ps_menuback_enabled,Helpers.readOneLine(PFK_MENUBACK_IGNORED_KP)));
            mMenuBackIrqChecks.setSummary(Helpers.readOneLine(PFK_MENUBACK_INTERRUPT_CHECKS));
            mMenuBackFirstErrWait.setSummary(Helpers.readOneLine(PFK_MENUBACK_FIRST_ERR_WAIT)+" ms");
            mMenuBackLastErrWait.setSummary(Helpers.readOneLine(PFK_MENUBACK_LAST_ERR_WAIT)+" ms");
        }
        if (!new File(BL_TIMEOUT_PATH).exists()) {
            PreferenceCategory hideCat = (PreferenceCategory) findPreference("bltimeout");
            getPreferenceScreen().removePreference(hideCat);
            }
        else{
            mBltimeout.setSummary(Helpers.readOneLine(BL_TIMEOUT_PATH)+" ms");
        }
        if (!new File(BL_TOUCH_ON_PATH).exists()) {
            PreferenceCategory hideCat = (PreferenceCategory) findPreference("bltouch");
            getPreferenceScreen().removePreference(hideCat);
            }
        else{
            mBltouch.setChecked(Helpers.readOneLine(BL_TOUCH_ON_PATH).equals("1"));
        }
        if (BLN_PATH==null) {
            PreferenceCategory hideCat = (PreferenceCategory) findPreference("bln");
            getPreferenceScreen().removePreference(hideCat);
        }
        else{
            mBln.setChecked(Helpers.readOneLine(BLN_PATH).equals("1"));
        }
        if (!new File(DYNAMIC_DIRTY_WRITEBACK_PATH).exists()) {
            mDirtyWriteback.setEnabled(true);
            PreferenceCategory hideCat = (PreferenceCategory) findPreference("cat_dynamic_write_back");
            getPreferenceScreen().removePreference(hideCat);
            }
        else{
            boolean ison=Helpers.readOneLine(DYNAMIC_DIRTY_WRITEBACK_PATH).equals("1");
            mDynamicWriteBackOn.setChecked(ison);
            mDirtyWriteback.setEnabled(!ison);
            mDynamicWriteBackActive.setSummary(Helpers.readOneLine(DIRTY_WRITEBACK_ACTIVE_PATH));
            mDynamicWriteBackSuspend.setSummary(Helpers.readOneLine(DIRTY_WRITEBACK_SUSPEND_PATH));
        }
		
	    mReadAhead.setValue(Helpers.readOneLine(READ_AHEAD_PATH[0]));
        mReadAhead.setSummary(getString(R.string.ps_read_ahead, Helpers.readOneLine(READ_AHEAD_PATH[0]) + "  kb"));
        mDirtyRatio.setSummary(Helpers.readOneLine(DIRTY_RATIO_PATH));
        mDirtyBackground.setSummary(Helpers.readOneLine(DIRTY_BACKGROUND_PATH));
        mDirtyExpireCentisecs.setSummary(Helpers.readOneLine(DIRTY_EXPIRE_PATH));
        mDirtyWriteback.setSummary(Helpers.readOneLine(DIRTY_WRITEBACK_PATH));
        mMinFreeK.setSummary(Helpers.readOneLine(MIN_FREE_PATH));
        mOvercommit.setSummary(Helpers.readOneLine(OVERCOMMIT_PATH));
        mSwappiness.setSummary(Helpers.readOneLine(SWAPPINESS_PATH));
        mVfs.setSummary(Helpers.readOneLine(VFS_CACHE_PRESSURE_PATH));
            
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

	if (preference == mDsync){
		if (Integer.parseInt(Helpers.readOneLine(DSYNC_PATH))==0){
			new CMDProcessor().su.runWaitFor("busybox echo 1 > " + DSYNC_PATH);
		}
		else{
			new CMDProcessor().su.runWaitFor("busybox echo 0 > " + DSYNC_PATH);
		}
            return true;
	}
	else if (preference == mBltimeout){
            String title = getString(R.string.bltimeout_title)+" (ms)";
            int currentProgress = Integer.parseInt(Helpers.readOneLine(BL_TIMEOUT_PATH));
            openDialog(currentProgress, title, 0,5000, preference,BL_TIMEOUT_PATH, PREF_BLTIMEOUT);
            return true;
	}
	else if (preference == mBltouch){
		if (Integer.parseInt(Helpers.readOneLine(BL_TOUCH_ON_PATH))==0){
			new CMDProcessor().su.runWaitFor("busybox echo 1 > " + BL_TOUCH_ON_PATH);
		}
		else{
			new CMDProcessor().su.runWaitFor("busybox echo 0 > " + BL_TOUCH_ON_PATH);
		}
            return true;
	}
    else if (preference == mBln){
        if (Integer.parseInt(Helpers.readOneLine(BLN_PATH))==0){
            new CMDProcessor().su.runWaitFor("busybox echo 1 > " + BLN_PATH);
        }
        else{
            new CMDProcessor().su.runWaitFor("busybox echo 0 > " + BLN_PATH);
        }
        return true;
    }
    else if (preference == mHomeOn){
		if (Integer.parseInt(Helpers.readOneLine(PFK_HOME_ENABLED))==0){
			new CMDProcessor().su.runWaitFor("busybox echo 1 > " + PFK_HOME_ENABLED);
		}
		else{
			new CMDProcessor().su.runWaitFor("busybox echo 0 > " + PFK_HOME_ENABLED);
		}
            return true;
	}
	else if (preference == mMenuBackOn){
		if (Integer.parseInt(Helpers.readOneLine(PFK_MENUBACK_ENABLED))==0){
			new CMDProcessor().su.runWaitFor("busybox echo 1 > " + PFK_MENUBACK_ENABLED);
		}
		else{
			new CMDProcessor().su.runWaitFor("busybox echo 0 > " + PFK_MENUBACK_ENABLED);
		}
            return true;
	}
	else if (preference == mHomeAllowedIrqs) {
            String title = getString(R.string.home_allowed_irq_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(PFK_HOME_ALLOWED_IRQ));
            openDialog(currentProgress, title, 1,32, preference, PFK_HOME_ALLOWED_IRQ, PREF_HOME_ALLOWED_IRQ);
            return true;		
	}
	else if (preference == mHomeReportWait) {
            String title = getString(R.string.home_report_wait_title)+" (ms)";
            int currentProgress = Integer.parseInt(Helpers.readOneLine(PFK_HOME_REPORT_WAIT));
            openDialog(currentProgress, title, 5,25, preference, PFK_HOME_REPORT_WAIT, PREF_HOME_REPORT_WAIT);
            return true;
	}
	else if (preference == mMenuBackIrqChecks) {
            String title = getString(R.string.menuback_interrupt_checks_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(PFK_MENUBACK_INTERRUPT_CHECKS));
            openDialog(currentProgress, title, 1,10, preference, PFK_MENUBACK_INTERRUPT_CHECKS, PREF_MENUBACK_INTERRUPT_CHECKS);
            return true;
	}
	else if (preference == mMenuBackFirstErrWait) {
            String title = getString(R.string.menuback_first_err_wait_title)+" (ms)";
            int currentProgress = Integer.parseInt(Helpers.readOneLine(PFK_MENUBACK_FIRST_ERR_WAIT));
            openDialog(currentProgress, title, 50,1000, preference, PFK_MENUBACK_FIRST_ERR_WAIT, PREF_MENUBACK_FIRST_ERR_WAIT);
            return true;
	}
	else if (preference == mMenuBackLastErrWait) {
            String title = getString(R.string.menuback_last_err_wait_title)+" (ms)";
            int currentProgress = Integer.parseInt(Helpers.readOneLine(PFK_MENUBACK_LAST_ERR_WAIT));
            openDialog(currentProgress, title, 50,100, preference,PFK_MENUBACK_LAST_ERR_WAIT, PREF_MENUBACK_LAST_ERR_WAIT);
            return true;
	}		
	else if (preference == mDirtyRatio) {
            String title = getString(R.string.dirty_ratio_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(DIRTY_RATIO_PATH));
            openDialog(currentProgress, title, 0,100, preference,DIRTY_RATIO_PATH, PREF_DIRTY_RATIO);
            return true;
        }
	else if (preference == mDirtyBackground) {
            String title = getString(R.string.dirty_background_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(DIRTY_BACKGROUND_PATH));
            openDialog(currentProgress, title, 0,100, preference,DIRTY_BACKGROUND_PATH, PREF_DIRTY_BACKGROUND);
            return true;
        }
	else if (preference == mDirtyExpireCentisecs) {
            String title = getString(R.string.dirty_expire_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(DIRTY_EXPIRE_PATH));
            openDialog(currentProgress, title, 0,5000, preference,DIRTY_EXPIRE_PATH, PREF_DIRTY_EXPIRE);
            return true;
        }
	else if (preference == mDirtyWriteback) {
            String title = getString(R.string.dirty_writeback_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(DIRTY_WRITEBACK_PATH));
            openDialog(currentProgress, title, 0,5000, preference,DIRTY_WRITEBACK_PATH, PREF_DIRTY_WRITEBACK);
            return true;
        }
	else if (preference == mMinFreeK) {
            String title = getString(R.string.min_free_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(MIN_FREE_PATH));
            openDialog(currentProgress, title, 0,8192, preference, MIN_FREE_PATH,PREF_MIN_FREE_KB);
            return true;
        }
	else if (preference == mOvercommit) {
            String title = getString(R.string.overcommit_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(OVERCOMMIT_PATH));
            openDialog(currentProgress, title, 0,100, preference,OVERCOMMIT_PATH, PREF_OVERCOMMIT);
            return true;
        }
	else if (preference == mSwappiness) {
            String title = getString(R.string.swappiness_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(SWAPPINESS_PATH));
            openDialog(currentProgress, title, 0,100, preference,SWAPPINESS_PATH, PREF_SWAPPINESS);
            return true;
        }
	else if (preference == mVfs) {
            String title = getString(R.string.vfs_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(VFS_CACHE_PRESSURE_PATH));
            openDialog(currentProgress, title, 0,200, preference,VFS_CACHE_PRESSURE_PATH, PREF_VFS);
            return true;
        }
	else if (preference == mDynamicWriteBackOn){
		if (Integer.parseInt(Helpers.readOneLine(DYNAMIC_DIRTY_WRITEBACK_PATH))==0){
			new CMDProcessor().su.runWaitFor("busybox echo 1 > " + DYNAMIC_DIRTY_WRITEBACK_PATH);
			mDirtyWriteback.setEnabled(false);
		}
		else{
			new CMDProcessor().su.runWaitFor("busybox echo 0 > " + DYNAMIC_DIRTY_WRITEBACK_PATH);
			mDirtyWriteback.setEnabled(true);
		}
            return true;
	}        
	else if (preference == mDynamicWriteBackActive) {
            String title = getString(R.string.dynamic_writeback_active_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(DIRTY_WRITEBACK_ACTIVE_PATH));
            openDialog(currentProgress, title, 0,5000, preference,DIRTY_WRITEBACK_ACTIVE_PATH, PREF_DIRTY_WRITEBACK_ACTIVE);
            return true;
        }
	else if (preference == mDynamicWriteBackSuspend) {
            String title = getString(R.string.dynamic_writeback_suspend_title);
            int currentProgress = Integer.parseInt(Helpers.readOneLine(DIRTY_WRITEBACK_SUSPEND_PATH));
            openDialog(currentProgress, title, 0,5000, preference,DIRTY_WRITEBACK_SUSPEND_PATH, PREF_DIRTY_WRITEBACK_SUSPEND);
            return true;
        }        
	
	else if (preference == mPurgeableAssetsPref) {
        SystemProperties.set(PURGEABLE_ASSETS_PERSIST_PROP,
                mPurgeableAssetsPref.isChecked() ? "1" : "0");
        return true;
    }

	else if (preference == mKSMPref) {
        Utils.fileWriteOneLine(KSM_RUN_FILE, mKSMPref.isChecked() ? "1" : "0");
        return true;
    }
        
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, String key) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if (key.equals(PREF_READ_AHEAD)) {
			String evalues = Helpers.readOneLine(READ_AHEAD_PATH[0]);
			String values = sharedPreferences.getString(key,evalues);
			if (!values.equals(evalues)){
				final StringBuilder sb = new StringBuilder();
				for(int i=0; i<READ_AHEAD_PATH.length; i++){
					sb.append("busybox echo "+values+" > " + READ_AHEAD_PATH[i] + "\n");
				}
				Helpers.shExec(sb);
			}
			mReadAhead.setSummary(sreadahead+values + " kb");
		}	

		else if (key.equals(PREF_BLTIMEOUT)) {
			mBltimeout.setSummary(Helpers.readOneLine(BL_TIMEOUT_PATH)+" ms");
		}
		else if (key.equals(PREF_HOME_REPORT_WAIT)){
			mHomeReportWait.setSummary(Helpers.readOneLine(PFK_HOME_REPORT_WAIT) +" ms");
		}
		else if (key.equals(PREF_MENUBACK_FIRST_ERR_WAIT)){
			mMenuBackFirstErrWait.setSummary(Helpers.readOneLine(PFK_MENUBACK_FIRST_ERR_WAIT)+" ms");
		}
		else if (key.equals(PREF_MENUBACK_LAST_ERR_WAIT)){
			mMenuBackLastErrWait.setSummary(Helpers.readOneLine(PFK_MENUBACK_LAST_ERR_WAIT)+" ms");
		}
    	else if (key.equals(BLX_SOB)) {
    			if(sharedPreferences.getBoolean(key,false)){
				editor.putInt(PREF_BLX, Integer.parseInt(Helpers.readOneLine(BLX_PATH))).apply();
    			}
    			else{
    				editor.remove(PREF_BLX).apply();
    			}
		}
    	else if (key.equals(BLTIMEOUT_SOB)) {
    			if(sharedPreferences.getBoolean(key,false)){
				editor.putInt(PREF_BLTIMEOUT, Integer.parseInt(Helpers.readOneLine(BL_TIMEOUT_PATH))).apply();
    			}
    			else{
    				editor.remove(PREF_BLTIMEOUT).apply();
    			}
		}
    	else if (key.equals(PFK_SOB)) {
    			if(sharedPreferences.getBoolean(key,false)){
				if(Helpers.readOneLine(PFK_HOME_ENABLED).equals("1")){
					editor.putBoolean(PFK_HOME_ON, true);
				}
				else{
					editor.putBoolean(PFK_HOME_ON, false);
				}
				editor.putInt(PREF_HOME_ALLOWED_IRQ, Integer.parseInt(Helpers.readOneLine(PFK_HOME_ALLOWED_IRQ)))
				.putInt(PREF_HOME_REPORT_WAIT, Integer.parseInt(Helpers.readOneLine(PFK_HOME_REPORT_WAIT)));
				if(Helpers.readOneLine(PFK_MENUBACK_ENABLED).equals("1")){
					editor.putBoolean(PFK_MENUBACK_ON,true);
				}
				else{
					editor.putBoolean(PFK_MENUBACK_ON,false);
				}
				editor.putInt(PREF_MENUBACK_INTERRUPT_CHECKS, Integer.parseInt(Helpers.readOneLine(PFK_MENUBACK_INTERRUPT_CHECKS)))
				.putInt(PREF_MENUBACK_FIRST_ERR_WAIT, Integer.parseInt(Helpers.readOneLine(PFK_MENUBACK_FIRST_ERR_WAIT)))
				.putInt(PREF_MENUBACK_LAST_ERR_WAIT, Integer.parseInt(Helpers.readOneLine(PFK_MENUBACK_LAST_ERR_WAIT)))
				.apply();
    			}
    			else{
				editor.remove(PFK_HOME_ON)
				.remove(PREF_HOME_ALLOWED_IRQ)
				.remove(PREF_HOME_REPORT_WAIT)
				.remove(PFK_MENUBACK_ON)
				.remove(PREF_MENUBACK_INTERRUPT_CHECKS)
				.remove(PREF_MENUBACK_FIRST_ERR_WAIT)
				.remove(PREF_MENUBACK_LAST_ERR_WAIT)
				.apply();
    			}
		}
    	else if (key.equals(DYNAMIC_DIRTY_WRITEBACK_SOB)) {
    			if(sharedPreferences.getBoolean(key,false)){
				if(Helpers.readOneLine(DYNAMIC_DIRTY_WRITEBACK_PATH).equals("1")){
					editor.putBoolean(PREF_DYNAMIC_DIRTY_WRITEBACK,true);
				}
				else{
					editor.putBoolean(PREF_DYNAMIC_DIRTY_WRITEBACK,false);
				}    				
				editor.putInt(PREF_DIRTY_WRITEBACK_ACTIVE, Integer.parseInt(Helpers.readOneLine(DIRTY_WRITEBACK_ACTIVE_PATH)))
				.putInt(PREF_DIRTY_WRITEBACK_SUSPEND, Integer.parseInt(Helpers.readOneLine(DIRTY_WRITEBACK_SUSPEND_PATH)))
				.apply();
    			}
    			else{
				editor.remove(PREF_DYNAMIC_DIRTY_WRITEBACK)
				.remove(PREF_DIRTY_WRITEBACK_ACTIVE)
				.remove(PREF_DIRTY_WRITEBACK_SUSPEND)
				.apply();
    			}
		}
    	else if (key.equals(VM_SOB)) {
    			if(sharedPreferences.getBoolean(key,false)){
				editor.putInt(PREF_DIRTY_RATIO, Integer.parseInt(Helpers.readOneLine(DIRTY_RATIO_PATH)))
				.putInt(PREF_DIRTY_BACKGROUND, Integer.parseInt(Helpers.readOneLine(DIRTY_BACKGROUND_PATH)))
				.putInt(PREF_DIRTY_EXPIRE, Integer.parseInt(Helpers.readOneLine(DIRTY_EXPIRE_PATH)))
				.putInt(PREF_DIRTY_WRITEBACK, Integer.parseInt(Helpers.readOneLine(DIRTY_WRITEBACK_PATH)))
				.putInt(PREF_MIN_FREE_KB, Integer.parseInt(Helpers.readOneLine(MIN_FREE_PATH)))
				.putInt(PREF_OVERCOMMIT, Integer.parseInt(Helpers.readOneLine(OVERCOMMIT_PATH)))
				.putInt(PREF_SWAPPINESS, Integer.parseInt(Helpers.readOneLine(SWAPPINESS_PATH)))
				.putInt(PREF_VFS, Integer.parseInt(Helpers.readOneLine(VFS_CACHE_PRESSURE_PATH)))				
				.apply();
    			}
    			else{
				editor.remove(PREF_DIRTY_RATIO)
				.remove(PREF_DIRTY_BACKGROUND)
				.remove(PREF_DIRTY_EXPIRE)
				.remove(PREF_DIRTY_WRITEBACK)
				.remove(PREF_MIN_FREE_KB)
				.remove(PREF_OVERCOMMIT)
				.remove(PREF_SWAPPINESS)
				.remove(PREF_VFS)
				.apply();
    			}
		}		
    }

    public void openDialog(int currentProgress, String title, final int min, final int max,final Preference pref, final String path, final String key) {
        Resources res = getActivity().getResources();
        String cancel = ("Cancle");
        String ok = ("Ok");
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View alphaDialog = factory.inflate(R.layout.seekbar_dialog, null);

        final SeekBar seekbar = (SeekBar) alphaDialog.findViewById(R.id.seek_bar);

        seekbar.setMax(max);
        seekbar.setProgress(currentProgress);
        
        settingText = (EditText) alphaDialog.findViewById(R.id.setting_text);
        settingText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				int val = Integer.parseInt(settingText.getText().toString());
				seekbar.setProgress(val);
				return true;
			}
			return false;
		}
		});
        settingText.setText(Integer.toString(currentProgress));
        settingText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int val = Integer.parseInt(s.toString());
                    if (val > max) {
                        s.replace(0, s.length(), Integer.toString(max));
                        val=max;
                    }
                    seekbar.setProgress(val);
                } catch (NumberFormatException ex) {
                }
            }
        });

        OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				mSeekbarProgress = seekbar.getProgress();
				if(fromUser){
					settingText.setText(Integer.toString(mSeekbarProgress));
				}
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {
            }
        };
        seekbar.setOnSeekBarChangeListener(seekBarChangeListener);

        new AlertDialog.Builder(getActivity())
			.setTitle(title)
			.setView(alphaDialog)
			.setNegativeButton(cancel,
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int which) {
				// nothing
				}
			})
			.setPositiveButton(ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int val = Integer.parseInt(settingText.getText().toString());
					if(val<min){val=min;}
					seekbar.setProgress(val);
					int newProgress = seekbar.getProgress();
					pref.setSummary(Integer.toString(newProgress));
					new CMDProcessor().su.runWaitFor("busybox echo " + newProgress + " > " + path);
					final SharedPreferences.Editor editor = mPreferences.edit();
					editor.putInt(key, newProgress);
					editor.commit();
				}
			}).create().show();
    }
}

