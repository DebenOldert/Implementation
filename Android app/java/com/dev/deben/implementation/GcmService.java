/*
Copyright 2015 Google Inc. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.dev.deben.implementation;

import android.os.Bundle;
import android.os.SystemClock;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;



/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
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
        int id = fn.notifier(from, "New VPN login request");
        SystemClock.sleep(30000);
        fn.cancelNotify(id);
    }

}