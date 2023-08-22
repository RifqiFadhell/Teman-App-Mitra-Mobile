package id.teman.app.mitra.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.location.PlaceDetailSpec
import id.teman.app.mitra.ui.maps.bitmapDescriptor
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@Composable
@Destination
fun SearchLocationScreen(
    viewModel: SearchLocationViewModel = hiltViewModel(),
    title: String = "Lokasi Kamu",
    resultNavigator: ResultBackNavigator<PlaceDetailSpec>
) {
    var search by remember { mutableStateOf("") }
    val searchUiState = viewModel.searchUiState
    var isPinByMappedResult by remember { mutableStateOf(false) }
    var pinPlaceResult by remember { mutableStateOf<PlaceDetailSpec?>(null) }
    var markerPosition by rememberSaveable {
        mutableStateOf(
            LatLng(-6.172131022973852, 107.0425259263954)
        )
    }

    val openDialog = remember { mutableStateOf("") }
    LaunchedEffect(key1 = searchUiState.error, block = {
        searchUiState.error?.consumeOnce {
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

    LaunchedEffect(key1 = searchUiState.pinLocationName, block = {
        searchUiState.pinLocationName?.consumeOnce {
            isPinByMappedResult = true
            pinPlaceResult = it
            search = it.formattedAddress
        }
    })

    LaunchedEffect(key1 = searchUiState.successGetPlaceDetail, block = {
        searchUiState.successGetPlaceDetail?.consumeOnce {
            resultNavigator.navigateBack(it)
        }
    })

    val uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = false)) }
    val properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = false
            )
        )
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 30f)
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = properties,
                    uiSettings = uiSettings,
                    cameraPositionState = cameraPositionState,
                    onMapClick = {
                        markerPosition = it
                        viewModel.getLocationName(it)
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(it, 30f, 0f, 0f)
                                )
                            )
                        }
                    }
                ) {
                    Marker(
                        state = MarkerState(position = markerPosition),
                        icon = bitmapDescriptor(context, R.drawable.ic_destination_location)
                    )
                }
                LazyColumn {
                    item {
                        if (title.isNotBlank()) {
                            Text(
                                title,
                                style = UiFont.poppinsP3SemiBold,
                                modifier = Modifier.padding(
                                    top = Theme.dimension.size_16dp,
                                    start = Theme.dimension.size_16dp,
                                    end = Theme.dimension.size_16dp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
                    }
                    item {
                        OutlinedTextField(
                            modifier = Modifier
                                .background(color = UiColor.white)
                                .fillMaxWidth()
                                .padding(horizontal = Theme.dimension.size_16dp),
                            value = search,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            shape = RoundedCornerShape(Theme.dimension.size_8dp),
                            onValueChange = { newValue ->
                                search = newValue
                                viewModel.searchDebounced(newValue)
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = UiColor.neutral100,
                                cursorColor = UiColor.black,
                                unfocusedBorderColor = UiColor.neutral100
                            ),
                            placeholder = {
                                Text(modifier = Modifier, text = "Search")
                            },
                            trailingIcon = {
                                GlideImage(
                                    R.drawable.ic_search,
                                    modifier = Modifier
                                        .size(Theme.dimension.size_24dp)
                                        .clickable {
                                            viewModel.searchLocation(search)
                                        }
                                )
                            }
                        )
                    }
                    items(searchUiState.availableLocation) { item ->
                        SearchDestinationSectionItem(item = item) {
                            viewModel.getLocationDetail(it)
                        }
                    }
                }
                if (searchUiState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        },
        bottomBar = {
            TemanFilledButton(
                content = "Lanjutkan",
                buttonType = ButtonType.Large,
                activeColor = UiColor.primaryRed500,
                activeTextColor = Color.White,
                borderRadius = Theme.dimension.size_30dp,
                modifier = Modifier.fillMaxWidth().padding(Theme.dimension.size_16dp),
                onClicked = {
                    if (pinPlaceResult != null && isPinByMappedResult) {
                        resultNavigator.navigateBack(pinPlaceResult!!)
                    }
                }
            )
        }
    )
}