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

package com.mihanjk.nopollenapp.data.models;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class AllergenNN implements Parcelable {
    public static final Creator<AllergenNN> CREATOR = new Creator<AllergenNN>() {
        @Override
        public AllergenNN createFromParcel(Parcel source) {
            return new AllergenNN(source);
        }

        @Override
        public AllergenNN[] newArray(int size) {
            return new AllergenNN[size];
        }
    };
    private String name;
    private String currentLevel;
    private int concentration;

    public AllergenNN() {
    }

    public AllergenNN(String name, String currentLevel, int concentration) {
        this.name = name;
        this.currentLevel = currentLevel;
        this.concentration = concentration;
    }

    protected AllergenNN(Parcel in) {
        this.name = in.readString();
        this.currentLevel = in.readString();
        this.concentration = in.readInt();
    }

    @NonNull
    public void formatProperties() {
        name = formatString(name);
        currentLevel = formatString(currentLevel);
    }

    private String formatString(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public String getName() {
        return name;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public int getConcentration() {
        return concentration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.currentLevel);
        dest.writeInt(this.concentration);
    }
}
