package com.example.wappo_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}

@Composable
fun Main() {
    val navController = rememberNavController()
    Column(Modifier.padding(8.dp)) {
        NavHost(
            navController,
            startDestination = NavRoutes.SelectLevel.route,
            modifier = Modifier.weight(1f)
        ) {
            composable(NavRoutes.SelectLevel.route) { Select_level() }
            composable(NavRoutes.MakeCustomLevel.route) { Make_custom_level() }
            composable(NavRoutes.Settings.route) { Settings() }
        }
        BottomNavigationBar(navController = navController)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(navItem.image),
                        contentDescription = navItem.title,
                        modifier = Modifier.size(48.dp)
                    )
                },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Select level",
            image = R.drawable.magnifying_glass,
            route = "select level"
        ),
        BarItem(
            title = "Make custom level",
            image = R.drawable.wrench,
            route = "make custom level"
        ),
        BarItem(
            title = "Settings",
            image = R.drawable.settings,
            route = "settings"
        )
    )
}

data class BarItem(
    val title: String,
    val image: Int,
    val route: String,
)

@Composable
fun Select_level() {
    Text("Select level", fontSize = 30.sp)
}

@Composable
fun Make_custom_level() {
    Text("Make custom level", fontSize = 30.sp)
}

@Composable
fun Settings() {
    Text("Settings", fontSize = 30.sp)


}

sealed class NavRoutes(val route: String) {
    object SelectLevel : NavRoutes("select level")
    object MakeCustomLevel : NavRoutes("make custom level")
    object Settings : NavRoutes("settings")
}