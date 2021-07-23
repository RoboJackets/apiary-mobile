# apiary-mobile

The companion mobile app for MyRoboJackets

## Local development

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