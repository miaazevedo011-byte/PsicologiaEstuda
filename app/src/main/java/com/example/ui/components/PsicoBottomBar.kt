package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.AppTab

@Composable
fun PsicoBottomBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier.testTag("bottom_nav_bar"),
        containerColor = SurfaceContainerLowest.copy(alpha = 0.95f),
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple(AppTab.INICIO, "Início", Icons.Default.Dashboard),
            Triple(AppTab.ENCICLOPEDIA, "Enciclopédia", Icons.Default.MenuBook),
            Triple(AppTab.DISCIPLINAS, "Disciplinas", Icons.Default.Psychology),
            Triple(AppTab.FLASHCARDS, "Flashcards", Icons.Default.Style),
            Triple(AppTab.MATERIAIS, "Materiais", Icons.Default.FolderShared)
        )

        items.forEach { (tab, label, icon) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PinePrimary,
                    selectedTextColor = PinePrimary,
                    indicatorColor = SageSecondaryContainer,
                    unselectedIconColor = TextOnSurfaceVariant,
                    unselectedTextColor = TextOnSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_${tab.route}")
            )
        }
    }
}
