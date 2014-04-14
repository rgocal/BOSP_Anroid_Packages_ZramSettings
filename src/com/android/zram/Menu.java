package com.android.zram;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.NxIndustries.Zram.R;

public class Menu extends PreferenceFragment {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.menu);
        }
}