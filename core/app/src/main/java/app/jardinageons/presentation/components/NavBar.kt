package app.jardinageons.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import app.jardinageons.R

data class BottomBarDestination(
    val route: String,
    @StringRes val labelRes: Int,
    @DrawableRes val iconResId: Int
)

object BottomBarRoutes {
    const val HOME = "home"
    const val GARDEN = "garden"
    const val INVENTORY = "inventory"
    const val HISTORY = "history"
    const val STATS = "statistics"
    const val VEGETABLE = "vegetable"
}

val bottomBarDestinations = listOf(
    BottomBarDestination(route = BottomBarRoutes.HOME,      labelRes = R.string.nav_home,       iconResId = R.drawable.home),
    BottomBarDestination(route = BottomBarRoutes.GARDEN,    labelRes = R.string.nav_garden,     iconResId = R.drawable.potted_plant),
    BottomBarDestination(route = BottomBarRoutes.INVENTORY, labelRes = R.string.nav_inventory,  iconResId = R.drawable.inventory),
    BottomBarDestination(route = BottomBarRoutes.HISTORY,   labelRes = R.string.nav_history,    iconResId = R.drawable.clock_arrow_down),
    BottomBarDestination(route = BottomBarRoutes.STATS,     labelRes = R.string.nav_stats,      iconResId = R.drawable.stats),
    BottomBarDestination(route = BottomBarRoutes.VEGETABLE, labelRes = R.string.nav_vegetables, iconResId = R.drawable.watter),
)

@Composable
fun AppBottomBar(
    selectedRoute: String,
    onDestinationSelected: (String) -> Unit
) {
    NavigationBar {
        bottomBarDestinations.forEach { destination ->
            val label = stringResource(destination.labelRes)
            NavigationBarItem(
                selected = selectedRoute == destination.route,
                onClick = { onDestinationSelected(destination.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.iconResId),
                        contentDescription = label
                    )
                },
                label = { Text(label) }
            )
        }
    }
}
