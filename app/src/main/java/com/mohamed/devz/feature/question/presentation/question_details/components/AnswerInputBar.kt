package com.mohamed.devz.feature.question.presentation.question_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QOutline
import com.mohamed.devz.ui.theme.QPrimary
import com.mohamed.devz.ui.theme.QPrimaryContainer
import com.mohamed.devz.ui.theme.QSurfaceHigh
@Composable
fun AnswerInputBar(
    answerText: String,
    onAnswerChange: (String) -> Unit,
    onPost: () -> Unit,
    answerCode: String? = null,
    onCodeClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(QBg.copy(alpha = 0.92f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (!answerCode.isNullOrBlank()) {
            Surface(
                onClick = onCodeClick,
                shape = RoundedCornerShape(10.dp),
                color = QPrimary.copy(alpha = 0.12f),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "< >",
                        color = QPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                    )
                    Text(
                        text = "Code attached",
                        color = QPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onCodeClick) {
                Text(
                    text = "< >",
                    color = if (answerCode.isNullOrBlank()) QOutline else QPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
            }

            TextField(
                value = answerText,
                onValueChange = onAnswerChange,
                modifier = Modifier.weight(1f),
                maxLines = 5,
                placeholder = {
                    Text(
                        text = "Write your answer...",
                        color = QOutline,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = TextFieldDefaults.colors(
                    cursorColor = QPrimary,
                    focusedContainerColor = QSurfaceHigh,
                    unfocusedContainerColor = QSurfaceHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 50.dp)
                    .background(
                        Brush.linearGradient(listOf(QPrimary, QPrimaryContainer)),
                        RoundedCornerShape(14.dp)
                    )
                    .clip(RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onPost) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF00363E))
                }
            }
        }
    }
}

