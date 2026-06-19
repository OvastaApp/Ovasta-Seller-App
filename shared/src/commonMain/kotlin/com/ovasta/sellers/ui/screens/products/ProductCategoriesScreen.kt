package com.ovasta.sellers.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.domain.model.ProductCategory
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.arrow_narrow_left
import com.ovasta.sellers.shared.resources.my_products
import com.ovasta.sellers.shared.resources.no_categories
import com.ovasta.sellers.shared.resources.products_count
import com.ovasta.sellers.ui.base.BaseScreen
import com.ovasta.sellers.ui.base.LocalNavigator
import com.ovasta.sellers.ui.components.CenteredTextAppBar
import com.ovasta.sellers.ui.components.shimmer
import com.ovasta.sellers.ui.theme.Gray100
import com.ovasta.sellers.ui.theme.Gray500
import com.ovasta.sellers.ui.theme.Gray600
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.mdRegular
import com.ovasta.sellers.ui.theme.smMedium
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductCategoriesScreen(viewModel: ProductCategoriesViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.onScreenAction(ProductCategoriesAction.LoadCategories)
    }

    BaseScreen(viewModel = viewModel) {
        ProductCategoriesContent(
            viewState = viewState,
            onCategoryClicked = { id ->
                viewModel.onScreenAction(ProductCategoriesAction.OnCategoryClicked(id))
            },
            onNavigateBack = { navigator.pop() }
        )
    }
}

@Composable
private fun ProductCategoriesContent(
    viewState: ProductCategoriesViewState,
    onCategoryClicked: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp, color = Color.White) {
                CenteredTextAppBar(
                    stringResource(Res.string.my_products),
                    onBackButtonPressed = { onNavigateBack() }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .background(Gray100),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = paddingValues.calculateTopPadding() + 12.dp,
                bottom = paddingValues.calculateBottomPadding() + 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (viewState.isLoading) {
                items(count = 6, key = { "shimmer_$it" }) {
                    CategoryShimmerCard()
                }
            } else if (viewState.categories.isEmpty()) {
                item(key = "empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.no_categories),
                            style = mdRegular,
                            color = Gray500
                        )
                    }
                }
            } else {
                items(
                    items = viewState.categories,
                    key = { it.id }
                ) { category ->
                    CategoryCard(category = category, onClick = { onCategoryClicked(category.id) })
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun CategoryShimmerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
            )
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
            )
        }
    }
}

@Composable
private fun CategoryCard(category: ProductCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = category.name, style = smMedium, color = Gray600)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.products_count, category.count.toString()),
                    style = smMedium,
                    color = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(Res.drawable.arrow_narrow_left),
                    contentDescription = null,
                    tint = Gray600,
                    modifier = Modifier.scale(scaleX = -1f, scaleY = 1f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProductCategoriesContentPreview() {
    ProductCategoriesContent(
        viewState = ProductCategoriesViewState(
            isLoading = false,
            categories = listOf(
                ProductCategory(id = 1, name = "المعلم", count = 5),
                ProductCategory(id = 2, name = "البقالة", count = 3),
            )
        )
    )
}

@Preview
@Composable
private fun ProductCategoriesLoadingPreview() {
    ProductCategoriesContent(viewState = ProductCategoriesViewState(isLoading = true))
}

@Preview
@Composable
private fun ProductCategoriesEmptyPreview() {
    ProductCategoriesContent(viewState = ProductCategoriesViewState(isLoading = false))
}
