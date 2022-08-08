package org.robojackets.apiary.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {
    // Based on https://medium.com/@Syex/jetpack-compose-navigation-architecture-with-viewmodels-1de467f19e1c
    private val _sharedFlow = MutableSharedFlow<NavigationAction>(extraBufferCapacity = 1)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun navigate(directions: NavigationAction) {
        Timber.i("Navigating! ${directions.destination}")
        _sharedFlow.tryEmit(directions)
    }
}
