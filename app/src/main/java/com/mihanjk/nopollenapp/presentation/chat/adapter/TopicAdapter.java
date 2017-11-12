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

package com.mihanjk.nopollenapp.presentation.chat.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.presentation.chat.presenter.ChatPresenter;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicViewHolder> {

    private List<String> mRoomsNames;
    private LayoutInflater inflater;
    private ChatPresenter presenter;


    public TopicAdapter(LayoutInflater inflater, ChatPresenter presenter, List<String> mRoomsNames) {
        this.inflater = inflater;
        this.mRoomsNames = mRoomsNames;
        this.presenter = presenter;
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View roomView = inflater.inflate(R.layout.room_item, parent, false);
        roomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView roomName = (TextView) v.findViewById(R.id.room_name);
                presenter.openTopicMessages(roomName.getText().toString());
            }
        });
        return new TopicViewHolder(roomView);
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        holder.bind(mRoomsNames.get(position));
    }

    @Override
    public int getItemCount() {
        return mRoomsNames.size();
    }
}
