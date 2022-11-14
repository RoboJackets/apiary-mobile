package org.robojackets.apiary.attendance.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.robojackets.apiary.auth.model.Permission
import org.robojackets.apiary.auth.model.Permission.*
import org.robojackets.apiary.auth.model.UserInfo
import org.robojackets.apiary.auth.network.UserRepository
import org.robojackets.apiary.auth.util.getMissingPermissions
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.navigation.NavigationActions
import org.robojackets.apiary.navigation.NavigationManager
import timber.log.Timber
import javax.inject.Inject

@Suppress("UnusedPrivateMember")
@HiltViewModel
class AttendableTypeSelectionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val navigationManager: NavigationManager,
    val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AttendableTypeSelectionState())

    private var loadingUserPermissions = MutableStateFlow(false)
    private var error = MutableStateFlow<String?>(null)
    private var userMissingPermissions = MutableStateFlow(emptyList<Permission>())
    private var user = MutableStateFlow<UserInfo?>(null)

    private var requiredPermissions = listOf(READ_USERS, CREATE_ATTENDANCE)

    val state: StateFlow<AttendableTypeSelectionState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    loadingUserPermissions,
                    error,
                    userMissingPermissions,
                    user,
                )
            ) { flows ->
                AttendableTypeSelectionState(
                    flows[0] as Boolean,
                    flows[1] as String?,
                    flows[2] as List<Permission>,
                    flows[3] as UserInfo?,
                )
            }
                .catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    fun checkUserAttendanceAccess() {
        if (user.value != null) {
            return
        }

        viewModelScope.launch {
            loadingUserPermissions.value = true
            userRepository.getLoggedInUserInfo().onSuccess {
                val missingPermissions = getMissingPermissions(this.data.user.allPermissions, requiredPermissions)
                userMissingPermissions.value = missingPermissions
                user.value = this.data.user
            }
                .onError { //TODO
                    Timber.w(this.message())
                }
                .onException { // TODO
                    Timber.e(this.exception)
                }
                .also {
                    loadingUserPermissions.value = false
                }
        }
    }

    fun navigateToAttendableSelection(attendableType: AttendableType) {
        navigationManager.navigate(
            NavigationActions.Attendance.attendableTypeSelectToAttendableSelect(
                attendableType.toString()
            )
        )
    }
}

data class AttendableTypeSelectionState(
    val loadingUserPermissions: Boolean = false,
    val error: String? = null,
    val userMissingPermissions: List<Permission> = emptyList(),
    val user: UserInfo? = null,
)
