# Popular Movies, Stage 1 & Stage 2
[Udacity Grow with Google](https://www.udacity.com/grow-with-google) [Android Developer Nanodegree Program](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801)

## Overview
The task was to create a Popular Movies application that will help users to discover the latest most popular and top rated movies.
The application lets users browse through movie posters sorted accordingly, look into movie details, read posted reviews, watch related video clips.
Users can mark a movie as a favourite and have all the marked videos listed.
To fetch all the needed data on movies the application uses [themoviedb.org](themoviedb.org) API.

## Screenshots
<p align="center">
    <img src="screenshots/Screenshot_1.png?raw=true" width=275 />
    <img src="screenshots/Demo.gif?raw=true" width=275 />
    <img src="screenshots/Screenshot_2.png?raw=true" width=275 />
</p>

## How to work with the project
Just clone this repository or download as an archive and import in Android Studio.
This application uses [TMDb (The Movie Database) API](https://www.themoviedb.org/documentation/api) to retrieve movies.
You have to provide Your API key in order to properly build and run the app, just replace `<your_TMDb_API_key>` in:
    ```
    /gradle.properties
    ```

## What have I learnt?
* [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) with Android Architecture Components (Room, LiveData, ViewModel)
* Advanced use of Room persistence library
* Infinite scroll implementation
* Handling network status and network failures
* Following Material Design guidelines
* Using Retrofit 2 library to fetch JSON data and transform into Java Objects
* Specifying shared element activity transition

## Libraries
* [AndroidX](https://developer.android.com/jetpack/androidx/) previously known as *'Android support Library'*
    * [ConstraintLayout](https://developer.android.com/training/constraint-layout) allows to create large and complex layouts
    * [RecyclerView](https://developer.android.com/guide/topics/ui/layout/recyclerview) is a more advanced and flexible version of *ListView*
    * [DataBinding](https://developer.android.com/topic/libraries/data-binding/) allows to bind UI components in layouts to data sources in app
    * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) the class designed to store and manage UI-related data in a lifecycle conscious way
    * [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) an observable data holder class
    * [Room](https://developer.android.com/topic/libraries/architecture/room) the persistence library that provides an abstraction layer over SQLite to allow for more robust database access while harnessing the full power of SQLite
* [Retrofit 2](https://github.com/square/retrofit) type-safe HTTP client by Square, Inc.
* [Gson](https://github.com/google/gson) helps with serialization/deserialization of Java Objects into JSON and back
* [Picasso](https://square.github.io/picasso/) allows for hassle-free image loading

## License
    Copyright 2020 demur

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.