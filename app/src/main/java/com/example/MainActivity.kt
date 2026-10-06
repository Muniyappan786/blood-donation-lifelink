package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AdminManagementScreen
import com.example.ui.screens.ApiEngineScreen
import com.example.ui.screens.BloodBanksScreen
import com.example.ui.screens.DonorRegistrationScreen
import com.example.ui.screens.DonorsScreen
import com.example.ui.screens.EducationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SosBroadcastsScreen
import com.example.ui.theme.BloodBridgeTheme
import com.example.ui.theme.BloodRed20
import com.example.ui.theme.BloodRed40
import com.example.ui.theme.BloodRed90
import com.example.viewmodel.BloodViewModel

enum class AppScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    BANKS("Banks", Icons.Filled.LocalHospital, Icons.Outlined.LocalHospital),
    DONORS("Donors", Icons.Filled.VolunteerActivism, Icons.Outlined.VolunteerActivism),
    SOS("SOS Alerts", Icons.Filled.AddAlert, Icons.Outlined.AddAlert),
    REGISTER("Intake", Icons.Filled.Person, Icons.Filled.Person),
    API_ENGINE("Mode A", Icons.Filled.Code, Icons.Outlined.Code),
    ADMIN("Admin", Icons.Filled.Shield, Icons.Outlined.Shield),
    LEARN("Guidelines", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
}

class MainActivity : ComponentActivity() {

    private val viewModel: BloodViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BloodBridgeTheme {
                var currentScreen by rememberSaveable { mutableStateOf(AppScreen.HOME) }

                BackHandler(enabled = currentScreen != AppScreen.HOME) {
                    currentScreen = AppScreen.HOME
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(BloodRed40)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "BloodBridge",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 19.sp,
                                        color = BloodRed20
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${currentScreen.title}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            tonalElevation = 6.dp
                        ) {
                            val bottomNavScreens = listOf(
                                AppScreen.HOME,
                                AppScreen.BANKS,
                                AppScreen.DONORS,
                                AppScreen.SOS,
                                AppScreen.API_ENGINE,
                                AppScreen.ADMIN
                            )

                            bottomNavScreens.forEach { screen ->
                                val selected = currentScreen == screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentScreen = screen },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = BloodRed20,
                                        selectedTextColor = BloodRed20,
                                        indicatorColor = BloodRed90
                                    ),
                                    modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            AppScreen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToSos = { currentScreen = AppScreen.SOS },
                                onNavigateToDonors = { currentScreen = AppScreen.DONORS },
                                onNavigateToBanks = { currentScreen = AppScreen.BANKS },
                                onNavigateToRegister = { currentScreen = AppScreen.REGISTER },
                                onNavigateToApiEngine = { currentScreen = AppScreen.API_ENGINE },
                                onNavigateToEducation = { currentScreen = AppScreen.LEARN }
                            )
                            AppScreen.BANKS -> BloodBanksScreen(
                                viewModel = viewModel
                            )
                            AppScreen.DONORS -> DonorsScreen(
                                viewModel = viewModel
                            )
                            AppScreen.SOS -> SosBroadcastsScreen(
                                viewModel = viewModel
                            )
                            AppScreen.REGISTER -> DonorRegistrationScreen(
                                viewModel = viewModel,
                                onRegistrationSuccess = { currentScreen = AppScreen.DONORS }
                            )
                            AppScreen.API_ENGINE -> ApiEngineScreen(
                                viewModel = viewModel
                            )
                            AppScreen.ADMIN -> AdminManagementScreen(
                                viewModel = viewModel
                            )
                            AppScreen.LEARN -> EducationScreen()
                        }
                    }
                }
            }
        }
    }
}
