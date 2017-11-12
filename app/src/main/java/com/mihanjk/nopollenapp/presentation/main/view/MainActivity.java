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

package com.mihanjk.nopollenapp.presentation.main.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.presentation.forecast.view.ForecastFragment;
import com.mihanjk.nopollenapp.presentation.login.view.LoginActivity;
import com.mihanjk.nopollenapp.presentation.main.presenter.MainPresenter;
import com.mihanjk.nopollenapp.presentation.news.view.NewsFragment;
import com.mihanjk.nopollenapp.presentation.settings.view.SettingsFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MvpAppCompatActivity implements MainView {
    public static final String TAG = "MainActivity";
    @InjectPresenter
    MainPresenter mMainPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.left_drawer)
    ListView mDrawerList;
    private boolean doubleBackToExitPressedOnce = false;
    private Drawer mDrawer;

    public static Intent getIntent(final Context context) {
        return new Intent(context, MainActivity.class);
    }

    public void onLogOut() {
        mMainPresenter.logout();
        //TODO check if fragments leak memory and needed to close their before finish activity
        startActivity(LoginActivity.getIntent(this));
        finish();
    }

    @Override
    public void initializeSettingsFragment(SettingsFragment settingsFragment) {
        settingsFragment.init(getMvpDelegate());
    }

    @Override
    public void displayFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        String tag;
        if (fragment instanceof NewsFragment) {
            tag = "news";
        } else {
            tag = "other";
        }
        transaction.replace(R.id.content_frame, fragment, tag);
        transaction.commit();
        mDrawer.closeDrawer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mMainPresenter.subscribeToNotification();
        // TODO: 6/3/2017 refactoring
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background)
                .withSelectionListEnabledForSingleProfile(false)
                .withCompactStyle(true)
                .addProfiles(getProfile())
                .build();

        mDrawer = new DrawerBuilder().withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(headerResult)
                .build();

        insertNavigationDrawerItems();

        mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                mMainPresenter.setNewFragment(iDrawerItem.getIdentifier());
                return true;
            }
        });


        //Manually displaying the first fragment - one time only
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, ForecastFragment.newInstance());
            transaction.commit();
        }
    }

    private ProfileDrawerItem getProfile() {
        ProfileDrawerItem var = new ProfileDrawerItem()
                .withEmail(mMainPresenter.getUserEmail())
                .withName(mMainPresenter.getUserName());
        String userIconLink = mMainPresenter.getUserIconLink();
        if (userIconLink != null) {
            var.withIcon(userIconLink);
        }
        return var;
    }

    private void insertNavigationDrawerItems() {
        mDrawer.addItems(
                new PrimaryDrawerItem().withName(getString(R.string.text_forecast))
                        .withIcon(FontAwesome.Icon.faw_sun_o).withIdentifier(0),
                new PrimaryDrawerItem().withName(getString(R.string.text_chat))
                        .withIcon(FontAwesome.Icon.faw_commenting_o).withIdentifier(1),
                new PrimaryDrawerItem().withName(getString(R.string.text_information))
                        .withIcon(FontAwesome.Icon.faw_info_circle).withIdentifier(2),

                new SectionDrawerItem().withName(getString(R.string.text_news)),

                new PrimaryDrawerItem().withName(getString(R.string.text_about_pollen))
                        .withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(3),
                new PrimaryDrawerItem().withName(getString(R.string.text_children))
                        .withIcon(FontAwesome.Icon.faw_child).withIdentifier(4),
                new PrimaryDrawerItem().withName(getString(R.string.text_clinic))
                        .withIcon(FontAwesome.Icon.faw_medkit).withIdentifier(5),
                new PrimaryDrawerItem().withName(getString(R.string.text_beauty))
                        .withIcon(FontAwesome.Icon.faw_smile_o).withIdentifier(6),
                new PrimaryDrawerItem().withName(getString(R.string.text_life_without_pollen))
                        .withIcon(FontAwesome.Icon.faw_heart_o).withIdentifier(7),
                new PrimaryDrawerItem().withName(getString(R.string.text_recipes))
                        .withIcon(FontAwesome.Icon.faw_cutlery).withIdentifier(8),
                new DividerDrawerItem(),
                new PrimaryDrawerItem().withName(getString(R.string.text_settings))
                        .withIcon(FontAwesome.Icon.faw_cog).withIdentifier(9),

                new DividerDrawerItem(),
                new PrimaryDrawerItem().withName(getString(R.string.text_log_out))
                        .withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(10));
    }

    @Override
    public void onBackPressed() {
        // TODO: 6/3/2017 try to get off from handler
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
            return;
        }

        // TODO: replace with property name current fragment
        NewsFragment fragment = (NewsFragment) getFragmentManager().findFragmentByTag("news");
        if (fragment != null && fragment.canGoBack()) {
            fragment.goBack();
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.message_before_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
