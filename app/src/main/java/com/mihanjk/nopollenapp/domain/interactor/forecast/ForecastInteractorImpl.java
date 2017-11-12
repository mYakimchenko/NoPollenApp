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

package com.mihanjk.nopollenapp.domain.interactor.forecast;


import com.mihanjk.nopollenapp.data.entity.Allergen;
import com.mihanjk.nopollenapp.data.entity.AllergenNN;
import com.mihanjk.nopollenapp.data.entity.Forecast;
import com.mihanjk.nopollenapp.repositories.forecast.ForecastRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class ForecastInteractorImpl implements ForecastInteractor {
    private final ForecastRepository forecastRepository;

    @Inject
    public ForecastInteractorImpl(ForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    @Override
    public Flowable<List<Forecast<AllergenNN>>> getNNForecast() {
        return forecastRepository.getNNForecast();
    }

    @Override
    public Flowable<List<Forecast<Allergen>>> getMoscowForecast() {
        return forecastRepository.getMoscowForecast();
    }
}
