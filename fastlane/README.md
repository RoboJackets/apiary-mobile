fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android testConnectionToGooglePlay

```sh
[bundle exec] fastlane android testConnectionToGooglePlay
```



### android test

```sh
[bundle exec] fastlane android test
```

Runs all the tests

### android createSentryRelease

```sh
[bundle exec] fastlane android createSentryRelease
```

Creates a Sentry release for this version and associates Git commits

### android uploadToInternalAppSharing

```sh
[bundle exec] fastlane android uploadToInternalAppSharing
```

Upload a release to Google Play internal app sharing

### android uploadToInternalTest

```sh
[bundle exec] fastlane android uploadToInternalTest
```

Upload a new, signed production release to the internal test track

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
