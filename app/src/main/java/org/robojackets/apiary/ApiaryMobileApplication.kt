package org.robojackets.apiary

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Note: this class has to be in the same module as the @AndroidEntryPoint annotated class, which
// is MainActivity.  In other words, you can't move this class to another module to solve
// dependency issues (use dependency injection instead!).
@HiltAndroidApp
class ApiaryMobileApplication : Application() {
    // Intentionally empty
}
