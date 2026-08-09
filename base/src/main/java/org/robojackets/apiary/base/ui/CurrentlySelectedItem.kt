package org.robojackets.apiary.base.ui

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.robojackets.apiary.base.ui.icons.StorefrontIcon
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.IconRow


@Composable
fun CurrentlySelectedItem(
    name: String,
    icon: @Composable () -> Unit,
    onChangeItem: () -> Unit,
) {
    IconRow(
        icon = icon,
        text = name,
        button = { TextButton(onClick = onChangeItem) { Text("Change") } },
    )
}

@Preview
@Composable
fun PreviewCurrentlySelectedItem() {
    ContentPadding {
        CurrentlySelectedItem("Test item with a super duper long name so it will get cut off",
            { StorefrontIcon() }) {}
    }
}
