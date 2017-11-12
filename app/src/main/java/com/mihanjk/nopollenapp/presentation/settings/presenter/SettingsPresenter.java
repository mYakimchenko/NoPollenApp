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

package com.mihanjk.nopollenapp.presentation.settings.presenter;


import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.entity.User;
import com.mihanjk.nopollenapp.domain.interactor.settings.SettingsInteractor;
import com.mihanjk.nopollenapp.presentation.settings.view.SettingsView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class SettingsPresenter extends MvpPresenter<SettingsView> {
    @Inject
    SettingsInteractor settingsInteractor;

    // TODO: 7/24/2017 need to remove this?
    @Inject
    User user;

    public SettingsPresenter() {
        // TODO: why user component null?
        NoPollenApplication.getUserComponent().inject(this);
    }

    public void customizePreference() {
        if (!user.getProvider().equals("password")) {
            getViewState().hideEmailPreference();
        }
    }

    public void updateUserPassword(String oldPassword, String newPassword) {
        settingsInteractor.updateUserPassword(oldPassword, newPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> showMessage(R.string.password_updated),
                        throwable -> showMessage(throwable.getLocalizedMessage()));
    }

    public void showMessage(String message) {
        getViewState().showMessage(message);
    }

    public void showMessage(int resourceId) {
        getViewState().showMessage(resourceId);
    }

    public void updateUserName(String name) {
        if (!name.isEmpty()) {
            settingsInteractor.updateUserName(name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                                user.setName(name);
                                showMessage(R.string.name_updated);
                            },
                            throwable -> showMessage(throwable.getLocalizedMessage()));
        } else {
            getViewState().showMessage(R.string.empty_name_message);
        }
    }

    public void toggleNotification(boolean enable) {
        // TODO: when i need subscribe first time?
        if (enable) {
            FirebaseMessaging.getInstance().subscribeToTopic(user.getCity());
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getCity());
        }
    }

    public void updateCity(String city) {
        settingsInteractor.updateUserCity(city)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            user.setCity(city);
                            showMessage(R.string.city_updated);
                        },
                        throwable -> showMessage(throwable.getLocalizedMessage()));
        // TODO: 7/24/2017 move to interactor
        if (settingsInteractor.isNotificationEnabled()) {
            FirebaseMessaging.getInstance().subscribeToTopic(city);
        }
    }

    public void updateAllergens(Set<String> allergens) {
        List<String> listAllergens = new ArrayList<>(allergens);
        settingsInteractor.updateUserAllergens(listAllergens)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            user.setAllergens(listAllergens);
                            showMessage(R.string.allergens_updated);
                        },
                        throwable -> showMessage(throwable.getLocalizedMessage()));
    }
}
