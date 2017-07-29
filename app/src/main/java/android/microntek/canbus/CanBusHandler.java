package android.microntek.canbus;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by timojaas on 20.4.2017.
 */

   public  class CanBusHandler  extends Handler {
    public static final String LOG_TAG = "CanBusHandler";
    private final WeakReference<CanBusServer> service;

    public  CanBusHandler(CanBusServer service) {
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
                Log.i(LOG_TAG, "Received from Canbus HEX: " + temp);
                Log.i(LOG_TAG, "Received from Canbus DEC: " + HelperFunctions.convertHexToDecimal(temp));
                Log.i(LOG_TAG, "Received from Canbus ASC: " + HelperFunctions.convertHexToString(temp));
                break;
            }
            case 1: {

                if ( canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.Resend((byte[])message.obj);
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