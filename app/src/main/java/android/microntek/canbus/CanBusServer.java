package android.microntek.canbus;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.microntek.serial.SerialManager;
import android.microntek.serial.SerialReceiver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

/**
 * Created by timojaas on 19.4.2017.
 * 5 = GM Raise
 9 = Mazda&ope (simple)
 27 = GM hiworld
 41 = GMC simple
 55 = GM simple
 */

public class CanBusServer extends Service {
    public static final String LOG_TAG = "AHUCanBusServer";
    public  CanBus44 mCanbus;
    public  Boolean btLock;

    public static final int canBusType = 44;
    private Handler mHandler;
    public AudioManager am;
    private SerialManager mSerialManager;
    private BroadcastReceiver CanBusReceiver;


    public  final int STATE_OFF = 0;
    public  final int STATE_AVIN = 1;
    public  final int STATE_BLUETOOTH = 2;
    public  final int STATE_MUSIC = 3;
    public  final int STATE_MOVIE = 4;
    public  final int STATE_IPOD = 5;
    public  final int STATE_ATV = 6;
    public  final int STATE_DTV = 7;
    public  final int STATE_NTV = 8;
    public  final int STATE_RADIO = 9;
    public  final int STATE_DVD = 10;
    public  final int STATE_A2DP = 11;
    public  final int STATE_USB = 12;
    public  final int STATE_TEST = 13;

    public  int canBusState;
    private Context mContext;

    public CanBusServer() {


        btLock = false;
        canBusState = 0;


    }
    public void WritePort(final byte[] byteArr) {
         int what = 1; //repeat message
        // we do scrolling text and not std repeeat
        if(canBusState == STATE_MUSIC) {
            what = 10;
        } else if(canBusState == STATE_A2DP) {
            what = 11;
        } else if(canBusState == STATE_OFF) {
            what = 0;
        }
        this.mHandler.removeMessages(what);
        String s = "";
        for (int length = byteArr.length, i = 0; i < length; ++i) {
            final StringBuilder append = new StringBuilder().append(s);
            final Locale us = Locale.US;
            final String s2 = "%d";
            s = append.append(String.format(us, s2, (byteArr[i] & 0xFF))).toString();
            if (i < length - 1) {
                s += ",";
            }
        }


        this.am.setParameters("canbus_rsp=" + s);

        final Message obtainMessage = this.mHandler.obtainMessage();
        obtainMessage.what = what;
        obtainMessage.obj = byteArr;
        this.mHandler.sendMessageDelayed(obtainMessage, 300L);
    }
    public void SendCanBusCmdData3(final byte byteParm, final byte[] array, final int arrayLength) {

        final byte[] array2 = new byte[arrayLength + 5];
        array2[0] = -3;
        array2[1] = (byte)(arrayLength + 4 & 0xFF);
        array2[2] = byteParm;
        short n4 = (short)(array2[1] + array2[2]);
        for (int i = 0; i < arrayLength; ++i) {
            array2[i + 3] = array[i];
            n4 += array2[i + 3];
        }
        array2[arrayLength + 3] = (byte)(n4 >> 8 & 0xFF);
        array2[arrayLength + 4] = (byte)(n4 & 0xFF);

        final String temp = HelperFunctions.getHex(array);
        Log.i(LOG_TAG, "Writing to Canbus HEX: " + temp);
        Log.i(LOG_TAG, "Writing to Canbus DEC: " + HelperFunctions.convertHexToDecimal(temp));
        Log.i(LOG_TAG, "Writing to Canbus ASC: " + HelperFunctions.convertHexToString(temp));
        sendCanBusText(array);
        this.WritePort(array2);
    }

    private void initCanBusApp() {

        if (SystemProperties.get("ro.product.canbusupdata", "false").equals("false")) {
            SystemProperties.set("ro.product.canbusupdata", "true");
            final PackageManager packageManager = this.getPackageManager();

            this.setApplicationEnabled("com.microntek.controlsettings", true, packageManager);
            this.setApplicationEnabled("com.microntek.controlinfo", true, packageManager);



        }
    }

