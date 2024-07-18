package org.robojackets.apiary.attendance.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.auth.ui.permissions.MissingHiddenTeamsCallout
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.IconWithText
import org.robojackets.apiary.base.ui.icons.WarningIcon
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.util.ContentPadding

@Composable
private fun <T> AttendableList(
    attendables: List<T>,
    onAttendableSelected: (attendable: T) -> Unit,
    title: @Composable () -> Unit,
    callout: @Composable () -> Unit = {},
    attendableContent: @Composable (attendable: T) -> Unit,
) {
    Column {
        title()
        callout()
        LazyColumn {
            itemsIndexed(attendables) { idx, attendable ->
                ListItem(
                    headlineContent = { attendableContent(attendable) },
                    Modifier.clickable {
                        onAttendableSelected(attendable)
                    }
                )
                if (idx < attendables.size - 1) {
                    Divider()
                }
            }
        }
    }
}

@Suppress("LongMethod")
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
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                IconWithText(
                    { WarningIcon(tint = danger) },
                    state.error ?: "An unknown error occurred",
                    TextAlign.Center
                )
                Button(onClick = {
                    viewModel.loadAttendables(attendableType)
                }, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Retry")
                }
            }
        }
        when (attendableType) {
            AttendableType.Team -> {
                AttendableList(
                    attendables = state.attendableTeams,
                    onAttendableSelected = {
                        viewModel.onAttendableSelected(it.toAttendable())
                    },
                    title = { Text("Select a team", style = MaterialTheme.typography.headlineSmall) },
                    callout = {
                        if (state.missingHiddenTeams == true) {
                            Spacer(Modifier.height(4.dp))
                            MissingHiddenTeamsCallout(onRefreshTeams = {
                                viewModel.loadAttendables(attendableType, forceRefresh = true)
                            })
                        }
                    }
                ) {
                    Text(it.name)
                }
            }
            AttendableType.Event -> {
                AttendableList(
                    attendables = state.attendableEvents,
                    onAttendableSelected = {
                        viewModel.onAttendableSelected(it.toAttendable())
                    },
                    title = { Text("Select an event", style = MaterialTheme.typography.headlineSmall) }
                ) {
                    Text(it.name)
                }
            }
        }
    }
}
