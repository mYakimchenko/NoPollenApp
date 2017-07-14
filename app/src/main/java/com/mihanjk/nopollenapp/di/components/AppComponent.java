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

package com.mihanjk.nopollenapp.di.components;


import com.mihanjk.nopollenapp.data.services.DatabaseService;
import com.mihanjk.nopollenapp.di.modules.AppModule;
import com.mihanjk.nopollenapp.di.modules.FirebaseModule;
import com.mihanjk.nopollenapp.di.modules.PreferenceModule;
import com.mihanjk.nopollenapp.di.modules.UserModule;
import com.mihanjk.nopollenapp.presentation.chat.ChatPresenter;
import com.mihanjk.nopollenapp.presentation.login.LoginPresenter;
import com.mihanjk.nopollenapp.presentation.splash.SplashPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, FirebaseModule.class, PreferenceModule.class})
public interface AppComponent {
    UserComponent plusUserComponent(UserModule userModule);

    void inject(LoginPresenter loginPresenter);

    void inject(SplashPresenter splashPresenter);

    // TODO: why i use it?
    void inject(DatabaseService databaseService);

    void inject(ChatPresenter chatPresenter);

}
