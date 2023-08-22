package id.teman.app.mitra.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.PagerIndicator
import id.teman.app.mitra.ui.destinations.LoginScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButtonCLicked
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(navigator: DestinationsNavigator, viewModel: OnboardingViewModel = hiltViewModel()) {

    val pages = listOf(
        OnBoardingState.FirstOnBoarding,
        OnBoardingState.SecondOnBoarding
    )

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                TemanLogo()
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
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = Theme.dimension.size_48dp)) {
                PagerIndicator(
                    pagerState = pagerState, modifier = Modifier
                        .padding(top = Theme.dimension.size_24dp)
                        .align(Alignment.CenterHorizontally),
                    indicatorCount = pages.count(),
                    activeColor = UiColor.neutral900,
                    indicatorSize = Theme.dimension.size_8dp
                )

                TemanCircleButtonCLicked(
                    icon = R.drawable.ic_arrow_right,
                    iconColor = UiColor.white,
                    circleBackgroundColor = UiColor.primaryRed500,
                    circleModifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = Theme.dimension.size_60dp)
                        .size(Theme.dimension.size_68dp),
                    iconModifier = Modifier.size(Theme.dimension.size_28dp),
                    onClick = {
                        if (pagerState.currentPage < pages.count() - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            viewModel.setUserHasSeenOnboarding()
                            navigator.navigate(LoginScreenDestination)
                        }
                    })
            }

        }
    )
}

@Composable
private fun PagerScreen(onBoardingPage: OnBoardingState) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    Column(modifier = Modifier.height(screenHeight * 0.8f)) {
        GlideImage(
            imageModel = onBoardingPage.backgroundRes,
            modifier = Modifier.height(screenHeight * 0.3f),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit
            )
        )

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

@Composable
fun ColumnScope.TemanLogo() {
    GlideImage(
        imageModel = R.drawable.ic_revamped_teman,
        modifier = Modifier
            .size(Theme.dimension.size_120dp)
            .align(Alignment.CenterHorizontally),
        imageOptions = ImageOptions(contentScale = ContentScale.Fit)
    )
}