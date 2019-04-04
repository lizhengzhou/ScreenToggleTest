package cn.lizz.cordova.plugin;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class BackGroundService extends Service {

    final String tag = "lizz.BackGroundService";
    DevicePolicyManager policyManager;
    PowerManager powerManager;
    KeyguardManager keyguardManager;
    PowerManager.WakeLock wakeLock;
    boolean CanRun = false;

    public BackGroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(tag, "OnBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(tag, "onCreate");
        super.onCreate();

        powerManager = (PowerManager) this.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        policyManager = (DevicePolicyManager) this.getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        keyguardManager = (KeyguardManager)this.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);

        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag + ":wakeLockTag");
        wakeLock.acquire();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(tag, "onStartCommand");

        CanRun = true;
        new Thread() {
            @Override
            public void run() {

                while (CanRun) {
                    try {

                        Log.i(tag, "DoSomething");

                        if (powerManager != null) {
                            boolean screen = powerManager.isScreenOn();
                            Log.i(tag, "isScreenOn：" + screen);
                            if (screen) {
                                turnOffScreen();
                            } else {
                                turnOnScreen();
                            }
                        }

                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        CanRun = false;
        wakeLock.release();

        super.onDestroy();
    }

    public void turnOnScreen() {
        Log.v(tag, "ON!");

        KeyguardManager.KeyguardLock keyLock = keyguardManager.newKeyguardLock("unlock");
        keyLock.disableKeyguard();

        PowerManager.WakeLock wakelock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.FULL_WAKE_LOCK, "bright");

        wakelock.acquire();
        wakelock.release();

    }

    public void turnOffScreen() {
        Log.v(tag, "Off!");
        policyManager.lockNow();
    }
}
