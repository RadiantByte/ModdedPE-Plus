package com.mcal.mcpelauncher.ui.pages

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.mcal.mcpelauncher.ui.components.LoadingDialog
import com.mcal.mcpelauncher.ui.components.PackageItem
import com.mcal.mcpelauncher.ui.components.PackagePickerList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

@Composable
fun PackagePickerPage(
    title: String = "Select Package",
    onPackageSelected: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var packages by remember { mutableStateOf<List<PackageItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val packageManager = context.packageManager
                val installedPackages = packageManager.getInstalledPackages(0)
                
                val packageList = installedPackages.map { packageInfo ->
                    val appName = try {
                        packageInfo.applicationInfo?.let { appInfo ->
                            packageManager.getApplicationLabel(appInfo).toString()
                        } ?: packageInfo.packageName
                    } catch (e: Exception) {
                        packageInfo.packageName
                    }
                    
                    val icon = try {
                        packageManager.getApplicationIcon(packageInfo.packageName)

                        null
                    } catch (e: Exception) {
                        null
                    }
                    
                    PackageItem(
                        packageName = packageInfo.packageName,
                        appName = appName,
                        icon = icon
                    )
                }.sortedBy { it.appName }
                
                packages = packageList
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }
    
    if (isLoading) {
        LoadingDialog(
            title = "Loading Packages",
            message = "Scanning installed applications..."
        )
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
            PackagePickerList(
                packages = packages,
                onPackageClick = { packageItem ->
                    onPackageSelected(packageItem.packageName)
                }
            )
        }
    }
}