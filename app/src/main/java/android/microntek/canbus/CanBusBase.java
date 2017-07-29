package android.microntek.canbus;

import android.content.Context;
import android.util.Log;

/**
 * Created by timojaas on 19.4.2017.
 */

public class CanBusBase {
    private byte[] airdata_back;
    public int canbustype;
    public byte door;
    protected int hour;
    public boolean isTempType;

    public Context mContext;

    public CanBusServer mServer;

    protected int minute;

    public boolean stateOne;

    public CanBusBase(final CanBusServer mServer, final Context mContext) {
        final boolean b = true;
        this.hour = 0;
        this.minute = 0;

        this.stateOne = b;
        this.isTempType = b;

        this.mServer = mServer;
        this.mContext = mContext;
    }



    public void CmdProc(final byte[] array, final int n, final int n2) {
    }

    public void Init() {
    }

    public void LoopSend(final String s) {
    }

    public void Resend(final byte[] array) {
    }

    public void SendA2DP() {
    }

    public void SendAtv() {
    }

    public void SendAvin() {
    }

    public void SendBT() {
    }

    public void SendDTV() {
    }

    public void SendDVD(final int n, final int n2, final int n3) {
    }

    public void SendIpod(final int n, final int n2, final byte b, final int n3, final int n4) {
    }

    public void SendMedia() {
    }

    public void SendMedia(final int n, final int n2, final int n3) {
    }

    public void SendMusic(final int n, final int n2, final int n3) {
    }

    public void SendOff() {
    }

    public void SendOff2() {
    }

    public void SendOn() {

    }

    public void SendRadio(final int b, final int n, final byte b2) {
    }

    public void UpdateLocale() {
    }

    public void UpdateTime() {
    }

    public void UpdateVol(final int n) {
    }

    protected void dbg_msg(final byte[] array, final int n, final int n2) {
        String string = " ";
        for (int i = 0; i < n2; ++i) {
            string += String.format("%02x ", array[i + n]);
        }
        Log.i("CanBus", string);
    }
}
