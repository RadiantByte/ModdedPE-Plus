package com.mcal.mcpelauncher.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mcal.mcpelauncher.ui.components.CustomActionBar
import com.mcal.mcpelauncher.ui.components.FileItem
import com.mcal.mcpelauncher.ui.components.FilePickerList
import java.io.File

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

@Composable
fun FilePickerPage(
    title: String = "Select File",
    initialPath: String = "/storage/emulated/0",
    fileExtensions: List<String> = listOf(".nmod", ".zip", ".apk", ".so"),
    onFileSelected: (File) -> Unit,
    onCancel: () -> Unit
) {
    LocalContext.current
    var currentPath by remember { mutableStateOf(File(initialPath)) }
    var files by remember { mutableStateOf<List<FileItem>>(emptyList()) }
    
    LaunchedEffect(currentPath) {
        try {
            val fileList = currentPath.listFiles()?.let { fileArray ->
                fileArray.filter { file ->
                    file.isDirectory || fileExtensions.any { ext ->
                        file.name.endsWith(ext, ignoreCase = true)
                    }
                }.map { file ->
                    FileItem(file)
                }.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.name })
            } ?: emptyList()
            
            files = if (currentPath.parent != null) {
                listOf(FileItem(currentPath.parentFile!!, name = "..")) + fileList
            } else {
                fileList
            }
        } catch (e: Exception) {
            files = emptyList()
        }
    }
    
    Scaffold(
        topBar = {
            CustomActionBar(
                title = title,
                showCloseButton = true,
                onActionClick = onCancel
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = currentPath.absolutePath,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            FilePickerList(
                files = files,
                onFileClick = { fileItem ->
                    if (fileItem.isDirectory) {
                        currentPath = fileItem.file
                    } else {
                        onFileSelected(fileItem.file)
                    }
                }
            )
        }
    }
}