package com.ovasta.sellers.base.components.sharedComposable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.Base_white
import com.ovasta.sellers.base.Gray200
import com.ovasta.sellers.base.Gray500
import com.ovasta.sellers.base.mdRegular

@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    searchKey: String,
    hint:String,
    onSearchKeyChange: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    keyboardType:KeyboardType?=null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Base_white,
        border = BorderStroke(1.dp, Gray200),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                BasicTextField(
                    value = searchKey,
                    onValueChange = { onSearchKeyChange(it) },
                    singleLine = true,
                    textStyle = mdRegular.copy(
                        color = Gray500,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search, keyboardType = keyboardType ?: KeyboardType.Number),
                    keyboardActions = KeyboardActions(onSearch = {
                        onSearchTriggered()
                    }),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (searchKey.isEmpty()) {
                                Text(
                                    text = hint,
                                    style = mdRegular.copy(color = Gray500),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterEnd)
                                )
                            }
                            innerTextField()
                        }
                    })
            }
        }
    }
}

@Preview(showBackground=true,locale="ar")
@Composable
fun PreviewSearchBox(){
    SearchBox(Modifier,"","search",{},{})
}
