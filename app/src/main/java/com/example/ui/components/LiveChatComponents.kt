package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.ChatMessage
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorder
import com.example.ui.theme.EtfcBorderSubtle
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcLiveRed
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcSuccess
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcSurfaceVariant
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary

@Composable
fun LiveChatContainer(
    messages: List<ChatMessage>,
    pinnedMessage: ChatMessage?,
    currentUser: UserAccount?,
    chatInput: String,
    mentionSuggestions: List<Pair<String, String>>,
    isMentionPopupVisible: Boolean,
    slowModeSeconds: Int,
    isVipOnlyChat: Boolean,
    onChatInputChange: (String) -> Unit,
    onInsertMention: (String) -> Unit,
    onSendMessage: (String?) -> Unit,
    onSendCheerReaction: (String) -> Unit,
    onPinMessage: (String) -> Unit,
    onUnpinMessage: (String) -> Unit,
    onDeleteMessage: (String, String) -> Unit,
    onTimeoutUser: (userId: String, handle: String, seconds: Long) -> Unit,
    onBanUser: (userId: String, handle: String, reason: String) -> Unit,
    onFlagMessage: (String) -> Unit,
    onSetSlowMode: (Int) -> Unit,
    onToggleVipOnly: () -> Unit,
    onOpenAuthDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var selectedMessageForMenu by remember { mutableStateOf<ChatMessage?>(null) }
    var selectedUserForModDialog by remember { mutableStateOf<ChatMessage?>(null) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(EtfcSurface)
            .testTag("live_chat_container")
    ) {
        // 1. Moderator Toolbar (Visible if currentUser can moderate)
        if (currentUser?.role?.canModerate() == true) {
            ModeratorToolbar(
                slowModeSeconds = slowModeSeconds,
                isVipOnlyChat = isVipOnlyChat,
                onSetSlowMode = onSetSlowMode,
                onToggleVipOnly = onToggleVipOnly
            )
        }

        // 2. Pinned Announcement Banner
        if (pinnedMessage != null && !pinnedMessage.isDeleted) {
            PinnedAnnouncementCard(
                message = pinnedMessage,
                canModerate = currentUser?.role?.canModerate() == true,
                onUnpin = { onUnpinMessage(pinnedMessage.id) }
            )
        }

        // 3. Message Feed
        Box(modifier = Modifier.weight(1f)) {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Arena live chat is warming up... Say hello!", color = EtfcTextMuted, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatMessageItem(
                            message = msg,
                            currentUserId = currentUser?.userId,
                            canModerate = currentUser?.role?.canModerate() == true,
                            onMentionClicked = { handle -> onInsertMention(handle) },
                            onOpenOptions = { selectedMessageForMenu = msg },
                            onOpenModUser = { selectedUserForModDialog = msg }
                        )
                    }
                }
            }

            // Mention Suggestions Dropdown Popup
            if (isMentionPopupVisible && mentionSuggestions.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EtfcCardInner,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.6f)),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .fillMaxWidth(0.9f)
                        .heightIn(max = 160.dp)
                ) {
                    LazyColumn(modifier = Modifier.padding(4.dp)) {
                        items(mentionSuggestions) { (handle, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onInsertMention(handle) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = handle,
                                    color = EtfcGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = desc,
                                    color = EtfcTextMuted,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Quick Cheer Reaction Bar
        QuickCheerReactionsRow(onReactionSelected = onSendCheerReaction)

        // 5. Chat Input Bar
        ChatInputBottomBar(
            inputText = chatInput,
            currentUser = currentUser,
            slowModeSeconds = slowModeSeconds,
            isVipOnlyChat = isVipOnlyChat,
            onInputChange = onChatInputChange,
            onSendMessage = { onSendMessage(null) },
            onQuickMentionTrigger = { onInsertMention("@") },
            onOpenAuth = onOpenAuthDialog
        )
    }

    // Message Options & Moderation Dialogs
    selectedMessageForMenu?.let { msg ->
        MessageActionMenuDialog(
            message = msg,
            canModerate = currentUser?.role?.canModerate() == true,
            onPin = {
                onPinMessage(msg.id)
                selectedMessageForMenu = null
            },
            onDelete = {
                onDeleteMessage(msg.id, "Violation of ETFC Chat Guidelines")
                selectedMessageForMenu = null
            },
            onFlag = {
                onFlagMessage(msg.id)
                selectedMessageForMenu = null
            },
            onMentionUser = {
                onInsertMention(msg.userHandle)
                selectedMessageForMenu = null
            },
            onOpenUserMod = {
                selectedUserForModDialog = msg
                selectedMessageForMenu = null
            },
            onDismiss = { selectedMessageForMenu = null }
        )
    }

    selectedUserForModDialog?.let { msg ->
        UserModerationDialog(
            targetUserId = msg.userId,
            targetHandle = msg.userHandle,
            targetName = msg.userName,
            onTimeout = { sec ->
                onTimeoutUser(msg.userId, msg.userHandle, sec)
                selectedUserForModDialog = null
            },
            onBan = { reason ->
                onBanUser(msg.userId, msg.userHandle, reason)
                selectedUserForModDialog = null
            },
            onDismiss = { selectedUserForModDialog = null }
        )
    }
}

@Composable
fun ModeratorToolbar(
    slowModeSeconds: Int,
    isVipOnlyChat: Boolean,
    onSetSlowMode: (Int) -> Unit,
    onToggleVipOnly: () -> Unit
) {
    Surface(
        color = EtfcCardInner,
        border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("moderator_toolbar")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = EtfcGold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "MOD SHIELD ACTIVE",
                    color = EtfcGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Slow Mode selector
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (slowModeSeconds > 0) EtfcGold.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (slowModeSeconds > 0) EtfcGold else EtfcBorderSubtle),
                    modifier = Modifier.clickable {
                        val next = when (slowModeSeconds) {
                            0 -> 5
                            5 -> 15
                            15 -> 30
                            else -> 0
                        }
                        onSetSlowMode(next)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = if (slowModeSeconds > 0) EtfcGold else EtfcTextMuted, modifier = Modifier.size(11.dp))
                        Text(
                            text = if (slowModeSeconds > 0) "Slow: ${slowModeSeconds}s" else "Slow: Off",
                            color = if (slowModeSeconds > 0) EtfcGold else EtfcTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // VIP Only toggle
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isVipOnlyChat) EtfcBlueCorner.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isVipOnlyChat) EtfcBlueCorner else EtfcBorderSubtle),
                    modifier = Modifier.clickable { onToggleVipOnly() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(text = "👑", fontSize = 10.sp)
                        Text(
                            text = if (isVipOnlyChat) "VIP Lock: ON" else "VIP Lock: OFF",
                            color = if (isVipOnlyChat) EtfcBlueCorner else EtfcTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PinnedAnnouncementCard(
    message: ChatMessage,
    canModerate: Boolean,
    onUnpin: () -> Unit
) {
    Surface(
        color = EtfcGold.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.8f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = null,
                tint = EtfcGold,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "PINNED ANNOUNCEMENT",
                        color = EtfcGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "• ${message.userHandle}",
                        color = EtfcTextSecondary,
                        fontSize = 9.sp
                    )
                }
                Text(
                    text = message.messageText,
                    color = EtfcTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            if (canModerate) {
                IconButton(onClick = onUnpin, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Unpin",
                        tint = EtfcGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    currentUserId: String?,
    canModerate: Boolean,
    onMentionClicked: (String) -> Unit,
    onOpenOptions: () -> Unit,
    onOpenModUser: () -> Unit
) {
    if (message.isDeleted) {
        Surface(
            color = Color.White.copy(alpha = 0.03f),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)
        ) {
            Text(
                text = "🚫 [Message deleted by moderator ${message.deletedByMod ?: ""}]",
                color = EtfcTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        return
    }

    val isMine = message.userId == currentUserId
    val roleColor = Color(message.userRole.colorHex)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenOptions() }
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top
    ) {
        // User Flag / Avatar
        Text(text = message.userFlag, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Role Badge
                Surface(
                    color = roleColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, roleColor.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(text = message.userRole.badgeEmoji, fontSize = 8.sp)
                        Text(
                            text = message.userRole.badgeTitle,
                            color = roleColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Handle
                Text(
                    text = message.userHandle,
                    color = if (isMine) EtfcGold else EtfcTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                if (message.cheerEmoji != null) {
                    Text(text = message.cheerEmoji, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Time ago
                Text(
                    text = message.formattedTime(),
                    color = EtfcTextMuted,
                    fontSize = 9.sp
                )
            }

            // Message text with rendered mentions
            if (message.messageText.isNotBlank()) {
                val annotatedText = buildAnnotatedString {
                    val words = message.messageText.split(" ")
                    words.forEachIndexed { index, word ->
                        if (word.startsWith("@") && word.length > 1) {
                            withStyle(
                                style = SpanStyle(
                                    color = EtfcGold,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(word)
                            }
                        } else {
                            withStyle(style = SpanStyle(color = EtfcTextPrimary)) {
                                append(word)
                            }
                        }
                        if (index < words.size - 1) append(" ")
                    }
                }

                Text(
                    text = annotatedText,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
    }
}

@Composable
fun QuickCheerReactionsRow(onReactionSelected: (String) -> Unit) {
    val cheers = listOf("🔥", "🥊", "🏆", "🇪🇹", "🦁", "💥", "👑")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(cheers) { emoji ->
            Surface(
                shape = CircleShape,
                color = EtfcCardInner,
                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier
                    .clickable { onReactionSelected(emoji) }
                    .testTag("cheer_reaction_$emoji")
            ) {
                Text(
                    text = emoji,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun ChatInputBottomBar(
    inputText: String,
    currentUser: UserAccount?,
    slowModeSeconds: Int,
    isVipOnlyChat: Boolean,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onQuickMentionTrigger: () -> Unit,
    onOpenAuth: () -> Unit
) {
    val isGuest = currentUser == null || currentUser.role == UserRole.GUEST

    Surface(
        color = EtfcSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isGuest) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sign in to send messages and cheer for fighters!",
                    color = EtfcTextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = EtfcGold,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .clickable { onOpenAuth() }
                        .testTag("chat_signin_prompt_btn")
                ) {
                    Text(
                        text = "SIGN IN",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Quick Mention Trigger Button
                IconButton(
                    onClick = onQuickMentionTrigger,
                    modifier = Modifier
                        .size(34.dp)
                        .background(EtfcCardInner, CircleShape)
                        .testTag("chat_quick_mention_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AlternateEmail,
                        contentDescription = "Mention user",
                        tint = EtfcGold,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Text Input Field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            text = if (isVipOnlyChat) "VIP Chat active... Type @ to mention" else "Send a message... Type @ to mention",
                            color = EtfcTextMuted,
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 38.dp, max = 80.dp)
                        .testTag("chat_message_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EtfcGold,
                        unfocusedBorderColor = EtfcBorderSubtle,
                        focusedContainerColor = EtfcCardInner,
                        unfocusedContainerColor = EtfcCardInner,
                        focusedTextColor = EtfcTextPrimary,
                        unfocusedTextColor = EtfcTextPrimary
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendMessage() }),
                    singleLine = true
                )

                // Send Button
                IconButton(
                    onClick = onSendMessage,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (inputText.isNotBlank()) EtfcGold else Color.White.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color.Black else EtfcTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageActionMenuDialog(
    message: ChatMessage,
    canModerate: Boolean,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    onFlag: () -> Unit,
    onMentionUser: () -> Unit,
    onOpenUserMod: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = EtfcSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
            modifier = Modifier.fillMaxWidth().testTag("message_action_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Message by ${message.userHandle}",
                        color = EtfcGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = EtfcTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mention user option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMentionUser() }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                    Text("Mention @${message.userHandle.removePrefix("@")}", color = EtfcTextPrimary, fontSize = 12.sp)
                }

                // Report / Flag option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFlag() }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null, tint = EtfcRedPrimary, modifier = Modifier.size(18.dp))
                    Text("Report Message to Arena Mods", color = EtfcTextPrimary, fontSize = 12.sp)
                }

                // Moderation Options (if user has Mod Shield)
                if (canModerate) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = EtfcGold.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "🛡️ MODERATOR CONTROLS",
                            color = EtfcGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPin() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PushPin, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                        Text("Pin Announcement to Arena", color = EtfcGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDelete() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = EtfcRedPrimary, modifier = Modifier.size(18.dp))
                        Text("Delete Message", color = EtfcRedPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenUserMod() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null, tint = EtfcRedPrimary, modifier = Modifier.size(18.dp))
                        Text("Timeout / Ban ${message.userHandle}...", color = EtfcRedPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun UserModerationDialog(
    targetUserId: String,
    targetHandle: String,
    targetName: String,
    onTimeout: (seconds: Long) -> Unit,
    onBan: (reason: String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = EtfcSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, EtfcRedPrimary.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth().testTag("user_moderation_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = EtfcRedPrimary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "MODERATION ACTION",
                        color = EtfcRedPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Target User: $targetName ($targetHandle)",
                    color = EtfcTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("TIMEOUT USER (SLOW/MUTE):", color = EtfcTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EtfcCardInner,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                        modifier = Modifier.weight(1f).clickable { onTimeout(60) }
                    ) {
                        Text("Timeout 60s", color = EtfcGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EtfcCardInner,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                        modifier = Modifier.weight(1f).clickable { onTimeout(300) }
                    ) {
                        Text("Timeout 5m", color = EtfcGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("PERMANENT BAN & PURGE:", color = EtfcTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EtfcRedPrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcRedPrimary),
                    modifier = Modifier.fillMaxWidth().clickable { onBan("Severe Terms of Service violation in live arena chat") }
                ) {
                    Text(
                        text = "BAN USER & PURGE ALL MESSAGES",
                        color = EtfcRedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(10.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    modifier = Modifier.fillMaxWidth().clickable { onDismiss() }
                ) {
                    Text("Cancel", color = EtfcTextMuted, fontSize = 11.sp, modifier = Modifier.padding(8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}
