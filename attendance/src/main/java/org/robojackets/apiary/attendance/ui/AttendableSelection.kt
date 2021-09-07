package org.robojackets.apiary.attendance.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.util.ContentPadding

@ExperimentalMaterialApi
@Composable
private fun <T> AttendableList(
    attendables: List<T>,
    onAttendableSelected: (attendable: T) -> Unit,
    title: @Composable () -> Unit,
    attendableContent: @Composable (attendable: T) -> Unit,
) {
    Column {
        title()
        LazyColumn {
            itemsIndexed(attendables) { idx, attendable ->
                ListItem(
                    Modifier.clickable {
                        onAttendableSelected(attendable)
                    }
                ) {
                    attendableContent(attendable)
                }
                if (idx < attendables.size - 1) {
                    Divider()
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AttendableSelectionScreen(
    viewModel: AttendanceViewModel,
    attendableType: AttendableType,
) {
    val state by viewModel.state.collectAsState()

    DisposableEffect(attendableType) {
        viewModel.loadAttendables(attendableType)
        onDispose { }
    }

    ContentPadding {
        if (state.loadingAttendables) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight()
            ) {
                CircularProgressIndicator()
            }
        }
        when (attendableType) {
            AttendableType.Team -> {
                AttendableList(
                    attendables = state.attendableTeams,
                    onAttendableSelected = {
                        viewModel.onAttendableSelected(it.toAttendable())
                    },
                    title = { Text("Select a team", style = MaterialTheme.typography.h5) },
                    attendableContent = {
                        Text(it.name)
                    }
                )
            }
            AttendableType.Event -> {
                AttendableList(
                    attendables = state.attendableEvents,
                    onAttendableSelected = {
                        viewModel.onAttendableSelected(it.toAttendable())
                    },
                    title = { Text("Select an event", style = MaterialTheme.typography.h5) },
                    attendableContent = {
                        Text(it.name)
                    }
                )
            }
        }
    }
}

