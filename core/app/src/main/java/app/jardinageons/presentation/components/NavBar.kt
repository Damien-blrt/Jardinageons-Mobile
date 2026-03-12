package app.jardinageons.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import app.jardinageons.R

data class BottomBarDestination(
    val route: String,
    val label: String,
    val iconResId: Int
)

object BottomBarRoutes {
    const val HOME = "home"
    const val GARDEN = "garden"
    const val INVENTORY = "inventory"
    const val HISTORY = "history"
    const val STATS = "statistics"
}

val bottomBarDestinations = listOf(
    BottomBarDestination(
        route = BottomBarRoutes.HOME,
        label = "Accueil",
        iconResId = R.drawable.home
    ),
    BottomBarDestination(
        route = BottomBarRoutes.GARDEN,
        label = "Potager",
        iconResId = R.drawable.potted_plant
    ),
    BottomBarDestination(
        route = BottomBarRoutes.INVENTORY,
        label = "Inventaire",
        iconResId = R.drawable.inventory
    ),
    BottomBarDestination(
        route = BottomBarRoutes.HISTORY,
        label = "Historique",
        iconResId = R.drawable.clock_arrow_down
    ),
    BottomBarDestination(
        route = BottomBarRoutes.STATS,
        label = "Stats",
        iconResId = R.drawable.stats
)
)

@Composable
fun AppBottomBar(
    selectedRoute: String,
    onDestinationSelected: (String) -> Unit
) {
    NavigationBar {
        bottomBarDestinations.forEach { destination ->
            NavigationBarItem(
                selected = selectedRoute == destination.route,
                onClick = { onDestinationSelected(destination.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.iconResId),
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) }
            )
        }
    }
}
