package id.teman.app.mitra.ui.bottomnav

import id.teman.app.mitra.R as RApp
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import id.teman.app.mitra.ui.destinations.FoodHomeScreenDestination
import id.teman.app.mitra.ui.destinations.FoodOrderListScreenDestination
import id.teman.app.mitra.ui.destinations.MenuScreenDestination
import id.teman.app.mitra.ui.destinations.MyAccountScreenDestination

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
) {
    Home(FoodHomeScreenDestination, RApp.drawable.ic_nav_bottom_home, RApp.string.nav_bottom_home),
    Order(FoodOrderListScreenDestination, RApp.drawable.ic_nav_bottom_order, RApp.string.nav_bottom_order),
    Menu(MenuScreenDestination, RApp.drawable.ic_nav_bottom_menu, RApp.string.nav_bottom_menu),
    Account(MyAccountScreenDestination, RApp.drawable.ic_nav_bottom_account, RApp.string.nav_bottom_account)
}