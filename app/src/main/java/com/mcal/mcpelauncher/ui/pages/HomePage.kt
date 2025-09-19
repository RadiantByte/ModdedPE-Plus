package com.mcal.mcpelauncher.ui.pages

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.app.AlertDialog
import com.mcal.mcpelauncher.BuildConfig
import com.mcal.mcpelauncher.ModdedPEApplication
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.activities.ComposePreloadActivity
import com.mcal.mcpelauncher.data.Preferences

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

@Composable
fun HomePage(onOpenAbout: () -> Unit = {}) {
	val context = LocalContext.current
	val minecraftInfo = ModdedPEApplication.getMPESdk().getMinecraftInfo()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		verticalArrangement = Arrangement.spacedBy(20.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = context.getString(R.string.app_name), 
			style = MaterialTheme.typography.headlineLarge,
			fontWeight = FontWeight.Bold
		)

		Card(
			modifier = Modifier.fillMaxWidth(),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surfaceVariant
			)
		) {
			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Text(
					text = "Minecraft Information",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold
				)
				
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(text = "Status:")
					Text(
						text = if (minecraftInfo.isMinecraftInstalled()) "Installed" else "Not Found",
						color = if (minecraftInfo.isMinecraftInstalled()) 
							MaterialTheme.colorScheme.primary 
						else MaterialTheme.colorScheme.error
					)
				}
				
				if (minecraftInfo.isMinecraftInstalled()) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text(text = "Version:")
						Text(text = minecraftInfo.minecraftVersionName ?: "Unknown")
					}
					
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text(text = "Package:")
						Text(text = "com.mojang.minecraftpe")
					}
				}
			}
		}

		if (Preferences.isSafeMode) {
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.errorContainer
				)
			) {
				Row(
					modifier = Modifier.padding(16.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						imageVector = Icons.Default.Warning,
						contentDescription = "Safe Mode",
						tint = MaterialTheme.colorScheme.onErrorContainer
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = "Safe Mode Enabled",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onErrorContainer
					)
				}
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		Button(
			onClick = {
				if (!minecraftInfo.isMinecraftInstalled()) {
					AlertDialog.Builder(context)
						.setTitle(R.string.no_mcpe_found_title)
						.setMessage(R.string.no_mcpe_found)
						.setPositiveButton(android.R.string.cancel) { d, _ -> d.dismiss() }
						.show()
				} else if (!minecraftInfo.isSupportedMinecraftVersion(
						context.resources.getStringArray(R.array.target_mcpe_versions)
					)) {
					AlertDialog.Builder(context)
						.setTitle(R.string.no_available_mcpe_version_found_title)
						.setMessage(context.getString(R.string.no_available_mcpe_version_found,
							minecraftInfo.minecraftVersionName,
							context.getString(R.string.app_game) + " " + BuildConfig.VERSION_NAME))
						.setNegativeButton(android.R.string.cancel) { d, _ -> d.dismiss() }
						.setPositiveButton(R.string.no_available_mcpe_version_continue) { d, _ ->
							startMinecraft(context)
							d.dismiss()
						}
						.show()
				} else {
					startMinecraft(context)
				}
			},
			modifier = Modifier.fillMaxWidth()
		) {
			Icon(
				imageVector = Icons.Default.PlayArrow,
				contentDescription = "Launch"
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(text = "Launch Minecraft")
		}

		OutlinedButton(
			onClick = onOpenAbout,
			modifier = Modifier.fillMaxWidth()
		) {
			Icon(
				imageVector = Icons.Default.Info,
				contentDescription = "About"
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(text = context.getString(R.string.about_title))
		}
	}
}

private fun startMinecraft(context: android.content.Context) {
	if (Preferences.isSafeMode) {
		AlertDialog.Builder(context)
			.setTitle(R.string.safe_mode_on_title)
			.setMessage(R.string.safe_mode_on_message)
			.setPositiveButton(android.R.string.ok) { d, _ ->
				context.startActivity(Intent(context, ComposePreloadActivity::class.java))
				d.dismiss()
			}
			.setNegativeButton(android.R.string.cancel) { d, _ -> d.dismiss() }
			.show()
	} else {
		context.startActivity(Intent(context, ComposePreloadActivity::class.java))
	}
}