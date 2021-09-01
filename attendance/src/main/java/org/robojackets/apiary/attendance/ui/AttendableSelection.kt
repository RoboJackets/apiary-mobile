package org.robojackets.apiary.attendance.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.base.model.Attendable
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.model.Event
import org.robojackets.apiary.base.model.Team
import org.robojackets.apiary.base.ui.util.ContentPadding

@ExperimentalMaterialApi
@Composable
private fun AttendableSelection(
    loading: Boolean,
    teams: List<Team>,
    events: List<Event>,
    onAttendableSelected: (attendable: Attendable) -> Unit
) {
    if (loading) {
        Text("Loading...")
    } else {
        Column {
            Text("Teams")
            LazyColumn {
                itemsIndexed(teams) { idx, team ->
                    ListItem(
                        Modifier.clickable {
                            onAttendableSelected(
                                Attendable(team.id, team.name, "", AttendableType.Team)
                            )
                        }
                    ) {
                        Text(team.name)
                    }
                    if (idx < teams.size - 1) {
                        Divider()
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AttendableSelectionScreen(
    viewModel: AttendanceViewModel
) {
    val state by viewModel.state.collectAsState()

    DisposableEffect(Unit) {
        viewModel.loadAttendables()
        onDispose { }
    }

    ContentPadding {
        AttendableSelection(
            state.loadingAttendables,
            state.attendableTeams,
            state.attendableEvents,
            onAttendableSelected = {
                viewModel.saveAttendableSelection(it)
            }
        )
    }
}
