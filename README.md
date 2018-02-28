# Socratic-Android

## Overview

Socratic is an Android app that provides a new way to get help for homework.
+ Project supports android 16+. (Jelly Bean)

## Getting Started

- Download Android Studio: https://developer.android.com/studio/index.html
- Open build.gradle with Android Studio (in the top level project folder).
- See the [API Keys](#api-keys) about setting up files for api keys
- Click the Play button on the top toolbar. You'll be prompted to run on either a connected device, or an emulator.
- See the [signing](#signing) section about setting up the keystore.properties file.

## API Keys

Socratic relies on API keys to use certain services.

Check the `gradle-example.properties` file in the root folder. Copy over the contents of the file to your own `gradle.properties` file located in the `.gradle` folder in the user home directory. Create the file if it does not exist. Replace the dummy values with correct ones.

Also check the `fabric-example.properties` file in the app module folder. Duplicate this file, rename it `fabric.properties` and update the dummy values.

For Firebase, a `google-services.json` configuration file is needed in the product flavor and build type folders. [Example](https://firebase.google.com/docs/admob/android/google-services.json) and more [info](https://firebase.google.com/support/guides/google-android#migrate_your_console_project).

## Contributing

+ `develop` is considered the mainline branch for the project. Project uses [Gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) as its Git workflow. Please review this workflow before contributing to the project.
+ Individual developers should use a feature-specific branch when doing work and then issue a pull request to the develop branch when it's ready to be merged into the mainline.

## Style Guide

Coming soon.

## Logging

We are currently using android.util.Log for logging. Log statements will be stripped out of release builds via proguard.

## Localization

- Put all string in strings.xml so they can be localized easily.

## Tools

Coming soon.

## Project Architecture

Coming soon.

## Signing

See the `keystore-example.properties` file in the root folder. Duplicate this file, rename it `keystore.properties`. Leave the dummy values.

Note that the `keystore.properties` should not be added to version control.

## Project Links

Coming soon.

## Troubleshooting

No issues to report at this time.
