# Check-Out!
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
