package com.android.zramconfig;

import java.util.List;

import android.preference.PreferenceActivity;

import com.NxIndustries.Zram.R;

public class zRAM extends PreferenceActivity {
	
    @Override
public void onBuildHeaders(List<Header> headers) {
    loadHeadersFromResource(R.xml.settings, headers);
    }
}