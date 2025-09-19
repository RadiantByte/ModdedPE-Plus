package com.mcal.mcpelauncher.ui.pages

import android.os.Build
import android.os.Environment
import android.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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

    LaunchedEffect(Unit) {
        enabled.clear()
        disabled.clear()
        soMods.clear()
        enabled.addAll(ModdedPEApplication.getMPESdk().getNModAPI().getImportedEnabledNMods())
        disabled.addAll(ModdedPEApplication.getMPESdk().getNModAPI().getImportedDisabledNMods())
        soMods.addAll(soModManager.mods)
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
                onClick = { showAddSoModDialog(context, soModManager) { soMods.clear(); soMods.addAll(soModManager.mods) } },
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
                            enabled.remove(nmod)
                            disabled.add(nmod)
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
                            disabled.remove(nmod)
                            enabled.add(nmod)
                        },
                        onDelete = {
                            AlertDialog.Builder(context)
                                .setTitle(R.string.nmod_delete_title)
                                .setMessage(R.string.nmod_delete_message)
                                .setPositiveButton(android.R.string.ok) { d, _ ->
                                    ModdedPEApplication.getMPESdk().getNModAPI().removeImportedNMod(nmod)
                                    disabled.remove(nmod)
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
                            soMods.clear()
                            soMods.addAll(soModManager.mods)
                        },
                        onDelete = {
                            AlertDialog.Builder(context)
                                .setTitle("Delete SoMod")
                                .setMessage("Are you sure you want to delete ${soMod.fileName}?")
                                .setPositiveButton(android.R.string.ok) { d, _ ->
                                    soModManager.removeSo(soMod.fileName)
                                    soMods.clear()
                                    soMods.addAll(soModManager.mods)
                                    d.dismiss()
                                }
                                .setNegativeButton(android.R.string.cancel, null)
                                .show()
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

private fun showAddDialog(context: android.content.Context) {
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

private fun checkPermissions(context: android.content.Context): Boolean {
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

private fun showAddSoModDialog(context: android.content.Context, soModManager: SoModManager, onRefresh: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle("Add SoMod")
        .setMessage("Select a .so file to import as a SoMod")
        .setPositiveButton("Browse Files") { d, _ ->
            if (checkPermissions(context)) {
                val intent = ComposeFilePickerActivity.createIntent(context, "Select SoMod (.so) File")
                context.startActivity(intent)
            }
            d.dismiss()
        }
        .setNegativeButton("Cancel", null)
        .show()
}
private fun importSoModFile(context: android.content.Context, filePath: String, soModManager: SoModManager, onRefresh: () -> Unit) {
    try {
        val sourceFile = java.io.File(filePath)
        if (!sourceFile.exists()) {
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Selected file does not exist")
                .setPositiveButton("OK", null)
                .show()
            return
        }
        
        if (!sourceFile.name.endsWith(".so")) {
            AlertDialog.Builder(context)
                .setTitle("Invalid File")
                .setMessage("Please select a .so file")
                .setPositiveButton("OK", null)
                .show()
            return
        }
        
        soModManager.importSoFile(sourceFile)
        onRefresh()
        
        AlertDialog.Builder(context)
            .setTitle("Success")
            .setMessage("SoMod imported successfully: ${sourceFile.name}")
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