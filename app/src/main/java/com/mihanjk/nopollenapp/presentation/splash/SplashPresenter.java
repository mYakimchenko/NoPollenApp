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

package com.mihanjk.nopollenapp.presentation.splash;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.models.User;
import com.mihanjk.nopollenapp.data.services.DatabaseService;
import com.mihanjk.nopollenapp.data.services.PreferencesService;

import javax.inject.Inject;

public class SplashPresenter extends MvpPresenter<SplashView> {
    @Inject
    PreferencesService preferencesService;
    @Inject
    DatabaseService databaseService;
    @Inject
    ConnectivityManager connectivityManager;

    public SplashPresenter() {
        NoPollenApplication.getAppComponent().inject(this);
    }

    @Override
    public void attachView(final SplashView view) {
        super.attachView(view);

        if (preferencesService.isUserExist()) {
            User userFromPreferences = preferencesService.getUserFromPreferences();
            // TODO: refactor
            NoPollenApplication.createUserComponent(userFromPreferences);
            view.setAuthorized(true);
        } else if (!isOffline()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                databaseService.getUser(currentUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User remoteUser = dataSnapshot.getValue(User.class);
                                if (remoteUser == null) {
                                    view.setAuthorized(false);
                                } else {
                                    preferencesService.setUserPreferences(remoteUser);
                                    NoPollenApplication.createUserComponent(remoteUser);
                                    view.setAuthorized(true);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                view.setAuthorized(false);
                            }
                        });
            } else {
                view.setAuthorized(false);
            }
        } else {
            view.setAuthorized(false);
        }
    }

    private boolean isOffline() {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo == null || !netInfo.isConnected();
    }
}
