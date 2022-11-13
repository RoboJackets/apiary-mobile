package org.robojackets.apiary.attendance.model

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.robojackets.apiary.attendance.model.AttendanceScreenState.*
import org.robojackets.apiary.attendance.network.AttendanceRepository
import org.robojackets.apiary.base.model.Attendable
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.model.Event
import org.robojackets.apiary.base.model.Team
import org.robojackets.apiary.base.repository.MeetingsRepository
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.navigation.NavigationActions
import org.robojackets.apiary.navigation.NavigationManager
import timber.log.Timber
import javax.inject.Inject

@Suppress("MagicNumber")
@HiltViewModel
class AttendanceViewModel @Inject constructor(
    @Suppress("UnusedPrivateMember") private val savedStateHandle: SavedStateHandle,
    val meetingsRepository: MeetingsRepository,
    val attendanceRepository: AttendanceRepository,
    val navManager: NavigationManager
) : ViewModel() {
    private val _state = MutableStateFlow(AttendanceState())

    private val lastAttendee = MutableStateFlow<AttendanceStoreResult?>(null)
    private val screenState = MutableStateFlow(ReadyForTap)
    private val totalAttendees = MutableStateFlow(0)
    private val loadingAttendables = MutableStateFlow(false)
    private val attendableTeams = MutableStateFlow(emptyList<Team>())
    private val attendableEvents = MutableStateFlow(emptyList<Event>())
    private val showAttendableSelection = MutableStateFlow(false)
    private val selectedAttendable = MutableStateFlow<Attendable?>(null)
    private val error = MutableStateFlow<String?>(null)

    val state: StateFlow<AttendanceState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(listOf(
                lastAttendee,
                screenState,
                totalAttendees,
                loadingAttendables,
                attendableTeams,
                attendableEvents,
                showAttendableSelection,
                selectedAttendable,
                error,
            )) {
                flows -> AttendanceState(
                    flows[0] as AttendanceStoreResult?,
                    flows[1] as AttendanceScreenState,
                    flows[2] as Int,
                    flows[3] as Boolean,
                    flows[4] as List<Team>,
                    flows[5] as List<Event>,
                    flows[6] as Boolean,
                    flows[7] as Attendable?,
                    flows[8] as String?,
                )
            }
                .catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    fun recordScan(tap: BuzzCardTap) {
        if (screenState.value == Loading) {
            Timber.d("Ignoring BuzzCard tap because another one is currently being processed")
            return
        }

        error.value = null
        screenState.value = Loading

        viewModelScope.launch {
            attendanceRepository.recordAttendance(
                selectedAttendable.value!!.type.toString().toLowerCase(Locale.current),
                selectedAttendable.value!!.id,
                tap.gtid,
                "MyRoboJackets Android - ${tap.source}"
            ).onSuccess {
                if (lastAttendee.value?.tap?.gtid != tap.gtid) {
                    totalAttendees.value += 1
                }
                lastAttendee.value = AttendanceStoreResult(
                    tap = tap,
                    name = this.data.attendance.attendee?.name ?: "Non-member"
                )
                screenState.value = ReadyForTap
            }
            .onError {
                Timber.e(this.toString(), "Error occurred while recording attendance")
                when (statusCode) {
                    StatusCode.Forbidden -> Unit // TODO
                }
                error.value = "The last tap was successful, but we couldn't save the data. " +
                        "Check your internet connection and try again."
                screenState.value = ReadyForTap
            }
            .onException {
                Timber.e(this.message, "Exception occurred while recording attendance")
                error.value = "The last tap was successful, but we couldn't save the data. " +
                        "Check your internet connection and try again."
                screenState.value = ReadyForTap
            }
        }
    }

    fun loadAttendables(attendableType: AttendableType) {
        error.value = null
        loadingAttendables.value = true
        viewModelScope.launch {
            if (attendableType == AttendableType.Team && attendableTeams.value.isNullOrEmpty()) {
                meetingsRepository.getTeams().onSuccess {
                    attendableTeams.value = this.data.teams
                        .filter { it.attendable }
                        .sortedBy { it.name }
                }.onError {
                    Timber.e(this.toString(), "Could not fetch attendable teams due to an error")
                    error.value = "Unable to fetch teams"
                }.onException {
                    Timber.e(this.message, "Could not fetch attendable teams due to an exception")
                    error.value = "Unable to fetch teams"
                }
            }
            if (attendableType == AttendableType.Event && attendableEvents.value.isNullOrEmpty()) {
                meetingsRepository.getEvents().onSuccess {
                    attendableEvents.value = this.data.events
                }.onError {
                    Timber.e(this.toString(), "Could not fetch attendable events due to an error")
                    error.value = "Unable to fetch events"
                }.onException {
                    Timber.e(this.message, "Could not fetch attendable events due to an exception")
                    error.value = "Unable to fetch events"
                }
            }
            loadingAttendables.value = false
        }
    }

    fun navigateToAttendableSelection() {
        navManager.navigate(NavigationActions.Attendance.attendanceToAttendableTypeSelect())
    }

    fun onAttendableSelected(attendable: Attendable) {
        navManager.navigate(
            NavigationActions.Attendance.attendableSelectionToAttendance(
                attendable.type.toString(),
                attendable.id
            )
        )
    }

    fun getAttendableInfo(attendableType: AttendableType, attendableId: Int) {
        error.value = null

        viewModelScope.launch {
            when (attendableType) {
                AttendableType.Team -> {
                    meetingsRepository.getTeam(attendableId).onSuccess {
                        val team = this.data.team

                        team.let {
                            selectedAttendable.value = it.toAttendable()
                        }
                    }.onError {
                        Timber.e(this.toString(), "Unable to get list of attendable teams")
                        error.value = "Unable to fetch team info"
                    }.onException {
                        Timber.e(
                            this.exception,
                            "Exception occurred while fetching attendable teams"
                        )
                        error.value = "Unable to fetch team info"
                    }
                }
                AttendableType.Event -> {
                    meetingsRepository.getEvent(attendableId).onSuccess {
                        val event = this.data.event

                        event.let {
                            selectedAttendable.value = it.toAttendable()
                        }
                    }.onError {
                        Timber.e(this.toString(), "Unable to get list of attendable events")
                        error.value = "Unable to fetch event info"
                    }.onException {
                        Timber.e(
                            this.exception,
                            "Exception occurred while fetching attendable events"
                        )
                        error.value = "Unable to fetch event info"
                    }
                }
            }
        }
    }
}

data class AttendanceState(
    val lastAttendee: AttendanceStoreResult? = null,
    val screenState: AttendanceScreenState = ReadyForTap,
    val totalAttendees: Int = 0,
    val loadingAttendables: Boolean = false,
    val attendableTeams: List<Team> = emptyList(),
    val attendableEvents: List<Event> = emptyList(),
    val showAttendableSelection: Boolean = false,
    val selectedAttendable: Attendable? = null,
    val error: String? = null,
)
