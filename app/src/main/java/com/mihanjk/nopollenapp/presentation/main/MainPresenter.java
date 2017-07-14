package com.mihanjk.nopollenapp.presentation.main;


import android.app.Fragment;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.models.User;
import com.mihanjk.nopollenapp.data.services.PreferencesService;
import com.mihanjk.nopollenapp.data.services.UserService;
import com.mihanjk.nopollenapp.presentation.chat.ChatFragment;
import com.mihanjk.nopollenapp.presentation.forecast.ForecastFragment;
import com.mihanjk.nopollenapp.presentation.news.NewsFragment;
import com.mihanjk.nopollenapp.presentation.settings.SettingsFragment;

import javax.inject.Inject;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    public static final String INFORMATION = "http://allergotop.com/allergoteka";
    public static final String ABOUT_POLLEN = "http://allergotop.com/allergoefir/pro-pyltsu";
    public static final String CHILDREN = "http://allergotop.com/allergoefir/deti";
    public static final String CLINIC = "http://allergotop.com/allergoefir/ambulatoriya";
    public static final String BEAUTY = "http://allergotop.com/allergoefir/krasota";
    public static final String LIFE_WITHOUT_POLLEN = "http://allergotop.com/allergoefir/zhizn-bez-allergii";
    public static final String RECIPES = "http://allergotop.com/allergoefir/gipoallergennye-retsepty";

    @Inject
    PreferencesService preferencesService;

    @Inject
    UserService userService;

    @Inject
    User user;

    public MainPresenter() {
        NoPollenApplication.getUserComponent().inject(this);
    }

    public String getUserName() {
        return user.getName();
    }

    public String getUserEmail() {
        return user.getEmail();
    }

    public String getUserIconLink() {
        return user.getPhotoUrl();
    }

    public void logout() {
        preferencesService.deleteUser();
        userService.logOut(user);
        NoPollenApplication.releaseUserComponent();
    }

    public Fragment getFragment(long identifier) {
        Fragment selectedFragment;
        if (identifier == 0) {
            selectedFragment = ForecastFragment.newInstance();
        } else if (identifier == 1) {
            selectedFragment = ChatFragment.newInstance();
        } else if (identifier == 2) {
            selectedFragment = NewsFragment.newInstance(INFORMATION);
        } else if (identifier == 3) {
            selectedFragment = NewsFragment.newInstance(ABOUT_POLLEN);
        } else if (identifier == 4) {
            selectedFragment = NewsFragment.newInstance(CHILDREN);
        } else if (identifier == 5) {
            selectedFragment = NewsFragment.newInstance(CLINIC);
        } else if (identifier == 6) {
            selectedFragment = NewsFragment.newInstance(BEAUTY);
        } else if (identifier == 7) {
            selectedFragment = NewsFragment.newInstance(LIFE_WITHOUT_POLLEN);
        } else if (identifier == 8) {
            selectedFragment = NewsFragment.newInstance(RECIPES);
        } else if (identifier == 9) {
            SettingsFragment settingsFragment = SettingsFragment.newInstance();
//            getViewState().initializeSettingsFragment(settingsFragment);
            selectedFragment = settingsFragment;
        } else if (identifier == 10) {
            getViewState().onLogOut();
            return null;
        } else {
            return null;
        }
        return selectedFragment;
    }

    public void subscribeToNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(preferencesService.getCity());
    }

    public void setNewFragment(long identifier) {
        Fragment fragment = getFragment(identifier);
        if (fragment != null) {
            getViewState().displayFragment(fragment);
        }
    }
}
