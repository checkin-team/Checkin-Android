# Check-Out! [![Build Status](https://travis-ci.com/checkin-team/Checkin-Android.svg?branch=master)](https://travis-ci.com/checkin-team/Checkin-Android)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dc1f262bc68c4573b3cc0accc971a55f)](https://app.codacy.com/app/checkin-team/Checkin-Android?utm_source=github.com&utm_medium=referral&utm_content=checkin-team/Checkin-Android&utm_campaign=Badge_Grade_Dashboard)
A budding startup project, named **Check-Out**! This repository contains the android project.

## Things to do before starting:
1. In the **keystore.properties** file, replace the value of *storeFile* to the relative path to the **signature-debug.jks** file in the project.
Example:
- In Windows, it is `storeFile=..\\signature-debug.jks`
- In Linux, it is `storeFile=../signature-debug.jks`

2. Use MVVM architecture whenever using an important data in an activity or fragment. 
- [Learn about MVVM](https://proandroiddev.com/mvvm-architecture-viewmodel-and-livedata-part-1-604f50cda1)
- [Android Architecture components guide](https://developer.android.com/jetpack/docs/guide#fetching_data)

3. Accounts are stored using Android AccountManager, so remove account from Settings to log out. Test Data is populated on success auth at initial step, so clear app data to solve possible local data issues.
