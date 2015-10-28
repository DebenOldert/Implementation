/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 *
 * Created by Deben Oldert
 */

package com.dev.deben.implementation;

import android.os.Bundle;
import android.os.SystemClock;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;

import java.io.IOException;

public class GcmService extends GcmListenerService {

    function fn = new function(this);

    @Override
    public void onMessageReceived(String from, Bundle data) {
        System.out.println("Msg from: " + from + ", " + data);
        try {
            fn.writeSetting("requestId", data.getString("requestId"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        int id = fn.notifier("Access control", "New VPN login request");
        SystemClock.sleep(30000);
        fn.cancelNotify(id);
    }

}