package com.mihanjk.nopollenapp.presentation.forecast;


import android.os.Parcelable;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mihanjk.nopollenapp.NoPollenApplication;
import com.mihanjk.nopollenapp.data.models.Allergen;
import com.mihanjk.nopollenapp.data.models.AllergenNN;
import com.mihanjk.nopollenapp.data.models.Forecast;
import com.mihanjk.nopollenapp.data.models.User;
import com.mihanjk.nopollenapp.data.services.DatabaseService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

@InjectViewState
public class ForecastPresenter extends MvpPresenter<ForecastView> {
    @Inject
    DatabaseService databaseService;
    @Inject
    User user;

    public ForecastPresenter() {
        // TODO: why user component may be null on start application?
        NoPollenApplication.getUserComponent().inject(this);
    }

    public void getForecast() {
        // TODO: remove link presenter into service
        databaseService.getForecastData(this);
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
