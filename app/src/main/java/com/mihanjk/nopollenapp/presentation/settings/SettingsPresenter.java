package com.mihanjk.nopollenapp.presentation.settings;


import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.models.User;
import com.mihanjk.nopollenapp.data.services.DatabaseService;
import com.mihanjk.nopollenapp.data.services.PreferencesService;
import com.mihanjk.nopollenapp.data.services.UserService;

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;

@InjectViewState
public class SettingsPresenter extends MvpPresenter<SettingsView> {
    @Inject
    UserService userService;
    @Inject
    DatabaseService databaseService;
    @Inject
    PreferencesService preferencesService;

    @Inject
    User user;

    public SettingsPresenter() {
        // TODO: why user component null?
        NoPollenApplication.getUserComponent().inject(this);
    }

    public void customizePreference() {
        if (!user.getProvider().equals("password")) {
            getViewState().hideEmailPreference();
        }
    }

    public void updateUserPassword(String oldPassword, String newPassword) {
        userService.updateUserPassword(this, user.getEmail(), oldPassword, newPassword);
    }

    public void showMessage(String message) {
        getViewState().showMessage(message);
    }

    public void updateUserName(String name) {
        if (!name.isEmpty()) {
            user.setName(name);
            userService.updateUserName(this, name);
        } else {
            getViewState().showMessage("Can't set empty name");
        }
    }

    public void toggleNotification(boolean enable) {
        // TODO: when i need subscribe first time?
        if (enable) {
            FirebaseMessaging.getInstance().subscribeToTopic(user.getCity());
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getCity());
        }
    }

    public void updateCity(String city) {
        // TODO: update user if successful
        databaseService.updateUserCity(city);
        user.setCity(city);
        if (preferencesService.getNotification()) {
            FirebaseMessaging.getInstance().subscribeToTopic(city);
        }
    }

    public void updateAllergens(Set<String> allergens) {
        // TODO: update user if successful
        ArrayList<String> listAllergens = new ArrayList<>(allergens);
        databaseService.updateUserAllergens(listAllergens);
        user.setAllergens(listAllergens);
    }
}
