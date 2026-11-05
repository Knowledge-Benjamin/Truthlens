package com.truthlens.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.truthlens.ui.screens.ActivityScreen
import com.truthlens.ui.screens.DiscoverScreen
import com.truthlens.ui.screens.HomeScreen
import com.truthlens.ui.screens.ProfileScreen

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        HomeScreen()
    }
}

object DiscoverTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Discover"
            val icon = rememberVectorPainter(Icons.Default.Search)
            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        DiscoverScreen()
    }
}

object ActivityTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Activity"
            val icon = rememberVectorPainter(Icons.Default.DateRange)
            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        ActivityScreen()
    }
}

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Profile"
            val icon = rememberVectorPainter(Icons.Default.Person)
            return remember {
                TabOptions(
                    index = 3u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        ProfileScreen()
    }
}
