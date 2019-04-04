package cn.lizz.cordova.plugin;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import android.app.Activity;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class ScreenToggle extends CordovaPlugin {

    DevicePolicyManager policyManager;
    ComponentName adminReceiver;
    KeyguardManager keyguardManager;
    PowerManager.WakeLock wakeLock;
    PowerManager powerManager;
    final String tag = "lizz.ScreenToggle";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        //获取GetContext
        Activity context = cordova.getActivity();
        //获取DevicePolicyManager
        policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //获取ComponentName
        adminReceiver = new ComponentName(context, ScreenOffAdminReceiver.class);
        //判断是否激活设备管理器
        if (!policyManager.isAdminActive(adminReceiver)) {
            Toast.makeText(context, "初始化检查：请求授权设备管理", Toast.LENGTH_LONG).show();
            requestDeviceAdmin();
        } else {
            Toast.makeText(context, "初始化检查：设备已被激活", Toast.LENGTH_LONG).show();
            
            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
            //todo  开启服务
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("open")) {
            this.open(callbackContext);
            return true;
        } else if (action.equals("close")) {
            this.close(callbackContext);
            return true;
        } else if (action.equals("enable")) {
            String message = args.getString(0);
            this.enable(message, callbackContext);
            return true;
        } else if (action.equals("disable")) {
            String message = args.getString(0);
            this.disable(message, callbackContext);
            return true;
        }
        return false;
    }

    private void open(CallbackContext callbackContext) {
        try {
            turnOnScreen();
            boolean isScreenOn = powerManager.isScreenOn();
            callbackContext.success(isScreenOn+"");
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }      
    }

    private void close(CallbackContext callbackContext) {
        try {
            turnOffScreen();
            boolean isScreenOn = powerManager.isScreenOn();
            callbackContext.success(isScreenOn+"");
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        } 
    }

    private void enable(String message, CallbackContext callbackContext) {

        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void disable(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Activity context = cordova.getActivity();
        if (policyManager.isAdminActive(adminReceiver)) {//判断超级管理员是否激活
            Toast.makeText(context, "设备已被激活", Toast.LENGTH_LONG).show();

            //todo  开启服务
        } else {
            Toast.makeText(context, "设备没有被激活", Toast.LENGTH_LONG).show();
        }
    }


    public void requestDeviceAdmin() {
        Intent policyIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        policyIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
        policyIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后就可以使用锁屏功能了...");
        this.cordova.startActivityForResult((CordovaPlugin) this, policyIntent, 0);
    }

    public void StartService(){



    }


    public void StopService(){


    }

    public void turnOnScreen() {
        Log.v(tag, "ON!");

        KeyguardManager.KeyguardLock keyLock = keyguardManager.newKeyguardLock("unlock");
        keyLock.disableKeyguard();


        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.FULL_WAKE_LOCK, "lizz:bright");

        wakeLock.acquire();
        wakeLock.release();

    }

    public void turnOffScreen() {
        Log.v(tag, "Off!");
        policyManager.lockNow();
    }
}
