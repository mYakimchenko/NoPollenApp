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

package com.mihanjk.nopollenapp.data.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class Allergen implements Parcelable {
    public static final Creator<Allergen> CREATOR = new Creator<Allergen>() {
        @Override
        public Allergen createFromParcel(Parcel source) {
            return new Allergen(source);
        }

        @Override
        public Allergen[] newArray(int size) {
            return new Allergen[size];
        }
    };
    private String name;
    private String currentLevel;
    private String tomorrowLevel;

    public Allergen() {
    }

    public Allergen(String name, String currentLevel, String tomorrowLevel) {
        this.name = name;
        this.currentLevel = currentLevel;
        this.tomorrowLevel = tomorrowLevel;
    }

    private Allergen(Parcel in) {
        this.name = in.readString();
        this.currentLevel = in.readString();
        this.tomorrowLevel = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public String getTomorrowLevel() {
        return tomorrowLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.currentLevel);
        dest.writeString(this.tomorrowLevel);
    }
}
