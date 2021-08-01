package org.robojackets.apiary.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.navigation.NavigationDirections
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val globalSettings: GlobalSettings,
    val navigationManager: NavigationManager,
) : ViewModel() {
    private fun navigateToLogin() {
        navigationManager.navigate(NavigationDirections.Authentication)
    }

    fun logout() {
        globalSettings.clearLoginInfo()
        navigateToLogin()
    }
}