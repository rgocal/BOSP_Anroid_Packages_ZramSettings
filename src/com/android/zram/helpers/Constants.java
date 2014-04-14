package com.android.zram.helpers;

public interface Constants {

    public static final String TAG = "Zram Settings";
    
    public static final String NUM_OF_CPUS_PATH = "/sys/devices/system/cpu/present";

    // Other settings
    public static final String MINFREE_PATH = "/sys/module/lowmemorykiller/parameters/minfree";
    public static final String MINFREE_ADJ_PATH = "/sys/module/lowmemorykiller/parameters/adj";
    public static final String[] READ_AHEAD_PATH ={ "/sys/devices/virtual/bdi/179:0/read_ahead_kb","/sys/devices/virtual/bdi/179:32/read_ahead_kb"};
    //"/sys/devices/virtual/bdi/default/read_ahead_kb"
    
    //public static final String FASTCHARGE_PATH = "/sys/kernel/fast_charge/force_fast_charge";
    public static final String INTENT_ACTION_FASTCHARGE = "com.aokp.romcontrol.FCHARGE_CHANGED";
    public static final String PREF_MINFREE = "pref_minfree";
    public static final String PREF_MINFREE_BOOT = "pref_minfree_boot";
    public static final String PREF_READ_AHEAD = "pref_read_ahead";
    public static final String PREF_READ_AHEAD_BOOT = "pref_read_ahead_boot";
    public static final String PREF_FASTCHARGE = "pref_fast_charge";
    
  //zRam
    public static final String ISZRAM = "busybox echo `busybox zcat /proc/config.gz | busybox grep ZRAM | busybox grep -v '^#'`";
    public static final String ZRAM_SIZE_PATH = "/sys/block/zram0/disksize";
    public static final String ZRAM_COMPR_PATH = "/sys/block/zram0/compr_data_size";
    public static final String ZRAM_ORIG_PATH = "/sys/block/zram0/orig_data_size";
    public static final String ZRAM_MEMTOT_PATH = "/sys/block/zram0/mem_used_total";
    public static final String PREF_ZRAM = "zram_size";
    public static final String ZRAM_SOB = "zram_boot";
    public static final String ZRAM_ON = "zram_on";
   //------ MinFree ------
    public static final String OOM_FOREGROUND_APP = "oom_foreground_app";
    public static final String OOM_VISIBLE_APP = "oom_visible_app";
    public static final String OOM_SECONDARY_SERVER = "oom_secondary_server";
    public static final String OOM_HIDDEN_APP = "oom_hidden_app";
    public static final String OOM_CONTENT_PROVIDERS = "oom_content_providers";
    public static final String OOM_EMPTY_APP = "oom_empty_app";
    //------ DoNotKillProc
    public static final String USER_PROC_PATH = "/sys/module/lowmemorykiller/parameters/donotkill_proc";
    public static final String SYS_PROC_PATH = "/sys/module/lowmemorykiller/parameters/donotkill_sysproc";
    public static final String USER_PROC_NAMES_PATH = "/sys/module/lowmemorykiller/parameters/donotkill_proc_names";
    public static final String USER_SYS_NAMES_PATH = "/sys/module/lowmemorykiller/parameters/donotkill_sysproc_names";
    public static final String USER_PROC_SOB = "user_proc_boot";
    public static final String SYS_PROC_SOB = "sys_proc_boot";
    public static final String PREF_USER_PROC = "pref_user_proc";
    public static final String PREF_SYS_PROC = "pref_sys_proc";
    public static final String PREF_USER_NAMES = "pref_user_names_proc";
    public static final String PREF_SYS_NAMES = "pref_sys_names_proc";
    //-------BLX---------
    public static final String PREF_BLX = "pref_blx";
    public static final String BLX_PATH = "/sys/class/misc/batterylifeextender/charging_limit";
    public static final String BLX_SOB = "blx_sob";
    public static final String SH_PATH = "/data/PerformanceControl";
    //-------DFsync---------
    public static final String DSYNC_PATH = "/sys/kernel/dyn_fsync/Dyn_fsync_active";
    public static final String PREF_DSYNC= "pref_dsync";
    //-------BL----
    public static final String PREF_BLTIMEOUT= "pref_bltimeout";
    public static final String BLTIMEOUT_SOB= "bltimeout_sob";
    public static final String PREF_BLTOUCH= "pref_bltouch";
    public static final String BL_TIMEOUT_PATH="/sys/class/misc/notification/bl_timeout";
    public static final String BL_TOUCH_ON_PATH="/sys/class/misc/notification/touchlight_enabled";
    //-------BLN---------
    //public static final String BLN_PATH="/sys/class/misc/backlightnotification/enabled";
    public static final String PREF_BLN= "pref_bln";
    //-------PFK---------
    public static final String PFK_VER = "/sys/class/misc/phantom_kp_filter/version";
    public static final String PFK_HOME_ON = "pfk_home_on";
    public static final String PREF_HOME_ALLOWED_IRQ= "pref_home_allowed_irq";
    public static final String PREF_HOME_REPORT_WAIT = "pref_home_report_wait";
    public static final String PFK_MENUBACK_ON = "pfk_menuback_on";
    public static final String PREF_MENUBACK_INTERRUPT_CHECKS = "pref_menuback_interrupt_checks";
    public static final String PREF_MENUBACK_FIRST_ERR_WAIT = "pref_menuback_first_err_wait";
    public static final String PREF_MENUBACK_LAST_ERR_WAIT = "pref_menuback_last_err_wait";

