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

package com.mihanjk.nopollenapp.domain.interactor.login;


import com.google.firebase.auth.AuthResult;
import com.mihanjk.nopollenapp.repositories.user.UserRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public class LoginInteractorImpl implements LoginInteractor {
    private UserRepository userRepository;

    @Inject
    public LoginInteractorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Maybe<AuthResult> emailAuth(String email, String password) {
        return userRepository.getEmailUser(email, password);
    }

    @Override
    public Completable sendEmailVerification() {
        return userRepository.sendEmailVerification();
    }

    @Override
    public Maybe<AuthResult> createAccount(String email, String password) {
        return userRepository.createAccount(email, password);
    }
}
