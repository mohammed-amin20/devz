package com.mohamed.devz.feature.question.presentation.question_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QOutline
import com.mohamed.devz.ui.theme.QPrimary
import com.mohamed.devz.ui.theme.QPrimaryContainer
import com.mohamed.devz.ui.theme.QSurfaceHigh
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnswerInputBar(
    answerText: String,
    onAnswerChange: (String) -> Unit,
    onPost: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(QBg.copy(alpha = 0.92f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = answerText,
                onValueChange = onAnswerChange,
                modifier = Modifier.weight(1f),
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
                    .size(width = 78.dp, height = 46.dp)
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