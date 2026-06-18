package com.ovasta.sellers.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.ovasta.sellers.domain.model.SellerProduct
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.active
import com.ovasta.sellers.shared.resources.confirm
import com.ovasta.sellers.shared.resources.inactive
import com.ovasta.sellers.shared.resources.my_products
import com.ovasta.sellers.shared.resources.no_products_available
import com.ovasta.sellers.shared.resources.price_currency
import com.ovasta.sellers.shared.resources.purchase_price
import com.ovasta.sellers.shared.resources.sales_price
import com.ovasta.sellers.shared.resources.show_in_app
import com.ovasta.sellers.shared.resources.hidden
import com.ovasta.sellers.shared.resources.shown
import com.ovasta.sellers.shared.resources.update_product
import com.ovasta.sellers.ui.base.BaseScreen
import com.ovasta.sellers.ui.base.LocalNavigator
import com.ovasta.sellers.ui.components.CenteredTextAppBar
import com.ovasta.sellers.ui.theme.Gray100
import com.ovasta.sellers.ui.theme.Gray500
import com.ovasta.sellers.ui.theme.Gray600
import com.ovasta.sellers.ui.theme.Green
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.mdRegular
import com.ovasta.sellers.ui.theme.mdSemiBold
import com.ovasta.sellers.ui.theme.smMedium
import com.ovasta.sellers.ui.theme.smSemiBold
import com.ovasta.sellers.ui.theme.xsMedium
import org.jetbrains.compose.resources.stringResource

@Composable
fun CategoryProductsScreen(viewModel: CategoryProductsViewModel, categoryId: Int) {
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current

    LaunchedEffect(categoryId) {
        viewModel.onScreenAction(CategoryProductsAction.LoadProducts(categoryId))
    }

    BaseScreen(viewModel = viewModel) {
        CategoryProductsContent(
            viewState = viewState,
            onAction = viewModel::onScreenAction,
            onNavigateBack = { navigator.pop() }
        )
    }
}

@Composable
private fun CategoryProductsContent(
    viewState: CategoryProductsViewState,
    onAction: (CategoryProductsAction) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp, color = Color.White) {
                CenteredTextAppBar(
                    viewState.categoryName.ifEmpty { stringResource(Res.string.my_products) },
                    onBackButtonPressed = { onNavigateBack() }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .background(Gray100)
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            if (viewState.subCategories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    items(items = viewState.subCategories, key = { it.id }) { sub ->
                        val selected = sub.id == viewState.selectedSubCategoryId
                        FilterChip(
                            selected = selected,
                            onClick = { onAction(CategoryProductsAction.OnSubCategorySelected(sub.id)) },
                            label = { Text(text = sub.name, style = smMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White,
                            )
                        )
                    }
                }
            }

            val products = viewState.visibleProducts
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = paddingValues.calculateBottomPadding() + 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (products.isEmpty()) {
                    item(key = "empty") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.no_products_available),
                                style = mdRegular,
                                color = Gray500
                            )
                        }
                    }
                } else {
                    items(items = products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onAction(CategoryProductsAction.OnProductClicked(product)) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    viewState.editingProduct?.let { product ->
        EditProductBottomSheet(
            product = product,
            onSubmit = { sales, purchase, show, active ->
                onAction(CategoryProductsAction.OnEditSubmitted(sales, purchase, show, active))
            },
            onDismiss = { onAction(CategoryProductsAction.DismissEdit) }
        )
    }
}

@Composable
private fun ProductCard(product: SellerProduct, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // View-only image placeholder (no shared image loader available).
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Gray100, RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name.orEmpty(),
                    style = smSemiBold,
                    color = Gray600,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.price_currency, product.salesPrice.toString()),
                    style = smMedium,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusPill(
                        text = if (product.active) stringResource(Res.string.active)
                        else stringResource(Res.string.inactive),
                        color = if (product.active) Green else Color.Gray
                    )
                    StatusPill(
                        text = if (product.show) stringResource(Res.string.shown)
                        else stringResource(Res.string.hidden),
                        color = if (product.show) Primary else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        contentColor = color
    ) {
        Text(
            text = text,
            style = xsMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProductBottomSheet(
    product: SellerProduct,
    onSubmit: (Double, Double, Boolean, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var salesPrice by remember(product.id) { mutableStateOf(product.salesPrice.toString()) }
    var purchasePrice by remember(product.id) { mutableStateOf(product.purchasePrice.toString()) }
    var active by remember(product.id) { mutableStateOf(product.active) }
    var show by remember(product.id) { mutableStateOf(product.show) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(Res.string.update_product),
                style = mdSemiBold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )

            if (!product.name.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = product.name, style = smMedium, color = Gray500)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = salesPrice,
                onValueChange = { salesPrice = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text(stringResource(Res.string.sales_price)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = purchasePrice,
                onValueChange = { purchasePrice = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text(stringResource(Res.string.purchase_price)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            ToggleRow(
                label = stringResource(Res.string.active),
                checked = active,
                onCheckedChange = { active = it }
            )
            ToggleRow(
                label = stringResource(Res.string.show_in_app),
                checked = show,
                onCheckedChange = { show = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSubmit(
                        salesPrice.toDoubleOrNull() ?: 0.0,
                        purchasePrice.toDoubleOrNull() ?: 0.0,
                        show,
                        active
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = stringResource(Res.string.confirm),
                    style = smMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = smMedium, color = Gray600)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Primary
            )
        )
    }
}
