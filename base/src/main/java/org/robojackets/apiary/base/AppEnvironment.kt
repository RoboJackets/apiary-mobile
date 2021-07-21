package org.robojackets.apiary.base

import android.net.Uri

enum class AppEnvironment(
    val apiBaseUrl: Uri,
    val clientId: String,
    val production: Boolean = true
) {
    Test(
        apiBaseUrl = Uri.parse("https://apiary-test.robojackets.org"),
        clientId = "93c98c66-dffa-4ad2-bcd7-fed4f767e08e",
        production = false,
    ),
    Production (
        apiBaseUrl = Uri.parse("https://my.robojackets.org"),
        clientId = "93f578df-38bb-4068-87d7-38f7112c4123",
        production = true,
    )
}