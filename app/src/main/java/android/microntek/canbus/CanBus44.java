package android.microntek.canbus;

import android.content.Context;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by timojaas on 19.4.2017.
 */

public class CanBus44  extends CanBusBase{
    public static final String LOG_TAG = "CanBus44";
    int mCurrentPosition= 0;
    public long updateTime = 0;
    final static int mDisplayTextLength= 12;
    public static String mArtistName;
    public static String mTrackName;
    public String mDisplayText;
    public String displayScrollText;
    public CanBus44(final CanBusServer canBusServer, final Context context) {
        super(canBusServer, context);
        this.canbustype = 44;
        this.mServer = canBusServer;
        this.mContext = context;

        this.mDisplayText = "";
        this.displayScrollText = "";

    }
    public void CmdProc(final byte[] array, final int n, final int n2) {
    }

    public void Init() {
        this.SendOn();
    }

    public void Resend(final byte[] array) {
        this.mServer.WritePort(array);
    }

    public void SendA2DP(final String displayText) {


        if (mDisplayText.equalsIgnoreCase(displayText)){

            updateScrollText();

        } else {

            mDisplayText = displayText;
            displayScrollText = displayText;
        }
        final byte[] array =("BT: " + getScrollText()).getBytes(Charset.forName("UTF-8"));
        this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
    }


    public void SendAvin() {
        final byte[] array = "Av In".getBytes(Charset.forName("UTF-8"));
        this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
    }

    public void SendBT() {
        final byte[] array ="BlueTooth".getBytes(Charset.forName("UTF-8"));
                this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
    }

    public void SendDTV() {
        final byte[] array ="DTV".getBytes(Charset.forName("UTF-8"));
        this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
    }

    public void SendDVD(final int n, final int totalLength, final int n3) {

        final byte[] array = new byte[]
                {
                        (int) 'D',
                        (int) 'V',
                        (int) 'D',
                        (int) ':',
                        (byte) (totalLength / 60 / 60 / 10 + 48),
                        (byte) (totalLength / 60 / 60 % 10 + 48),
                        (int) ':',
                        (byte) (totalLength / 60 % 60 / 10 + 48),
                        (byte) (totalLength / 60 % 60 % 10 + 48),
                        (int) ':',
                        (byte)(totalLength / 60 / 10 + 48),
                        (byte)(totalLength % 60 % 10 + 48)
                };
        this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
    }



    public void SendMedia() {
        final byte[] array ="Media".getBytes(Charset.forName("UTF-8"));
        this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
    }
    public void reSendMusic() {
        if(this.mServer.canBusState == this.mServer.STATE_MUSIC) {
            String temp = millisToMmSs(mCurrentPosition);
            updateScrollText();
            final byte[] array =(temp +" " + getScrollText()).getBytes(Charset.forName("UTF-8"));


            this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
        }

    }
    public void reSendA2DP() {
        if(this.mServer.canBusState == this.mServer.STATE_A2DP) {

            updateScrollText();
            final byte[] array =("BT: " + getScrollText()).getBytes(Charset.forName("UTF-8"));


            this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
        }

    }
    public void SendMusic(final int n, final int n2, final int curPos) {

        mCurrentPosition = curPos;

        String temp = millisToMmSs(curPos);
        final byte[] array =("" + getScrollText()).getBytes(Charset.forName("UTF-8"));


        this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
    }

    public void  SendOff() {

        final byte[] array = "#12".getBytes(Charset.forName("UTF-8"));
        this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
    }
    public void SendPowerOff() {
        this.mServer.canBusState = this.mServer.STATE_OFF;
        final byte[] array ="Goodbye!".getBytes(Charset.forName("UTF-8"));

        this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
    }
    public void SendOn() {

        byte [] array = "Welcome".getBytes(Charset.forName("UTF-8"));
        this.mServer.SendCanBusCmdData3((byte)0, array, array.length);
    }

    public void SendTest(final String text, final int fontModify, final int textOrientation, final int textLocation, final int textNumber, final String command) {
        byte [] array = null;
        byte cmdType = 0;
        if (null != text) {


            array = text.getBytes(Charset.forName("UTF-8"));
            switch (fontModify) {
                case 1: //Small
                    array = addFontModify(array, "b");
                    break;
                case 2: //middle
                    array = addFontModify(array, "d");
                    break;
                case 3: //Big
                    array = addFontModify(array, "g");
                    break;
                default:
                    break;

            }
            switch (textOrientation) {
                case 1: //Left
                    array = addAlignment(array, "l", textLocation);
                    break;
                case 2: //Right
                    array = addAlignment(array, "r", textLocation);
                    break;
                default:
                    break;

            }
            if (textNumber > 0) {
                array = addTextNumber(array, textNumber);
            }
        }
        if (null != command) {
            if (command.equalsIgnoreCase("setIcon")) {
                cmdType = 10;
                array =new byte[]
                        {

                                (byte) 0,
                                (byte) 80,
                                (byte) 62
                        };
            } else if(command.equalsIgnoreCase("testAux")) {
                array =new byte[]
                        {
                                (byte) 192,
                                (byte) 0,
                                (byte) 3,
                                (byte) 2,
                                (byte) 65,
                                (byte) 117,
                                (byte) 120,
                                (byte) 1,
                                (byte) 32,
                                (byte) 10,
                                (byte) 114,
                                (byte) 111,
                                (byte) 119,
                                (byte) 49,
                                (byte) 114,
                                (byte) 111,
                                (byte) 119,
                                (byte) 50,
                                (byte) 114,
                                (byte) 111,
                                (byte) 119,
                                (byte) 51
                        };
            }

            else {
                array = addCommand(array, command);
            }
        }
        if (null != array) {
            this.mServer.SendCanBusCmdData3(cmdType, array, array.length);
        }

    }

