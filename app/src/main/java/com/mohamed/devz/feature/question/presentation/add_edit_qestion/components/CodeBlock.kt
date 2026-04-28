package com.mohamed.devz.feature.question.presentation.add_edit_qestion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.feature.question.presentation.util.tokenize
import com.mohamed.devz.ui.theme.DevzTheme

@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    language: SyntaxLanguage = SyntaxLanguage.KOTLIN,
    fileName: String? = null,
    showCopyButton: Boolean = true,
    showTrafficLights: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) {
    val clipboardManager = LocalClipboard.current
    var copied by remember { mutableStateOf(false) }

    val annotatedCode = remember(code, language) {
        buildAnnotatedString {
            tokenize(code, language).forEach { token ->
                withStyle(SpanStyle(color = token.color)) {
                    append(token.text)
                }
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0E0E0E),
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, Color(0xFF3C494C).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2A2A2A).copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (showTrafficLights) {
                        listOf(
                            Color(0xFFFF5F57),
                            Color(0xFFFFBD2E),
                            Color(0xFF28C840)
                        ).forEach { dotColor ->
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .clip(CircleShape)
                                    .background(dotColor.copy(alpha = 0.5f))
                            )
                        }
                    }
                    fileName?.let {
                        Spacer(modifier = Modifier.width(if (showTrafficLights) 8.dp else 0.dp))
                        Text(
                            text = it,
                            color = Color(0xFF869396),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                if (showCopyButton) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {

                                copied = true
                            }
                            .background(if (copied) Color(0xFF44D8F1).copy(alpha = 0.1f) else Color.Transparent)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            null,
                            tint = if (copied) Color(0xFF44D8F1) else Color(0xFF869396),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (copied) "COPIED!" else "COPY",
                            color = if (copied) Color(0xFF44D8F1) else Color(0xFF869396),
                            fontSize = 9.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Text(
                text = annotatedCode,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 20.sp,
                maxLines = maxLines,
                softWrap = false,
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp)
            )
        }
    }

    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(2000)
            copied = false
        }
    }
}

@Preview
@Composable
private fun PreviewCodeBlock() {
    DevzTheme {
        CodeBlock(
            code = """
def fetch_paged_data(limit: int) -> list:
# Query the database
query = db.collection("items")
.order_by("timestamp")
.limit(limit)
return query.get()
    """.trimIndent(),
            language = SyntaxLanguage.PYTHON,
            fileName = "data_service.py"
        )
    }
}