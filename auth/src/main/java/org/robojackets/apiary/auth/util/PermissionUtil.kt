package org.robojackets.apiary.auth.util

import org.robojackets.apiary.auth.model.Permission

/**
 * Given the permissions a user has and a list of permissions the user needs to have, returns
 * the permissions that the user does NOT have.
 *
 * @param actual The user's list of permissions
 * @param expected Required permissions to verify
 *
 * @return A list of permissions the user doesn't have
 */
fun getMissingPermissions(actual: List<Permission>, expected: List<Permission>): List<Permission> {
    return expected.subtract(actual.toSet()).toList()
}
