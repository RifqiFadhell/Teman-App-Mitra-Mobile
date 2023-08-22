package id.teman.app.mitra.ui.food.menu

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.google.android.gms.common.api.BooleanResult
import com.google.android.gms.common.api.Status
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CustomChip
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.chipBackgroundColor
import id.teman.app.mitra.domain.model.chipTextColor
import id.teman.app.mitra.ui.destinations.AddNewCategoryScreenDestination
import id.teman.app.mitra.ui.destinations.AddNewMenuScreenDestination
import id.teman.app.mitra.ui.destinations.EditMenuScreenDestination
import id.teman.app.mitra.ui.food.menu.domain.MenuFilter
import id.teman.app.mitra.ui.food.menu.domain.MenuSpec
import id.teman.app.mitra.ui.registration.common.FormTextField
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun MenuScreen(navigator: DestinationsNavigator, viewModel: FoodMenuViewModel = hiltViewModel()) {

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getMenuCategories()
    }
    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.updateMenuSuccess, block = {
        uiState.updateMenuSuccess?.consumeOnce {
            Toast.makeText(context, "Sukses memperbaharui nama grup", Toast.LENGTH_SHORT).show()
        }
    })

    val openDialog = remember { mutableStateOf("") }
    LaunchedEffect(key1 = uiState.error, block = {
        uiState.error?.consumeOnce {
            openDialog.value = it
        }
    })

    if (openDialog.value.isNotEmpty()) {
        GeneralDialogPrompt(title = "Ups, Ada Kesalahan", subtitle = openDialog.value,
            actionButtons = {
                GeneralActionButton(
                    text = "Ok",
                    textColor = UiColor.primaryRed500,
                    isFirstAction = true
                ) {
                    openDialog.value = ""

                }
            }, dismissible = true) {
            openDialog.value = ""
        }
    }

    var isOpen by remember { mutableStateOf(false) }
    var selectedFilter by rememberSaveable { mutableStateOf(MenuFilter.ALL) }
    var selectedCategory by rememberSaveable { mutableStateOf<MenuSpec?>(null) }
    var selectedGroupName by rememberSaveable { mutableStateOf("") }
    var isValid by rememberSaveable { mutableStateOf(true) }

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        sheetElevation = Theme.dimension.size_8dp,
        modifier = Modifier.wrapContentSize(),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimension.size_16dp)
            ) {
                Spacer(modifier = Modifier.padding(top = Theme.dimension.size_32dp))
                Text(
                    "Ubah Nama Group", style = UiFont.poppinsH3sSemiBold.copy(
                        textAlign = TextAlign.Center,
                    )
                )
                FormTextField(
                    title = "Nama Grup",
                    hint = "",
                    keyboardType = KeyboardType.Text,
                    textFieldValue = selectedGroupName,
                    errorMessage = if (!isValid && selectedGroupName.isEmpty()) "Mohon masukkan nama group" else null
                ) {
                    selectedGroupName = it
                }

                TemanFilledButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Theme.dimension.size_16dp,
                            vertical = Theme.dimension.size_16dp
                        ),
                    content = "Simpan",
                    buttonType = ButtonType.Medium,
                    activeColor = UiColor.primaryRed500,
                    borderRadius = Theme.dimension.size_30dp,
                    activeTextColor = UiColor.white
                ) {
                    if (selectedGroupName.isNotEmpty()) {
                        val isActive = when (selectedFilter) {
                            MenuFilter.ALL -> null
                            MenuFilter.AVAILABLE -> true
                            MenuFilter.NOT_AVAILABLE -> false
                        }
                        viewModel.updateMenuCategory(selectedGroupName, selectedCategory!!, isActive = isActive)
                        selectedCategory = null
                        selectedGroupName = ""
                        coroutineScope.launch {
                            modalSheetState.hide()
                        }
                    } else {
                        isValid = false
                    }
                }
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = Theme.dimension.size_100dp)
                ) {
                    item {
//                        SearchWidget {
//
//                        }
                    }
                    item {
                        MenuFilterWidget {
                            selectedFilter = it
                            when (it) {
                                MenuFilter.ALL -> viewModel.getMenuCategories(null)
                                MenuFilter.AVAILABLE -> viewModel.getMenuCategories(true)
                                MenuFilter.NOT_AVAILABLE -> viewModel.getMenuCategories(false)
                            }
                        }
                    }
                    itemsIndexed(uiState.menuCategories) { index, item ->
                        MenuListWidget(
                            if (index == 0) {
                                Modifier.padding(
                                    top = Theme.dimension.size_24dp,
                                    start = Theme.dimension.size_16dp,
                                    end = Theme.dimension.size_16dp
                                )
                            } else {
                                Modifier.padding(
                                    top = Theme.dimension.size_16dp,
                                    start = Theme.dimension.size_16dp,
                                    end = Theme.dimension.size_16dp
                                )
                            },
                            uiModel = item,
                            onChangeGroupName = { menu ->
                                selectedGroupName = menu.menuGroupName
                                selectedCategory = menu
                                coroutineScope.launch {
                                    modalSheetState.show()
                                }
                            },
                            onSwitchChanged = { isActive, spec ->
                                viewModel.updateProductStatus(spec, isActive)
                            },
                            onMenuClick = { spec ->
                                navigator.navigate(EditMenuScreenDestination(spec.copy(
                                    categoryName = item.menuGroupName
                                )))
                            }
                        )
                    }
                }
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
                FloatingActionBottomLayout(
                    isOpen = BooleanResult(Status.RESULT_SUCCESS, isOpen),
                    onToggle = {
                        isOpen = !isOpen
                    },
                    onClose = { state, selectedMenu ->
                        when (selectedMenu) {
                            RestaurantMenu.ADD_CATEGORY -> navigator.navigate(
                                AddNewCategoryScreenDestination
                            )
                            RestaurantMenu.ADD_PRODUCT -> navigator.navigate(
                                AddNewMenuScreenDestination
                            )
                            null -> Unit
                        }
                        if (state) {
                            isOpen = !state
                        }
                    })
            }
        }
    )
}

