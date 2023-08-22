package id.teman.app.mitra.ui.registration.driver

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.PagerIndicator
import id.teman.app.mitra.ui.destinations.RegistrationBasicInformationFormScreenDestination
import id.teman.app.mitra.ui.onboarding.OnBoardingState
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Destination
@Composable
fun RegistrationIllustrationScreen(navigator: DestinationsNavigator, selectedPartnerTitle: String) {
    val pages = listOf(
        OnBoardingState.FirstRegistrationOnBoarding,
        OnBoardingState.SecondRegistrationOnBoarding,
        OnBoardingState.ThirdRegistrationOnBoarding,
        OnBoardingState.FourthRegistrationOnBoarding
    )

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    count = pages.count(),
                    state = pagerState,
                    verticalAlignment = Alignment.Top
                ) { position ->
                    PagerScreen(onBoardingPage = pages[position])
                }
            }
        },
        bottomBar = {
            val bottomAlpha = if (pagerState.currentPage > 0) 1f else 0f
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = Theme.dimension.size_16dp)) {
                PagerIndicator(
                    pagerState = pagerState, modifier = Modifier
                        .padding(top = Theme.dimension.size_48dp)
                        .align(Alignment.CenterHorizontally),
                    indicatorCount = pages.count(),
                    activeColor = UiColor.neutral900,
                    indicatorSize = Theme.dimension.size_8dp
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_48dp))
                TemanFilledButton(
                    content = "Lanjutkan",
                    buttonType = ButtonType.Large,
                    activeColor = UiColor.primaryRed500,
                    activeTextColor = UiColor.white,
                    borderRadius = Theme.dimension.size_30dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Theme.dimension.size_24dp),
                    onClicked = {
                        if (pagerState.currentPage < 3) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            navigator.navigate(
                                RegistrationBasicInformationFormScreenDestination(
                                    selectedPartnerTitle = selectedPartnerTitle
                                )
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_26dp))
                Text(
                    "Kembali", style = UiFont.poppinsP3SemiBold.copy(color = UiColor.primaryRed500),
                    modifier = Modifier.alpha(bottomAlpha).align(Alignment.CenterHorizontally).clickable {
                        if (pagerState.currentPage - 1 >= 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }, textAlign = TextAlign.Center
                )
            }
        }
    )
}

@Composable
private fun PagerScreen(onBoardingPage: OnBoardingState) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    Column(
        modifier = Modifier.padding(
            Theme.dimension.size_24dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = Theme.dimension.size_8dp
                )
                .height((screenHeight * 0.4).dp),
        ) {
            GlideImage(
                imageModel = onBoardingPage.backgroundRes,
                modifier = Modifier.fillMaxSize(),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Fit
                )
            )
        }
        Text(
            onBoardingPage.title,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = Theme.dimension.size_16dp),
            style = UiFont.poppinsH3Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            onBoardingPage.subtitle,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = Theme.dimension.size_16dp),
            style = UiFont.poppinsSubHMedium,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}