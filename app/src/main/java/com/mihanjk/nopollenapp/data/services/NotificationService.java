/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.mihanjk.nopollenapp.data.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.models.User;
import com.mihanjk.nopollenapp.presentation.main.MainActivity;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;


public class NotificationService extends FirebaseMessagingService {
    public static final String TAG = "NotificationService";
    @Inject
    User user;

    public NotificationService() {
        NoPollenApplication.getUserComponent().inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage.getData());
    }

    private void sendNotification(Map<String, String> messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.primary))
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getNotificationContent(messageBody))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private String getNotificationContent(Map<String, String> allergens) {
        // TODO: parse notification
        StringBuilder extraHigh = new StringBuilder();
        StringBuilder high = new StringBuilder();
        StringBuilder medium = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (Entry<String, String> entry : allergens.entrySet()) {
            List<String> userAllergens = user.getAllergens();
            String name = entry.getKey();
            // TODO: user doesn't contains general level
            // TODO: get string from resource
            if (userAllergens.contains(name)) {
                String level = entry.getValue();
                if (level.equals("[\"Extra high\"]")) {
                    extraHigh.append(name).append(", ");
                } else if (level.equals("[\"High\"]")) {
                    high.append(name).append(", ");
                } else if (level.equals("[\"Medium\"]")) {
                    medium.append(name).append(", ");
                }
            }
        }

        if (extraHigh.length() != 0) {
            extraHigh.setLength(extraHigh.length() - 2);
            result.append(getString(R.string.extra_high_level)).append(": ").append(extraHigh)
                    .append("\n");
        }

        if (high.length() != 0) {
            high.setLength(high.length() - 2);
            result.append(getString(R.string.high_level)).append(": ").append(high)
                    .append("\n");
        }

        if (medium.length() != 0) {
            medium.setLength(medium.length() - 2);
            result.append(getString(R.string.medium_level)).append(": ").append(extraHigh)
                    .append("\n");
        }

        if (result.length() == 0) {
            return getString(R.string.no_allergens);
        } else {
            return result.toString();
        }
    }
}
