package me.hetian.flutterdeviceinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.webkit.WebSettings;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterDeviceInfoPlugin */
public class FlutterDeviceInfoPlugin implements MethodCallHandler {

    Context mContext;
    Registrar registrar;
    HashMap<String, Object> constants;
    WifiInfo wifiInfo;

    public FlutterDeviceInfoPlugin(Registrar registrar) {
        mContext = registrar.context();
        this.registrar = registrar;
        this.getiConstants();
    }


    /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_device_info");
    channel.setMethodCallHandler(new FlutterDeviceInfoPlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {

    switch (call.method) {
        case "getConstants":
            result.success(constants);
            break;
        case "batteryLevel":
            getBatteryLevel(call, result);
            break;
        case "IPAddress":
            getIpAddress(call, result);
            break;
        default:
            result.notImplemented();
    }

  }

    @SuppressLint("MissingPermission")
    private WifiInfo getWifiInfo() {
        if (this.wifiInfo == null) {
            WifiManager manager = (WifiManager) this.mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            this.wifiInfo = manager.getConnectionInfo();
        }
        return this.wifiInfo;
    }

    public void getIpAddress(MethodCall call, Result result) {
        String ipAddress = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            ipAddress = Formatter.formatIpAddress(getWifiInfo().getIpAddress());
        }
        result.success(ipAddress);
    }

  // 获取电量
    public void getBatteryLevel(MethodCall call, Result result) {
        Intent batteryIntent = this.mContext.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryLevel = level / (float) scale;
        result.success(batteryLevel);
    }

    private String getCurrentLanguage() {
        Locale current;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            current = this.registrar.activeContext().getApplicationContext().getResources().getConfiguration().getLocales().get(0);
        } else {
            current = this.registrar.activeContext().getApplicationContext().getResources().getConfiguration().locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return current.toLanguageTag();
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(current.getLanguage());
            if (current.getCountry() != null) {
                builder.append("-");
                builder.append(current.getCountry());
            }
            return builder.toString();
        }
    }

    private String getCurrentCountry() {
        Locale current;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            current = this.registrar.activeContext().getApplicationContext().getResources().getConfiguration().getLocales().get(0);
        } else {
            current = this.registrar.activeContext().getApplicationContext().getResources().getConfiguration().locale;
        }

        return current.getCountry();
    }

    public String getCarrier() {
        TelephonyManager telMgr = (TelephonyManager) this.mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telMgr.getNetworkOperatorName();
    }

    public Integer getTotalDiskCapacity() {
        try {
            StatFs root = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            return root.getBlockCount() * root.getBlockSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getFreeDiskStorage() {
        try {
            StatFs external = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            return external.getAvailableBlocks() * external.getBlockSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getIMEI(Context context) {
        String imei;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            imei = "";
        }
        return imei;
    }

  public void getiConstants() {
      constants = new HashMap<String, Object>();

      PackageManager packageManager = this.mContext.getPackageManager();
      String packageName = this.mContext.getPackageName();

      constants.put("appVersion", "not available");
      constants.put("appName", "not available");
      constants.put("buildNumber", "0");

      try {
          PackageInfo info = packageManager.getPackageInfo(packageName, 0);
          String applicationName = null;
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
              applicationName = this.registrar.context().getApplicationInfo().loadLabel(this.mContext.getPackageManager()).toString();
          }
          constants.put("appVersion", info.versionName);
          constants.put("buildNumber", info.versionCode);
          constants.put("firstInstallTime", info.firstInstallTime);
          constants.put("lastUpdateTime", info.lastUpdateTime);
          constants.put("appName", applicationName);
      } catch (PackageManager.NameNotFoundException e) {
          e.printStackTrace();
      }

      String deviceName = "Unknown";

      String permission = "android.permission.BLUETOOTH";
      int res = this.mContext.checkCallingOrSelfPermission(permission);
      if (res == PackageManager.PERMISSION_GRANTED) {
          try {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
              BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                  if (myDevice != null) {
                      deviceName = myDevice.getName();
                  }
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
      }


      constants.put("serialNumber", Build.SERIAL);
      constants.put("deviceName", deviceName);
      constants.put("systemName", "Android");
      constants.put("systemVersion", Build.VERSION.RELEASE);
      constants.put("model", Build.MODEL);
      constants.put("brand", Build.BRAND);
      constants.put("deviceId", Build.BOARD);
      constants.put("apiLevel", String.valueOf(Build.VERSION.SDK_INT));
      constants.put("deviceLocale", this.getCurrentLanguage());
      constants.put("deviceCountry", this.getCurrentCountry());
      constants.put("uniqueId", Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
      constants.put("systemManufacturer", Build.MANUFACTURER);
      constants.put("bundleId", packageName);

      constants.put("timezone", TimeZone.getDefault().getID());
      constants.put("carrier", this.getCarrier());
      constants.put("totalDiskCapacity", this.getTotalDiskCapacity());
      constants.put("freeDiskStorage", this.getFreeDiskStorage());
      constants.put("imei", this.getIMEI());

      Runtime rt = Runtime.getRuntime();
      constants.put("maxMemory", String.valueOf(rt.maxMemory()));
      ActivityManager actMgr = (ActivityManager) this.mContext.getSystemService(Context.ACTIVITY_SERVICE);
      ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
      actMgr.getMemoryInfo(memInfo);
      constants.put("totalMemory", String.valueOf(memInfo.totalMem));
  }
}
