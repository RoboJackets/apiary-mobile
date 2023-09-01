package org.robojackets.apiary.attendance.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.attendance.model.AttendableTypeSelectionViewModel
import org.robojackets.apiary.auth.ui.permissions.InsufficientPermissions
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.error.ErrorMessageWithRetry
import org.robojackets.apiary.base.ui.icons.EventIcon
import org.robojackets.apiary.base.ui.icons.GroupsIcon
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.LoadingSpinner

@Suppress("LongMethod")
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

    ContentPadding {
        if (state.permissionsCheckError?.isNotEmpty() == true) {
            ErrorMessageWithRetry(
                message = state.permissionsCheckError ?: "An unknown error occurred",
                onRetry = { viewModel.checkUserAttendanceAccess(forceRefresh = true) }
            )
            return@ContentPadding
        }

        if (state.userMissingPermissions.isNotEmpty()) {
            InsufficientPermissions(
                featureName = "Attendance",
                onRefreshRequest = {
                                   viewModel.checkUserAttendanceAccess(forceRefresh = true)
                },
                missingPermissions = state.userMissingPermissions,
                requiredPermissions = viewModel.requiredPermissions,
            )
            return@ContentPadding
        }

        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text("What do you want to take attendance for?", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.defaultMinSize(minHeight = 16.dp))
            Divider()
            ListItem(
                leadingContent = {
                    GroupsIcon(Modifier.size(36.dp))
                },
                headlineContent = {
                    Text("Team", style = MaterialTheme.typography.titleLarge)
                },
                modifier = Modifier
                    .defaultMinSize(minHeight = 80.dp)
                    .clickable { viewModel.navigateToAttendableSelection(AttendableType.Team) }
            )
            Divider()
            ListItem(
                leadingContent = {
                    EventIcon(Modifier.size(36.dp))
                },
                headlineContent = {
                    Text("Event", style = MaterialTheme.typography.titleLarge)
                },
                modifier = Modifier
                    .defaultMinSize(minHeight = 80.dp)
                    .clickable { viewModel.navigateToAttendableSelection(AttendableType.Event) }
            )
            Divider()
        }
    }
}
