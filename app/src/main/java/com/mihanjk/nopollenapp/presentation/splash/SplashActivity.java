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

import android.content.Intent;
import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mihanjk.nopollenapp.presentation.login.LoginActivity;
import com.mihanjk.nopollenapp.presentation.main.MainActivity;

public class SplashActivity extends MvpAppCompatActivity implements SplashView {

    @InjectPresenter
    SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // By default view attaches to presenter in onResume() method,
        // but we attach it manually for earlier check authorization state.
        getMvpDelegate().onAttach();
    }

    @Override
    public void setAuthorized(boolean isAuthorized) {
        startActivity(new Intent(this, isAuthorized ? MainActivity.class : LoginActivity.class));
        finish();
    }
}
