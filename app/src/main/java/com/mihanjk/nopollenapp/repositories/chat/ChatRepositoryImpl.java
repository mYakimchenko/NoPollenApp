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

package com.mihanjk.nopollenapp.repositories.chat;


import com.mihanjk.nopollenapp.data.datasources.DatabaseDataSource;
import com.mihanjk.nopollenapp.data.entity.MessageEntity;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class ChatRepositoryImpl implements ChatRepository {
    private DatabaseDataSource databaseDataSource;

    @Inject
    public ChatRepositoryImpl(DatabaseDataSource databaseDataSource) {
        this.databaseDataSource = databaseDataSource;
    }

    @Override
    public Flowable<List<String>> getTopicsNames() {
        return databaseDataSource.getTopicsNames();
    }

    @Override
    public Flowable<List<MessageEntity>> getTopicMessages(String topicName) {
        return databaseDataSource.getTopicMessages(topicName);
    }

    @Override
    public Completable sendMessage(String topicName, MessageEntity message) {
        return databaseDataSource.isTopicEmpty(topicName).subscribe(aBoolean -> {
            if (aBoolean) databaseDataSource.sendFirstMessage(topicName, message);
            else databaseDataSource.sendMessage(topicName, message);
        });
    }
}
