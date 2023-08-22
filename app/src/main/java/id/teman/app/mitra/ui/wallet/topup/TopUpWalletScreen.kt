@file:OptIn(ExperimentalTextApi::class, ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)

package id.teman.app.mitra.ui.wallet.topup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CustomLoading
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.wallet.TopUpItemSpec
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.ui.destinations.TopUpWalletScreenDestination
import id.teman.app.mitra.ui.destinations.WebviewScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@Composable
@Destination
fun TopUpWalletScreen(
    navigator: DestinationsNavigator, viewModel: TopUpWalletViewModel = hiltViewModel()
) {
    val uiState = viewModel.topUpWalletUiState
    var amountSelected by remember { mutableStateOf(0) }
    var amountCustom by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val openDialog = remember { mutableStateOf("") }

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

    LaunchedEffect(key1 = uiState.successRequestTopUp, block = {
        uiState.successRequestTopUp?.consumeOnce {
            navigator.navigate(WebviewScreenDestination(it)){
                popUpTo(TopUpWalletScreenDestination.route) {
                    inclusive = true
                }
            }
        }
    })

    LaunchedEffect(key1 = uiState.error, block = {
        uiState.error?.consumeOnce {
            openDialog.value = it
        }
    })

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
    )
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        sheetElevation = Theme.dimension.size_8dp,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            EnterCustomAmountWidget(onChange = {
                amountSelected = if (it.isEmpty()) 0 else it.toInt().orZero()
                amountCustom = amountSelected
            })
            TemanFilledButton(
                content = "Lanjut",
                buttonType = ButtonType.Large,
                activeColor = UiColor.primaryRed500,
                activeTextColor = Color.White,
                isEnabled = amountCustom > 9999,
                borderRadius = Theme.dimension.size_30dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_24dp
                    ),
                onClicked = {
                    coroutineScope.launch {
                        modalSheetState.hide()
                        keyboardController?.hide()
                    }
                }
            )
        },
        content = {
            Scaffold(
                topBar = {
                    id.teman.app.mitra.common.TopBar(title = "Top Up Kantong") {
                        navigator.popBackStack()
                    }
                }, content = {
                    val listState = rememberLazyGridState()
                    var selectedIndex by remember { mutableStateOf(-1) }
                    Column {
                        Text(
                            text = "Mau Top up berapa?",
                            modifier = Modifier
                                .padding(
                                    top = Theme.dimension.size_16dp,
                                    start = Theme.dimension.size_18dp,
                                    bottom = Theme.dimension.size_22dp
                                )
                                .align(Alignment.Start),
                            style = UiFont.poppinsSubHSemiBold,
                            color = UiColor.black
                        )
                        LazyVerticalGrid(
                            modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp),
                            columns = GridCells.Fixed(2),
                            state = listState
                        ) {
                            items(TopUpItemSpec.values().size) { item ->
                                CardTopUp(
                                    selected = selectedIndex,
                                    value = if (item == 6) amountCustom else TopUpItemSpec.values()[item].value.orZero(),
                                    position = item,
                                    amountCustom = amountCustom
                                ) { selected, value ->
                                    amountSelected = value
                                    selectedIndex = selected
                                    coroutineScope.launch {
                                        if (selectedIndex == 6) {
                                            modalSheetState.show()
                                        } else {
                                            amountCustom = 0
                                        }
                                    }
                                }
                            }
                        }
                        if (uiState.loading) {
                            Dialog(
                                onDismissRequest = { },
                                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                            ) {
                                CustomLoading(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                },
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        TemanFilledButton(
                            content = "Lanjut Top Up",
                            buttonType = ButtonType.Large,
                            activeColor = UiColor.primaryRed500,
                            activeTextColor = Color.White,
                            isEnabled = amountSelected > 9999,
                            borderRadius = Theme.dimension.size_30dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = Theme.dimension.size_16dp,
                                    vertical = Theme.dimension.size_24dp
                                ),
                            onClicked = {
                                viewModel.requestTopUpWallet(amountSelected)
                            }
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun EnterCustomAmountWidget(onChange: (String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(
            top = Theme.dimension.size_36dp,
            start = Theme.dimension.size_18dp,
            end = Theme.dimension.size_18dp
        )
    ) {
        Text(
            "Masukkan nominal Top up",
            style = UiFont.poppinsP2Medium
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(Theme.dimension.size_52dp),
            value = amount,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            onValueChange = {
                amount = it
                onChange(amount)
            },
            placeholder = {
                Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "0")
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(TextFieldDefaults.MinHeight)
                        .background(color = UiColor.neutral100)
                ) {
                    Text(
                        "Rp ", style = UiFont.poppinsCaptionSemiBold,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
        Text(text = "*Minimal Top Up Rp 10.000",
            color = UiColor.neutral600,
            style = UiFont.cabinCaptionMedium,
            modifier = Modifier.padding(start = Theme.dimension.size_16dp)
        )
    }
}

@Composable
fun CardTopUp(selected: Int, position: Int, value: Int, amountCustom: Int, onClick: (Int, Int) -> Unit) {
    var selectedItem by remember { mutableStateOf(-1) }
    Card(
        modifier = Modifier
            .padding(horizontal = Theme.dimension.size_8dp, vertical = Theme.dimension.size_6dp)
            .selectable(selected = selected == position, onClick = {
                selectedItem = if (selected != position) position else -1
                onClick(selectedItem, value)
            }),
        elevation = Theme.dimension.size_2dp,
        shape = RoundedCornerShape(Theme.dimension.size_12dp),
        border = BorderStroke(
            Theme.dimension.size_1dp,
            if (position == selected) UiColor.tertiaryBlue500 else UiColor.white
        ),
        backgroundColor = if (position == selected) UiColor.tertiaryBlue50 else UiColor.white
    ) {
        Column(
            modifier = Modifier.padding(
                top = Theme.dimension.size_12dp,
                start = Theme.dimension.size_16dp,
                bottom = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            )
        ) {
            Text(
                "Teman Kantong",
                modifier = Modifier.padding(bottom = Theme.dimension.size_4dp),
                style = UiFont.poppinsH5SemiBold.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    ),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.LastLineBottom
                    )
                )
            )
            Text(
                if (value == 0 && amountCustom == 0) "Nominal Lainnya" else value.toDouble().convertToRupiah(),
                style = UiFont.poppinsCaptionMedium.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                color = UiColor.tertiaryBlue500
            )
        }
    }
}