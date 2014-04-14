package com.android.zramconfig;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.NxIndustries.Zram.R;

public class zRAMstatus extends Activity {
	private Zram zram;
	private Timer recalculateTimer;
	private final int recalculateTimerInterval = 5 * 1000;
	private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        zram = new Zram();
        recalculateTimerSchedule();
        
        mBtn = (Button) findViewById(R.id.action_reload);
        
        mBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				recalculate();
			}
		});
    }
    
    private void recalculateTimerSchedule() {
    	recalculateTimer = new Timer();
    	recalculateTimer.schedule(
    			new TimerTask() {
					
					@Override
					public void run() {
						recalculateTimerMethod();
					}
				}, 
    			0, recalculateTimerInterval);
    }
    
    private void recalculateTimerMethod() {
    	runOnUiThread(RecalculateTimerTick);
    }
    
    private Runnable RecalculateTimerTick = new Runnable() {
		
		@Override
		public void run() {
			recalculate();
		}
	};
    
    private void recalculate() {
    	zram.clearCache();
    	
    	try {
    		NumberFormat nf = NumberFormat.getNumberInstance();
    		
    		TextView tvZramDiskSize = (TextView) findViewById(R.id.zram_disk_size);
    		tvZramDiskSize.setText(
        			("zRAM Disk Size") 
        			+ " "
        			+ nf.format(zram.getDiskSize())
        			+ " bytes"
    			);
    		
    		TextView tvZramCompressedDataSize = (TextView) findViewById(R.id.zram_compressed_data_size);
    		tvZramCompressedDataSize.setText(
        			("zRAM Compressed Data") 
        			+ " "
        			+ nf.format(zram.getCompressedDataSize())
        			+ " bytes"
    			);
    		
    		TextView tvZramOriginalDataSize = (TextView) findViewById(R.id.zram_original_data_size);
    		tvZramOriginalDataSize.setText(
        			("Original zRAM Data") 
        			+ " "
        			+ nf.format(zram.getOriginalDataSize())
        			+ " bytes"
    			);
    		
    		TextView tvZramMemUsedTotal = (TextView) findViewById(R.id.zram_mem_used_total);
    		tvZramMemUsedTotal.setText(
        			("zRAM Memory Used") 
        			+ " "
        			+ nf.format(zram.getMemUsedTotal())
        			+ " bytes"
    			);
    		
    		final int compressionRatio = Math.round(zram.getCompressionRatio() * 100);
    		ProgressBar pbZramCompressionRatio = (ProgressBar) findViewById(R.id.zram_compression_ratio_bar);
    		pbZramCompressionRatio.setProgress(compressionRatio);
    		
    		TextView tvZramCompressionRatio = (TextView) findViewById(R.id.zram_compression_ratio);
    		tvZramCompressionRatio.setText(
        			("zRAM Compression Ratio") 
        			+ " "
        			+ Integer.toString(compressionRatio)
        			+ "%"
    			);
    		
    		final int usedRatio = Math.round(zram.getUsedRatio() * 100);
    		ProgressBar pbZramUsedRatio = (ProgressBar) findViewById(R.id.zram_used_ratio_bar);
    		pbZramUsedRatio.setProgress(usedRatio);
    		
    		TextView tvZramUsedRatio = (TextView) findViewById(R.id.zram_used_ratio);
    		tvZramUsedRatio.setText(
        			("zRAM Used Ratio") 
        			+ " "
        			+ Integer.toString(usedRatio)
        			+ "%"
    			);
    		
    		MemoryInfo mi = new MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            
            //long memoryTotal = mi.totalMem;
            long memoryAvail = mi.availMem;
            //long memoryUsed = memoryTotal - memoryAvail;

            TextView tvRamAvailable = (TextView) findViewById(R.id.ram_available);
            tvRamAvailable.setText(
            		("RAM Available")
            		+ " " + nf.format(memoryAvail)
            		+ " bytes"
            	);
            
    	} catch (Exception e) {
    		Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    		finish();
    	}
    }
}
