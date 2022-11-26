# apiary-mobile

The companion Android app for MyRoboJackets

## Local development

### Dependencies

Most dependencies are provided via Gradle.

NFC functionality uses the NXP MIFARE TapLinx Android SDK.  You must provide a license key and offline
license key from the TapLinx Developer Center on https://www.mifare.net/en/products/tools/taplinx/.

Additionally, important licensing information about the TapLinx library is included in the [`libs`](libs)
directory, including the [license](libs/LA_OPT_NXP_Software_License.txt) 
and [Software Content Register](libs/Taplinx_Android_SDK_SCR.txt).

**Note:** For RoboJackets developers, reach out in #apiary-mobile in Slack to obtain our keys.

Once you have the two keys, append the following two lines to your Gradle [`local.properies`](local.properties)
file.  Do **not** commit this file to version control.

```groovy
taplinxKey=KEY_HERE
taplinxOfflineKey=OFFLINE_KEY_HERE
sentryDsn=SENTRY_DSN_HERE
```

### Repository structure

There are 5 modules encompassing features and utilities.

- **app** - The main application module
  
Note: To avoid circular dependencies, the below modules **must not** have the **app** module as a
dependency.  Place such code in the `base` (or a new) module.

- **base** - Base files that are used in all other modules, settings screen UI
- **navigation** - Utility code for navigating between screens
- **auth** - Authentication logic and UI
- **attendance** - Attendance logic and UI

#### Dependency management

Dependency versions are managed centrally in 
[Dependencies.kt](buildSrc/src/main/java/Dependencies.kt) in the `buildSrc` module.  If you change
a version in `Dependencies.kt`, make sure to manually sync Gradle because Android Studio might not
recognize that the change requires a Gradle sync.

After adding a dependency in `Dependencies.kt`, you must also add it to the appropriate Gradle 
Script (take a look at a `build.gradle.kts` file for one of the modules for examples).

### Environment configuration

Environment definitions that allow changing what MyRoboJackets instance to use are specified in
[AppEnvironment.kt](base/src/main/java/org/robojackets/apiary/base/AppEnvironment.kt) in the `base`
module.

It's important to fetch environment values as close as possible to using them, since they could
change after being retrieved.

Since public OAuth2 (with PKCE) is used for authentication, there is no OAuth2 client secret.
If the included client ID for an environment does not work, you can create a new OAuth2 client
in Nova in MyRoboJackets (requires the admin role).

### Dependency injection

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is used for
dependency injection.  The [Hilt and Dagger annotations cheat sheet](https://developer.android.com/training/dependency-injection/hilt-cheatsheet)
is a helpful resource.

### Code linting

Detekt is used for linting Kotlin code.  The recommended command to run it is

_(Windows)_
```bash
./gradlew detektAll -PdetektAutoFix=true
```

_(*nix)_
```bash
./gradlew detektAll -PdetektAutoFix=true
```

The detektAutoFix parameter will automatically fix simple issues.

### Fastlane

We use Fastlane to automate steps of the Android release process, in combination with Concourse CI.

To install Fastlane, you'll need Ruby with the development kit installed.  On Windows, install
the latest 64-bit version of Ruby+Devkit from https://rubyinstaller.org/downloads/.

You can install the Fastlane dependencies by running `bundle install` from the root of this repository.

#### Additional Fastlane dependencies/notes
 - When running any Fastlane lane on Windows that requires `sentry-cli`, bear in mind that sometimes
`which sentry-cli` is run. `which` does not exist on Windows, but the similarly named `where`
command does, so you can get around any errors stemming from this by aliasing the command `where`
to `which`.
 - You need to install [GitVersion](https://github.com/GitTools/GitVersion) yourself.

## Release management

1. After you've merged all PRs to be included in the release, ensure the `.update-priority`
file is set correctly according to the table below. Use priority 2 as the default.

| Update priority | Description | Examples | Update timeline for users |
| --- | --- |--- | --- |
| 0/1/2 | Very low  or low priority | UI touchups that don't impact functionality, releases with options to opt-in to beta features | No prompt initially. Optional prompt starting 14 days after release. Immediate update 21 days after release. |
| 3 | Medium priority |  Medium-priority bug fixes, performance improvements, non-time-sensitive feature launches | No prompts for the first 3 days. Optional starting 4 days after release. Immediate update 21 days after release. |
2. Create a new release on `main` using a tag with a name like `v1.0.0`. Use semantic versioning
to determine how to increment the version number.
3. Upon creating a new tag/release, a Concourse job to create a draft Google Play internal test
release should run.
4. If ready, release the build to internal test and post to #apiary-mobile on Slack to get
assistance verifying the build.
   1. Internal testers may need to uninstall the app to see the update if it was recently published.
5. If no issues are found, it's time to release! Promote the build to the Production track in
Google Play, add release notes, and save the release.
6. Google Play typically spends a day or two reviewing the release, then makes it available. In
general, expect it to take at least ~24 hours for a production release to be available to users.