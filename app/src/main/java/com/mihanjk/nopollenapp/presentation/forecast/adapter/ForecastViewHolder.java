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

package com.mihanjk.nopollenapp.presentation.forecast.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.entity.Forecast;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ForecastViewHolder extends GroupViewHolder {
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    Context context;

    @BindView(R.id.forecast_textview)
    TextView mForecastTextView;

    @BindView(R.id.arrow_expand_imageview)
    ImageView mArrowExpandImageView;

    public ForecastViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public void bind(Forecast forecast) {
        String name = getNameFromResources(forecast.getTitle());
        mForecastTextView.setText(name);
    }

    private String getNameFromResources(String forecastName) {
        switch (forecastName) {
            case "Tree":
                return getString(R.string.tree);
            case "Weed":
                return getString(R.string.weed);
            case "Spore":
                return getString(R.string.spore);
            case "Cereal":
                return getString(R.string.cereal);
            default:
                return null;
        }
    }

    private String getString(int resourceId) {
        return context.getResources().getString(resourceId);
    }

    // TODO: refactoring this
    @Override
    public void expand() {
        super.expand();
        mArrowExpandImageView.setRotation(ROTATED_POSITION);
        RotateAnimation rotateAnimation;
        rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                INITIAL_POSITION,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        mArrowExpandImageView.startAnimation(rotateAnimation);
    }

    @Override
    public void collapse() {
        super.collapse();
        mArrowExpandImageView.setRotation(INITIAL_POSITION);
        RotateAnimation rotateAnimation;
        rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                INITIAL_POSITION,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        mArrowExpandImageView.startAnimation(rotateAnimation);
    }
}
