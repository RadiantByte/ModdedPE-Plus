package com.mcal.mcpelauncher.ui.pages

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mcal.mcpelauncher.ModdedPEApplication
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.activities.ComposeFilePickerActivity
import com.mcal.mcpelauncher.activities.ComposePackagePickerActivity
import com.mcal.mcpelauncher.ui.view.Dialogs
import com.mcal.pesdk.nmod.NMod
import com.mcal.pesdk.somod.SoMod
import com.mcal.pesdk.somod.SoModManager
import java.io.File

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

@Composable
fun NModsPage() {
    val context = LocalContext.current
    val enabled = remember { mutableStateListOf<NMod>() }
    val disabled = remember { mutableStateListOf<NMod>() }
    val soMods = remember { mutableStateListOf<SoMod>() }
    val soModManager = remember { SoModManager(context) }

    fun refreshLists() {
        enabled.clear()
        disabled.clear()
        soMods.clear()
        enabled.addAll(ModdedPEApplication.getMPESdk().getNModAPI().getImportedEnabledNMods())
        disabled.addAll(ModdedPEApplication.getMPESdk().getNModAPI().getImportedDisabledNMods())
        soMods.addAll(soModManager.mods)
    }

    LaunchedEffect(Unit) {
        refreshLists()
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.width(280.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = context.getString(R.string.manage_nmod_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAddDialog(context) },
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add NMod",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Add NMod", fontSize = 14.sp)
                }

                Button(
                    onClick = { showAddSoModDialog(context, soModManager) { refreshLists() } },
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Add SoMod",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Add SoMod", fontSize = 14.sp)
                }

                TextButton(
                    onClick = { refreshLists() },
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Refresh", fontSize = 13.sp)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            count = enabled.size,
                            label = "Enabled",
                            color = MaterialTheme.colorScheme.primary
                        )
                        StatItem(
                            count = disabled.size,
                            label = "Disabled",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            count = soMods.size,
                            label = "SoMods",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        StatItem(
                            count = enabled.size + disabled.size + soMods.size,
                            label = "Total",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (enabled.isNotEmpty() || disabled.isNotEmpty() || soMods.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Quick Actions",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (disabled.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    disabled.forEach { nmod ->
                                        ModdedPEApplication.getMPESdk().getNModAPI().setEnabled(nmod, true)
                                    }
                                    refreshLists()
                                },
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("Enable All NMods", fontSize = 12.sp)
                            }
                        }

                        if (enabled.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    enabled.forEach { nmod ->
                                        ModdedPEApplication.getMPESdk().getNModAPI().setEnabled(nmod, false)
                                    }
                                    refreshLists()
                                },
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("Disable All NMods", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (enabled.isEmpty() && disabled.isEmpty() && soMods.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "No NMods",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Mods Installed",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Add NMods or SoMods to get started",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (enabled.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SectionHeader(
                                title = context.getString(R.string.nmod_enabled_title),
                                color = MaterialTheme.colorScheme.primary,
                                count = enabled.size
                            )
                        }
                        items(enabled) { nmod ->
                            CompactNModCard(
                                nmod = nmod,
                                isEnabled = true,
                                onToggle = {
                                    ModdedPEApplication.getMPESdk().getNModAPI().setEnabled(nmod, false)
                                    refreshLists()
                                },
                                onDelete = null
                            )
                        }
                    }

                    if (disabled.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SectionHeader(
                                title = context.getString(R.string.nmod_disabled_title),
                                color = MaterialTheme.colorScheme.secondary,
                                count = disabled.size
                            )
                        }
                        items(disabled) { nmod ->
                            CompactNModCard(
                                nmod = nmod,
                                isEnabled = false,
                                onToggle = {
                                    ModdedPEApplication.getMPESdk().getNModAPI().setEnabled(nmod, true)
                                    refreshLists()
                                },
                                onDelete = {
                                    AlertDialog.Builder(context)
                                        .setTitle(R.string.nmod_delete_title)
                                        .setMessage(R.string.nmod_delete_message)
                                        .setPositiveButton(android.R.string.ok) { d, _ ->
                                            ModdedPEApplication.getMPESdk().getNModAPI().removeImportedNMod(nmod)
                                            refreshLists()
                                            d.dismiss()
                                        }
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .show()
                                }
                            )
                        }
                    }

                    if (soMods.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SectionHeader(
                                title = "SoMods (.so files)",
                                color = MaterialTheme.colorScheme.tertiary,
                                count = soMods.size
                            )
                        }
                        items(soMods) { soMod ->
                            CompactSoModCard(
                                soMod = soMod,
                                onToggle = {
                                    soModManager.setEnabled(soMod.fileName, !soMod.isEnabled)
                                    refreshLists()
                                },
                                onDelete = {
                                    handleSoModRemoval(soMod, soModManager, context, ::refreshLists)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    count: Int,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    color: androidx.compose.ui.graphics.Color,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Surface(
            color = color.copy(alpha = 0.2f),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "$count",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CompactNModCard(
    nmod: NMod,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    onDelete: (() -> Unit)?
) {
    OutlinedCard(
        modifier = Modifier.height(90.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isEnabled)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nmod.name ?: nmod.packageName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (nmod.name != null && nmod.packageName != nmod.name) {
                    Text(
                        text = nmod.packageName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = if (isEnabled) "Enabled" else "Disabled",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isEnabled) Icons.Default.CheckCircle else Icons.Default.Clear,
                        contentDescription = if (isEnabled) "Disable" else "Enable",
                        tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactSoModCard(
    soMod: SoMod,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.height(90.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (soMod.isEnabled)
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = soMod.fileName.removeSuffix(".so"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "SoMod • ${soMod.fileName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (soMod.isEnabled) "Enabled" else "Disabled",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (soMod.isEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (soMod.isEnabled) Icons.Default.CheckCircle else Icons.Default.Clear,
                        contentDescription = if (soMod.isEnabled) "Disable" else "Enable",
                        tint = if (soMod.isEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun showAddDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle(R.string.nmod_add_new_title)
        .setMessage(R.string.nmod_add_new_message)
        .setNegativeButton(R.string.nmod_add_new_pick_installed) { d, _ ->
            val intent = ComposePackagePickerActivity.createIntent(context, "Select NMod Package")
            context.startActivity(intent)
            d.dismiss()
        }
        .setPositiveButton(R.string.nmod_add_new_pick_storage) { d, _ ->
            if (checkPermissions(context)) {
                val intent = ComposeFilePickerActivity.createIntent(context, "Select NMod File")
                context.startActivity(intent)
            }
            d.dismiss()
        }
        .show()
}

private fun checkPermissions(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        return if (Environment.isExternalStorageManager()) {
            true
        } else {
            Dialogs.showScopedStorageDialog(context)
            false
        }
    }
    return true
}

private fun showAddSoModDialog(context: Context, soModManager: SoModManager, onRefresh: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle("Add SoMod")
        .setMessage("Select a .so file to import as a SoMod")
        .setPositiveButton("Browse Files") { d, _ ->
            if (checkPermissions(context)) {
                showFilePathInputDialog(context, soModManager, onRefresh)
            }
            d.dismiss()
        }
        .setNeutralButton("Import from Downloads") { d, _ ->
            importFromCommonLocations(context, soModManager, onRefresh)
            d.dismiss()
        }
        .setNegativeButton("Cancel", null)
        .show()
}

private fun importSoModFile(context: Context, filePath: String, soModManager: SoModManager, onRefresh: () -> Unit) {
    try {
        val sourceFile = File(filePath)
        if (!sourceFile.exists()) {
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Selected file does not exist: $filePath")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        if (!sourceFile.name.endsWith(".so")) {
            AlertDialog.Builder(context)
                .setTitle("Invalid File")
                .setMessage("Please select a .so file. Selected: ${sourceFile.name}")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        soModManager.importSoFile(sourceFile)
        onRefresh()

        val modsDir = soModManager.modsDir
        val importedFiles = modsDir.listFiles { file -> file.name.endsWith(".so") }
        val debugInfo = "SoMod imported successfully!\n\n" +
                "File: ${sourceFile.name}\n" +
                "Size: ${sourceFile.length()} bytes\n" +
                "Mods directory: ${modsDir.absolutePath}\n" +
                "Total .so files: ${importedFiles?.size ?: 0}"

        AlertDialog.Builder(context)
            .setTitle("Success")
            .setMessage(debugInfo)
            .setPositiveButton("OK", null)
            .show()

    } catch (e: Exception) {
        AlertDialog.Builder(context)
            .setTitle("Import Failed")
            .setMessage("Failed to import SoMod: ${e.message}")
            .setPositiveButton("OK", null)
            .show()
    }
}

private fun showFilePathInputDialog(context: Context, soModManager: SoModManager, onRefresh: () -> Unit) {
    val input = android.widget.EditText(context)
    input.hint = "/storage/emulated/0/Download/example.so"

    AlertDialog.Builder(context)
        .setTitle("Enter SoMod File Path")
        .setMessage("Enter the full path to your .so file:")
        .setView(input)
        .setPositiveButton("Import") { _, _ ->
            val filePath = input.text.toString().trim()
            if (filePath.isNotEmpty()) {
                importSoModFile(context, filePath, soModManager, onRefresh)
            }
        }
        .setNegativeButton("Cancel", null)
        .show()
}

private fun importFromCommonLocations(context: Context, soModManager: SoModManager, onRefresh: () -> Unit) {
    val commonPaths = listOf(
        "/storage/emulated/0/Download",
        "/storage/emulated/0/Documents",
        "/sdcard/Download",
        "/sdcard/Documents"
    )

    val soFiles = mutableListOf<File>()

    for (path in commonPaths) {
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            dir.listFiles { file -> file.name.endsWith(".so") }?.let { files ->
                soFiles.addAll(files)
            }
        }
    }

    if (soFiles.isEmpty()) {
        AlertDialog.Builder(context)
            .setTitle("No SoMods Found")
            .setMessage("No .so files found in common locations (Downloads, Documents)")
            .setPositiveButton("OK", null)
            .show()
        return
    }

    val fileNames = soFiles.map { "${it.name} (${it.parent})" }.toTypedArray()

    AlertDialog.Builder(context)
        .setTitle("Select SoMod to Import")
        .setItems(fileNames) { _, which ->
            val selectedFile = soFiles[which]
            importSoModFile(context, selectedFile.absolutePath, soModManager, onRefresh)
        }
        .setNegativeButton("Cancel", null)
        .show()
}

private fun handleSoModRemoval(soMod: SoMod, soModManager: SoModManager, context: Context, refreshLists: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle("Delete SoMod")
        .setMessage("Are you sure you want to delete ${soMod.fileName}?")
        .setPositiveButton(android.R.string.ok) { d, _ ->
            soModManager.removeSo(soMod.fileName)
            soModManager.cleanCache(context)
            refreshLists()
            d.dismiss()
        }
        .setNegativeButton(android.R.string.cancel, null)
        .show()
}