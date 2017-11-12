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

package com.mihanjk.nopollenapp.data.entity;


import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class User {
    @Exclude
    String uid;
    String email;
    String provider;
    String photoUrl;
    String name;
    String city;
    @Exclude
    @NonNull
    List<String> allergens;

    public User() {
    }

    private User(String uid) {
        this.uid = uid;
    }

    public User(String uid, String email, String name, String photoUrl, String provider, List<String> allergens, String city) {
        this.uid = uid;
        this.email = email;
        this.provider = provider;
        this.photoUrl = photoUrl;
        this.name = name;
        this.allergens = allergens;
        this.city = city;
    }

    public static User newInstance(FirebaseUser firebaseUser, UserInfo provider) {
        User user = new User(firebaseUser.getUid());
        String providerId = provider.getProviderId();
        user.setProvider(providerId);
        if (providerId.equals("password") || providerId.equals("google.com")) {
            user.setEmail(firebaseUser.getEmail());
        }

        if (!providerId.equals("password")) {
            user.setName(provider.getDisplayName());
            user.setPhotoUrl(provider.getPhotoUrl().toString());
        }
        return user;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Exclude
    @NonNull
    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Exclude
    @NonNull
    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(@NonNull List<String> allergens) {
        this.allergens = allergens;
    }
}