@Composable
private fun MenuFilterWidget(onFilterStatus: (MenuFilter) -> Unit) {
    var activeCategory by remember { mutableStateOf(MenuFilter.ALL) }
    LazyRow(modifier = Modifier.padding(top = Theme.dimension.size_20dp)) {
        itemsIndexed(MenuFilter.values()) { index, item ->
            CustomChip(
                title = item.value,
                backgroundColor = (item == activeCategory).chipBackgroundColor,
                textColor = (item == activeCategory).chipTextColor,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(
                        end = Theme.dimension.size_8dp,
                        start = if (index == 0) Theme.dimension.size_16dp else Theme.dimension.size_0dp
                    ),
                textModifier = Modifier.padding(
                    vertical = Theme.dimension.size_8dp,
                    horizontal = Theme.dimension.size_12dp
                )
            ) {
                activeCategory = item
                onFilterStatus(item)
            }
        }
    }
}

@Composable
fun BoxScope.FloatingActionBottomLayout(
    isOpen: BooleanResult,
    onToggle: () -> Unit,
    onClose: (state: Boolean, selectedMenu: RestaurantMenu?) -> Unit
) {
    val transition = updateTransition(targetState = isOpen, label = "")

    val rotation = transitionAnimation(
        transition = transition,
        valueForTrue = 225f, valueForFalse = 0f
    )

    val alpha = transitionAnimation(
        transition = transition,
        valueForTrue = .5f, valueForFalse = 0f
    )

    val actionMenuScale = transitionAnimation(
        transition = transition,
        valueForTrue = 1f, valueForFalse = 0f
    )

    Surface(
        modifier = Modifier
            .wrapContentSize()
            .align(Alignment.BottomEnd)
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clickable(
                    indication = null,
                    interactionSource = MutableInteractionSource(),
                    onClick = {
                        onClose(isOpen.value, null)
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 30.dp, end = 20.dp)
                    .wrapContentSize(),
                horizontalAlignment = Alignment.End
            ) {
                if (isOpen.value) {
                    FloatingActionMenu(
                        title = "Tambah Kategori",
                        icon = R.drawable.ic_restaurant_add_category,
                        menu = RestaurantMenu.ADD_CATEGORY,
                        isOpen = isOpen.value,
                        actionMenuScale = actionMenuScale, onClose = onClose
                    )
                    Spacer(modifier = Modifier.padding(vertical = Theme.dimension.size_16dp))
                    FloatingActionMenu(
                        title = "Tambah Hidangan",
                        icon = R.drawable.ic_restaurant_add,
                        menu = RestaurantMenu.ADD_PRODUCT,
                        isOpen = isOpen.value,
                        actionMenuScale = actionMenuScale, onClose = onClose
                    )
                    Spacer(modifier = Modifier.padding(vertical = Theme.dimension.size_16dp))
                }

                FloatingActionButton(
                    onClick = {
                        onToggle()
                    },
                    shape = CircleShape,
                    backgroundColor = UiColor.primaryRed500,
                    contentColor = UiColor.white,
                    elevation = FloatingActionButtonDefaults.elevation(),
                    content = {
                        Icon(
                            Icons.Filled.Add, contentDescription = "",
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                )

                Spacer(modifier = Modifier.padding(vertical = 10.dp))
            }
        }
    }
}

@Composable
fun FloatingActionMenu(
    title: String,
    @DrawableRes icon: Int,
    menu: RestaurantMenu,
    isOpen: Boolean,
    actionMenuScale: Float,
    onClose: (state: Boolean, selectedMenu: RestaurantMenu) -> Unit
) {
    Row(modifier = Modifier
        .scale(actionMenuScale)
        .padding(end = Theme.dimension.size_4dp)
        .clickable {
            onClose(isOpen, menu)
        }) {
        Text(
            title, modifier = Modifier
                .background(
                    color = UiColor.neutral30,
                    shape = RoundedCornerShape(Theme.dimension.size_30dp)
                )
                .padding(
                    vertical = Theme.dimension.size_8dp,
                    horizontal = Theme.dimension.size_16dp
                ),
            style = UiFont.poppinsP2SemiBold
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
        TemanCircleButton(
            icon = icon,
            circleModifier = Modifier
                .size(Theme.dimension.size_48dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(bounded = true, color = Color.DarkGray),
                    onClick = { onClose(isOpen, menu) }
                ),
            iconModifier = Modifier.size(Theme.dimension.size_28dp),
            circleBackgroundColor = UiColor.neutral30,
            iconColor = UiColor.primaryRed500
        )
    }
}

@Composable
fun transitionAnimation(
    transition: Transition<BooleanResult>,
    valueForTrue: Float,
    valueForFalse: Float
): Float {
    val animationValue: Float by transition.animateFloat(
        label = "",
        transitionSpec = { tween(durationMillis = 350) }
    ) {
        if (it.value) {
            valueForTrue
        } else {
            valueForFalse
        }
    }

    return animationValue
}

enum class RestaurantMenu {
    ADD_CATEGORY,
    ADD_PRODUCT
}