    public static final String PFK_HOME_ENABLED = "/sys/class/misc/phantom_kp_filter/home_enabled";
    public static final String PFK_HOME_ALLOWED_IRQ = "/sys/class/misc/phantom_kp_filter/home_allowed_irqs";
    public static final String PFK_HOME_REPORT_WAIT = "/sys/class/misc/phantom_kp_filter/home_report_wait";
    public static final String PFK_HOME_IGNORED_KP = "/sys/class/misc/phantom_kp_filter/home_ignored_kp";
    public static final String PFK_MENUBACK_ENABLED = "/sys/class/misc/phantom_kp_filter/menuback_enabled";
    public static final String PFK_MENUBACK_INTERRUPT_CHECKS = "/sys/class/misc/phantom_kp_filter/menuback_interrupt_checks";
    public static final String PFK_MENUBACK_FIRST_ERR_WAIT = "/sys/class/misc/phantom_kp_filter/menuback_first_err_wait";
    public static final String PFK_MENUBACK_LAST_ERR_WAIT = "/sys/class/misc/phantom_kp_filter/menuback_last_err_wait";
    public static final String PFK_MENUBACK_IGNORED_KP = "/sys/class/misc/phantom_kp_filter/menuback_ignored_kp";
    public static final String PFK_SOB = "pfk_sob";
    //------------------
    public static final String DYNAMIC_DIRTY_WRITEBACK_PATH = "/proc/sys/vm/dynamic_dirty_writeback";
    public static final String DIRTY_WRITEBACK_ACTIVE_PATH = "/proc/sys/vm/dirty_writeback_active_centisecs";
    public static final String DIRTY_WRITEBACK_SUSPEND_PATH = "/proc/sys/vm/dirty_writeback_suspend_centisecs";
    public static final String PREF_DYNAMIC_DIRTY_WRITEBACK = "pref_dynamic_dirty_writeback";
    public static final String PREF_DIRTY_WRITEBACK_ACTIVE = "pref_dynamic_writeback_active";
    public static final String PREF_DIRTY_WRITEBACK_SUSPEND = "pref_dynamic_writeback_suspend";
    public static final String DYNAMIC_DIRTY_WRITEBACK_SOB = "dynamic_write_back_sob";

    // VM settings
    public static final String PREF_DIRTY_RATIO = "pref_dirty_ratio";
    public static final String PREF_DIRTY_BACKGROUND = "pref_dirty_background";
    public static final String PREF_DIRTY_EXPIRE = "pref_dirty_expire";
    public static final String PREF_DIRTY_WRITEBACK = "pref_dirty_writeback";
    public static final String PREF_MIN_FREE_KB = "pref_min_free_kb";
    public static final String PREF_OVERCOMMIT = "pref_overcommit";
    public static final String PREF_SWAPPINESS = "pref_swappiness";
    public static final String PREF_VFS = "pref_vfs";
    public static final String DIRTY_RATIO_PATH = "/proc/sys/vm/dirty_ratio";
    public static final String DIRTY_BACKGROUND_PATH = "/proc/sys/vm/dirty_background_ratio";
    public static final String DIRTY_EXPIRE_PATH = "/proc/sys/vm/dirty_expire_centisecs";
    public static final String DIRTY_WRITEBACK_PATH = "/proc/sys/vm/dirty_writeback_centisecs";
    public static final String MIN_FREE_PATH = "/proc/sys/vm/min_free_kbytes";
    public static final String OVERCOMMIT_PATH = "/proc/sys/vm/overcommit_ratio";
    public static final String SWAPPINESS_PATH = "/proc/sys/vm/swappiness";
    public static final String VFS_CACHE_PRESSURE_PATH = "/proc/sys/vm/vfs_cache_pressure";
    public static final String VM_SOB = "vm_sob";

}


