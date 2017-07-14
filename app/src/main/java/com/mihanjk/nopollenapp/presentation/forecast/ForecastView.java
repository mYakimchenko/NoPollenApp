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

package com.mihanjk.nopollenapp.presentation.forecast;

import com.arellomobile.mvp.MvpView;
import com.mihanjk.nopollenapp.data.models.Allergen;
import com.mihanjk.nopollenapp.data.models.AllergenNN;
import com.mihanjk.nopollenapp.data.models.Forecast;

import java.util.List;

public interface ForecastView extends MvpView {

    void setForecastMoscow(List<Forecast<Allergen>> data);

    void setForecastNN(List<Forecast<AllergenNN>> data);

    void showMessage(String message);

    void setFirstDate(String date);

    void setSecondDate(String date);

    void setConcentrationHeader();

    void showMoscowCity();

    void showNNCity();

    void showUnknownCity();
}
