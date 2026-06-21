package com.ovasta.sellers.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.domain.model.ProductSubCategory
import com.ovasta.sellers.domain.model.SellerProduct
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.active
import com.ovasta.sellers.shared.resources.add_product
import com.ovasta.sellers.shared.resources.confirm
import com.ovasta.sellers.shared.resources.hidden
import com.ovasta.sellers.shared.resources.ic_add
import com.ovasta.sellers.shared.resources.ic_edit_task
import com.ovasta.sellers.shared.resources.inactive
import com.ovasta.sellers.shared.resources.my_products
import com.ovasta.sellers.shared.resources.no_products_available
import com.ovasta.sellers.shared.resources.other
import com.ovasta.sellers.shared.resources.price
import com.ovasta.sellers.shared.resources.price_currency
import com.ovasta.sellers.shared.resources.product_name
import com.ovasta.sellers.shared.resources.show_in_app
import com.ovasta.sellers.shared.resources.shown
import com.ovasta.sellers.shared.resources.update_product
import com.ovasta.sellers.ui.base.BaseScreen
import com.ovasta.sellers.ui.base.LocalNavigator
import com.ovasta.sellers.ui.components.CenteredTextAppBar
import com.ovasta.sellers.ui.components.shimmer
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage
import com.ovasta.sellers.data.remote.ApiConstants
import com.ovasta.sellers.shared.resources.product_description
import com.ovasta.sellers.ui.theme.Gray100
import com.ovasta.sellers.ui.theme.Gray500
import com.ovasta.sellers.ui.theme.Gray600
import com.ovasta.sellers.ui.theme.Green
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.mdRegular
import com.ovasta.sellers.ui.theme.mdSemiBold
import com.ovasta.sellers.ui.theme.smMedium
import com.ovasta.sellers.ui.theme.smNormal
import com.ovasta.sellers.ui.theme.smSemiBold
import com.ovasta.sellers.ui.theme.xsMedium
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val CHIP_LABEL_MAX_CHARS = 12

