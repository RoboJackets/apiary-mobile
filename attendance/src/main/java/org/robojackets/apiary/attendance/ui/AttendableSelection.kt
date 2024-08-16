package org.robojackets.apiary.attendance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import org.robojackets.apiary.base.ui.form.ItemList
import org.robojackets.apiary.base.ui.icons.WarningIcon
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.util.ContentPadding

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
        } else {
            when (attendableType) {
                AttendableType.Team -> {
                    ItemList(
                        items = state.attendableTeams,
                        onItemSelected = {
                            viewModel.onAttendableSelected(it.toAttendable())
                        },
                        title = {
                            Text(
                                "Select a team",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        },
                        callout = {
                            if (state.missingHiddenTeams == true) {
                                Spacer(Modifier.height(4.dp))
                                MissingHiddenTeamsCallout(onRefreshTeams = {
                                    viewModel.loadAttendables(attendableType, forceRefresh = true)
                                })
                            }
                        },
                        // This is missing the empty state, but the empty state is rarely shown and
                        // was briefly flickering on screen last time I tried it. It wasn't worth
                        // the flickering for an edge case, so I just omitted it.
                        postItem = {
                            when {
                                it < state.attendableTeams.lastIndex -> HorizontalDivider()
                            }
                        },
                        itemKey = {
                            state.attendableTeams[it].id
                        }
                    ) {
                        Text(it.name)
                    }
                }

                AttendableType.Event -> {
                    ItemList(
                        items = state.attendableEvents,
                        onItemSelected = {
                            viewModel.onAttendableSelected(it.toAttendable())
                        },
                        title = {
                            Text("Select an event", style = MaterialTheme.typography.headlineSmall)
                        },
                        postItem = {
                            when {
                                it < state.attendableEvents.lastIndex -> HorizontalDivider()
                            }
                        },
                        // This is missing the empty state, but the empty state is rarely shown and
                        // was briefly flickering on screen last time I tried it. It wasn't worth
                        // the flickering for an edge case, so I just omitted it.
                        itemKey = {
                            state.attendableEvents[it].id
                        }
                    ) {
                        Text(it.name)
                    }
                }
            }
        }
    }
}
