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

package com.mihanjk.nopollenapp.presentation.chat.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.presentation.chat.adapter.TopicAdapter;
import com.mihanjk.nopollenapp.presentation.chat.presenter.ChatPresenter;
import com.mihanjk.nopollenapp.presentation.main.view.MainActivity;
import com.mihanjk.nopollenapp.presentation.message.view.MessageFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChatFragment extends MvpFragment implements ChatView {
    public static final String TAG = "ChatFragment";
    @BindView(R.id.add_new_topic)
    FloatingActionButton addNewTopic;
    @BindView(R.id.progress_bar_rooms)
    ProgressBar mProgressBar;
    @BindView(R.id.rooms_recycler_view)
    RecyclerView mRecyclerView;
    @InjectPresenter
    ChatPresenter mChatPresenter;
    private Unbinder unbinder;

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        mProgressBar.setVisibility(View.VISIBLE);
        mChatPresenter.getTopics();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        return view;

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setRooms(List<String> rooms) {
        mProgressBar.setVisibility(View.GONE);
        TopicAdapter topicAdapter = new TopicAdapter(getActivity().getLayoutInflater(), mChatPresenter, rooms);
        mRecyclerView.setAdapter(topicAdapter);
        mRecyclerView.invalidate();
    }

    @Override
    public void showMessage(int stringID) {
        showMessage(getString(stringID));
    }

    @Override
    public void openTopicMessages(String name) {
        MainActivity activity = (MainActivity) getActivity();
        activity.displayFragment(MessageFragment.newInstance(name));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.add_new_topic)
    public void onAddNewTopicClicked() {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final EditText input = new EditText(getActivity());
        builder.setView(input)
                .setTitle(R.string.add_new_topic_title)
                .setCancelable(false)
                .setPositiveButton(R.string.add_new_topic_postivite_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: check if topic already exist with this name
                        mProgressBar.setVisibility(View.VISIBLE);
                        mChatPresenter.addTopic(input.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }
}
