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

package com.mihanjk.nopollenapp.repositories.user;

import com.google.firebase.auth.AuthResult;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.entity.User;
import com.mihanjk.nopollenapp.data.repository.datasources.DatabaseDataSource;
import com.mihanjk.nopollenapp.data.repository.datasources.PreferencesDataSource;
import com.mihanjk.nopollenapp.data.repository.datasources.UserDataSource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public class UserRepositoryImpl implements UserRepository {
    private UserDataSource userDataSource;
    private DatabaseDataSource databaseDataSource;
    private PreferencesDataSource preferencesDataSource;

    @Inject
    public UserRepositoryImpl(UserDataSource userDataSource,
                              DatabaseDataSource databaseDataSource,
                              PreferencesDataSource preferencesDataSource) {
        this.userDataSource = userDataSource;
        this.preferencesDataSource = preferencesDataSource;
        this.databaseDataSource = databaseDataSource;
    }

    @Override
    public Maybe<AuthResult> getEmailUser(String email, String password) {
        return userDataSource.getUserWithEmail(email, password);
    }

    @Override
    public Completable sendEmailVerification() {
        return userDataSource.sendEmailVerification();
    }

    @Override
    public Maybe<AuthResult> createAccount(String email, String password) {
        return userDataSource.createUserWithEmail(email, password);
    }

    @Override
    public User getUserInstance() {
        if (preferencesDataSource.isUserExist()) {
            return preferencesDataSource.getUserFromPreferences();
        } else if (NoPollenApplication.isOffline()) {
            return null;
        } else {
            // TODO: 7/20/2017 check if this work asynchronous?
            User user = databaseDataSource.getUserInstance().blockingGet(null);
            // TODO: 7/23/2017 where i need to treat this case
            if (user != null) {
                preferencesDataSource.setUserPreferences(user);
            }
            return user;
        }
    }

    @Override
    public Completable updateUserPassword(String oldPassword, String newPassword) {
        return userDataSource.updateUserPassword(oldPassword, newPassword);
    }

    @Override
    public Completable updateUserName(String name) {
        return userDataSource.updateUserName(name);
    }

    @Override
    public Completable updateUserCity(String city) {
        return databaseDataSource.updateUserCity(city);
    }

    @Override
    public boolean isNotificationEnabled() {
        return preferencesDataSource.isNotificationEnabled();
    }

    @Override
    public Completable updateUserAllergens(List<String> listAllergens) {
        return databaseDataSource.updateUserAllergens(listAllergens);
    }
}
