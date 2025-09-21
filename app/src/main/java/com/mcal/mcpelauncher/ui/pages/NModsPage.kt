package com.mcal.mcpelauncher.ui.pages

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = context.getString(R.string.manage_nmod_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showAddDialog(context) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add NMod"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add NMod")
            }

            Button(
                onClick = { showAddSoModDialog(context, soModManager) { refreshLists() } },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Add SoMod"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add SoMod")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { refreshLists() }) {
                Text("Refresh")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${enabled.size}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "Enabled", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${disabled.size}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(text = "Disabled", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${soMods.size}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(text = "SoMods", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${enabled.size + disabled.size + soMods.size}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(text = "Total", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (enabled.isNotEmpty()) {
                item {
                    Text(
                        text = context.getString(R.string.nmod_enabled_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(enabled) { nmod ->
                    NModCard(
                        nmod = nmod,
                        isEnabled = true,
                        onToggle = {
                            ModdedPEApplication.getMPESdk().getNModAPI().setEnabled(nmod, false)
                            refreshLists()
                        },
                        onDelete = null
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            if (disabled.isNotEmpty()) {
                item {
                    Text(
                        text = context.getString(R.string.nmod_disabled_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                items(disabled) { nmod ->
                    NModCard(
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
                item {
                    Text(
                        text = "SoMods (.so files)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                items(soMods) { soMod ->
                    SoModCard(
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
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            if (enabled.isEmpty() && disabled.isEmpty() && soMods.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "No NMods",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Mods installed",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Add NMods or SoMods to get started",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NModCard(
    nmod: NMod,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    onDelete: (() -> Unit)?
) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isEnabled)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nmod.name ?: nmod.packageName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                if (nmod.name != null && nmod.packageName != nmod.name) {
                    Text(
                        text = nmod.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (isEnabled) Icons.Default.CheckCircle else Icons.Default.Clear,
                        contentDescription = if (isEnabled) "Disable" else "Enable",
                        tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
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



@Composable
private fun SoModCard(
    soMod: SoMod,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (soMod.isEnabled)
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = soMod.fileName.removeSuffix(".so"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "SoMod • ${soMod.fileName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (soMod.isEnabled) Icons.Default.CheckCircle else Icons.Default.Clear,
                        contentDescription = if (soMod.isEnabled) "Disable" else "Enable",
                        tint = if (soMod.isEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
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