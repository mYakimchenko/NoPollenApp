# <img src="https://www.dropbox.com/s/mzzv78gsec0kk9s/icon.png?dl=1" width="60"> No Pollen

Android application that show pollen forecast data for two cities: Moscow and Nizhny Novgorod

It's use information from two web-resources [allergotop.com](http://allergotop.com/) and [nika.nn](http://nika-nn.ru/) appropriately

## Features

 * possibility to receive a notification when forecast data update
 * communication between users through chat allows to know the approximate level of the pollen background in other regions
 * in-app access to information and news from [allergotop.com](http://allergotop.com/)
 * filter allergens which have to display in forecast screen

## Project setup
* Add Firebase to Android Project. (As a result you should have google-services.json file in app module)
* Go to the Firebase Console and navigate to your project:
* Select the Auth panel and then click the Sign In Method tab.
* Enable Email, Google, Facebook, Twitter methods, then click Save.
* Compile the app module and run on your device or emulator. (should have Play Services enabled)


## TODO
 - [ ] viewing previous data of pollen forecast for selected allergen
 - [ ] phone number authentication
 - [ ] photo support for chat messages
 - [ ] user interface improvement
 
## License
```
Copyright 2017 Mikhail Yakimchenko

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```