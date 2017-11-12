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
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.entity.Allergen;
import com.mihanjk.nopollenapp.data.entity.AllergenNN;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AllergenViewHolder<T> extends ChildViewHolder {
    @BindView(R.id.allergen_textview)
    TextView mAllergenName;
    @BindView(R.id.current_forecast_textview)
    TextView mCurrentForecast;
    @BindView(R.id.allergen_icon)
    ImageView mAllergenIcon;
    @BindView(R.id.current_forecast_icon)
    ImageView mCurrentLevel;
    @BindView(R.id.tomorrow_forecast_icon)
    ImageView mTomorrowLevel;
    @BindView(R.id.allergen_concentration)
    TextView mAllergenConcentration;
    private Context context;
    private int parentPosition;

    public AllergenViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public void bind(T allergen, int parentPosition) {
        this.parentPosition = parentPosition;

        String name;
        String currentLevel;

        if (allergen instanceof Allergen) {
            Allergen allergenType = (Allergen) allergen;
            name = allergenType.getName();
            currentLevel = allergenType.getCurrentLevel();

            String tomorrowLevel = allergenType.getTomorrowLevel();
            mTomorrowLevel.setImageDrawable(getLevelIcon(tomorrowLevel));
            mAllergenConcentration.setVisibility(View.GONE);
        } else {
            AllergenNN allergenType = (AllergenNN) allergen;
            name = allergenType.getName();
            currentLevel = allergenType.getCurrentLevel();

            int concentration = allergenType.getConcentration();
            mAllergenConcentration.setText(String.valueOf(concentration));
            mTomorrowLevel.setVisibility(View.GONE);
        }

        mCurrentForecast.setText(getLevelName(currentLevel));
        setAllergenNameAndIcon(name, mAllergenIcon);
        mCurrentLevel.setImageDrawable(getLevelIcon(currentLevel));
    }

    private String getLevelName(String level) {
        switch (level) {
            case "Nothing":
                return context.getString(R.string.nothing_level);
            case "Low":
                return context.getString(R.string.low_level);
            case "Medium":
                return context.getString(R.string.medium_level);
            case "High":
                return context.getString(R.string.high_level);
            case "Extra high":
                return context.getString(R.string.extra_high_level);
            default:
                return context.getString(R.string.undefined_level);
        }
    }


    private void setAllergenNameAndIcon(String name, ImageView target) {
        switch (name) {
            case "Ольха":
                loadIconInto(R.drawable.alder, target);
                setAllergenName(R.string.alder);
                break;
            case "Орешник":
                loadIconInto(R.drawable.hazel, target);
                setAllergenName(R.string.hazel);
                break;
            case "Берёза":
                loadIconInto(R.drawable.birch, target);
                setAllergenName(R.string.birch);
                break;
            case "Вяз":
                loadIconInto(R.drawable.elm, target);
                setAllergenName(R.string.elm);
                break;
            case "Клён":
                loadIconInto(R.drawable.maple, target);
                setAllergenName(R.string.maple);
                break;
            case "Ясень":
                loadIconInto(R.drawable.ash, target);
                setAllergenName(R.string.ash);
                break;
            case "Ива":
                loadIconInto(R.drawable.willow, target);
                setAllergenName(R.string.willow);
                break;
            case "Дуб":
                loadIconInto(R.drawable.oak, target);
                setAllergenName(R.string.oak);
                break;
            case "Полынь":
                loadIconInto(R.drawable.sagebrush, target);
                setAllergenName(R.string.sagebrush);
                break;
            case "Амброзия":
                loadIconInto(R.drawable.ragweed, target);
                setAllergenName(R.string.raqweed);
                break;
            case "Маревые":
                loadIconInto(R.drawable.quinoa, target);
                setAllergenName(R.string.quinoa);
                break;
            case "Общий уровень":
                loadIconInto(R.drawable.mashroom, target);
                setAllergenName(R.string.mashroom);
                break;
            case "Кладоспориум":
                loadIconInto(R.drawable.cladosporium, target);
                setAllergenName(R.string.cladosporium);
                break;
            case "Альтернария":
                loadIconInto(R.drawable.alternaria, target);
                setAllergenName(R.string.alternaria);
                break;
            case "Тополь":
                loadIconInto(R.drawable.poplar, target);
                setAllergenName(R.string.poplar);
                break;
            case "Сосна":
                loadIconInto(R.drawable.pine, target);
                setAllergenName(R.string.pine);
                break;
            case "Ель":
                loadIconInto(R.drawable.spruce, target);
                setAllergenName(R.string.spruce);
                break;
            case "Липа":
                loadIconInto(R.drawable.linden, target);
                setAllergenName(R.string.linden);
                break;
            case "Крапива":
                loadIconInto(R.drawable.nettle, target);
                setAllergenName(R.string.nettle);
                break;
            case "Подорожник":
                loadIconInto(R.drawable.plaintain, target);
                setAllergenName(R.string.plantain);
                break;
            case "Щавель":
                loadIconInto(R.drawable.sorrel, target);
                setAllergenName(R.string.sorrel);
                break;
            case "Общий фон":
                int resId;
                if (parentPosition == 0) {
                    resId = R.drawable.cereal;
                } else if (parentPosition == 2) {
                    resId = R.drawable.tree;
                } else {
                    resId = R.drawable.weed;
                }
                loadIconInto(resId, target);
                setAllergenName(R.string.general_background);
                break;
            default:
                break;
        }
    }

    private Drawable getLevelIcon(String level) {
        switch (level) {
            case "Nothing":
                return getDrawable(R.drawable.nothing_level);
            case "Low":
                return getDrawable(R.drawable.low_level);
            case "Medium":
                return getDrawable(R.drawable.medium_level);
            case "High":
                return getDrawable(R.drawable.high_level);
            case "Extra high":
                return getDrawable(R.drawable.extra_high_level);
            default:
                return null;
        }
    }

    private void loadIconInto(int recourseId, ImageView target) {
        Glide.with(context).load(recourseId).into(target);
    }

    private Drawable getDrawable(int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

    private void setAllergenName(int resId) {
        mAllergenName.setText(context.getString(resId));
    }
}
