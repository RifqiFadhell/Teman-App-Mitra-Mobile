package id.teman.app.mitra.ui.food.menu

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CenteredTopNavigation
import id.teman.app.mitra.ui.registration.common.FormTextField
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor

@Destination
@Composable
fun AddNewCategoryScreen(navigator: DestinationsNavigator, viewModel: FoodFormViewModel = hiltViewModel()) {
    var isValid by rememberSaveable { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    var categoryName by rememberSaveable { mutableStateOf("") }
    var categoryDescription by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val uiState = viewModel.uiState

    LaunchedEffect(key1 = uiState.error, block = {
        uiState.error?.consumeOnce {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            navigator.popBackStack()
        }
    })

    LaunchedEffect(key1 = uiState.success, block = {
        uiState.success?.consumeOnce {
            Toast.makeText(context, "Sukses menambahkan kategori", Toast.LENGTH_SHORT).show()
            navigator.popBackStack()
        }
    })

    Scaffold(topBar = {
        CenteredTopNavigation(title = "Tambah Kategori Baru") {
            navigator.popBackStack()
        }
    }, content = {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(Theme.dimension.size_16dp)
            ) {
                FormTextField(
                    title = "Nama Kategori",
                    hint = "Masukkan nama kategori",
                    keyboardType = KeyboardType.Text,
                    textFieldValue = categoryName,
                    errorMessage = if (!isValid && categoryName.isEmpty()) "Mohon masukkan nama kategori" else null
                ) {
                    categoryName = it
                }

                FormTextField(
                    title = "Deskripsi",
                    hint = "Deskripsi kategori",
                    keyboardType = KeyboardType.Text,
                    textFieldValue = categoryDescription,
                    errorMessage = if (!isValid && categoryDescription.isEmpty()) "Mohon masukkan deskripsi kategori" else null
                ) {
                    categoryDescription = it
                }
            }
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        }
    }, bottomBar = {
        TemanFilledButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Theme.dimension.size_16dp, vertical = Theme.dimension.size_16dp
                ),
            content = "Simpan",
            buttonType = ButtonType.Medium,
            activeColor = UiColor.primaryRed500,
            borderRadius = Theme.dimension.size_30dp,
            activeTextColor = UiColor.white
        ) {
            if (uiState.isLoading) return@TemanFilledButton
            if (categoryDescription.isNotEmpty() && categoryName.isNotEmpty()) {
                viewModel.createRestaurantCategory(categoryName, categoryDescription)
            } else {
                isValid = false
            }
        }
    })
}