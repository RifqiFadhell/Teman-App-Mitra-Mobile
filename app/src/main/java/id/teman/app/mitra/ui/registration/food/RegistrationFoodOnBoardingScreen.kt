package id.teman.app.mitra.ui.registration.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.parseBold
import id.teman.app.mitra.ui.destinations.RegistrationFoodChecklistScreenDestination
import id.teman.app.mitra.ui.registration.uimodel.FoodOnBoardingState
import id.teman.app.mitra.ui.registration.uimodel.firstOnBoardingUiModel
import id.teman.app.mitra.ui.registration.uimodel.secondOnBoardingUiModel
import id.teman.app.mitra.ui.registration.uimodel.thirdOnBoardingUiModel
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Destination
@Composable
fun RegistrationFoodOnBoardingScreen(
    navigator: DestinationsNavigator
) {
    val pages = listOf(
        firstOnBoardingUiModel,
        secondOnBoardingUiModel,
        thirdOnBoardingUiModel
    )

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        content = {
            HorizontalPager(
                count = pages.count(),
                state = pagerState,
                verticalAlignment = Alignment.Top
            ) { position ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = Theme.dimension.size_16dp,
                            vertical = Theme.dimension.size_16dp
                        ),
                    contentPadding = PaddingValues(bottom = Theme.dimension.size_80dp)
                ) {
                    item {
                        TemanCircleButton(
                            icon = R.drawable.ic_teman_food,
                            iconModifier = Modifier.size(Theme.dimension.size_28dp),
                            circleBackgroundColor = UiColor.primaryYellow50,
                            circleModifier = Modifier
                                .size(Theme.dimension.size_56dp)
                        )
                    }
                    items(pages[position]) { item -> RenderItem(state = item) }
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimension.size_16dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (pagerState.currentPage > 0) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        shape = RoundedCornerShape(
                            Theme.dimension.size_30dp
                        ),
                        border = BorderStroke(
                            color = UiColor.primaryRed500,
                            width = Theme.dimension.size_1dp
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = Theme.dimension.size_0dp
                        ),
                        content = {
                            Text(
                                "Sebelumnya",
                                style = UiFont.poppinsP1SemiBold.copy(color = UiColor.primaryRed500),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = {
                            if (pagerState.currentPage > 0) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage-1)
                                }
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                TemanFilledButton(
                    modifier = Modifier.weight(1f),
                    content = if (pagerState.currentPage > 1) "Daftar Sekarang" else "Lanjut",
                    buttonType = ButtonType.Medium,
                    activeColor = UiColor.primaryRed500,
                    borderRadius = Theme.dimension.size_30dp,
                    isEnabled = true,
                    activeTextColor = UiColor.white
                ) {
                    if (pagerState.currentPage < 2) {
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        navigator.navigate(RegistrationFoodChecklistScreenDestination)
                    }
                }
            }
        }
    )

}

@Composable
private fun RenderItem(state: FoodOnBoardingState) {
    when (state) {
        is FoodOnBoardingState.Item -> {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = Theme.dimension.size_28dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TemanCircleButton(
                    icon = state.value.icon,
                    circleModifier = Modifier.size(Theme.dimension.size_56dp),
                    iconModifier = Modifier.size(Theme.dimension.size_32dp),
                    circleBackgroundColor = UiColor.primaryRed50
                )
                Spacer(modifier = Modifier.padding(start = Theme.dimension.size_16dp))
                Text(state.value.title.parseBold(), style = UiFont.poppinsP2Medium)
            }
        }
        is FoodOnBoardingState.SectionTitle -> Text(
            state.value,
            style = UiFont.poppinsP3SemiBold,
            modifier = Modifier.padding(top = Theme.dimension.size_32dp)
        )
        is FoodOnBoardingState.Subtitle -> Text(
            state.value,
            style = UiFont.poppinsP3Medium,
            modifier = Modifier.padding(top = Theme.dimension.size_16dp)
        )
        is FoodOnBoardingState.Title -> Text(
            state.value,
            style = UiFont.poppinsH3sSemiBold,
            modifier = Modifier.padding(top = Theme.dimension.size_8dp)
        )
    }
}