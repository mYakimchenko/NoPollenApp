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

package com.mihanjk.nopollenapp.data.services;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.models.User;
import com.mihanjk.nopollenapp.presentation.login.LoginPresenter;
import com.mihanjk.nopollenapp.presentation.settings.SettingsPresenter;
import com.twitter.sdk.android.core.TwitterSession;

public class UserService {
    // TODO: try remove applicationContext
    private Context applicationContext;

    private FirebaseAuth firebaseAuth;

    // TODO maybe memory leaks cause of activity
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    // facebook
    private CallbackManager callbackManager;

    public UserService(Context applicationContext) {
        this.applicationContext = applicationContext;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> getUserWithEmail(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> createUserWithEmail(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Intent getUserWithGoogle() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(applicationContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // TODO onConnectionFailedListener
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            googleApiClient = new GoogleApiClient.Builder(applicationContext)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }

    public Task<AuthResult> getAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        return firebaseAuth.signInWithCredential(credential);
    }

    public CallbackManager getUserWithFacebook() {
        callbackManager = CallbackManager.Factory.create();
        return callbackManager;
    }

    public Task<AuthResult> getAuthWithFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        return firebaseAuth.signInWithCredential(credential);
    }

    public Task<AuthResult> getAuthWithTwitter(TwitterSession session) {
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);
        return firebaseAuth.signInWithCredential(credential);
    }

    public void logOut(User user) {
        firebaseAuth.signOut();
        String provider = user.getProvider();
        // TODO may be null. When?
        switch (provider) {
            case "facebook.com":
                if (!FacebookSdk.isInitialized()) {
                    FacebookSdk.sdkInitialize(applicationContext);
                }
                LoginManager.getInstance().logOut();
                firebaseAuth.signOut();
                break;
            case "google.com":
                googleLogOut();
                break;
        }
    }

    private void googleLogOut() {
        // TODO: refactoring
        if (gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(applicationContext.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
        }

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(applicationContext)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        googleApiClient.connect();
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Auth.GoogleSignInApi.signOut(googleApiClient);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
    }

    //     TODO: use it in settings fragment
    public void updateUserName(final SettingsPresenter presenter, String name) {
        UserProfileChangeRequest builder = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        firebaseAuth.getCurrentUser().updateProfile(builder)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // TODO:
//                            presenter.showSuccessUpdate();
                        } else {
                            // TODO:
//                            presenter.showErrorWhileUpdate(task.getException().getMessage());
                        }
                    }
                });
    }

    public Task<Void> resetPassword(String email) {
        return firebaseAuth.sendPasswordResetEmail(email);
    }

    public void sendEmailVerification(final LoginPresenter presenter) {
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    presenter.showEmailVerificationSent();
                } else {
                    presenter.showErrorWhileSending(task.getException().getMessage());
                }
            }
        });
    }

    public boolean isUserEmailVerified() {
        return firebaseAuth.getCurrentUser().isEmailVerified();
    }

    public void updateUserPassword(final SettingsPresenter presenter, String email, String oldPassword, final String newPassword) {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        // todo try refactoring this
        currentUser.reauthenticate(EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                presenter.showMessage("Password updated");
                                            } else {
                                                presenter.showMessage("Updating failed" + task.getException().getMessage());
                                            }
                                        }
                                    });
                        } else {
                            presenter.showMessage("Authentification error: " + task.getException().getMessage());
                        }
                    }
                });
    }
}
