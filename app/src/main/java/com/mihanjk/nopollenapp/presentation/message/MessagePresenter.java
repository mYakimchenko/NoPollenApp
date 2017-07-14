package com.mihanjk.nopollenapp.presentation.message;


import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.models.ChatMessage;
import com.mihanjk.nopollenapp.data.models.User;
import com.mihanjk.nopollenapp.data.services.DatabaseService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@InjectViewState
public class MessagePresenter extends MvpPresenter<MessageView> {
    @Inject
    DatabaseService databaseService;

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
        databaseService.getTopicMessages(this, topicName);
    }

    public void showTopicMessages(List<ChatMessage> messages) {
        getViewState().showMessages(messages);
    }

    public void showMessage(String message) {
        getViewState().showMessage(message);
    }

    public void sendMessage(String messageText) {
        ChatMessage message = new ChatMessage(user.getUid(), messageText, user.getName(),
                user.getPhotoUrl(), getCurrentTime());
        databaseService.sendMessage(this, topicName, message);
        getMessages();
    }

    private String getCurrentTime() {
        return defaultDateFormat.format(Calendar.getInstance().getTime());
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getLocalizedDateTime(ChatMessage chatMessage) {
        // TODO: exception
        try {
            return deviceDateFormat.format(defaultDateFormat.parse(chatMessage.getDateTime()));
        } catch (ParseException e) {
            getViewState().showMessage(e.getLocalizedMessage());
            return null;
        }
    }
}