// Toggle on when the add-product feature is ready to ship.
private const val SHOW_ADD_PRODUCT = false

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
                    onBackButtonPressed = { onNavigateBack() },
                    actions = {
                        // Add-product is hidden until the feature is released.
                        if (SHOW_ADD_PRODUCT && viewState.selectedSubCategoryId != null) {
                            IconButton(onClick = { onAction(CategoryProductsAction.OnAddProductClicked) }) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_add),
                                    contentDescription = stringResource(Res.string.add_product),
                                    tint = Primary
                                )
                            }
                        }
                    }
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
                    items(items = viewState.subCategories, key = { it.id ?: -1 }) { sub ->
                        val selected = sub.id == viewState.selectedSubCategoryId
                        // Groups with a null/blank name (the API's ungrouped bucket) fall back to "Other".
                        val name = sub.name?.takeIf { it.isNotBlank() }
                            ?: stringResource(Res.string.other)
                        val label = if (name.length > CHIP_LABEL_MAX_CHARS)
                            name.take(CHIP_LABEL_MAX_CHARS) + ".." else name
                        FilterChip(
                            selected = selected,
                            onClick = { onAction(CategoryProductsAction.OnSubCategorySelected(sub.id)) },
                            modifier = Modifier.width(110.dp),
                            label = {
                                Text(
                                    text = label,
                                    style = smMedium,
                                    color = if (selected) Color.White else Gray600,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
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
                if (viewState.isLoading) {
                    items(count = 6, key = { "shimmer_$it" }) {
                        ProductShimmerCard()
                    }
                } else if (products.isEmpty()) {
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

    when {
        viewState.editingProduct != null -> {
            ProductEditorBottomSheet(
                product = viewState.editingProduct,
                onSubmit = { name, description, price, show, active ->
                    onAction(CategoryProductsAction.OnEditSubmitted(name, description, price, show, active))
                },
                onDismiss = { onAction(CategoryProductsAction.DismissEditor) }
            )
        }

        viewState.isAddingProduct -> {
            ProductEditorBottomSheet(
                product = null,
                onSubmit = { name, description, price, show, active ->
                    onAction(CategoryProductsAction.OnNewProductSubmitted(name, description, price, show, active))
                },
                onDismiss = { onAction(CategoryProductsAction.DismissEditor) }
            )
        }
    }
}

/**
 * Builds a loadable URL from the API's image field, which may be either an
 * absolute URL or a path relative to the host root. Returns null when blank.
 */
private fun resolveImageUrl(image: String?): String? {
    val raw = image?.trim().orEmpty()
    if (raw.isEmpty()) return null
    return if (raw.startsWith("http", ignoreCase = true)) raw
    else ApiConstants.IMAGE_BASE_URL.trimEnd('/') + "/" + raw.trimStart('/')
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
            val imageUrl = resolveImageUrl(product.image)
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Gray100)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Gray100, RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name.orEmpty(),
                    style = smSemiBold,
                    color = Gray600,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!product.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = product.description,
                        style = smNormal,
                        color = Gray500,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
            // Edit affordance so the seller knows the row is editable.
            Icon(
                painter = painterResource(Res.drawable.ic_edit_task),
                contentDescription = stringResource(Res.string.update_product),
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ProductShimmerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer()
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmer()
                    )
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmer()
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
private fun ProductEditorBottomSheet(
    product: SellerProduct?,
    onSubmit: (String, String, Double, Boolean, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val isAdd = product == null
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editorKey = product?.id ?: 0

    var name by remember(editorKey) { mutableStateOf(product?.name.orEmpty()) }
    var description by remember(editorKey) { mutableStateOf(product?.description.orEmpty()) }
    var price by remember(editorKey) {
        mutableStateOf(product?.salesPrice?.toString().orEmpty())
    }
    var active by remember(editorKey) { mutableStateOf(product?.active ?: true) }
    var show by remember(editorKey) { mutableStateOf(product?.show ?: true) }

    val parsedPrice = price.toDoubleOrNull()
    val isValid = name.isNotBlank() && parsedPrice != null

    val hasChanges = isAdd || (
        name != product?.name.orEmpty() ||
            description != product?.description.orEmpty() ||
            parsedPrice != product?.salesPrice ||
            active != product.active ||
            show != product.show
        )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White
    ) {
        // Outer column lifts the whole sheet above the keyboard; only the fields scroll,
        // keeping the submit button pinned and visible while typing.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(if (isAdd) Res.string.add_product else Res.string.update_product),
                    style = mdSemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    // Name can only be set when adding; it is read-only when editing an existing product.
                    readOnly = !isAdd,
                    enabled = isAdd,
                    label = { Text(stringResource(Res.string.product_name), style = smNormal) },
                    textStyle = smNormal,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(Res.string.product_description), style = smNormal) },
                    textStyle = smNormal,
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text(stringResource(Res.string.price), style = smNormal) },
                    textStyle = smNormal,
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
            }

            val enabled = isValid && hasChanges
            Button(
                onClick = {
                    onSubmit(
                        name.trim(),
                        description.trim(),
                        parsedPrice ?: 0.0,
                        show,
                        active
                    )
                },
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = Primary.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = stringResource(if (isAdd) Res.string.add_product else Res.string.confirm),
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

private val previewProductsState = CategoryProductsViewState(
    isLoading = false,
    categoryName = "المعلم",
    selectedSubCategoryId = 1,
    subCategories = listOf(
        ProductSubCategory(
            id = 1,
            name = "ساندويتشات",
            products = listOf(
                SellerProduct(id = 101, name = "ساندويتش فراخ", active = true, show = true, salesPrice = 45.0),
                SellerProduct(id = 102, name = "ساندويتش لحمة", active = true, show = false, salesPrice = 60.0),
                SellerProduct(id = 103, name = "ساندويتش طعمية", active = false, show = false, salesPrice = 15.0),
            )
        ),
        ProductSubCategory(id = 2, name = "مشروبات", products = emptyList()),
    )
)

@Preview
@Composable
private fun CategoryProductsContentPreview() {
    CategoryProductsContent(viewState = previewProductsState, onAction = {})
}

@Preview
@Composable
private fun CategoryProductsLoadingPreview() {
    CategoryProductsContent(
        viewState = CategoryProductsViewState(isLoading = true, categoryName = "المعلم"),
        onAction = {}
    )
}

@Preview
@Composable
private fun CategoryProductsEmptyPreview() {
    CategoryProductsContent(
        viewState = previewProductsState.copy(selectedSubCategoryId = 2),
        onAction = {}
    )
}

@Preview
@Composable
private fun ProductCardPreview() {
    ProductCard(
        product = SellerProduct(id = 1, name = "ساندويتش فراخ", active = true, show = true, salesPrice = 45.0),
        onClick = {}
    )
}

@Preview
@Composable
private fun ProductEditorBottomSheetPreview() {
    ProductEditorBottomSheet(
        product = SellerProduct(id = 1, name = "ساندويتش فراخ", description = "ساندويتش فراخ مشوية", active = true, show = true, salesPrice = 45.0),
        onSubmit = { _, _, _, _, _ -> },
        onDismiss = {}
    )
}
