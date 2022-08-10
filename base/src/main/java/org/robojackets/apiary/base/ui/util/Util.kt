package org.robojackets.apiary.base.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

// https://stackoverflow.com/a/68423182
fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}
