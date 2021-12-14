package org.robojackets.apiary.base

import android.net.Uri

enum class AppEnvironment(
    val apiBaseUrl: Uri,
    val clientId: String,
    val production: Boolean = true
) {
    Test(
        apiBaseUrl = Uri.parse("https://apiary-test.robojackets.org"),
        clientId = "93f7c104-ed5d-4b88-86a4-3605d95e0e1f",
        production = false,
    ),
    Production(
        apiBaseUrl = Uri.parse("https://my.robojackets.org"),
        clientId = "93f578df-38bb-4068-87d7-38f7112c4123",
        production = true,
    ),
    Demo(
        apiBaseUrl = Uri.parse("https://apiary-google-play-review.robojackets.org"),
        clientId = "95158a03-4ce9-489d-9550-05655f9f27eb",
        production = false,
    )
}
