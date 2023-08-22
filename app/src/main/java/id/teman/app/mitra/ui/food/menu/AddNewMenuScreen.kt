package id.teman.app.mitra.ui.food.menu

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CustomChip
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.TopBar
import id.teman.app.mitra.common.amountTransformation
import id.teman.app.mitra.common.noRippleClickable
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.camera.CameraResult
import id.teman.app.mitra.domain.model.camera.CameraSpec
import id.teman.app.mitra.domain.model.camera.CameraType
import id.teman.app.mitra.ui.BasicTopNavigation
import id.teman.app.mitra.ui.destinations.SmallCameraScreenDestination
import id.teman.app.mitra.ui.food.menu.domain.MenuSpec
import id.teman.app.mitra.ui.registration.common.FormTextField
import id.teman.app.mitra.ui.registration.driver.SingleTextFormField
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun AddNewMenuScreen(
    navigator: DestinationsNavigator,
    result: ResultRecipient<SmallCameraScreenDestination, String>,
    viewModel: FoodFormViewModel = hiltViewModel()
) {
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getRestaurantMenuCategories()
    }

    val uiState = viewModel.uiState
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                    viewModel.getRestaurantMenuCategories()
                }
            }, dismissible = true) {
            openDialog.value = ""
        }
    }

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
    )

    LaunchedEffect(key1 = uiState.success, block = {
        uiState.success?.consumeOnce {
            Toast.makeText(context, "Sukses menambahkan hidangan", Toast.LENGTH_SHORT).show()
            launch {
                delay(500)
                navigator.popBackStack()
            }

        }
    })

    var menuPhoto by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var menuName by rememberSaveable { mutableStateOf("") }
    var menuDescription by rememberSaveable { mutableStateOf("") }
    var menuPrice by rememberSaveable { mutableStateOf("") }
    var menuPromoPrice by rememberSaveable { mutableStateOf("") }
    var menuCategory by rememberSaveable { mutableStateOf("") }
    var isValid by rememberSaveable { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    var profileImagePath by rememberSaveable { mutableStateOf("") }

    result.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val value = Json.decodeFromString<CameraResult>(it.value)
                menuPhoto = Uri.parse(value.uri)
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
        }
    }

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { results ->
        if (results.isSuccessful) {
            menuPhoto = results.uriContent
            profileImagePath = results.getUriFilePath(context).orEmpty()
            coroutineScope.launch {
                modalSheetState.hide()
            }
        } else {
            results.error
        }
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            val cropOptions = CropImageContractOptions(uri, CropImageOptions())
            imageCropLauncher.launch(cropOptions)
        }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        sheetElevation = Theme.dimension.size_8dp,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            TopBar(title = "Mau upload foto dari mana?", icon = R.drawable.ic_round_close) {
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimension.size_16dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = UiColor.white,
                            shape = RoundedCornerShape(Theme.dimension.size_4dp)
                        )
                        .shadow(elevation = Theme.dimension.size_1dp)
                        .padding(Theme.dimension.size_16dp)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        }
                ) {
                    GlideImage(
                        imageModel = R.drawable.ic_gallery,
                        modifier = Modifier
                            .padding(horizontal = Theme.dimension.size_16dp)
                            .size(Theme.dimension.size_72dp)
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Galeri",
                        style = UiFont.poppinsP3SemiBold,
                        color = UiColor.neutral900
                    )
                }
                Column(
                    modifier = Modifier
                        .background(
                            color = UiColor.white,
                            shape = RoundedCornerShape(Theme.dimension.size_4dp)
                        )
                        .shadow(elevation = Theme.dimension.size_1dp)
                        .padding(Theme.dimension.size_16dp)
                        .clickable {
                            navigator.navigate(
                                SmallCameraScreenDestination(
                                    cameraSpec = CameraSpec(
                                        title = "Foto Hidangan",
                                        largeCamera = false,
                                        cameraType = CameraType.STOREPHOTOOUTLET,
                                        cameraCrop = Pair(1f, 0.4f)
                                    )
                                )
                            )
                        }
                ) {
                    GlideImage(
                        imageModel = R.drawable.ic_camera,
                        modifier = Modifier
                            .padding(horizontal = Theme.dimension.size_16dp)
                            .size(Theme.dimension.size_72dp)
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Kamera",
                        style = UiFont.poppinsP3SemiBold,
                        color = UiColor.neutral900
                    )
                }
            }
        },
        content = {

            Scaffold(topBar = {
                BasicTopNavigation(title = "Tambah Hidangan Baru") {
                    navigator.popBackStack()
                }
            }, content = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp,
                                bottom = Theme.dimension.size_72dp
                            )
                    ) {
                        Row(modifier = Modifier.padding(top = Theme.dimension.size_16dp)) {
                            GlideImage(imageModel = menuPhoto,
                                modifier = Modifier
                                    .size(Theme.dimension.size_48dp)
                                    .clip(CircleShape),
                                failure = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_person),
                                        contentDescription = "failed"
                                    )
                                })
                            Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                            CustomChip(title = "Upload Foto",
                                backgroundColor = UiColor.white,
                                borderColor = UiColor.tertiaryBlue500,
                                contentColor = UiColor.white,
                                textColor = UiColor.tertiaryBlue500,
                                onClick = {
                                    coroutineScope.launch {
                                        modalSheetState.show()
                                    }
                                })

                        }
                        if (!isValid && menuPhoto == Uri.EMPTY) {
                            Text(
                                "Harap upload foto hidangan",
                                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.primaryRed500),
                                modifier = Modifier.padding(top = Theme.dimension.size_4dp)
                            )
                        }

                        SingleTextFormField(
                            textFieldValue = menuName,
                            title = "Nama Hidangan",
                            keyboardType = KeyboardType.Text,
                            errorMessage = if (!isValid && menuName.isEmpty()) "Harap isi nama hidangan" else null
                        ) {
                            menuName = it
                        }
                        SingleTextFormField(
                            textFieldValue = menuDescription,
                            title = "Deskripsi",
                            keyboardType = KeyboardType.Text,
                            errorMessage = if (!isValid && menuDescription.isEmpty()) "Harap isi deskripsi" else null
                        ) {
                            menuDescription = it
                        }
                        SingleTextFormField(
                            textFieldValue = menuPrice,
                            title = "Harga",
                            keyboardType = KeyboardType.Number,
                            visualTransformation = {
                                amountTransformation(it.text)
                            },
                            errorMessage = if (!isValid && menuPrice.isEmpty()) "Harap isi harga" else null
                        ) {
                            menuPrice = it
                        }
                        FormTextField(
                            title = "Harga Promo",
                            hint = "Input Harga Promo",
                            textFieldValue = menuPromoPrice,
                            keyboardType = KeyboardType.Number,
                            visualTransformation = {
                                amountTransformation(it.text)
                            },
                            errorMessage = if (!isValid && menuPromoPrice.isNotEmpty() && (menuPrice.isNotEmpty() && menuPromoPrice > menuPrice)) "harga tidak boleh lebih dari harga normal" else null
                        ) {
                            menuPromoPrice = it
                            isValid = true
                        }
                        RestaurantCategoryField(categories = uiState.menuCategories,
                            errorMessage = if (!isValid && menuCategory.isEmpty()) "Harap pilih kategori" else null,
                            onSelected = {
                                menuCategory = it
                            })
                    }
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = UiColor.primaryRed500
                        )
                    }
                }
            }, bottomBar = {
                TemanFilledButton(
                    modifier = Modifier
                        .background(color = UiColor.white)
                        .fillMaxWidth()
                        .padding(
                            horizontal = Theme.dimension.size_16dp,
                            vertical = Theme.dimension.size_16dp
                        ),
                    content = "Simpan",
                    isEnabled = !uiState.isLoading,
                    buttonType = ButtonType.Medium,
                    activeColor = UiColor.primaryRed500,
                    borderRadius = Theme.dimension.size_30dp,
                    activeTextColor = UiColor.white
                ) {
                    if (menuName.isNotEmpty() && menuDescription.isNotEmpty() && menuPrice.isNotEmpty() && menuCategory.isNotEmpty() && menuPhoto != Uri.EMPTY) {
                        if (menuPromoPrice.isNotEmpty()) {
                            if (menuPromoPrice.toDouble() < menuPrice.toDouble()) {
                                viewModel.createRestaurantMenu(
                                    menuPhoto,
                                    menuName,
                                    menuDescription,
                                    menuPrice.toDouble(),
                                    menuCategory,
                                    menuPromoPrice,
                                    uriPath = profileImagePath
                                )
                            } else {
                                isValid = false
                            }
                        } else {
                            viewModel.createRestaurantMenu(
                                menuPhoto,
                                menuName,
                                menuDescription,
                                menuPrice.toDouble(),
                                menuCategory,
                                uriPath = profileImagePath
                            )
                        }
                    } else {
                        isValid = false
                    }
                }
            })
        })
}

