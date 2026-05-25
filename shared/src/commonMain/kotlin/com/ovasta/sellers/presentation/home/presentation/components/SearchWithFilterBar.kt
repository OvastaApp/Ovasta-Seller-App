package com.ovasta.sellers.presentation.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.components.sharedComposable.SearchBox

@Composable
fun SearchWithFilterBar(
    searchKey: String,
    onSearchKeyChange: (String) -> Unit,
    onSearchTriggered: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchBox(
            modifier = Modifier
                .weight(1f)
                .testTag("searchBox"),
            searchKey,
            hint = "",
            onSearchKeyChange = onSearchKeyChange,
            onSearchTriggered = onSearchTriggered,
            keyboardType = KeyboardType.Number
        )
    }
}

@Preview
@Composable
fun SearchWithFilterBarPreview() {
    SearchWithFilterBar(
        searchKey = "",
        onSearchKeyChange = {},
        onSearchTriggered = {}
    )
}
