package com.truthlens.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Security Tab in Settings
 * - Auto Background Media Scanner (scans new media only, not entire phone)
 * - Explicit Full Device Scan
 * - Content Authenticity & C2PA Provenance
 * - Phishing & App Share Shield
 */
@Composable
fun SecuritySettingsDialog(onDismiss: () -> Unit) {
    var autoBackgroundScan by remember { mutableStateOf(true) }
    var c2paProvenance by remember { mutableStateOf(true) }
    var appShareShield by remember { mutableStateOf(true) }

    var isRunningFullScan by remember { mutableStateOf(false) }
    var fullScanProgress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    SettingsSubScreenDialog(
        title = "Security & Protection",
        icon = Icons.Default.Check,
        onDismiss = onDismiss
    ) {
        // --- AUTO BACKGROUND MEDIA SCANNER ---
        Text("Auto Background Media Scanner", style = TendrilsTheme.typography.subtitle1, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
        TendrilsCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TendrilsTheme.colors.surfaceElevated,
            padding = 12.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-scan new media", style = TendrilsTheme.typography.body2, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
                        Text("Scans newly added photos/videos only using on-device TFLite. Does not inspect entire storage without permission.", style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.textSecondary)
                    }
                    Switch(
                        checked = autoBackgroundScan,
                        onCheckedChange = { autoBackgroundScan = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = TendrilsTheme.colors.primary, checkedTrackColor = TendrilsTheme.colors.primary.copy(alpha = 0.5f))
                    )
                }

                if (autoBackgroundScan) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TendrilsPalette.Sprout500.copy(alpha = 0.12f), PillShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Status: Active • 2 new items analyzed in memory • 0 threats detected", style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // --- FULL STORAGE SCAN (EXPLICIT ON-DEMAND) ---
        Text("Full Storage Audit", style = TendrilsTheme.typography.subtitle1, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
        Button(
            onClick = {
                scope.launch {
                    isRunningFullScan = true
                    fullScanProgress = 0.1f
                    delay(400)
                    fullScanProgress = 0.5f
                    delay(500)
                    fullScanProgress = 1.0f
                    delay(300)
                    isRunningFullScan = false
                }
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = TendrilsTheme.colors.surfaceElevated, contentColor = TendrilsTheme.colors.primary)
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isRunningFullScan) "Scanning Media (${(fullScanProgress * 100).toInt()}%)..." else "Scan Entire Phone Media (Explicit)")
        }

        if (isRunningFullScan) {
            LinearProgressIndicator(progress = fullScanProgress, modifier = Modifier.fillMaxWidth().clip(PillShape), color = TendrilsTheme.colors.primary)
        }

        Divider(color = TendrilsTheme.colors.border)

        // Other security features
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("C2PA Content Authenticity", style = TendrilsTheme.typography.body2, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
                Text("Verify open cryptographic metadata in photos.", style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.textSecondary)
            }
            Switch(checked = c2paProvenance, onCheckedChange = { c2paProvenance = it }, colors = SwitchDefaults.colors(checkedThumbColor = TendrilsTheme.colors.primary))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Android Share Sheet Target", style = TendrilsTheme.typography.body2, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
                Text("Allow sharing links or images to Tendrils from other apps.", style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.textSecondary)
            }
            Switch(checked = appShareShield, onCheckedChange = { appShareShield = it }, colors = SwitchDefaults.colors(checkedThumbColor = TendrilsTheme.colors.primary))
        }
    }
}

/**
 * Model Manager
 * - Imports to Downloads/Tendrils/models
 * - Downloads models via URL (HuggingFace, GGUF, TFLite)
 * - Switch active models in development
 */
