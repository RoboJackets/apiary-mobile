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

### android helloWorld

```sh
[bundle exec] fastlane android helloWorld
```

Hello World

### android testing1

```sh
[bundle exec] fastlane android testing1
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

### android releaseToInternalTest

```sh
[bundle exec] fastlane android releaseToInternalTest
```

Propose a new release to the internal test track

### android deploy

```sh
[bundle exec] fastlane android deploy
```

Deploy a new version to the Google Play

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
