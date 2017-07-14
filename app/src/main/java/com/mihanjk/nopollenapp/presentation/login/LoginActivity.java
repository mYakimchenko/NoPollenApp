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

package com.mihanjk.nopollenapp.presentation.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dd.processbutton.iml.ActionProcessButton;
import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.presentation.main.MainActivity;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends MvpAppCompatActivity implements LoginView {
    public static final String TAG = "LoginActivity";
    public static final int REQUEST_SIGN_GOOGLE = 9001;
    @InjectPresenter
    LoginPresenter mLoginPresenter;
    @BindView(R.id.email_edit_text)
    EditText mEmailEditText;
    @BindView(R.id.password_edit_text)
    EditText mPasswordEditText;
    //TODO remove this and on create twitter api
    @BindView(R.id.twitter_login_button)
    TwitterLoginButton mTwitterLoginButton;
    @BindView(R.id.password_text_input_layout)
    TextInputLayout mPasswordLayout;
    @BindView(R.id.email_text_input_layout)
    TextInputLayout mEmailLayout;
    // TODO: update button state in presenter
    @BindView(R.id.email_sign_in_button)
    ActionProcessButton mEmailButton;
    // just for facebook login
    private CallbackManager callbackManager;

    public static Intent getIntent(final Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        mEmailButton.setMode(ActionProcessButton.Mode.ENDLESS);

        mTwitterLoginButton.setCallback(mLoginPresenter.getTwitterCallback());
    }

    @OnClick(R.id.email_sign_in_button)
    public void onBtnEmail(View view) {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        mLoginPresenter.loginWithEmail(email, password);
    }

    @Override
    public void showLoading() {
        mEmailButton.setProgress(1);
    }

    @Override
    public void hideLoading() {
        mEmailButton.setProgress(0);
    }

    @Override
    public void showEmailButtonError(String errorMessage) {
        mEmailButton.setError(errorMessage);
    }

    @OnClick(R.id.reset_password_text_view)
    public void onResetTextView() {
        mLoginPresenter.resetPassword(mEmailEditText.getText().toString());
    }

    @OnClick(R.id.google_login_button)
    public void onBtnLoginWithGoogle() {
        startActivityForResult(mLoginPresenter.loginWithGoogle(), REQUEST_SIGN_GOOGLE);
    }

    @OnClick(R.id.facebook_login_button)
    public void onBtnLoginWithFacebook() {
        callbackManager = mLoginPresenter.loginWithFacebook();
    }


    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showWrongEmail() {
        mEmailEditText.setError(getString(R.string.email_error));
    }

    // TODO remove when password become right
    @Override
    public void showWrongPassword() {
        mPasswordLayout.setError(getString(R.string.password_error));
    }

    @Override
    public void loginSuccess() {
        showToastMessage(getString(R.string.success_log_in));
        startActivity(MainActivity.getIntent(this));
        this.finish();
    }

    @Override
    public void resetSuccess() {
        showToastMessage(getString(R.string.reset_success));
    }

    @Override
    public void showEmailVerification() {
        showToastMessage(getString(R.string.email_verification_message));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // google
        if (requestCode == REQUEST_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mLoginPresenter.getAuthWithGoogle(result);
        }
        // facebook
        else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            // TODO: try remove if statement
            if (callbackManager == null) {
                onBtnLoginWithFacebook();
            }
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        // twitter
        else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
