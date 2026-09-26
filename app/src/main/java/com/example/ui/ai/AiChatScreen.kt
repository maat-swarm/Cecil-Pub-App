package com.example.ui.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TavernViewModel
import com.example.ui.components.CoreIqBrandingBadge
import com.example.ui.theme.PubAmber
import com.example.ui.theme.PubBorder
import com.example.ui.theme.PubPrimary
import com.example.ui.theme.PubPrimaryDark
import com.example.ui.theme.PubTextMuted
import com.example.ui.theme.PubTextPrimary
import com.example.ui.theme.PubTextSecondary
import com.example.ui.theme.WhatsAppAiBubble
import com.example.ui.theme.WhatsAppChatBg
import com.example.ui.theme.WhatsAppHeader
import com.example.ui.theme.WhatsAppUserBubble
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AiChatScreen(
    viewModel: TavernViewModel
) {
    val messages by viewModel.aiMessages.collectAsState()
    val isAiTyping by viewModel.isAiTyping.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val suggestedQuestions = listOf(
        "What must I order for Monday?",
        "How many Black Labels do I have?",
        "What sold most this week?",
        "Any stock missing?",
        "How's my warehouse stock?"
    )

    LaunchedEffect(messages.size, isAiTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhatsAppChatBg)
            .testTag("ai_chat_screen")
    ) {
        // WhatsApp Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhatsAppHeader)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PubAmber),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🍺", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cecil's Operations Partner",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isAiTyping) "typing..." else "online • Powered by CoreIQ",
                    fontSize = 11.sp,
                    color = if (isAiTyping) PubAmber else Color(0xFFC8E6C9)
                )
            }
            IconButton(onClick = { viewModel.clearAiMessages() }) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Chat",
                    tint = Color.White
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Welcome Bubble if empty
                if (messages.isEmpty()) {
                    WhatsAppMessageBubble(
                        role = "assistant",
                        content = "Aweh Cecil! 👋 I'm your Tavern Operations Assistant powered by CoreIQ.\n\nI watch your live floor stock, warehouse cases, and POS numbers right here in Tembisa. Ask me anything like:\n• \"What must I order for Monday?\"\n• \"How many Black Labels do I have?\"\n• \"Any stock missing?\"",
                        timestamp = System.currentTimeMillis()
                    )
                }
            }

            items(messages) { msg ->
                WhatsAppMessageBubble(
                    role = msg.role,
                    content = msg.content,
                    timestamp = msg.createdAt
                )
            }

            if (isAiTyping) {
                item {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(WhatsAppAiBubble)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = PubPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Checking live stock & sales...",
                            fontSize = 12.sp,
                            color = PubTextMuted
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Suggested Chips Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            suggestedQuestions.forEach { question ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable {
                        viewModel.sendAiMessage(question)
                    }
                ) {
                    Text(
                        text = question,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PubPrimaryDark,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask your operations partner...", fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_input_field"),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF6F8F7),
                    unfocusedContainerColor = Color(0xFFF6F8F7),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val text = inputText
                        inputText = ""
                        viewModel.sendAiMessage(text)
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(WhatsAppHeader)
                    .testTag("ai_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun WhatsAppMessageBubble(
    role: String,
    content: String,
    timestamp: Long
) {
    val isUser = role == "user"
    val bubbleColor = if (isUser) WhatsAppUserBubble else WhatsAppAiBubble
    val align = if (isUser) Alignment.End else Alignment.Start

    val timeStr = SimpleDateFormat("HH:mm", Locale.US).format(Date(timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 14.dp
            ),
            color = bubbleColor,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = content,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = PubTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeStr,
                    fontSize = 10.sp,
                    color = PubTextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
