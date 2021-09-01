package org.robojackets.apiary.attendance.model

import android.util.Log
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.getOrElse
import com.skydoves.sandwich.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.robojackets.apiary.attendance.model.AttendanceScreenState.Loading
import org.robojackets.apiary.attendance.model.AttendanceScreenState.ReadyForTap
import org.robojackets.apiary.attendance.network.AttendanceRepository
import org.robojackets.apiary.base.model.*
import org.robojackets.apiary.base.repository.MeetingsRepository
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.navigation.NavigationActions
import org.robojackets.apiary.navigation.NavigationManager
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
            )) {
                flows -> AttendanceState(
                    flows[0] as AttendanceStoreResult?,
                    flows[1] as AttendanceScreenState,
                    flows[2] as Int,
                    flows[3] as Boolean,
                    flows[4] as List<Team>,
                    flows[5] as List<Event>,
                    flows[6] as Boolean,
                    flows[7] as Attendable?
                )
            }
                .catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    fun recordScan(tap: BuzzCardTap) {
        if (screenState.value == Loading) {
            Log.d("AttendanceScreen", "Ignoring BuzzCard tap because another one is currently being processed")
            return
        }

        screenState.value = Loading

        viewModelScope.launch {
            val storeResult = attendanceRepository.recordAttendance(
                selectedAttendable.value!!.type.toString().toLowerCase(Locale.current),
                selectedAttendable.value!!.id,
                tap.gtid,
                "MyRoboJackets Android - ${tap.source}"
            ).getOrThrow()

            lastAttendee.value = AttendanceStoreResult(
                tap = tap,
                name = storeResult.attendance.attendee.name
            )
            screenState.value = ReadyForTap
            totalAttendees.value += 1
        }
    }

    fun loadAttendables() {
        loadingAttendables.value = true
        viewModelScope.launch {
            val teams = meetingsRepository.getTeams().getOrElse(TeamsHolder()).teams
                .filter { it.attendable }
                .sortedBy { it.name }
            val events = meetingsRepository.getEvents().getOrElse(EventsHolder()).events
            attendableTeams.value = teams
            attendableEvents.value = events
            loadingAttendables.value = false
        }
    }

    fun navigateToAttendableSelection() {
        navManager.navigate(NavigationActions.Attendance.attendanceToAttendableSelection())
    }

    fun saveAttendableSelection(attendable: Attendable) {
        navManager.navigate(NavigationActions.Attendance.attendableSelectionToAttendance(attendable.type.toString(), attendable.id))
    }

    fun saveAttendableSelectionToState(attendable: Attendable) {
        selectedAttendable.value = attendable
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
    val selectedAttendable: Attendable? = null
)
