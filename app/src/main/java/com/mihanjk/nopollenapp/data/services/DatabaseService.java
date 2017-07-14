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

package com.mihanjk.nopollenapp.data.services;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.models.Allergen;
import com.mihanjk.nopollenapp.data.models.AllergenNN;
import com.mihanjk.nopollenapp.data.models.ChatMessage;
import com.mihanjk.nopollenapp.data.models.Forecast;
import com.mihanjk.nopollenapp.data.models.User;
import com.mihanjk.nopollenapp.presentation.chat.ChatPresenter;
import com.mihanjk.nopollenapp.presentation.forecast.ForecastPresenter;
import com.mihanjk.nopollenapp.presentation.login.LoginPresenter;
import com.mihanjk.nopollenapp.presentation.message.MessagePresenter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DatabaseService {
    private static final String USERS = "Users";
    private static final String CITY = "city";
    private static final String ALLERGENS = "UserAllergens";
    private static final String TOPICS = "Topics";

    @Inject
    // TODO: try to remove this or inject user also
            PreferencesService preferencesService;

    private DatabaseReference databaseRef;

    public DatabaseService() {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        // saving data to disk cache
        // TODO: why take more than 100 mb space in device?
//        instance.setPersistenceEnabled(true);
        this.databaseRef = instance.getReference();
        NoPollenApplication.getAppComponent().inject(this);
    }


    public Task<Void> createUser(User user) {
        return databaseRef.child(USERS).child(user.getUid()).setValue(user);
    }

    public DatabaseReference getUser(String userUid) {
        return databaseRef.child(USERS).child(userUid);
    }

    public void getForecastData(final ForecastPresenter presenter) {
        // TODO: refactoring
//        String city = preferencesService.getStringValue(PreferencesService.CITY);
        String city = preferencesService.getCity();
        presenter.setCityHeader(city);
        final Query lastForecast = databaseRef.child(city).orderByKey().limitToLast(1);
        if (city.equals("Moscow")) {
            lastForecast.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Forecast<Allergen>> result = new ArrayList<>();
                    for (DataSnapshot date : dataSnapshot.getChildren()) {
                        try {
                            presenter.setDateForMoscow(date.getKey());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            presenter.showMessage(e.getLocalizedMessage());
                        }
                        for (DataSnapshot group : date.getChildren()) {
                            List<Allergen> allergens = new ArrayList<>();
                            for (DataSnapshot allergen : group.getChildren()) {
                                Allergen value = allergen.getValue(Allergen.class);
                                allergens.add(value);
                            }
                            result.add(new Forecast<>(group.getKey(), allergens));
                        }
                    }
                    presenter.updateMoscowForecast(result);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // TODO: give some error
                    Log.d("DatabaseService", "forecast getting error");
                }
            });
        } else {
            lastForecast.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Forecast<AllergenNN>> result = new ArrayList<>();
                    for (DataSnapshot date : dataSnapshot.getChildren()) {
                        presenter.setDateForNN(date.getKey());
                        for (DataSnapshot group : date.getChildren()) {
                            List<AllergenNN> allergens = new ArrayList<>();
                            for (DataSnapshot allergen : group.getChildren()) {
                                AllergenNN value = allergen.getValue(AllergenNN.class);
                                value.formatProperties();
                                allergens.add(value);
                            }
                            result.add(new Forecast<>(group.getKey(), allergens));
                        }
                    }
                    presenter.updateNNForecast(result);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // TODO: give some error
                    Log.d("DatabaseService", "forecast getting error");
                }
            });
        }
    }

    public void updateUserCity(String city) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef.child(USERS).child(uid).child(CITY).setValue(city);
    }

    public void updateUserAllergens(List<String> allergens) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef.child(ALLERGENS).child(uid).setValue(allergens);
    }

    public void getUserAllergens(final LoginPresenter presenter, final User user) {
        final List<String> result = new ArrayList<>();
        databaseRef.child(ALLERGENS).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot allergen : dataSnapshot.getChildren()) {
                    result.add((String) allergen.getValue());
                }
                user.setAllergens(result);
                presenter.completeLogIn(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: sent message to presenter
                presenter.showErrorLoadingFromDatabase(databaseError.getMessage());
            }
        });
    }

    public void getTopicsNames(final ChatPresenter presenter) {
        final List<String> result = new ArrayList<>();
        databaseRef.child(TOPICS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot topic : dataSnapshot.getChildren()) {
                    result.add(topic.getKey());
                }
                presenter.setNames(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: sent message to presenter
                presenter.showErrorLoadingFromDatabase(databaseError.getMessage());
            }
        });
    }

    public void addTopic(final ChatPresenter presenter, final String topicName) {
        // TODO: refactoring
        databaseRef.child(TOPICS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(topicName)) {
                    databaseRef.child(TOPICS).child(topicName).setValue(topicName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                getTopicsNames(presenter);
                            } else {
                                presenter.showMessage(task.getException().getLocalizedMessage());
                            }
                        }
                    });

                } else {
                    presenter.showMessage(R.string.already_exist);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                presenter.showErrorLoadingFromDatabase(databaseError.getMessage());
            }
        });
    }

    public void getTopicMessages(final MessagePresenter presenter, String topicName) {
        // TODO: add pagination to chat messages check this https://github.com/natuanorg/FirebasePaginator/blob/master/app/src/main/java/com/natuan/firebasepaginator/EndlessRecyclerViewScrollListener.java
        final List<ChatMessage> messages = new ArrayList<>();
        databaseRef.child(TOPICS).child(topicName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot message : dataSnapshot.getChildren()) {
                    ChatMessage chatMessage = message.getValue(ChatMessage.class);
                    chatMessage.setDateTime(presenter.getLocalizedDateTime(chatMessage));
                    messages.add(chatMessage);
                }
                presenter.showTopicMessages(messages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                presenter.showMessage(databaseError.getMessage());
            }
        });
    }

    public void sendMessage(final MessagePresenter presenter, final String topicName, final ChatMessage message) {
        final DatabaseReference topicRef = databaseRef.child(TOPICS).child(topicName);
        topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().equals(topicName)) {
                    topicRef.child("1").setValue(message);
                } else {
                    topicRef.child(String.valueOf(dataSnapshot.getChildrenCount() + 1)).setValue(message);
                    getTopicMessages(presenter, topicName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
