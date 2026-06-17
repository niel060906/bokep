package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.home.*
import com.example.ui.player.MiniPlayer
import com.example.ui.player.PlayerViewModel
import com.example.ui.theme.MusicStreamTheme
import com.example.ui.search.SearchViewModel
import com.example.ui.favorite.LibraryScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicStreamTheme {
                MainContainer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer() {
    val context = LocalContext.current
    val app = context.applicationContext as MusicApplication
    
    val navController = rememberNavController()
    
    // Manual ViewModel creation instead of hiltViewModel()
    val homeViewModel: HomeViewModel = viewModel(
        factory = viewModelFactory { HomeViewModel(app.musicRepository, app.musicController) }
    )
    val playerViewModel: PlayerViewModel = viewModel(
        factory = viewModelFactory { PlayerViewModel(app.musicController, app.favoriteRepository) }
    )
    val searchViewModel: SearchViewModel = viewModel(
        factory = viewModelFactory { SearchViewModel(app.musicRepository, app.musicController) }
    )

    val currentSong by homeViewModel.currentSong.collectAsState()
    val isPlaying by homeViewModel.isPlaying.collectAsState()
    val currentPosition by homeViewModel.currentPosition.collectAsState()
    val duration by homeViewModel.duration.collectAsState()
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPlayerSheet by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (showPlayerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPlayerSheet = false },
            sheetState = sheetState,
            dragHandle = null,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            com.example.ui.player.FullPlayer(viewModel = playerViewModel, onCollapse = { showPlayerSheet = false })
        }
    }

    Scaffold(
        bottomBar = {
            Column {
                currentSong?.let { song ->
                    MiniPlayer(
                        song = song,
                        isPlaying = isPlaying,
                        currentPosition = currentPosition,
                        duration = duration,
                        onTogglePlay = { homeViewModel.togglePlayPause() },
                        onClick = { showPlayerSheet = true }
                    )
                }
                HorizontalDivider(color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.1f), thickness = 0.5.dp)
                NavigationBar(
                    containerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.8f)
                ) {
                    BottomNavItem.entries.forEach { item ->
                        val selected = currentDestination?.route == item.route
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)) },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.4f),
                                unselectedTextColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.4f),
                                indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                            ),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(viewModel = homeViewModel)
            }
            composable(BottomNavItem.Search.route) {
                com.example.ui.search.SearchScreen(viewModel = searchViewModel)
            }
            composable(BottomNavItem.Favorites.route) {
                LibraryScreen(viewModel = homeViewModel)
            }
        }
    }
}

// Utility for providing ViewModels without Hilt
@Suppress("UNCHECKED_CAST")
fun <VM : androidx.lifecycle.ViewModel> viewModelFactory(initializer: () -> VM): androidx.lifecycle.ViewModelProvider.Factory {
    return object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return initializer() as T
        }
    }
}

enum class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    Home("home", Icons.Default.Home, "Listen Now"),
    Search("search", Icons.Default.Search, "Search"),
    Favorites("favorites", Icons.Default.Favorite, "Library")
}
