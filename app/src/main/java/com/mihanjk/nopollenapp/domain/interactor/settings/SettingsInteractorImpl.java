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

package com.mihanjk.nopollenapp.domain.interactor.settings;


import com.mihanjk.nopollenapp.repositories.user.UserRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;

public class SettingsInteractorImpl implements SettingsInteractor {
    private UserRepository userRepository;

    @Inject
    public SettingsInteractorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Completable updateUserPassword(String oldPassword, String newPassword) {
        return userRepository.updateUserPassword(oldPassword, newPassword);

    }

    @Override
    public Completable updateUserName(String name) {
        return userRepository.updateUserName(name);
    }

    @Override
    public Completable updateUserCity(String city) {
        return userRepository.updateUserCity(city);
    }

    @Override
    public boolean isNotificationEnabled() {
        return userRepository.isNotificationEnabled();
    }

    @Override
    public Completable updateUserAllergens(List<String> listAllergens) {
        return userRepository.updateUserAllergens(listAllergens);
    }
}
