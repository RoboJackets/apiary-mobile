package org.robojackets.apiary.base.model

import com.squareup.moshi.Json
import java.util.Locale

enum class ShirtSize {
    @Json(name = "s")
    SMALL,

    @Json(name = "m")
    MEDIUM,

    @Json(name = "l")
    LARGE,

    @Json(name = "xl")
    EXTRA_LARGE,

    @Json(name = "xxl")
    XXL,

    @Json(name = "xxxl")
    XXXL;

    override fun toString(): String {
        return when (name) {
            "EXTRA_LARGE" -> "Extra Large"
            "XXL" -> "XXL"
            "XXXL" -> "XXXL"
            else -> name
                .lowercase(Locale.getDefault())
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
        }
    }
}