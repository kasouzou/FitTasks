package com.kasouzou.fittasks.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasouzou.fittasks.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    currentLanguageCode: String?,
    onThemeSelected: (Int) -> Unit,
    currentThemeMode: Int,
    onDynamicColorChanged: (Boolean) -> Unit,
    useDynamicColor: Boolean
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    
    val languages = listOf(
        "ja" to stringResource(R.string.language_japanese),
        "en" to stringResource(R.string.language_english),
        "zh" to stringResource(R.string.language_chinese),
        "ko" to stringResource(R.string.language_korean)
    )
    
    val themes = listOf(
        0 to stringResource(R.string.theme_system),
        1 to stringResource(R.string.theme_light),
        2 to stringResource(R.string.theme_dark)
    )
    
    val currentLanguageLabel = languages.find { it.first == currentLanguageCode }?.second ?: stringResource(R.string.language_japanese)
    val currentThemeLabel = themes.find { it.first == currentThemeMode }?.second ?: stringResource(R.string.theme_system)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.current_language)) },
                supportingContent = { Text(currentLanguageLabel) },
                leadingContent = { Icon(Icons.Default.Language, contentDescription = null) },
                modifier = Modifier.clickable { showLanguageDialog = true }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.theme_setting)) },
                supportingContent = { Text(currentThemeLabel) },
                leadingContent = { Icon(Icons.Default.Contrast, contentDescription = null) },
                modifier = Modifier.clickable { showThemeDialog = true }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.theme_dynamic)) },
                leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = useDynamicColor,
                        onCheckedChange = { onDynamicColorChanged(it) }
                    )
                }
            )
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            },
            title = { Text(stringResource(R.string.welcome_message)) },
            text = {
                Column {
                    languages.forEach { (code, label) ->
                        ListItem(
                            headlineContent = { Text(label) },
                            modifier = Modifier.clickable {
                                onLanguageSelected(code)
                                showLanguageDialog = false
                            }
                        )
                    }
                }
            }
        )
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            },
            title = { Text(stringResource(R.string.theme_setting)) },
            text = {
                Column {
                    themes.forEach { (mode, label) ->
                        ListItem(
                            headlineContent = { Text(label) },
                            modifier = Modifier.clickable {
                                onThemeSelected(mode)
                                showThemeDialog = false
                            }
                        )
                    }
                }
            }
        )
    }
}
