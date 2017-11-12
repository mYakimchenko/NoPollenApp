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

package com.mihanjk.nopollenapp.presentation.settings.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.presentation.main.view.MainActivity;
import com.mihanjk.nopollenapp.presentation.settings.presenter.SettingsPresenter;

public class SettingsFragment extends PreferenceFragment implements SettingsView,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int REQUEST_PASSWORDS = 1;

    @InjectPresenter
    SettingsPresenter mSettingsPresenter;

    private MvpDelegate mParentDelegate;
    private MvpDelegate<SettingsFragment> mMvpDelegate;

    public static SettingsFragment newInstance() {

        SettingsFragment fragment = new SettingsFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    // TODO: call this method after activity destroy
    public void init(MvpDelegate parentDelegate) {
        mParentDelegate = parentDelegate;
        initMvpDelegate();

        mMvpDelegate.onCreate();
        mMvpDelegate.onAttach();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mMvpDelegate.onSaveInstanceState();
        mMvpDelegate.onDetach();
    }

    public void initMvpDelegate() {
        mMvpDelegate = new MvpDelegate<>(this);
        mMvpDelegate.setParentDelegate(mParentDelegate, String.valueOf(getId()));
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        activity.initializeSettingsFragment(this);
        addPreferencesFromResource(R.xml.preferences);
        mSettingsPresenter.customizePreference();
    }

    @Override
    public void hideEmailPreference() {
        // TODO: 7/11/2017 no fragment after switch to another
        PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference(getString(R.string.preference_screen));
        // TODO: refactoring
        if (preferenceScreen != null) {
            Preference preference = findPreference(getString(R.string.email_preference));
            preferenceScreen.removePreference(preference);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(int resourceId) {
        showMessage(getString(resourceId));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        // TODO: 7/23/2017 why this code duplicated?
        if (preference.getKey().equals(getString(R.string.change_password_preference))) {
            DialogFragment fragment = new ChangePasswordDialogFragment();
            fragment.setTargetFragment(this, REQUEST_PASSWORDS);
            fragment.show(getFragmentManager(), fragment.getClass().getName());
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO: 7/23/2017 why this code duplicated?
        if (key.equals(getString(R.string.change_password_preference))) {
            DialogFragment fragment = new ChangePasswordDialogFragment();
            fragment.setTargetFragment(this, REQUEST_PASSWORDS);
            fragment.show(getFragmentManager(), fragment.getClass().getName());
        } else if (key.equals(getString(R.string.update_name_preference))) {
            mSettingsPresenter.updateUserName(((EditTextPreference) findPreference(key)).getText());
        } else if (key.equals(getString(R.string.preference_notification))) {
            mSettingsPresenter.toggleNotification(sharedPreferences.getBoolean(key, true));
        } else if (key.equals(getString(R.string.preference_allergens))) {
            mSettingsPresenter.updateAllergens(((MultiSelectListPreference) findPreference(key)).getValues());
        } else if (key.equals(getString(R.string.preference_city))) {
            mSettingsPresenter.updateCity(((ListPreference) findPreference(key)).getValue());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: test it
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PASSWORDS) {
            String oldPassword = data.getStringExtra(ChangePasswordDialogFragment.OLD_PASSWORD);
            String newPassword = data.getStringExtra(ChangePasswordDialogFragment.NEW_PASSWORD);
            String confirmPassword = data.getStringExtra(ChangePasswordDialogFragment.CONFIRM_PASSWORD);
            // TODO: 7/23/2017 move login into presenter or interactor
            if (newPassword.equals(confirmPassword)) {
                mSettingsPresenter.updateUserPassword(oldPassword, newPassword);
            } else {
                showMessage(getString(R.string.passwords_not_match));
            }
        }
    }
}
