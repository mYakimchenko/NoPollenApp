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

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.models.Allergen;
import com.mihanjk.nopollenapp.data.models.Forecast;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;


public class ForecastMoscowAdapter extends ExpandableRecyclerViewAdapter<ForecastViewHolder, AllergenViewHolder<Allergen>> {
    private LayoutInflater layoutInflater;

    public ForecastMoscowAdapter(LayoutInflater inflater, @NonNull List<Forecast<Allergen>> parentList) {
        super(parentList);
        layoutInflater = inflater;
    }

    @Override
    public ForecastViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View forecastView = layoutInflater.inflate(R.layout.list_item_group, parent, false);
        return new ForecastViewHolder(forecastView);
    }

    @Override
    public AllergenViewHolder<Allergen> onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View allergenView = layoutInflater.inflate(R.layout.list_item_allergen, parent, false);
        return new AllergenViewHolder<>(allergenView);
    }

    @Override
    public void onBindChildViewHolder(AllergenViewHolder<Allergen> holder, int flatPosition, ExpandableGroup group, int childIndex) {
        holder.bind((Allergen) group.getItems().get(childIndex), flatPosition);
    }

    @Override
    public void onBindGroupViewHolder(ForecastViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.bind((Forecast) group);
    }
}
