# apiary-mobile

The companion mobile app for MyRoboJackets

## Local development

### Repository structure

There are 5 modules encompassing features and utilities.

- **app** - The main application module
  
Note: To avoid circular dependencies, the below modules **must not** have the **app** module as a
dependency.  Place such code in the `base` (or a new) module.

- **base** - Base files that are used in all other modules, settings screen UI
- **navigation** - Utility code for navigating between screens
- **auth** - Authentication logic and UI
- **attendance** - Attendance logic and UI

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
    gradlew detektAll -PdetektAutoFix=true
```

_(*nix)_
```bash
    ./gradlew detektAll -PdetektAutoFix=true
```

The detektAutoFix parameter will automatically fix simple issues.