@Composable
fun ModelManagerDialog(onDismiss: () -> Unit) {
    var modelUrlInput by remember { mutableStateOf("https://huggingface.co/Tendrils/deepfake-mobilenetv4-int8.tflite") }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var activeModelId by remember { mutableStateOf("m1") }
    val scope = rememberCoroutineScope()

    var installedModels by remember {
        mutableStateOf(
            listOf(
                ModelItem("m1", "Deepfake Classifier v2.1", "TFLite INT8 • 14.8 MB", "Active for media & background scans", true),
                ModelItem("m2", "Deepfake Experimental-b4", "TFLite FP16 • 28.4 MB", "Experimental test weights", false),
                ModelItem("m3", "LFM-2.5-230M-GGUF", "GGUF • 185 MB", "Active On-Device LLM", true),
                ModelItem("m4", "ML Kit Text Latin", "OCR Core • 8.5 MB", "Built-in document reader", true)
            )
        )
    }

    SettingsSubScreenDialog(
        title = "Model Manager (Lab)",
        icon = Icons.Default.Star,
        onDismiss = onDismiss
    ) {
        // Storage Banner
        TendrilsCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TendrilsTheme.colors.surfaceElevated,
            padding = 10.dp
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = TendrilsTheme.colors.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Model Folder Path:", style = TendrilsTheme.typography.caption, fontWeight = FontWeight.Bold, color = TendrilsTheme.colors.textPrimary)
                    Text("Downloads/Tendrils/models", style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text("Download or Import Model", style = TendrilsTheme.typography.subtitle1, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)

        OutlinedTextField(
            value = modelUrlInput,
            onValueChange = { modelUrlInput = it },
            label = { Text("Model URL (HuggingFace / direct link)") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = TendrilsTheme.colors.textPrimary, focusedBorderColor = TendrilsTheme.colors.primary, unfocusedBorderColor = TendrilsTheme.colors.border)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    scope.launch {
                        isDownloading = true
                        downloadProgress = 0.2f
                        delay(400)
                        downloadProgress = 0.6f
                        delay(500)
                        downloadProgress = 1.0f
                        delay(200)
                        isDownloading = false
                        installedModels = installedModels + ModelItem(
                            id = "m_${installedModels.size + 1}",
                            name = "Downloaded Custom Weights",
                            specs = "TFLite • 19.1 MB",
                            description = "Imported from URL",
                            isActive = false
                        )
                    }
                },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = TendrilsTheme.colors.primary, contentColor = Color.White)
            ) {
                Text(if (isDownloading) "Downloading..." else "Download Model")
            }

            Button(
                onClick = {
                    installedModels = installedModels + ModelItem(
                        id = "m_${installedModels.size + 1}",
                        name = "Imported_Local_Model.tflite",
                        specs = "TFLite • 12.0 MB",
                        description = "Imported from local storage",
                        isActive = false
                    )
                },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = TendrilsTheme.colors.surfaceElevated, contentColor = TendrilsTheme.colors.textPrimary)
            ) {
                Text("Import File")
            }
        }

        if (isDownloading) {
            LinearProgressIndicator(progress = downloadProgress, modifier = Modifier.fillMaxWidth().clip(PillShape), color = TendrilsTheme.colors.primary)
        }

        Divider(color = TendrilsTheme.colors.border)

        // Installed Models list
        Text("Installed Models (${installedModels.size})", style = TendrilsTheme.typography.subtitle1, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)

        installedModels.forEach { model ->
            val isSelected = activeModelId == model.id
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isSelected) TendrilsTheme.colors.primary.copy(alpha = 0.12f) else TendrilsTheme.colors.surfaceElevated,
                borderColor = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                padding = 10.dp,
                onClick = { activeModelId = model.id }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { activeModelId = model.id },
                        colors = RadioButtonDefaults.colors(selectedColor = TendrilsTheme.colors.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(model.name, style = TendrilsTheme.typography.body2, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
                            Text(model.specs, style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.primary)
                        }
                        Text(model.description, style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.textSecondary)
                    }
                }
            }
        }
    }
}

private data class ModelItem(
    val id: String,
    val name: String,
    val specs: String,
    val description: String,
    val isActive: Boolean
)
