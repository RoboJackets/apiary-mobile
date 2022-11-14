package org.robojackets.apiary.attendance.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.attendance.model.AttendableTypeSelectionViewModel
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.icons.EventIcon
import org.robojackets.apiary.base.ui.icons.GroupsIcon
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.LoadingSpinner

@ExperimentalMaterialApi
@Composable
fun AttendableTypeSelectionScreen(
    viewModel: AttendableTypeSelectionViewModel,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(true) {
        viewModel.checkUserAttendanceAccess()
    }

    if (state.loadingUserPermissions) {
        LoadingSpinner()
        return
    }

    if (state.userMissingPermissions.isNotEmpty()) {
        Text("Missing permissions: ${state.userMissingPermissions}")
        return
    }

    ContentPadding {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()) {
            Text("What do you want to take attendance for?", style = MaterialTheme.typography.h5)
            Spacer(Modifier.defaultMinSize(minHeight = 16.dp))
            Divider()
            ListItem(
                icon = { GroupsIcon(Modifier.size(36.dp)) },
                modifier = Modifier
                    .defaultMinSize(minHeight = 80.dp)
                    .clickable { viewModel.navigateToAttendableSelection(AttendableType.Team) }
            ) {
                Text("Team", style = MaterialTheme.typography.h6)
            }
            Divider()
            ListItem(
                icon = { EventIcon(Modifier.size(36.dp)) },
                modifier = Modifier
                    .defaultMinSize(minHeight = 80.dp)
                    .clickable { viewModel.navigateToAttendableSelection(AttendableType.Event) }
            ) {
                Text("Event", style = MaterialTheme.typography.h6)
            }
            Divider()
        }
    }
}
