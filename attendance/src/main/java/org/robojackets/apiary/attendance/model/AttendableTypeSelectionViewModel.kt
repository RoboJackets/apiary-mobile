package org.robojackets.apiary.attendance.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.navigation.NavigationActions
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class AttendableTypeSelectionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val navigationManager: NavigationManager,
) : ViewModel() {
    fun navigateToAttendableSelection(attendableType: AttendableType) {
        navigationManager.navigate(
            NavigationActions.Attendance.attendableTypeSelectToAttendableSelect(
                attendableType.toString()
            )
        )
    }
}