    public void setApplicationEnabled(final String packageName, final boolean enabled, final PackageManager packageManager) {


            if (this.isPackageInstalled(packageName, packageManager)) {


                if (enabled) {
                    packageManager.setApplicationEnabledSetting(packageName, 1, 1);
                }
                else {
                    packageManager.setApplicationEnabledSetting(packageName, 2, 1);
                }

            }

    }
    public void onCreate() {
        this.mContext = this.getApplicationContext();
        this.am = (AudioManager) getSystemService(AUDIO_SERVICE);
        this.mHandler = new CanBusHandler(this);
        this.mCanbus =  new CanBus44(this, mContext);
        this.CanBusReceiver = new CanBusReceiver(this);
        this.initCanBusApp();
        this.CanbusSerialinit();
        Log.i("CanBusServer", "CanBus is Start 5.1 ! " + canBusType);






        super.onCreate();
    }

    private boolean isPackageInstalled(final String s, final PackageManager packageManager) {
        final List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (int size = installedPackages.size(), i = 0; i < size; ++i) {
            if (s.equalsIgnoreCase(installedPackages.get(i).packageName)) {
                return true;
            }
        }
        return false;
    }

    public void CanbusSerialinit() {


        this.mSerialManager = new SerialManager();
        try {
            this.mSerialManager.openPort(4, 38400);
            this.mSerialManager.updateReceiver(4, new SerialReceiver() {
                @Override
                public void onSerialReceived(final byte[] obj) {
                    final Message obtainMessage = mHandler.obtainMessage();
                    obtainMessage.what = 5;
                    obtainMessage.obj = obj;
                    mHandler.sendMessage(obtainMessage);
                }
                });

        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
            ex.printStackTrace();
        }

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.microntek.canbusdisplay");
        intentFilter.addAction("com.hiworld.canbus.send");
        intentFilter.addAction("com.microntek.canbus20activity");
        intentFilter.addAction("com.microntek.bootcheck");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction("com.microntek.VOLUME_CHANGED");
        intentFilter.addAction("com.microntek.btcanbusinfo");
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        this.registerReceiver(this.CanBusReceiver, intentFilter);

        if (this.mCanbus != null) {
            this.mCanbus.Init();
        }

        this.mHandler.removeMessages(2);
        this.mHandler.sendEmptyMessageDelayed(2, 1000L);
        Settings.System.putInt(this.getContentResolver(), "com.microntek.hiworld.ari", 0);

    }
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        this.unregisterReceiver(this.CanBusReceiver);
        this.mHandler.removeCallbacksAndMessages(null);
        if (this.mSerialManager != null) {
            this.mSerialManager.closePort(4);
        }
        this.mSerialManager = null;
        this.mCanbus = null;

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }


    private void sendCanBusText(byte[] array) {
        final Intent intent = new Intent("com.ahucanbus.display");
        intent.putExtra("text", array);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT_OR_SELF);
    }

    private static class CanBusHandler extends Handler {
        private final WeakReference<CanBusServer> service;

        public CanBusHandler(CanBusServer service) {
            this.service = new WeakReference<>(service);
        }

        public  void handleMessage(final Message message) {
            CanBusServer canBusServer = service.get();
            switch (message.what) {
                case 5: {
                    byte[] tempA = (byte[])message.obj;
                    // CanBusServer(byte[])message.obj);
                    //cmdProc

                    final String temp = HelperFunctions.getHex(tempA);
                    if(!temp.equalsIgnoreCase("2E00050000000000FA")) {
                        Log.i(LOG_TAG, "Received from Canbus HEX: " + temp);
                        Log.i(LOG_TAG, "Received from Canbus DEC: " + HelperFunctions.convertHexToDecimal(temp));
                        Log.i(LOG_TAG, "Received from Canbus ASC: " + HelperFunctions.convertHexToString(temp));
                    }

                    break;
                }
                case 1: {

                    if ( canBusServer.mCanbus != null) {
                        canBusServer.mCanbus.Resend((byte[])message.obj);
                        break;
                    }
                    break;
                }
                case 10: {

                    if ( canBusServer.mCanbus != null) {
                        canBusServer.mCanbus.reSendMusic();
                        break;
                    }
                    break;
                } case 11: {

                    if ( canBusServer.mCanbus != null) {
                        canBusServer.mCanbus.reSendA2DP();
                        break;
                    }
                    break;
                }
                case 2: {
                    if (canBusServer.mCanbus != null) {
                        canBusServer.mCanbus.UpdateTime();
                        break;
                    }
                    break;
                }
                case 3: {
                    final int arg1 = message.arg1;
                    if (canBusServer.mCanbus != null) {
                        canBusServer.mCanbus.UpdateVol(arg1);
                    }
                    break;
                }
                case 6: {

                    //KLD thing

                    break;
                }
            }
        }
    }
}
