package com.mcal.mcpelauncher.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.mcal.mcpelauncher.ui.pages.AboutPage
import com.mcal.mcpelauncher.ui.pages.HomePage
import com.mcal.mcpelauncher.ui.pages.NModsPage
import com.mcal.mcpelauncher.ui.pages.SettingsPage
import com.mcal.mcpelauncher.ui.component.SimpleSidebar
import com.mcal.mcpelauncher.ui.component.SidebarItem

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

@Immutable
enum class ModMainPages(val label: String, val icon: ImageVector) {
	Home("Home", Icons.Rounded.Home),
	Manage("NMods", Icons.Rounded.Build),
	Settings("Settings", Icons.Rounded.Settings),
	About("About", Icons.Rounded.Info)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
	var selected by remember { mutableStateOf(ModMainPages.Home) }

	Row(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		val items = remember {
			ModMainPages.entries.map { SidebarItem(it, it.label, it.icon) }
		}
		SimpleSidebar(items = items, selected = selected, onSelect = { selected = it })

		MainContent(selected = selected, onSelect = { selected = it })
	}
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun RowScope.MainContent(selected: ModMainPages, onSelect: (ModMainPages) -> Unit) {
	Box(
		modifier = Modifier
			.weight(1f)
			.fillMaxHeight()
	) {
		AnimatedContent(
			targetState = selected,
			transitionSpec = {
				(slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400, easing = FastOutSlowInEasing)) +
						fadeIn(animationSpec = tween(400))) togetherWith
						(slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400, easing = FastOutSlowInEasing)) +
								fadeOut(animationSpec = tween(400)))
			},
			label = "main_page"
		) { page ->
			when (page) {
				ModMainPages.Home -> HomePage(onOpenAbout = { onSelect(ModMainPages.About) })
				ModMainPages.Manage -> NModsPage()
				ModMainPages.Settings -> SettingsPage()
				ModMainPages.About -> AboutPage()
			}
		}
	}
}


