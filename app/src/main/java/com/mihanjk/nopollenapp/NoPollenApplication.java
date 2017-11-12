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

package com.mihanjk.nopollenapp;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mihanjk.nopollenapp.data.entity.User;
import com.mihanjk.nopollenapp.di.components.AppComponent;
import com.mihanjk.nopollenapp.di.components.DaggerAppComponent;
import com.mihanjk.nopollenapp.di.components.UserComponent;
import com.mihanjk.nopollenapp.di.modules.AppModule;
import com.mihanjk.nopollenapp.di.modules.UserModule;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.twitter.sdk.android.core.Twitter;

import io.flowup.FlowUp;

public class NoPollenApplication extends android.app.Application {
    private static AppComponent appComponent;
    private static UserComponent userComponent;
    private static ConnectivityManager connectivityManager;


    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static UserComponent createUserComponent(User user) {
        userComponent = appComponent.plusUserComponent(new UserModule(user));
        return userComponent;
    }

    public static UserComponent getUserComponent() {
        return userComponent;
    }

    public static void releaseUserComponent() {
        userComponent = null;
    }

    // TODO: 7/20/2017 where is this must be?
    public static boolean isOffline() {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo == null || !netInfo.isConnected();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO try move initialization of twitter api into login presenter
        Twitter.initialize(this);

        appComponent = buildAppComponent();
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        initializeDrawImageLoader();

        initializeFlowUp();

        // TODO: decide when i need to set default values from preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    private void initializeFlowUp() {
        FlowUp.Builder.with(this)
                .apiKey("9ba677819a7d416f91ad64bab335a3a5")
                .forceReports(BuildConfig.DEBUG)
                .start();
    }

    private void initializeDrawImageLoader() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext()).load(uri).into(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return new IconicsDrawable(ctx).icon(FontAwesome.Icon.faw_user_circle_o).color(-1);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.with(imageView.getContext()).clear(imageView);
            }
        });
    }

    private AppComponent buildAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
