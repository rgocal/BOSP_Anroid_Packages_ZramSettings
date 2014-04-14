package com.android.zram.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public class Helpers implements Constants {

    private static final String NOT_FOUND = null;
	private static String mVoltagePath;

    /**
     * Checks device for SuperUser permission
     *
     * @return If SU was granted or denied
     */
    public static boolean checkSu() {
        if (!new File("/system/bin/su").exists() && !new File("/system/xbin/su").exists()) {
            Log.e(TAG, "su does not exist!!!");
            return false; // tell caller to bail...
        }
        try {
            if ((new CMDProcessor().su.runWaitFor("ls /data/app-private")).success()) {
                Log.i(TAG, " SU exists and we have permission");
                return true;
            } else {
                Log.i(TAG, " SU exists but we dont have permission");
                return false;
            }
        }
        catch (final NullPointerException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    /**
     * Checks to see if Busybox is installed in "/system/"
     *
     * @return If busybox exists
     */
    public static boolean checkBusybox() {
        if (!new File("/system/bin/busybox").exists() && !new File("/system/xbin/busybox").exists()) {
            Log.e(TAG, "Busybox not in xbin or bin!");
            return false;
        }
        try {
            if (!new CMDProcessor().su.runWaitFor("busybox mount").success()) {
                Log.e(TAG, " Busybox is there but it is borked! ");
                return false;
            }
        }
        catch (final NullPointerException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Return mount points
     *
     * @param path
     * @return line if present
     */
    public static String[] getMounts(final String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/mounts"), 256);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.contains(path)) {
                    return line.split(" ");
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "/proc/mounts does not exist");
        } catch (IOException e) {
            Log.d(TAG, "Error reading /proc/mounts");
        }
        return null;
    }
    
    public static long getMem(String tip) {
        long v=0;
        CMDProcessor.CommandResult cr = new CMDProcessor().sh.runWaitFor("busybox echo `busybox grep "+tip+" /proc/meminfo | busybox grep -E -o '[[:digit:]]+'`");
        if(cr.success() && cr.stdout!=null && cr.stdout.length()>0){
            try{
               v = (long) Integer.parseInt(cr.stdout);//kb
            }
            catch (NumberFormatException e) {
                Log.d(TAG, tip+" conversion err: "+e);
            }
        }
        return v;
    }
    
    public static String shExec(StringBuilder s,Context c,Boolean su){
        //final String dn=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+TAG+"/logs";
        //new CMDProcessor().sh.runWaitFor("busybox mkdir -p "+dn );
        get_assetsScript("run", c, "", s.toString());
        new CMDProcessor().sh.runWaitFor("busybox chmod 750 "+ c.getFilesDir()+"/run" );
        CMDProcessor.CommandResult cr=null;
        if(su){
            //cr=new CMDProcessor().su.runWaitFor(c.getFilesDir()+"/run > " + dn + "/run.log 2>&1");
            cr=new CMDProcessor().su.runWaitFor(c.getFilesDir()+"/run");
        }
        else{
            //cr=new CMDProcessor().sh.runWaitFor(c.getFilesDir()+"/run > " + dn + "/run.log 2>&1");
            cr=new CMDProcessor().sh.runWaitFor(c.getFilesDir()+"/run");
        }
        if(cr.success()){
            return cr.stdout;
        }
        else{
            Log.d(TAG, "execute run error: "+cr.stderr);
            return "nok";
        }
    }

    /**
     * Get mounts
     *
     * @param mount
     * @return success or failure
     */
    public static boolean getMount(final String mount) {
        final CMDProcessor cmd = new CMDProcessor();
        final String mounts[] = getMounts("/system");
        if (mounts != null && mounts.length >= 3) {
            final String device = mounts[0];
            final String path = mounts[1];
            final String point = mounts[2];
            if (cmd.su.runWaitFor("mount -o " + mount + ",remount -t " + point + " " + device+ " " + path).success()) {
                return true;
            }
        }
        return (cmd.su.runWaitFor("busybox mount -o remount," + mount + " /system").success());
    }

    /**
     * Read one line from file
     *
     * @param fname
     * @return line
     */
    public static String readOneLine(String fname) {
        String line = null;
        if (new File(fname).exists()) {
        	BufferedReader br;
	        try {
	            br = new BufferedReader(new FileReader(fname), 512);
	            try {
	                line = br.readLine();
	            } finally {
	                br.close();
	            }
	        } catch (Exception e) {
	            Log.e(TAG, "IO Exception when reading sys file", e);
	            // attempt to do magic!
	            return readFileViaShell(fname, true);
	        }
        }
        return line;
    }

    /**
     * Read file via shell
     *
     * @param filePath
     * @param useSu
     * @return file output
     */
    public static String readFileViaShell(String filePath, boolean useSu) {
        CMDProcessor.CommandResult cr = null;
        if (useSu) {
            cr = new CMDProcessor().su.runWaitFor("cat " + filePath);
        } else {
            cr = new CMDProcessor().sh.runWaitFor("cat " + filePath);
        }
        if (cr.success())
            return cr.stdout;
        return null;
    }

    /**
     * Write one line to a file
     *
     * @param fname
     * @param value
     * @return if line was written
     */
    public static boolean writeOneLine(String fname, String value) {
    	if (!new File(fname).exists()) {return false;}
        try {
            FileWriter fw = new FileWriter(fname);
            try {
                fw.write(value);
            } finally {
                fw.close();
            }
        } catch (IOException e) {
            String Error = "Error writing to " + fname + ". Exception: ";
            Log.e(TAG, Error, e);
            return false;
        }
        return true;
    }

    /**
     * Reads string array from file
     *
     * @param fname
     * @return string array
     */
    private static String[] readStringArray(String fname) {
        String line = readOneLine(fname);
        if (line != null) {
            return line.split(" ");
        }
        return null;
    }

    /**
     * Get total number of cpus
     * @return total number of cpus
     */
    public static int getNumOfCpus() {
        int numOfCpu = 1;
        String numOfCpus = Helpers.readOneLine(NUM_OF_CPUS_PATH);
        String[] cpuCount = numOfCpus.split("-");
        if (cpuCount.length > 1) {
            try {
                int cpuStart = Integer.parseInt(cpuCount[0]);
                int cpuEnd = Integer.parseInt(cpuCount[1]);

                numOfCpu = cpuEnd - cpuStart + 1;

                if (numOfCpu < 0)
                    numOfCpu = 1;
            } catch (NumberFormatException ex) {
                numOfCpu = 1;
            }
        }
        return numOfCpu;
    }

    /**
     * Sets the voltage file to be used by the rest of the app elsewhere.
     * @param voltageFile
     */
    public static void setVoltagePath(String voltageFile) {
        Log.d(TAG, "UV table path detected: "+voltageFile);
        mVoltagePath = voltageFile;
    }

    /**
     * Gets the currently set voltage path
     * @return voltage path
     */
    public static String getVoltagePath() {
        return mVoltagePath;
    }

    /**
     * Convert to MHz and append a tag
     * @param mhzString
     * @return tagged and converted String
     */
    public static String toMHz(String mhzString) {
        return new StringBuilder().append(Integer.parseInt(mhzString) / 1000).append(" MHz").toString();
    }

    /**
     * Restart the activity smoothly
     * @param activity
     */
    public static void restartPC(final Activity activity) {
        if (activity == null)
            return;
        final int enter_anim = android.R.anim.fade_in;
        final int exit_anim = android.R.anim.fade_out;
        activity.overridePendingTransition(enter_anim, exit_anim);
        activity.finish();
        activity.overridePendingTransition(enter_anim, exit_anim);
        activity.startActivity(activity.getIntent());
    }

    /**
     * Helper to create a bitmap to set as imageview or bg
     * @param bgcolor
     * @return bitmap
     */
    public static Bitmap getBackground(int bgcolor) {
        try {
            Bitmap.Config config = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = Bitmap.createBitmap(2, 2, config);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(bgcolor);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
    public static String binExist(String b) {
        CMDProcessor.CommandResult cr = null;
        cr = new CMDProcessor().sh.runWaitFor("busybox which " + b);
        if (cr.success()){ return  cr.stdout; }
        else{ return NOT_FOUND;}
    }

    public static String getCachePartition() {
        CMDProcessor.CommandResult cr = null;
        cr = new CMDProcessor().sh.runWaitFor("busybox echo `busybox mount | busybox grep cache | busybox cut -d' ' -f1`");
        if(cr.success()&& !cr.stdout.equals("") ){return cr.stdout;}
        else{return NOT_FOUND;}
    }

    public static boolean showBattery() {
	    return ((new File(BLX_PATH).exists()) || (fastcharge_path()!=null));
    }

	public static void shCreate(){
		if (! new File(SH_PATH).exists()) {
			new CMDProcessor().su.runWaitFor("busybox touch "+SH_PATH );	
			new CMDProcessor().su.runWaitFor("busybox chmod 755 "+SH_PATH );
			Log.d(TAG, "create: "+SH_PATH);
		}
	}
	public static String shExec(StringBuilder s){
		if (new File(SH_PATH).exists()) {
			s.insert(0,"#!"+binExist("sh")+"\n\n");
			new CMDProcessor().su.runWaitFor("busybox echo \""+s.toString()+"\" > " + SH_PATH );
            CMDProcessor.CommandResult cr = null;
			cr=new CMDProcessor().su.runWaitFor(SH_PATH);
			//Log.d(TAG, "execute: "+s.toString());
            if(cr.success()){return cr.stdout;}
            else{Log.d(TAG, "execute: "+cr.stderr);return "";}
		}
		else{
			Log.d(TAG, "missing file: "+SH_PATH);
            return "";
		}
	}

    public static void get_assetsScript(String fn,Context c,String prefix,String postfix){
        byte[] buffer;
        final AssetManager assetManager = c.getAssets();
        try {
            InputStream f =assetManager.open(fn);
            buffer = new byte[f.available()];
            f.read(buffer);
            f.close();
            final String s = new String(buffer);
            final StringBuffer sb = new StringBuffer(s);
            if(!postfix.equals("")){ sb.append("\n\n"+postfix); }
            if(!prefix.equals("")){ sb.insert(0,prefix+"\n"); }
            sb.insert(0,"#!"+Helpers.binExist("sh")+"\n\n");
            try {
                FileOutputStream fos;
                fos = c.openFileOutput(fn, Context.MODE_PRIVATE);
                fos.write(sb.toString().getBytes());
                fos.close();

            } catch (IOException e) {
                Log.d(TAG, "error write "+fn+" file");
                e.printStackTrace();
            }

        }
        catch (IOException e) {
            Log.d(TAG, "error read "+fn+" file");
            e.printStackTrace();
        }
    }
    public static String ReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = String.valueOf("KMGTPE".charAt(exp-1));
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    public static void removeCurItem(MenuItem item,int idx,ViewPager vp){
        for(int i=0;i< vp.getAdapter().getCount();i++){
            if(item.getItemId() == idx+i+1) {
                vp.setCurrentItem(i);
            }
        }
    }
    public static void addItems2Menu(Menu menu,int idx,String nume,ViewPager vp){
        final SubMenu smenu = menu.addSubMenu(0, idx, 0,nume);
        for(int i=0;i< vp.getAdapter().getCount();i++){
            if(i!=vp.getCurrentItem())
                smenu.add(0,idx +i+1, 0, vp.getAdapter().getPageTitle(i));
        }
    }
    public static String bln_path() {
        if (new File("/sys/class/misc/backlightnotification/enabled").exists()) {
            return "/sys/class/misc/backlightnotification/enabled";
        }
        else if (new File("/sys/class/leds/button-backlight/blink_buttons").exists()) {
            return "/sys/class/leds/button-backlight/blink_buttons";
        }
        else{
            return null;
        }
    }
    public static String fastcharge_path() {
        if (new File("/sys/kernel/fast_charge/force_fast_charge").exists()) {
            return "/sys/kernel/fast_charge/force_fast_charge";
        }
        else if (new File("/sys/module/msm_otg/parameters/fast_charge").exists()) {
            return "/sys/module/msm_otg/parameters/fast_charge";
        }
        else if (new File("/sys/devices/platform/htc_battery/fast_charge").exists()) {
            return "/sys/devices/platform/htc_battery/fast_charge";
        }
        else{
            return null;
        }
    }
    public static String fsync_path() {
        if (new File("/sys/class/misc/fsynccontrol/fsync_enabled").exists()) {
            return "/sys/class/misc/fsynccontrol/fsync_enabled";
        }
        else if (new File("/sys/module/sync/parameters/fsync_enabled").exists()) {
            return "/sys/module/sync/parameters/fsync_enabled";
        }
        else{
            return null;
        }
    }
}
