package com.mohamed.devz.feature.profile.presentation.view_profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

@Composable
fun ProfileAnswerCard(answer: ProfileAnswerUiModel) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DevzCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = answer.questionTitle,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (answer.isAccepted) CyanPrimary.copy(alpha = 0.15f)
                            else Color(0xFF2A2A2A)
                        )
                        .border(
                            0.5.dp,
                            if (answer.isAccepted) CyanPrimary.copy(alpha = 0.4f)
                            else Color(0xFF3A3A3A),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (answer.isAccepted) "ACCEPTED" else "PENDING",
                        color = if (answer.isAccepted) CyanPrimary else TextGray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Answer preview
            Text(
                text = answer.preview,
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.ThumbUp, null, tint = TextGray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(answer.likes.toString(), color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Filled.ChatBubbleOutline, null, tint = TextGray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(answer.comments.toString(), color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Answered ${answer.timeAgo}",
                    color = TextGray,
                    fontSize = 11.sp
                )
            }
        }
    }
}