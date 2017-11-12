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

package com.mihanjk.nopollenapp.presentation.message.presenter;


import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.entity.MessageEntity;
import com.mihanjk.nopollenapp.data.entity.User;
import com.mihanjk.nopollenapp.domain.interactor.chat.ChatInteractor;
import com.mihanjk.nopollenapp.presentation.message.view.MessageView;

import org.reactivestreams.Subscription;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MessagePresenter extends MvpPresenter<MessageView> {
    @Inject
    ChatInteractor chatInteractor;

    @Inject
    User user;

    // TODO: restore opening chat while device change configuration

    private String topicName;
    // TODO: make date format according locale of device
    private DateFormat defaultDateFormat = new SimpleDateFormat("d MMM, HH:mm", Locale.US);
    private DateFormat deviceDateFormat = new SimpleDateFormat("d MMM, HH:mm", Locale.getDefault());

    public MessagePresenter() {
        NoPollenApplication.getUserComponent().inject(this);
    }

    public void getMessages() {
        // TODO: 7/25/2017 create transformer
        chatInteractor.getTopicMessages(topicName)
                .doOnSubscribe(this::showLoading)
                .doOnTerminate(this::hideLoading)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showTopicMessages, this::showError);
    }

    private void showLoading(Subscription subscription) {
        getViewState().showLoading();
    }

    private void showError(Throwable throwable) {
        getViewState().showMessage(throwable.getLocalizedMessage());
    }

    private void hideLoading() {
        getViewState().hideLoading();
    }

    public void showTopicMessages(List<MessageEntity> messages) {
        getViewState().showMessages(messages);
    }

    public void showToastMessage(String message) {
        getViewState().showMessage(message);
    }

    public void sendMessage(String messageText) {
        MessageEntity message = new MessageEntity(user.getUid(), messageText, user.getName(),
                user.getPhotoUrl(), getCurrentTime());
        // TODO: 7/25/2017 how to send message if empty topic
        chatInteractor.sendMessage(topicName, message)
                .doOnSubscribe(this::showLoading)
                .doOnTerminate(this::hideLoading)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showTopicMessages, this::showError);
        ;
        getMessages();
    }

    private String getCurrentTime() {
        return defaultDateFormat.format(Calendar.getInstance().getTime());
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getLocalizedDateTime(MessageEntity messageEntity) {
        // TODO: exception
        try {
            return deviceDateFormat.format(defaultDateFormat.parse(messageEntity.getDateTime()));
        } catch (ParseException e) {
            getViewState().showMessage(e.getLocalizedMessage());
            return null;
        }
    }
}
