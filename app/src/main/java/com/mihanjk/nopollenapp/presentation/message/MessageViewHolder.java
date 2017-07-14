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

package com.mihanjk.nopollenapp.presentation.message;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.models.ChatMessage;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.profile_photo)
    ImageView mPhoto;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.date_time)
    TextView mDateTime;
    @BindView(R.id.text_message)
    TextView mTextMessage;

    private View view;

    public MessageViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        ButterKnife.bind(this, view);
    }

    public void bind(ChatMessage chatMessage) {
        String photoUrl = chatMessage.getPhotoUrl();

        if (photoUrl != null) {
            Glide.with(view.getContext()).load(photoUrl).into(mPhoto);
        } else {
            mPhoto.setImageDrawable(new IconicsDrawable(view.getContext(),
                    FontAwesome.Icon.faw_user_circle_o));
        }

        if (chatMessage.getName() != null) {
            mName.setText(chatMessage.getName());
        } else {
            mName.setText(R.string.anonymous);
        }

        // TODO: convert into device locale
        mDateTime.setText(chatMessage.getDateTime());
        mTextMessage.setText(chatMessage.getText());
    }
}
