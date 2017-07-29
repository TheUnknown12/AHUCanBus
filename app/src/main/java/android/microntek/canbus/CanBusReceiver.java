package android.microntek.canbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by timojaas on 20.4.2017.
 */

public class CanBusReceiver extends BroadcastReceiver {
    public static final String LOG_TAG = "CanBusReceiver";
    private final CanBusServer canBusServer;
    private Intent displayIntent;

    public CanBusReceiver(CanBusServer service) {
        this.canBusServer = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String intentAction = intent.getAction();
        
        if (intentAction.equals("com.microntek.canbusdisplay")) {
            displayIntent = intent;
            if (!canBusServer.btLock) {
                canBusDisplay(intent);
            }

        } else if (intentAction.equals("com.microntek.bootcheck")) {
            final String stringExtra = intent.getStringExtra("class");
            if (stringExtra.equals("phonecallin")) {
                canBusServer.btLock = true;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendBT();
                }

            }
            else if (stringExtra.equals("phonecallout")) {
                canBusServer.btLock = false;
                if (displayIntent != null) {
                    canBusDisplay(displayIntent);

                }
                else {
                    if (canBusServer.mCanbus != null) {
                        canBusServer.mCanbus.SendOff();
                    }

                }
            } else if (stringExtra.equals("poweroff")) {
                canBusServer.mCanbus.SendPowerOff();
            }
        }
    }

    public void canBusDisplay(final Intent intent) {

        Log.d(LOG_TAG, "Canbus State: " + canBusServer.canBusState + " mCAnbus exists: " + (null != canBusServer.mCanbus) + " " + intent.toUri(0));
        final String stringExtra = intent.getStringExtra("type");
        if (stringExtra != null) {

            if (stringExtra.equals("radio") && canBusServer.canBusState == canBusServer.STATE_RADIO) {
                final int freqListNum = intent.getIntExtra("group", 0);
                final int frequency = intent.getIntExtra("fre", 87500);
                final byte index = intent.getByteExtra("index", (byte)0);

                final String channelName = intent.getStringExtra("psn");
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendRadio(freqListNum, frequency, index, channelName);
                }

            }
            else if (stringExtra.equals("dvd") && canBusServer.canBusState == canBusServer.STATE_DVD) {
                final int intExtra2 = intent.getIntExtra("item", 0);
                intent.getIntExtra("all", 0);
                final int intExtra3 = intent.getIntExtra("time", 0);
                final int intExtra4 = intent.getIntExtra("mDiskType", -1);
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendDVD(intExtra2, intExtra3, intExtra4);
                }

            }
            else if (stringExtra.equals("music") && canBusServer.canBusState == canBusServer.STATE_MUSIC) {
                final int totIndex = intent.getIntExtra("all", 0);
                final int curIndex = intent.getIntExtra("cur", 0);
                final int curPos = intent.getIntExtra("time", 0);
                String musicText ="";
                final String trackName = intent.getStringExtra("trackname");
                final String artistName = intent.getStringExtra("artistname");
                if (null != artistName && null != trackName) {
                    musicText = artistName + " - " + trackName;
                } else if (null == trackName) {
                    musicText = artistName;
                } else {
                    musicText = trackName;
                }


                if (canBusServer.mCanbus != null) {
                    if (canBusServer.mCanbus.mDisplayText.equalsIgnoreCase(musicText)){

                        canBusServer.mCanbus.updateScrollText();

                    } else {

                        canBusServer.mCanbus.mDisplayText = musicText;
                        canBusServer.mCanbus.displayScrollText = musicText;
                        canBusServer.mCanbus.updateTime = System.currentTimeMillis();
                    }

                    canBusServer.mCanbus.SendMusic(totIndex, curIndex, curPos);
                }

            }
            else if (stringExtra.equals("usb") && canBusServer.canBusState == canBusServer.STATE_USB) {
                final int intExtra8 = intent.getIntExtra("all", 0);
                final int intExtra9 = intent.getIntExtra("cur", 0);
                final int intExtra10 = intent.getIntExtra("time", 0);
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendMusic(intExtra8, intExtra9, intExtra10);
                }

            }
            else if (stringExtra.equals("movie") && canBusServer.canBusState == canBusServer.STATE_MOVIE) {
                final int intExtra11 = intent.getIntExtra("all", 0);
                final int intExtra12 = intent.getIntExtra("cur", 0);
                final int intExtra13 = intent.getIntExtra("time", 0);
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendMedia();
                    canBusServer.mCanbus.SendMedia(intExtra11, intExtra12, intExtra13);
                }

            }
            else if (stringExtra.equals("bluetooth") && canBusServer.canBusState == canBusServer.STATE_BLUETOOTH) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendBT();
                }

            }
            else if (stringExtra.equals("a2dp") && canBusServer.canBusState == canBusServer.STATE_A2DP) {
                if (canBusServer.mCanbus != null) {

                    String musicText = intent.getStringExtra("text");
                    if (null != musicText) {
                        musicText.replace("\n", " - ");
                    } else {
                        musicText = "";
                    }
                    canBusServer.mCanbus.SendA2DP(musicText);
                }

            }
            else if (stringExtra.equals("avin") && canBusServer.canBusState == canBusServer.STATE_AVIN) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendAvin();
                }

            }
            else if (stringExtra.equals("dtv") && canBusServer.canBusState == canBusServer.STATE_DTV) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendDTV();
                }

            }
            else if (stringExtra.equals("ntv") && canBusServer.canBusState == canBusServer.STATE_NTV) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendDTV();
                }

            }
            else if (stringExtra.equals("atv") && canBusServer.canBusState == canBusServer.STATE_ATV) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendAtv();
                }

            }
            else if (stringExtra.equals("ipod") && canBusServer.canBusState == canBusServer.STATE_IPOD) {
                final int intExtra14 = intent.getIntExtra("all", 0);
                final int intExtra15 = intent.getIntExtra("cur", 0);
                final int intExtra16 = intent.getIntExtra("time", 0);
                final int intExtra17 = intent.getIntExtra("musicposition", 0);
                final byte byteExtra3 = intent.getByteExtra("bili", (byte)0);
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendIpod(intExtra14, intExtra15, byteExtra3, intExtra17, intExtra16);
                }

            }else if (stringExtra.equals("test") && canBusServer.canBusState == canBusServer.STATE_TEST) {
                final int fontSetting = intent.getIntExtra("font", 0);
                final int textLocation = intent.getIntExtra("textlocation", 0);
                final int textOrientation = intent.getIntExtra("textorientation", 0);
                final int textNumber = intent.getIntExtra("textnumber", 0);
                String text = intent.getStringExtra("text");
                String command = intent.getStringExtra("command");

                if (null == text && null == command) {
                    return;
                }
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendTest(text, fontSetting, textOrientation, textLocation,textNumber, command);
                }

            }
            else if (stringExtra.equals("ipod-on")) {
                canBusServer.canBusState = canBusServer.STATE_IPOD;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendIpod(0, 0, (byte)0, 0, 0);
                }

            }
            else if (stringExtra.equals("radio-on")) {
                canBusServer.canBusState = canBusServer.STATE_RADIO;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendRadio((byte)0, 0, (byte)0);
                }

            }
            else if (stringExtra.equals("movie-on")) {
                canBusServer.canBusState = canBusServer.STATE_MOVIE;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendMedia();
                    canBusServer.mCanbus.SendMedia(0, 0, 0);
                }

            }
            else if (stringExtra.equals("ntv-on")) {
                canBusServer.canBusState = canBusServer.STATE_NTV;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendDTV();
                }

            }
            else if (stringExtra.equals("atv-on")) {
                canBusServer.canBusState = canBusServer.STATE_ATV;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendAtv();
                }

            }
            else if (stringExtra.equals("dtv-on")) {
                canBusServer.canBusState = canBusServer.STATE_DTV;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendDTV();
                }

            }
            else if (stringExtra.equals("avin-on")) {
                canBusServer.canBusState = canBusServer.STATE_AVIN;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendAvin();
                }

            }
            else if (stringExtra.equals("bluetooth-on")) {
                canBusServer.canBusState = canBusServer.STATE_BLUETOOTH;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendBT();
                }

            }
            else if (stringExtra.equals("a2dp-on")) {
                canBusServer.canBusState = canBusServer.STATE_A2DP;
                if (canBusServer.mCanbus != null) {

                    canBusServer.mCanbus.SendA2DP();
                }

            }
            else if (stringExtra.equals("dvd-on")) {
                canBusServer.canBusState = canBusServer.STATE_DVD;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendDVD(0, 0, 0);
                }

            }
            else if (stringExtra.equals("music-on")) {
                canBusServer.canBusState = canBusServer.STATE_MUSIC;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendMusic(0, 0, 0);
                }

            }
            else if (stringExtra.equals("usb-on")) {
                canBusServer.canBusState = canBusServer.STATE_USB;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendMusic(0, 0, 0);
                }

            } else if (stringExtra.equals("test-on")) {
                canBusServer.canBusState = canBusServer.STATE_TEST;
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendTest("TEST MODE", 0, 0, 0,0, null);
                }

            }
            else if (stringExtra.equals("ipod-off") && canBusServer.canBusState == canBusServer.STATE_IPOD) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }
               }
            else if (stringExtra.equals("radio-off") && canBusServer.canBusState == canBusServer.STATE_RADIO) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("movie-off") && canBusServer.canBusState == canBusServer.STATE_MOVIE) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("ntv-off") && canBusServer.canBusState == canBusServer.STATE_NTV) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("atv-off") && canBusServer.canBusState == canBusServer.STATE_ATV) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("dtv-off") && canBusServer.canBusState == canBusServer.STATE_DTV) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("avin-off") && canBusServer.canBusState == canBusServer.STATE_AVIN) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("bluetooth-off") && canBusServer.canBusState == canBusServer.STATE_BLUETOOTH) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("a2dp-off") && canBusServer.canBusState == canBusServer.STATE_A2DP) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("dvd-off") && canBusServer.canBusState == canBusServer.STATE_DVD) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("music-off") && canBusServer.canBusState == canBusServer.STATE_MUSIC) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("usb-off") && canBusServer.canBusState == canBusServer.STATE_USB) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            } else if (stringExtra.equals("test-off") && canBusServer.canBusState == canBusServer.STATE_TEST) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOff();
                }

            }
            else if (stringExtra.equals("off")) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendPowerOff();

                }

            }
            else if (stringExtra.equals("on")) {
                if (canBusServer.mCanbus != null) {
                    canBusServer.mCanbus.SendOn();
                }

            }
        }
    }
}
