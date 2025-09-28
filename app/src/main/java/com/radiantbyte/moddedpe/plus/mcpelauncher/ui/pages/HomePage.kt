package com.radiantbyte.moddedpe.plus.mcpelauncher.ui.pages

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radiantbyte.moddedpe.plus.R
import com.radiantbyte.moddedpe.plus.BuildConfig
import com.radiantbyte.moddedpe.plus.mcpelauncher.ModdedPEApplication
import com.radiantbyte.moddedpe.plus.mcpelauncher.activities.ComposePreloadActivity
import com.radiantbyte.moddedpe.plus.mcpelauncher.data.Preferences
import com.radiantbyte.moddedpe.plus.pesdk.somod.SoModManager

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

@SuppressLint("LocalContextResourcesRead")
@Composable
fun HomePage(onOpenAbout: () -> Unit = {}) {
	val context = LocalContext.current
	val minecraftInfo = ModdedPEApplication.getMPESdk().getMinecraftInfo()

	val infiniteTransition = rememberInfiniteTransition(label = "home_animation")
	val pulseScale by infiniteTransition.animateFloat(
		initialValue = 0.95f,
		targetValue = 1.05f,
		animationSpec = infiniteRepeatable(
			animation = tween(2000, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse
		),
		label = "pulse"
	)

	val shimmerAlpha by infiniteTransition.animateFloat(
		initialValue = 0.3f,
		targetValue = 0.7f,
		animationSpec = infiniteRepeatable(
			animation = tween(1500, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse
		),
		label = "shimmer"
	)

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(
				brush = Brush.verticalGradient(
					colors = listOf(
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
					)
				)
			)
			.padding(20.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Card(
				modifier = Modifier
					.weight(1f)
					.fillMaxHeight(),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
			) {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(16.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					Box(
						contentAlignment = Alignment.Center
					) {
						Box(
							modifier = Modifier
								.size(48.dp)
								.scale(shimmerAlpha)
								.clip(RoundedCornerShape(24.dp))
								.background(
									brush = Brush.radialGradient(
										colors = listOf(
											MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
											Color.Transparent
										)
									)
								)
						)

						Icon(
							imageVector = Icons.Default.Home,
							contentDescription = "ModdedPE",
							modifier = Modifier
								.size(28.dp)
								.scale(pulseScale),
							tint = MaterialTheme.colorScheme.primary
						)
					}

					Spacer(modifier = Modifier.height(8.dp))

					Text(
						text = context.getString(R.string.app_name),
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)

					Text(
						text = "Enhanced Minecraft Launcher",
						style = MaterialTheme.typography.labelSmall,
						color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
					)
				}
			}

			Card(
				modifier = Modifier
					.weight(1f)
					.fillMaxHeight(),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
			) {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(12.dp)
				) {
					Text(
						text = "Quick Actions",
						style = MaterialTheme.typography.titleSmall,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.onSecondaryContainer
					)

					Button(
						onClick = {
							if (!minecraftInfo.isMinecraftInstalled()) {
								AlertDialog.Builder(context)
									.setTitle(R.string.no_mcpe_found_title)
									.setMessage(R.string.no_mcpe_found)
									.setPositiveButton(android.R.string.cancel) { d, _ -> d.dismiss() }
									.show()
							} else {
								startMinecraft(context)
							}
						},
						modifier = Modifier
							.fillMaxWidth()
							.height(44.dp),
						colors = ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.primary,
							contentColor = MaterialTheme.colorScheme.onPrimary
						),
						elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
					) {
						Icon(
							imageVector = Icons.Default.PlayArrow,
							contentDescription = "Launch",
							modifier = Modifier.size(16.dp)
						)
						Spacer(modifier = Modifier.width(6.dp))
						Text(
							text = "Launch Minecraft",
							fontSize = 13.sp,
							fontWeight = FontWeight.SemiBold
						)
					}

					OutlinedButton(
						onClick = onOpenAbout,
						modifier = Modifier
							.fillMaxWidth()
							.height(40.dp)
					) {
						Icon(
							imageVector = Icons.Default.Info,
							contentDescription = "About",
							modifier = Modifier.size(14.dp)
						)
						Spacer(modifier = Modifier.width(6.dp))
						Text(
							text = context.getString(R.string.about_title),
							fontSize = 12.sp
						)
					}

					if (Preferences.isSafeMode) {
						Card(
							modifier = Modifier.fillMaxWidth(),
							colors = CardDefaults.cardColors(
								containerColor = MaterialTheme.colorScheme.errorContainer
							),
							elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
						) {
							Row(
								modifier = Modifier.padding(8.dp),
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(6.dp)
							) {
								Icon(
									imageVector = Icons.Default.Warning,
									contentDescription = "Safe Mode",
									tint = MaterialTheme.colorScheme.onErrorContainer,
									modifier = Modifier.size(12.dp)
								)
								Text(
									text = "Safe Mode Active",
									style = MaterialTheme.typography.labelSmall,
									fontWeight = FontWeight.SemiBold,
									color = MaterialTheme.colorScheme.onErrorContainer
								)
							}
						}
					}
				}
			}
		}

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Card(
				modifier = Modifier
					.weight(1f)
					.fillMaxHeight(),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surfaceVariant
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
			) {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(10.dp)
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(6.dp)
					) {
						Icon(
							imageVector = Icons.Default.Games,
							contentDescription = "Minecraft Info",
							tint = MaterialTheme.colorScheme.primary,
							modifier = Modifier.size(16.dp)
						)
						Text(
							text = "Minecraft Information",
							style = MaterialTheme.typography.titleSmall,
							fontWeight = FontWeight.SemiBold
						)
					}

					HorizontalDivider(
						Modifier,
						DividerDefaults.Thickness,
						color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
					)

					InfoRow(
						label = "Status",
						value = if (minecraftInfo.isMinecraftInstalled()) "Installed" else "Not Found",
						valueColor = if (minecraftInfo.isMinecraftInstalled())
							MaterialTheme.colorScheme.primary
						else MaterialTheme.colorScheme.error,
						icon = if (minecraftInfo.isMinecraftInstalled())
							Icons.Default.CheckCircle
						else Icons.Default.Error
					)

					if (minecraftInfo.isMinecraftInstalled()) {
						InfoRow(
							label = "Version",
							value = minecraftInfo.minecraftVersionName ?: "Unknown",
							icon = Icons.Default.Info
						)

						InfoRow(
							label = "Package",
							value = "com.mojang.minecraftpe",
							icon = Icons.Default.Apps
						)
					}
				}
			}

			Card(
				modifier = Modifier
					.weight(1f)
					.fillMaxHeight(),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
			) {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(10.dp)
				) {
					Text(
						text = "System Info",
						style = MaterialTheme.typography.titleSmall,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.onTertiaryContainer
					)

					SystemInfoRow(
						label = "ModdedPE Version",
						value = BuildConfig.VERSION_NAME
					)

					SystemInfoRow(
						label = "Build Type",
						value = if (BuildConfig.DEBUG) "Debug" else "Release"
					)

					val soModManager = remember { SoModManager(context) }
					SystemInfoRow(
						label = "Active Mods",
						value = "${soModManager.mods.count { it.isEnabled }}"
					)
				}
			}
		}
	}
}

@Composable
private fun InfoRow(
	label: String,
	value: String,
	valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
	icon: ImageVector? = null
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = label,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(4.dp)
		) {
			if (icon != null) {
				Icon(
					imageVector = icon,
					contentDescription = null,
					tint = valueColor,
					modifier = Modifier.size(14.dp)
				)
			}
			Text(
				text = value,
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Medium,
				color = valueColor
			)
		}
	}
}

@Composable
private fun SystemInfoRow(
	label: String,
	value: String
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(
			text = label,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
		)
		Text(
			text = value,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			color = MaterialTheme.colorScheme.onTertiaryContainer
		)
	}
}

private fun startMinecraft(context: Context) {
	val soModManager = SoModManager(context)
	soModManager.cleanCache(context)

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