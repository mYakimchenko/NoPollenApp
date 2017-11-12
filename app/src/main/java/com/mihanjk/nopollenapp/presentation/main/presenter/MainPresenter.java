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

package com.mihanjk.nopollenapp.presentation.main.presenter;


import android.app.Fragment;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.entity.User;
import com.mihanjk.nopollenapp.data.repository.datasources.PreferencesDataSource;
import com.mihanjk.nopollenapp.data.repository.datasources.UserDataSource;
import com.mihanjk.nopollenapp.presentation.chat.view.ChatFragment;
import com.mihanjk.nopollenapp.presentation.forecast.view.ForecastFragment;
import com.mihanjk.nopollenapp.presentation.main.view.MainView;
import com.mihanjk.nopollenapp.presentation.news.view.NewsFragment;
import com.mihanjk.nopollenapp.presentation.settings.view.SettingsFragment;

import javax.inject.Inject;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    public static final String INFORMATION = "http://allergotop.com/allergoteka";
    public static final String ABOUT_POLLEN = "http://allergotop.com/allergoefir/pro-pyltsu";
    public static final String CHILDREN = "http://allergotop.com/allergoefir/deti";
    public static final String CLINIC = "http://allergotop.com/allergoefir/ambulatoriya";
    public static final String BEAUTY = "http://allergotop.com/allergoefir/krasota";
    public static final String LIFE_WITHOUT_POLLEN = "http://allergotop.com/allergoefir/zhizn-bez-allergii";
    public static final String RECIPES = "http://allergotop.com/allergoefir/gipoallergennye-retsepty";

    @Inject
    PreferencesDataSource preferencesDataSource;

    @Inject
    UserDataSource userService;

    @Inject
    User user;

    public MainPresenter() {
        NoPollenApplication.getUserComponent().inject(this);
    }

    public String getUserName() {
        return user.getName();
    }

    public String getUserEmail() {
        return user.getEmail();
    }

    public String getUserIconLink() {
        return user.getPhotoUrl();
    }

    public void logout() {
        preferencesDataSource.deleteUser();
        userService.logOut(user);
        NoPollenApplication.releaseUserComponent();
    }

    public Fragment getFragment(long identifier) {
        Fragment selectedFragment;
        if (identifier == 0) {
            selectedFragment = ForecastFragment.newInstance();
        } else if (identifier == 1) {
            selectedFragment = ChatFragment.newInstance();
        } else if (identifier == 2) {
            selectedFragment = NewsFragment.newInstance(INFORMATION);
        } else if (identifier == 3) {
            selectedFragment = NewsFragment.newInstance(ABOUT_POLLEN);
        } else if (identifier == 4) {
            selectedFragment = NewsFragment.newInstance(CHILDREN);
        } else if (identifier == 5) {
            selectedFragment = NewsFragment.newInstance(CLINIC);
        } else if (identifier == 6) {
            selectedFragment = NewsFragment.newInstance(BEAUTY);
        } else if (identifier == 7) {
            selectedFragment = NewsFragment.newInstance(LIFE_WITHOUT_POLLEN);
        } else if (identifier == 8) {
            selectedFragment = NewsFragment.newInstance(RECIPES);
        } else if (identifier == 9) {
            SettingsFragment settingsFragment = SettingsFragment.newInstance();
//            getViewState().initializeSettingsFragment(settingsFragment);
            selectedFragment = settingsFragment;
        } else if (identifier == 10) {
            getViewState().onLogOut();
            return null;
        } else {
            return null;
        }
        return selectedFragment;
    }

    public void subscribeToNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(preferencesDataSource.getCity());
    }

    public void setNewFragment(long identifier) {
        Fragment fragment = getFragment(identifier);
        if (fragment != null) {
            getViewState().displayFragment(fragment);
        }
    }
}
