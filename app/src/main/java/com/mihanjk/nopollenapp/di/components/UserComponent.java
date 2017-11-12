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


import com.mihanjk.nopollenapp.data.repository.datasources.NotificationDataSource;
import com.mihanjk.nopollenapp.di.modules.UserModule;
import com.mihanjk.nopollenapp.di.scope.UserScope;
import com.mihanjk.nopollenapp.presentation.forecast.presenter.ForecastPresenter;
import com.mihanjk.nopollenapp.presentation.main.presenter.MainPresenter;
import com.mihanjk.nopollenapp.presentation.message.presenter.MessagePresenter;
import com.mihanjk.nopollenapp.presentation.settings.presenter.SettingsPresenter;

import dagger.Subcomponent;

@UserScope
@Subcomponent(modules = {UserModule.class})
public interface UserComponent {
    void inject(SettingsPresenter settingsPresenter);

    void inject(MessagePresenter messagePresenter);

    void inject(MainPresenter mainPresenter);

    void inject(ForecastPresenter forecastPresenter);

    void inject(NotificationDataSource notificationDataSource);
}
