package org.robojackets.apiary.auth.ui.permissions

import androidx.compose.runtime.Composable
import org.robojackets.apiary.auth.model.Permission

@Composable
fun PermissionGate(userPermissions: List<Permission>,
                   requiredPermissions: List<Permission>,
                   insufficientPermissions: @Composable () -> Unit,
                   content: @Composable () -> Unit,
) {
    val missingPermissions = userPermissions.subtract(requiredPermissions.toSet())

    when (missingPermissions.isEmpty()) {
        true -> content()
        false -> insufficientPermissions()
    }
}