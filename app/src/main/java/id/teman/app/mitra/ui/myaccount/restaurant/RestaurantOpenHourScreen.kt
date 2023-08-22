package id.teman.app.mitra.ui.myaccount.restaurant

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.MyTimePicker
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.restaurant.RestaurantOpenHourSpec
import id.teman.app.mitra.ui.BasicTopNavigation
import id.teman.app.mitra.ui.myaccount.viewmodel.RestaurantOpenHoursViewModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont


@Destination
@Composable
fun RestaurantOpenHourScreen(
    navigator: DestinationsNavigator,
    viewModel: RestaurantOpenHoursViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiSpec
    val context = LocalContext.current
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
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getOpenHours()
    }
    LaunchedEffect(key1 = uiState.successUpdateRestaurantHours, block = {
        uiState.successUpdateRestaurantHours?.consumeOnce {
            Toast.makeText(context, "Sukses update jam buka reso", Toast.LENGTH_SHORT).show()
        }
    })
    Scaffold(
        topBar = {
            BasicTopNavigation(title = "Atur Jam Buka") {
                navigator.popBackStack()
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = Theme.dimension.size_56dp)
                    ) {
                        itemsIndexed(uiState.restaurantHours) { index, item ->
                            OpenHourCard(
                                dayOfWeek = item.dayOfWeekName,
                                spec = item,
                                isRestaurantOpen = { isOpen ->
                                    viewModel.updateIsOpen(index, isOpen)
                                },
                                changeOpen24Hours = { isOpen24Hours ->
                                    viewModel.update24Hours(index, isOpen24Hours)
                                },
                                changeEndTime = { timeIndex, newTime ->
                                    viewModel.updateCloseHours(index, timeIndex, newTime)
                                },
                                changeStartTime = { timeIndex, newTime ->
                                    viewModel.updateOpenHours(index, timeIndex, newTime)
                                },
                                onRemoveAdditionalTime = { timeIndex ->
                                    viewModel.updateRemoveAdditionalTime(index, timeIndex)
                                },
                                onAddAdditionalTime = {
                                    viewModel.addAdditionalTime(index)
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
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
                viewModel.saveChangesHours()
            }
        }
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun OpenHourCard(
    dayOfWeek: String,
    spec: RestaurantOpenHourSpec,
    isRestaurantOpen: (Boolean) -> Unit,
    changeOpen24Hours: (Boolean) -> Unit,
    changeStartTime: (Int, String) -> Unit,
    changeEndTime: (Int, String) -> Unit,
    onRemoveAdditionalTime: (Int) -> Unit,
    onAddAdditionalTime: () -> Unit
) {
    val switchColor = if (spec.isOpenForTheDay) UiColor.blue else UiColor.white
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column(
        modifier = Modifier
            .padding(Theme.dimension.size_16dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(Theme.dimension.size_1dp, color = UiColor.neutral50),
                shape = RoundedCornerShape(Theme.dimension.size_16dp),
            )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    top = Theme.dimension.size_16dp,
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(dayOfWeek, style = UiFont.poppinsH5SemiBold)
            Switch(
                checked = spec.isOpenForTheDay,
                onCheckedChange = {
                    isRestaurantOpen(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = switchColor,
                    checkedTrackColor = UiColor.tertiaryBlue50,
                    uncheckedThumbColor = UiColor.neutral100
                )
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = spec.isOpen24Hour,
                onCheckedChange = { changeOpen24Hours(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = UiColor.blue
                )
            )
            Spacer(modifier = Modifier.width(Theme.dimension.size_10dp))
            Text(
                "Buka 24 Jam", style = UiFont.poppinsP1Medium.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        }
        AnimatedVisibility(
            visible = !spec.isOpen24Hour,
            content = {
                Column {
                    spec.openHours.mapIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = Theme.dimension.size_16dp,
                                    end = Theme.dimension.size_16dp,
                                    bottom = Theme.dimension.size_16dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.width(screenWidth * 0.35f)) {
                                Text("Buka", style = UiFont.poppinsP2Medium)
                                OutlinedTextField(
                                    value = item.startTime,
                                    enabled = false,
                                    textStyle = UiFont.poppinsP2Medium,
                                    shape = RoundedCornerShape(Theme.dimension.size_4dp),
                                    onValueChange = {
                                        changeStartTime(index, it)
                                    },
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = UiColor.neutral100,
                                        cursorColor = UiColor.black,
                                        unfocusedBorderColor = UiColor.neutral100
                                    ),
                                    modifier = Modifier
                                        .padding(top = Theme.dimension.size_8dp)
                                        .clickable {
                                            MyTimePicker(context = context, selectedDate = {
                                                changeStartTime(index, it)
                                            })
                                        },
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next,
                                        keyboardType = KeyboardType.Text
                                    ),
                                    visualTransformation = { text ->
                                        TimeFilter(text)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
                            Column(modifier = Modifier.width(screenWidth * 0.35f)) {
                                Text("Tutup", style = UiFont.poppinsP2Medium)
                                OutlinedTextField(
                                    value = item.endTime,
                                    shape = RoundedCornerShape(Theme.dimension.size_4dp),
                                    enabled = false,
                                    onValueChange = {
                                    },
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = UiColor.neutral100,
                                        cursorColor = UiColor.black,
                                        unfocusedBorderColor = UiColor.neutral100
                                    ),
                                    modifier = Modifier
                                        .padding(top = Theme.dimension.size_8dp)
                                        .clickable {
                                            MyTimePicker(context = context, selectedDate = {
                                                changeEndTime(index, it)
                                            })
                                        },
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Text
                                    ),
                                )
                            }
                            if (index > 0) {
                                Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
                                TemanCircleButton(
                                    icon = R.drawable.ic_minus,
                                    circleModifier = Modifier
                                        .size(Theme.dimension.size_32dp)
                                        .align(Alignment.CenterVertically)
                                        .clickable {
                                            onRemoveAdditionalTime(index)
                                        },
                                    iconModifier = Modifier
                                        .width(Theme.dimension.size_16dp)
                                        .height(Theme.dimension.size_2dp),
                                    circleBackgroundColor = UiColor.tertiaryBlue50,
                                    iconColor = UiColor.tertiaryBlue500
                                )
                            }
                        }
                    }
                }
            }
        )
        Text(
            "+ Tambah jam buka", style = UiFont.poppinsP2SemiBold.copy(
                color = UiColor.tertiaryBlue500,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            modifier = Modifier.padding(
                horizontal = Theme.dimension.size_16dp,
                vertical = Theme.dimension.size_20dp
            ).clickable {
                onAddAdditionalTime()
            }
        )
    }
}

fun TimeFilter(text: AnnotatedString): TransformedText {
    val timeTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset + 1
            return 5
        }

        override fun transformedToOriginal(offset: Int): Int {
            Log.d("yudith", "offset $offset")
            if (offset <= 2) return offset
            if (offset <= 4) return offset - 2
            return 4
        }

    }

    return TransformedText(text, timeTranslator)
}