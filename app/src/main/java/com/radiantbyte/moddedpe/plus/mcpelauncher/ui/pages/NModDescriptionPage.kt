package com.radiantbyte.moddedpe.plus.mcpelauncher.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.radiantbyte.moddedpe.plus.mcpelauncher.ui.components.CustomActionBar
import com.radiantbyte.moddedpe.plus.pesdk.nmod.NMod

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

@Composable
fun NModDescriptionPage(
    nmod: NMod,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            CustomActionBar(
                title = "NMod Details",
                showCloseButton = true,
                onActionClick = onClose
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with icon and basic info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (nmod.icon != null) {
                        Image(
                            bitmap = nmod.icon.asImageBitmap(),
                            contentDescription = "NMod Icon",
                            modifier = Modifier.size(64.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "NMod",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Column {
                        Text(
                            text = nmod.name ?: nmod.packageName,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        
                        Text(
                            text = nmod.packageName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // About section
            InfoSection(
                title = "About",
                content = listOf(
                    "Author" to (nmod.author ?: "Unknown"),
                    "Version" to (nmod.versionName ?: "Unknown"),
                    "Game Version" to (nmod.minecraftVersionName ?: "Unknown"),
                    "Package Name" to nmod.packageName
                )
            )
            
            // Description section
            if (!nmod.description.isNullOrBlank()) {
                InfoSection(
                    title = "Description",
                    description = nmod.description
                )
            }
            
            // What's New section - commented out as whatsNew property may not exist
            // if (!nmod.whatsNew.isNullOrBlank()) {
            //     InfoSection(
            //         title = "What's New",
            //         description = nmod.whatsNew
            //     )
            // }
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    content: List<Pair<String, String>>? = null,
    description: String? = null
) {
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (content != null) {
                content.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$label:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}