    public void SendRadio(int freqListNum, final int frequency, final byte index, final String channelName) {
        freqListNum = freqListNum - 80;
        byte[] array = null;
        if (frequency == 0) {
            array ="RADIO".getBytes(Charset.forName("UTF-8"));

        } else if (null != channelName) {
            array =channelName.getBytes(Charset.forName("UTF-8"));
        }
        else if (null == channelName) {



                String freqString = Integer.toString(frequency);

                //FM
                if ((freqListNum) >= 0 && freqListNum <= 2) {
                    if (frequency < 10000) {
                        freqString = " " + Integer.toString(frequency);
                    }
                    array =         ("FM" +
                                    (freqListNum+1) +
                                    ":" +
                                    freqString.substring(0,3) +
                                    "." +
                                    freqString.substring(3,5) +
                                    " MHz"
                    ).getBytes(Charset.forName("UTF-8"));

                    //AM
                } else if (freqListNum >= 3 && freqListNum <= 4) {
                    if (frequency < 1000) {
                        freqString = " " + Integer.toString(frequency);
                    }
                    array =
                            ("AM" +
                                    (freqListNum-2) +
                                    ":" +
                                    freqString +
                                    " KHz").getBytes(Charset.forName("UTF-8"));

                }



        }
        if (null != array) {
            this.mServer.SendCanBusCmdData3((byte) 0, array, array.length);
        }

    }

    public void UpdateTime() {
    }

    public void UpdateVol(final int n) {
    }




    public byte[] addFontModify(final byte[] origArray, final String fontModifier) {
        final byte [] cmdArray =("[fS_" + fontModifier + "m").getBytes(Charset.forName("UTF-8"));
        byte [] newArray = new byte[(origArray.length + cmdArray.length +1 )];
        newArray[0] = (int) 27; // 1B '\'
        int i = 1;
        for ( final byte b : cmdArray ) {
            newArray[i] = b;
            i++;
        }
        for ( final byte b : origArray ) {
            newArray[i] = b;
            i++;
        }

        return newArray;


    }
    public byte[] addCommand(final byte[] origArray, final String command) {
        final byte [] cmdArray =command.getBytes(Charset.forName("UTF-8"));
        byte [] newArray = new byte[(origArray.length + cmdArray.length +1 )];
        newArray[0] = (int) 27; // 1B '\'
        int i = 1;
        for ( final byte b : cmdArray ) {
            newArray[i] = b;
            i++;
        }
        for ( final byte b : origArray ) {
            newArray[i] = b;
            i++;
        }

        return newArray;


    }

    public byte[] addTextNumber(final byte[] origArray, final int number) {

        byte [] newArray = new byte[(origArray.length + 3 )];
        newArray[0] = 0x27; //first byte for circled number
        newArray[1] = (byte) (127 + number); //2nd byte for circled number
        newArray[2] = 0x20; //whitespace
        int i = 3;

        for ( final byte b : origArray ) {
            newArray[i] = b;
            i++;
        }

        return newArray;


    }
    public byte[] addAlignment(final byte[] origArray, final String orientation, final int measure) {
        final byte [] cmdArray =("[t" + orientation + measure + "m").getBytes(Charset.forName("UTF-8"));

        byte [] newArray = new byte[(origArray.length + cmdArray.length +1 )];
        newArray[0] = (int) 27; // 1B '\'
        int i = 1;
        for ( final byte b : cmdArray ) {
            newArray[i] = b;
            i++;
        }
        for ( final byte b : origArray ) {
            newArray[i] = b;
            i++;
        }

        return newArray;
    }

    public byte[] encByteArray(String text) {
        byte [] newArray = new byte[text.length()];
        int i= 0;
        for (char tempChar : text.toCharArray()) {
            newArray[0] = (byte) tempChar;
            i++;
        }
        return  newArray;

    }
    public static String millisToMmSs(Integer millis) {


    return String.format(Locale.US, "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
    public void updateScrollText() {
        if (displayScrollText.length() <= 1) {

            displayScrollText = mDisplayText;
            updateTime = System.currentTimeMillis();
        } else  if (System.currentTimeMillis() - updateTime > 1000) {
                displayScrollText = displayScrollText.substring(1);

            updateTime = System.currentTimeMillis();

        }

    }

    public String getScrollText() {
        if (displayScrollText.length() > mDisplayTextLength) {
            return displayScrollText.substring(0, mDisplayTextLength);
        } else {
            return displayScrollText;
        }



    }
}
