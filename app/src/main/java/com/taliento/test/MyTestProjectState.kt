package com.taliento.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberMyTestProjectState(coroutineScope: CoroutineScope = rememberCoroutineScope(),
                               navController: NavHostController = rememberNavController()) : MyTestProjectState  {
    return remember(
        navController,
        coroutineScope) {
        MyTestProjectState(navController = navController,
            coroutineScope = coroutineScope)
    }
}

@Stable
class MyTestProjectState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    fun navigate(route: String) = navController.navigate(route)
}