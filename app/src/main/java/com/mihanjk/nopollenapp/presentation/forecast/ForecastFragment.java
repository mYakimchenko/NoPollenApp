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

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mihanjk.nopollenapp.R;
import com.mihanjk.nopollenapp.data.models.Allergen;
import com.mihanjk.nopollenapp.data.models.AllergenNN;
import com.mihanjk.nopollenapp.data.models.Forecast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ForecastFragment extends MvpFragment implements ForecastView {
    public static final String TAG = "ForecastFragment";
    @BindView(R.id.allergens_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.forecast_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.current_date)
    TextView mCurrentDate;
    @BindView(R.id.tomorrow_date)
    TextView mTomorrowDate;
    @BindView(R.id.progress_bar_forecast)
    ProgressBar mProgressBar;
    @InjectPresenter
    ForecastPresenter mForecastPresenter;
    @BindView(R.id.header_city)
    TextView mHeaderCity;
    private Unbinder unbinder;

    public static ForecastFragment newInstance() {
        ForecastFragment fragment = new ForecastFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    // TODO: saving state of collapsing groups

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        unbinder = ButterKnife.bind(this, view);

        mProgressBar.setVisibility(View.VISIBLE);
        mForecastPresenter.getForecast();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mForecastPresenter.getForecast();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    // TODO: use genetics if possible
    @Override
    public void setForecastNN(List<Forecast<AllergenNN>> data) {
        mProgressBar.setVisibility(View.GONE);
        mForecastPresenter.filterData(data);
        ForecastNNAdapter forecastNNAdapter = new ForecastNNAdapter(getActivity().getLayoutInflater(), data);
        mRecyclerView.setAdapter(forecastNNAdapter);
        mRecyclerView.invalidate();
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    // TODO: use genetics if possible
    @Override
    public void setForecastMoscow(List<Forecast<Allergen>> data) {
        mForecastPresenter.filterData(data);
        ForecastMoscowAdapter forecastMoscowAdapter = new ForecastMoscowAdapter(getActivity().getLayoutInflater(), data);
        mRecyclerView.setAdapter(forecastMoscowAdapter);
        mRecyclerView.invalidate();
        mProgressBar.setVisibility(View.GONE);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setFirstDate(String date) {
        mCurrentDate.setText(date);
    }

    @Override
    public void setSecondDate(String date) {
        mTomorrowDate.setText(date);
    }

    @Override
    public void setConcentrationHeader() {
        mTomorrowDate.setText(getString(R.string.header_concentration));
    }

    @Override
    public void showMoscowCity() {
        setHeaderCityText(R.string.Moscow);
    }

    @Override
    public void showNNCity() {
        setHeaderCityText(R.string.NN);
    }

    @Override
    public void showUnknownCity() {
        setHeaderCityText(R.string.unknown_city);
    }

    private void setHeaderCityText(int stringResource) {
        mHeaderCity.setText(getString(R.string.city) + ": " + getString(stringResource));
    }
}
