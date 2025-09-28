package com.radiantbyte.moddedpe.plus.mcpelauncher.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

@Composable
fun <P : Enum<P>> SimpleSidebar(
    items: List<SidebarItem<P>>,
    selected: P,
    onSelect: (P) -> Unit
) {
    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        header = {}
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach { item ->
                NavigationRailItem(
                    selected = item.page == selected,
                    onClick = { onSelect(item.page) },
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(item.label) },
                    alwaysShowLabel = false
                )
            }
        }
    }
}

data class SidebarItem<P>(
    val page: P,
    val label: String,
    val icon: ImageVector
)