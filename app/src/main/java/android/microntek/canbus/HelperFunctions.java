package android.microntek.canbus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by timojaas on 21.4.2017.
 */

public class HelperFunctions {
    static final String HEXES = "0123456789ABCDEF";
    private final static String LOG_TAG = "HelperFunctions";

    public static String getHex(byte[] raw) {

        if (raw == null) {
            return "";
        }

        if ( raw == null ) {
            return null;
        }
        final StringBuilder hex = new StringBuilder( 2 * raw.length );
        for ( final byte b : raw ) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public static String convertHexToDecimal(String hex) {


        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character


            temp.append(decimal + " ");
        }


        return temp.toString();
    }

    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }
        return sb.toString();
    }
    public static void dumpIntentBundle(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();

            while (it.hasNext()) {
                String key = it.next();
                Log.i(LOG_TAG ,"[" + key + "=" + bundle.get(key) + "]");
                //Log.e("myIntentLogger", "[" + key + "hex=" + getHex((byte[]) bundle.get(key)) + "]");
                if (bundle.get(key).getClass() == byte[].class) {
                    String hex = getHex(intent.getByteArrayExtra(key));
                    Log.i(LOG_TAG, "[" + key + " in hex=" + hex);
                    Log.i(LOG_TAG, "[" + key + " in decimal=" + convertHexToDecimal(hex));
                    Log.i(LOG_TAG, "[" + key + " in ASCII="  + convertHexToString(hex));
                }
            }

        }
    }

}
