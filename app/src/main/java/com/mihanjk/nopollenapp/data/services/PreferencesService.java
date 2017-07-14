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


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferencesService {
    public static final String UID = "Uid";
    public static final String EMAIL = "Email";
    public static final String NAME = "Name";
    public static final String CITY = "city";
    public static final String PHOTO_URL = "PhotoURL";
    public static final String PROVIDER = "Provider";
    public static final String ALLERGENS = "Allergens";
    public static final String NOTIFICATION = "Notification";
    private static final String DEFAULT_CITY = "Moscow";
    private final Set<String> defaultAllergens;
    private SharedPreferences sharedPreferences;
    private Context appContext;

    public PreferencesService(Context context) {
        appContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String[] stringArray = appContext.getResources().getStringArray(R.array.allergens);
        defaultAllergens = new HashSet<>(Arrays.asList(stringArray));
    }

    public void setUserPreferences(User user) {
        sharedPreferences.edit()
                .putString(UID, user.getUid())
                .putString(EMAIL, user.getEmail())
                .putString(NAME, user.getName())
                .putString(CITY, user.getCity())
                .putString(PHOTO_URL, user.getPhotoUrl())
                .putString(PROVIDER, user.getProvider())
                .putStringSet(ALLERGENS, new HashSet<>(user.getAllergens()))
                .apply();
    }

    public boolean isUserExist() {
        return sharedPreferences.getString(UID, null) != null;
    }

    public User getUserFromPreferences() {
        String uid = sharedPreferences.getString(UID, null);
        String email = sharedPreferences.getString(EMAIL, null);
        String name = sharedPreferences.getString(NAME, null);
        String photoURL = sharedPreferences.getString(PHOTO_URL, null);
        String provider = sharedPreferences.getString(PROVIDER, null);
        Set<String> allergens = sharedPreferences.getStringSet(ALLERGENS, defaultAllergens);
        String city = sharedPreferences.getString(CITY, DEFAULT_CITY);
        return new User(uid, email, name, photoURL, provider, new ArrayList<>(allergens), city);
    }

    public void setStringValue(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getStringValue(String key) {
        return sharedPreferences.getString(key, null);
    }

    // TODO: restore default values preferences after log out
    public String getCity() {
        return sharedPreferences.getString(CITY, DEFAULT_CITY);
    }

    public List<String> getAllergens() {
        // TODO: what can appear if return null?
        return new ArrayList<>(sharedPreferences.getStringSet(ALLERGENS, defaultAllergens));
    }

    public void setAllergens(Set<String> allergens) {
        sharedPreferences.edit().putStringSet(ALLERGENS, allergens).apply();
    }

    public boolean getNotification() {
        return sharedPreferences.getBoolean(NOTIFICATION, true);
    }

    public void deleteUser() {
        sharedPreferences.edit().clear().apply();
    }
}
