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

package com.mihanjk.nopollenapp.presentation.chat;


import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.services.DatabaseService;

import java.util.List;

import javax.inject.Inject;

@InjectViewState
public class ChatPresenter extends MvpPresenter<ChatView> {
    @Inject
    DatabaseService databaseService;

    public ChatPresenter() {
        NoPollenApplication.getAppComponent().inject(this);
    }

    public void getTopics() {
        databaseService.getTopicsNames(this);
    }

    public void showErrorLoadingFromDatabase(String message) {
        getViewState().showMessage(message);
    }

    public void setNames(List<String> result) {
        getViewState().setRooms(result);
    }

    public void addTopic(String topicName) {
        databaseService.addTopic(this, topicName);
    }

    public void showMessage(int stringID) {
        getViewState().showMessage(stringID);
    }

    public void showMessage(String localizedMessage) {
        getViewState().showMessage(localizedMessage);
    }

    public void openTopicMessages(String topicName) {
        getViewState().openTopicMessages(topicName);
    }

}
