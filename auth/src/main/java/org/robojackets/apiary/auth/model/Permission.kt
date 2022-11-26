package org.robojackets.apiary.auth.model

import com.squareup.moshi.Json
import java.util.*

/**
 * Nov-2022 (evan10s): This class has a custom serializer; see MainActivityModule in the `app`
 * module. The custom serializer will ignore permissions sent in the API response that are not
 * in the enum already. This deviates from the default Moshi behavior to throw an exception if a
 * permission ID that doesn't map to a member of this enum is encountered.
 */
enum class Permission {
    @Json(name = "create-attendance")
    CREATE_ATTENDANCE,
    @Json(name = "read-events")
    READ_EVENTS,
    @Json(name = "read-teams")
    READ_TEAMS,
    @Json(name = "read-teams-hidden")
    READ_TEAMS_HIDDEN,
    @Json(name = "read-users")
    READ_USERS;

    override fun toString(): String {
        return name.replace("_", "-").lowercase(Locale.getDefault())
    }
}
