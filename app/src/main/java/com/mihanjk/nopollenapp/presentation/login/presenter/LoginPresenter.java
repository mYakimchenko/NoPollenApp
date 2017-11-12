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

package com.mihanjk.nopollenapp.presentation.login.presenter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Patterns;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.entity.User;
import com.mihanjk.nopollenapp.data.repository.datasources.DatabaseDataSource;
import com.mihanjk.nopollenapp.data.repository.datasources.PreferencesDataSource;
import com.mihanjk.nopollenapp.domain.interactor.login.LoginInteractor;
import com.mihanjk.nopollenapp.presentation.login.view.LoginView;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {
    @Inject
    LoginInteractor loginInteractor;
    @Inject
    DatabaseDataSource databaseDataSource;
    @Inject
    PreferencesDataSource preferencesDataSource;

    private CompositeDisposable compositeDisposable;

    public LoginPresenter() {
        NoPollenApplication.getAppComponent().inject(this);
    }

    public void loginWithEmail(String email, String password) {
        // TODO refactoring
        if (!isValidEmail(email)) {
            getViewState().showWrongEmail();
            return;
        }

        if (!isValidPassword(password)) {
            getViewState().showWrongPassword();
            return;
        }

        getViewState().showLoading();

        compositeDisposable.add(loginInteractor.emailAuth(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(authResult -> {
                            if (authResult.getUser().isEmailVerified()) {
                                processLogin(authResult);
                            } else {
                                sendEmailVerification();
                            }
                        },
                        throwable -> createAccount(email, password),
                        () -> getViewState().hideLoading()));
    }

    private void sendEmailVerification() {
        compositeDisposable.add(loginInteractor.sendEmailVerification()
                .subscribe(() -> getViewState().showEmailVerification(),
                        throwable -> getViewState().showToastMessage(throwable.getLocalizedMessage())));
    }

    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return !password.isEmpty() && password.length() >= 6;
    }

    private void processLogin(AuthResult authResult) {
        FirebaseUser firebaseUser = authResult.getUser();
        UserInfo userInfo = firebaseUser.getProviderData().get(1);

        final User user = User.newInstance(firebaseUser, userInfo);
        user.setCity(preferencesDataSource.getStringValue(PreferencesDataSource.CITY));
        user.setAllergens(preferencesDataSource.getAllergens());


        // TODO: create method
        databaseDataSource.getUser(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User remoteUser = dataSnapshot.getValue(User.class);
                        if (remoteUser == null) {
                            databaseDataSource.createUser(user);
                            databaseDataSource.updateUserAllergens(user.getAllergens());
                            completeLogIn(user);
                        } else {
                            user.setCity(remoteUser.getCity());
                            databaseDataSource.getUserAllergens(LoginPresenter.this, user);
                            user.setAllergens();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        getViewState().showToastMessage(databaseError.getMessage());
                    }
                });
    }

    public void completeLogIn(User user) {
        loginInteractor.setUserPreferences(user);
        NoPollenApplication.createUserComponent(user);
        getViewState().loginSuccess();
    }

    private void createAccount(String email, String password) {
        compositeDisposable.add(loginInteractor.createAccount(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(authResult -> {
                    getViewState().showEmailVerification();
                    sendEmailVerification();
                }, this::showErrorMessage));
    }

    private void showErrorMessage(Throwable throwable) {
        getViewState().showToastMessage(throwable.getLocalizedMessage());
    }

    public Intent loginWithGoogle() {
        return userService.getUserWithGoogle();
    }

    public void getAuthWithGoogle(GoogleSignInResult result) {
        if (result.isSuccess()) {
            getViewState().showLoading();
            GoogleSignInAccount acct = result.getSignInAccount();
            userService.getAuthWithGoogle(acct)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            getViewState().hideLoading();
                            if (task.isSuccessful()) {
                                processLogin(task);
                            } else {
                                getViewState().showToastMessage(task.getException().getMessage());
                            }
                        }
                    });
        } else {
            getViewState().showToastMessage(result.getStatus().getStatusMessage());
        }
    }

    public CallbackManager loginWithFacebook() {
        CallbackManager callbackManager = userService.getUserWithFacebook();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getAuthWithFacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        getViewState().showToastMessage("Login was cancelled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        getViewState().showToastMessage(error.getMessage());
                    }
                });
        return callbackManager;
    }

    private void getAuthWithFacebook(final AccessToken accessToken) {
        getViewState().showLoading();
        userService.getAuthWithFacebook(accessToken)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        getViewState().hideLoading();
                        if (task.isSuccessful()) {
                            processLogin(task);
                        } else {
                            getViewState().showToastMessage(task.getException().getMessage());
                        }
                    }
                });
    }

    @NonNull
    public Callback<TwitterSession> getTwitterCallback() {
        return new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                getViewState().showToastMessage(exception.getLocalizedMessage());
            }
        };
    }


    private void handleTwitterSession(final TwitterSession session) {
        getViewState().showLoading();
        userService.getAuthWithTwitter(session)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        getViewState().hideLoading();
                        if (task.isSuccessful()) {
                            processLogin(task);
                        } else {
                            getViewState().showToastMessage("Authentication failed.");
                        }
                    }
                });
    }

    public void resetPassword(String email) {
        if (isValidEmail(email)) {
            getViewState().showLoading();
            userService.resetPassword(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    getViewState().hideLoading();
                    if (task.isSuccessful()) {
                        getViewState().resetSuccess();
                    } else {
                        getViewState().showEmailButtonError(task.getException().getLocalizedMessage());
                    }
                }
            });
        } else {
            getViewState().showWrongEmail();
        }
    }


    public void showEmailVerificationSent() {
        getViewState().showEmailVerification();
    }

    // TODO: refactoring
    public void showErrorWhileSending(String message) {
        getViewState().showToastMessage(message);
    }

    public void showErrorLoadingFromDatabase(String message) {
        getViewState().showToastMessage(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