@Composable
private fun RestaurantCategoryField(
    categories: List<MenuSpec>, onSelected: (String) -> Unit, errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    var textFiledSize by remember { mutableStateOf(Size.Zero) }
    var categoryName by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .padding(
                top = Theme.dimension.size_24dp
            )
            .fillMaxWidth()
    ) {
        Text("Kategori", style = UiFont.poppinsP2Medium)
        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
        OutlinedTextField(value = categoryName,
            enabled = false,
            textStyle = UiFont.poppinsP2Medium.copy(color = UiColor.neutral900),
            placeholder = {
                Text(
                    "Pilih Kategori",
                    style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500)
                )
            },
            shape = RoundedCornerShape(Theme.dimension.size_4dp),
            onValueChange = {
                // no op
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100,
                disabledBorderColor = UiColor.neutral100
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFiledSize = coordinates.size.toSize()
                }
                .noRippleClickable { expanded = !expanded },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            trailingIcon = {
                Icon(icon, "", Modifier.clickable {
                    expanded = !expanded
                })
            })
        errorMessage?.let {
            Text(
                it,
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.primaryRed500),
                modifier = Modifier.padding(top = Theme.dimension.size_4dp)
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }, modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })) {
            categories.forEach { item ->
                DropdownMenuItem(onClick = {
                    onSelected(item.categoryId)
                    categoryName = item.menuGroupName
                    expanded = false
                }) {
                    Text(
                        item.menuGroupName,
                        style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral900)
                    )
                }
            }
        }
    }
}