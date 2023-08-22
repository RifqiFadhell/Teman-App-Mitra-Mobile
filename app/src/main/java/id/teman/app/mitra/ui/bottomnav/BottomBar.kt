package id.teman.app.mitra.ui.bottomnav

import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popBackStack
import com.ramcosta.composedestinations.navigation.popUpTo
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.ui.destinations.Destination
import id.teman.app.mitra.ui.destinations.FoodHomeScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BottomBar(navController: NavHostController, currentDestinations: Destination) {
    BottomNavigation(
        backgroundColor = Color.White,
    ) {
        BottomBarDestination.values().forEach { destination ->
            val selectedDestination = currentDestinations == destination.direction
            BottomNavigationItem(
                selected = selectedDestination,
                onClick = {
//                    if (currentDestinations == HomeScreenDestination) {
//                        navController.clearBackStack(HomeScreenDestination.route)
//                    }
                    if (selectedDestination) {
                        // When we click again on a bottom bar item and it was already selected
                        // we want to pop the back stack until the initial destination of this bottom bar item
                        navController.popBackStack(destination.direction, false)
                        return@BottomNavigationItem
                    }

                    navController.navigate(destination.direction) {
                        // Pop up to the root of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(FoodHomeScreenDestination) {
                            saveState = true
                        }

                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    GlideImage(
                        imageModel = destination.icon,
                        modifier = Modifier.size(Theme.dimension.size_20dp),
                        imageOptions = ImageOptions(
                            colorFilter = ColorFilter.tint(
                                color = if (selectedDestination) UiColor.primaryRed500 else UiColor.neutral200
                            )
                        )
                    )
                },
                label = {
                    Text(
                        stringResource(id = destination.label),
                        style = UiFont.poppinsCaptionSmallSemiBold
                    )
                }
            )
        }
    }
}