package org.robojackets.apiary.auth.ui.permissions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.callout.WarningCallout

@Composable
fun MissingHiddenTeamsCallout(onRefreshTeams: () -> Unit) {
    WarningCallout(
        titleText = "Some teams are hidden",
        padding = PaddingValues(start = 10.dp, top = 8.dp, end = 10.dp, bottom = 5.dp)
    ) {
        Column {
            Text("You don't have permission to view all teams, including " +
                    "training teams. Ask in #it-helpdesk for access.")
            OutlinedButton(onClick = onRefreshTeams, Modifier.padding(top = 0.dp)) {
                Text("Refresh teams")
            }
        }
    }
}
