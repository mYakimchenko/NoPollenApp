package com.mihanjk.nopollenapp.presentation.login;


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
import com.mihanjk.nopollenapp.data.models.User;
import com.mihanjk.nopollenapp.data.services.DatabaseService;
import com.mihanjk.nopollenapp.data.services.PreferencesService;
import com.mihanjk.nopollenapp.data.services.UserService;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import javax.inject.Inject;

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {
    @Inject
    UserService userService;
    @Inject
    DatabaseService databaseService;
    @Inject
    PreferencesService preferencesService;

    public LoginPresenter() {
        NoPollenApplication.getAppComponent().inject(this);
    }

    public void loginWithEmail(final String email, final String password) {
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

        userService.getUserWithEmail(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (!userService.isUserEmailVerified()) {
                                userService.sendEmailVerification(LoginPresenter.this);
                                getViewState().showEmailVerification();
                            } else {
                                processLogin(task);
                            }
                        } else {
                            createAccount(email, password);
                        }
                        getViewState().hideLoading();
                    }
                });
    }

    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return !password.isEmpty() && password.length() >= 6;
    }

    private void processLogin(Task<AuthResult> task) {
        FirebaseUser firebaseUser = task.getResult().getUser();
        UserInfo userInfo = firebaseUser.getProviderData().get(1);

        final User user = User.newInstance(firebaseUser, userInfo);
        user.setCity(preferencesService.getStringValue(PreferencesService.CITY));
        user.setAllergens(preferencesService.getAllergens());


        // TODO: create method
        databaseService.getUser(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User remoteUser = dataSnapshot.getValue(User.class);
                        if (remoteUser == null) {
                            databaseService.createUser(user);
                            databaseService.updateUserAllergens(user.getAllergens());
                            completeLogIn(user);
                        } else {
                            user.setCity(remoteUser.getCity());
                            databaseService.getUserAllergens(LoginPresenter.this, user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        getViewState().showToastMessage(databaseError.getMessage());
                    }
                });
    }

    public void completeLogIn(User user) {
        preferencesService.setUserPreferences(user);
        NoPollenApplication.createUserComponent(user);
        getViewState().loginSuccess();
    }

    private void createAccount(String email, String password) {
        userService.createUserWithEmail(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getViewState().showEmailVerification();
                            userService.sendEmailVerification(LoginPresenter.this);
                        } else {
                            getViewState().showToastMessage(task.getException().getMessage());
                        }
                    }
                });
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
}
