//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mst.ffmpegx264record;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DeviceUtils {
    public DeviceUtils() {
    }

    public static boolean hasFroyo() {
        return VERSION.SDK_INT >= 8;
    }

    public static boolean hasGingerbread() {
        return VERSION.SDK_INT >= 9;
    }

    public static boolean hasHoneycomb() {
        return VERSION.SDK_INT >= 11;
    }

    public static boolean hasHoneycombMR1() {
        return VERSION.SDK_INT >= 12;
    }

    public static boolean hasICS() {
        return VERSION.SDK_INT >= 14;
    }

    public static boolean hasJellyBean() {
        return VERSION.SDK_INT >= 16;
    }

    public static boolean hasJellyBeanMr1() {
        return VERSION.SDK_INT >= 17;
    }

    public static boolean hasJellyBeanMr2() {
        return VERSION.SDK_INT >= 18;
    }

    public static boolean hasKitkat() {
        return VERSION.SDK_INT >= 19;
    }

    public static int getSDKVersionInt() {
        return VERSION.SDK_INT;
    }

    public static String getSDKVersion() {
        return VERSION.SDK;
    }

    public static String getReleaseVersion() {
        return StringUtils.makeSafe(VERSION.RELEASE);
    }

    public static boolean isZte() {
        return getDeviceModel().toLowerCase().indexOf("zte") != -1;
    }

    public static boolean isSamsung() {
        return getManufacturer().toLowerCase().indexOf("samsung") != -1;
    }

    public static boolean isHTC() {
        return getManufacturer().toLowerCase().indexOf("htc") != -1;
    }

    public static boolean isDevice(String... devices) {
        String model = getDeviceModel();
        if (devices != null && model != null) {
            String[] var5 = devices;
            int var4 = devices.length;

            for (int var3 = 0; var3 < var4; ++var3) {
                String device = var5[var3];
                if (model.indexOf(device) != -1) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getDeviceModel() {
        return StringUtils.trim(Build.MODEL);
    }

    public static String getManufacturer() {
        return StringUtils.trim(Build.MANUFACTURER);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public static boolean isHoneycombTablet(Context context) {
        return hasHoneycomb() && isTablet(context);
    }

    public static int dipToPX(Context ctx, float dip) {
        return (int) TypedValue.applyDimension(1, dip, ctx.getResources().getDisplayMetrics());
    }

    public static String getCpuInfo() {
        String cpuInfo = "";

        try {
            if ((new File("/proc/cpuinfo")).exists()) {
                FileReader fr = new FileReader("/proc/cpuinfo");
                BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
                cpuInfo = localBufferedReader.readLine();
                localBufferedReader.close();
                if (cpuInfo != null) {
                    cpuInfo = cpuInfo.split(":")[1].trim().split(" ")[0];
                }
            }
        } catch (IOException var3) {
            ;
        } catch (Exception var4) {
            ;
        }

        return cpuInfo;
    }

    public static boolean isSupportCameraLedFlash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                FeatureInfo[] var5 = features;
                int var4 = features.length;

                for (int var3 = 0; var3 < var4; ++var3) {
                    FeatureInfo f = var5[var3];
                    if (f != null && "android.hardware.camera.flash".equals(f.name)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isSupportCameraHardware(Context context) {
        return context != null && context.getPackageManager().hasSystemFeature("android.hardware.camera");
    }

    public static int getScreenWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    public static int getScreenHeight(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getHeight();
    }
}
