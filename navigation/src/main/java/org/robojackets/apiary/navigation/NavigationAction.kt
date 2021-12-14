package org.robojackets.apiary.navigation

import android.os.Parcelable
import androidx.navigation.NavOptions

// Based on https://proandroiddev.com/how-to-make-jetpack-compose-navigation-easier-and-testable-b4b19fd5f2e4
interface NavigationAction {
    val destination: String
    val parcelableArguments: Map<String, Parcelable>
        get() = emptyMap() // Default to no arguments
    val navOptions: NavOptions
        get() = NavOptions.Builder().build() // Default to no NavOptions
}
