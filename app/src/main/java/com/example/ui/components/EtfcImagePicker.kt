package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.util.LocalImageStorageManager
import kotlinx.coroutines.launch

/**
 * High-quality Material 3 Local File Picker component for ETFC Admin & CMS Portal.
 * Allows picking local images from device gallery/storage, persists to internal app files,
 * and passes the persistent file:// URI back to the caller.
 */
@Composable
fun EtfcImagePicker(
    selectedImageUrl: String?,
    onImageSelected: (String) -> Unit,
    label: String = "Profile Picture / Image Asset",
    helperText: String = "Select an image from device local storage or gallery",
    prefix: String = "media",
    isCircular: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }

    // ActivityResultLauncher for picking images from local device storage
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploading = true
            coroutineScope.launch {
                val persistentPath = LocalImageStorageManager.saveImageFromUri(
                    context = context,
                    sourceUri = uri,
                    prefix = prefix
                )
                isUploading = false
                onImageSelected(persistentPath)
            }
        }
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCardInner),
        border = BorderStroke(1.dp, if (!selectedImageUrl.isNullOrBlank()) EtfcGold.copy(alpha = 0.5f) else EtfcBorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        color = EtfcGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = helperText,
                        color = EtfcTextMuted,
                        fontSize = 10.sp
                    )
                }

                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = EtfcGold,
                        strokeWidth = 2.dp
                    )
                }
            }

            // Preview & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image Preview Box
                Box(
                    modifier = Modifier
                        .size(if (isCircular) 64.dp else 80.dp, height = if (isCircular) 64.dp else 56.dp)
                        .clip(if (isCircular) CircleShape else RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .border(
                            1.dp,
                            if (!selectedImageUrl.isNullOrBlank()) EtfcGold else EtfcBorderSubtle,
                            if (isCircular) CircleShape else RoundedCornerShape(8.dp)
                        )
                        .clickable { filePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (!selectedImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = selectedImageUrl,
                            contentDescription = label,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Pick Image",
                                tint = EtfcTextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "UPLOAD",
                                color = EtfcTextMuted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Action Buttons
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { filePickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EtfcGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .testTag("btn_pick_local_file_$prefix")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedImageUrl.isNullOrBlank()) "PICK FROM DEVICE" else "CHANGE LOCAL FILE",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    if (!selectedImageUrl.isNullOrBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedImageUrl.startsWith("file://")) "📍 Local Device File" else "🌐 External / Bundled",
                                color = if (selectedImageUrl.startsWith("file://")) EtfcGold else EtfcTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            TextButton(
                                onClick = { onImageSelected("") },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text(
                                    text = "REMOVE",
                                    color = EtfcRedPrimary,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Presets row for fast testing/selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Quick Presets:",
                    color = EtfcTextMuted,
                    fontSize = 9.sp
                )
                listOf(
                    "Sedo" to "https://images.unsplash.com/photo-1549719386-74dfcbf7dbed?w=600&auto=format&fit=crop&q=80",
                    "Jhonny" to "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=600&auto=format&fit=crop&q=80",
                    "Poster 1" to "https://images.unsplash.com/photo-1517438322307-e67111335449?w=900&auto=format&fit=crop&q=80",
                    "Adwa Arena" to "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=900&auto=format&fit=crop&q=80"
                ).forEach { (labelStr, urlStr) ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EtfcSurfaceVariant,
                        border = BorderStroke(0.5.dp, EtfcBorderSubtle),
                        modifier = Modifier.clickable { onImageSelected(urlStr) }
                    ) {
                        Text(
                            text = labelStr,
                            color = EtfcTextSecondary,
                            fontSize = 8.5.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
