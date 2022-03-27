package com.chatty.compose.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun MyBottomNavigationBar(
    selectedScreen: Int,
    screens: List<Screens>,
    onClick: (targetIndex: Int) -> Unit
) {
    BottomNavigation(
        backgroundColor = Color.White
    ) {
        screens.forEachIndexed { index, screen ->
            BottomNavigationItem(
                selected = selectedScreen == index,
                onClick = { onClick(index) },
                icon = { Icon(painterResource(screen.resId), contentDescription = null, modifier = Modifier.size(24.dp)) },
                label = { Text(screen.label) },
            )
        }
    }
}

data class Screens(
    val label: String,
    val resId: Int,
    val content: @Composable () -> Unit
)