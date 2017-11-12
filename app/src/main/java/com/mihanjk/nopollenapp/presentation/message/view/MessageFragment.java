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

package com.mihanjk.nopollenapp.presentation.message.view;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.entity.MessageEntity;
import com.mihanjk.nopollenapp.presentation.message.adapter.MessageAdapter;
import com.mihanjk.nopollenapp.presentation.message.presenter.MessagePresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MessageFragment extends MvpFragment implements MessageView {
    public static final String TAG = "MessageFragment";
    public static final String TOPIC_NAME = "Topic";

    @InjectPresenter
    MessagePresenter mMessagePresenter;

    @BindView(R.id.message_recycler_view)
    RecyclerView mMessageRecyclerView;
    @BindView(R.id.message_edit_text)
    EditText mMessageEditText;
    Unbinder unbinder;
    String mTopicName;


    public static MessageFragment newInstance(String topicName) {
        MessageFragment fragment = new MessageFragment();

        Bundle args = new Bundle();
        args.putString(TOPIC_NAME, topicName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        unbinder = ButterKnife.bind(this, view);
        mTopicName = getArguments().getString(TOPIC_NAME);
        mMessagePresenter.setTopicName(mTopicName);
        mMessagePresenter.getMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        mMessageRecyclerView.addItemDecoration(decoration);
        return view;
    }


    @Override
    public void showMessages(List<MessageEntity> messages) {
        MessageAdapter messageAdapter = new MessageAdapter(getActivity().getLayoutInflater(), messages);
        mMessageRecyclerView.setAdapter(messageAdapter);
        mMessageRecyclerView.invalidate();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void messageSent() {
        mMessageEditText.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.send_message)
    public void onViewClicked() {
        mMessagePresenter.sendMessage(mMessageEditText.getText().toString());
    }
}
