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

package com.mihanjk.nopollenapp.presentation.forecast.presenter;


import android.os.Parcelable;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.datasources.DatabaseDataSource;
import com.mihanjk.nopollenapp.data.entity.Allergen;
import com.mihanjk.nopollenapp.data.entity.AllergenNN;
import com.mihanjk.nopollenapp.data.entity.Forecast;
import com.mihanjk.nopollenapp.data.entity.User;
import com.mihanjk.nopollenapp.domain.interactor.forecast.ForecastInteractor;
import com.mihanjk.nopollenapp.presentation.forecast.view.ForecastView;

import org.reactivestreams.Subscription;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class ForecastPresenter extends MvpPresenter<ForecastView> {
    @Inject
    ForecastInteractor forecastInteractor;
    @Inject
    User user;

    public ForecastPresenter() {
        // TODO: why user component may be null on start application?
        NoPollenApplication.getUserComponent().inject(this);
    }

    public void getForecast() {
        // TODO: remove link presenter into service
        String city = user.getCity();
        setCityHeader(city);
        // TODO: 8/2/2017 get date
        if (city.equals(DatabaseDataSource.MOSCOW)) {
            forecastInteractor.getMoscowForecast()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showLoading)
                    .doOnTerminate(this::hideLoading)
                    .subscribe(this::updateMoscowForecast,
                            throwable -> showMessage(throwable.getLocalizedMessage()));
        } else if (city.equals(DatabaseDataSource.NN)) {
            forecastInteractor.getNNForecast()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showLoading)
                    .doOnTerminate(this::hideLoading)
                    .subscribe(this::updateNNForecast,
                            throwable -> showMessage(throwable.getLocalizedMessage()));
        } else {
            getViewState().showMessage("Incorrect city");
        }
    }

    private void showLoading(Subscription subscription) {
        getViewState().showLoading();
    }

    private void hideLoading() {
        getViewState().hideLoading();
    }

    public void updateMoscowForecast(List<Forecast<Allergen>> forecasts) {
        getViewState().setForecastMoscow(forecasts);
    }

    public void updateNNForecast(List<Forecast<AllergenNN>> forecasts) {
        getViewState().setForecastNN(forecasts);
    }

    public void setCityHeader(String city) {
        if (city.equals("Moscow")) {
            getViewState().showMoscowCity();
        } else if (city.equals("NN")) {
            getViewState().showNNCity();
        } else {
            getViewState().showUnknownCity();
        }
    }

    // 2017-06-05
    // TODO: refactor this hell
    public void setDateForMoscow(String nonFormatDate) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(nonFormatDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM");
        getViewState().setFirstDate(formatDate.format(date));
        getViewState().setSecondDate(formatDate.format(calendar.getTime()));
    }

    public void setDateForNN(String date) {
        getViewState().setFirstDate(date.substring(date.length() - 2) + "." + date.substring(5, 7));
        getViewState().setConcentrationHeader();
    }

    public <T extends Parcelable> void filterData(List<Forecast<T>> data) {
        // TODO: refactoring
        List<String> userAllergens = user.getAllergens();
        for (Iterator<Forecast<T>> categoryIterator = data.iterator(); categoryIterator.hasNext(); ) {
            Forecast<T> category = categoryIterator.next();
            for (Iterator<T> allergensIterator = category.getChildList().iterator(); allergensIterator.hasNext(); ) {
                String name;
                T allergen = allergensIterator.next();

                if (allergen instanceof AllergenNN) {
                    name = ((AllergenNN) allergen).getName();
                } else {
                    name = ((Allergen) allergen).getName();
                }

                if (name.equals("Общий фон")) {
                    name = name + " " + category.getTitle();
                }

                if (!userAllergens.contains(name)) {
                    allergensIterator.remove();
                }
            }
            if (category.getChildList().isEmpty()) {
                categoryIterator.remove();
            }
        }
    }

    public void showMessage(String localizedMessage) {
        getViewState().showMessage(localizedMessage);
    }
}
