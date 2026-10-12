package com.mohamed.devz.feature.question.presentation.question_details.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.mohamed.devz.R
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.feature.question.presentation.util.tokenize
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors
import kotlinx.coroutines.delay


@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    language: SyntaxLanguage = SyntaxLanguage.KOTLIN,
    showCopyButton: Boolean = true,
    showTrafficLights: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    val extended = MaterialTheme.extendedColors
    val palette = extended.codeSyntax
    val annotatedCode = remember(code, language, palette) {
        buildAnnotatedString {
            tokenize(code, language, palette).forEach { token ->
                withStyle(SpanStyle(color = token.color)) {
                    append(token.text)
                }
            }
        }
    }
    val copiedToast = stringResource(R.string.code_copied_toast)

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = extended.codeBackground,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, extended.codeBorder, RoundedCornerShape(16.dp))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (showTrafficLights) {
                        listOf(
                            Color(0xFFA6574C),
                            Color(0xFF9F6C3B),
                            Color(0xFF2D8F9F)
                        ).forEach { dotColor ->
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .clip(CircleShape)
                                    .background(dotColor.copy(alpha = 0.5f))
                            )
                        }
                    }
                }

                if (showCopyButton) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("code", code))
                                Toast.makeText(context, copiedToast, Toast.LENGTH_SHORT).show()
                                copied = true
                            }
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            if (copied) Icons.Rounded.Done else Icons.Rounded.ContentCopy,
                            contentDescription = stringResource(R.string.code_copy_desc),
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (copied) stringResource(R.string.code_copied) else stringResource(R.string.code_copy),
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            if (code.isBlank()) {
                Text(
                    text = stringResource(R.string.code_none),
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
        }
    }
}

@Preview(name = "CodeBlock — Dark")
@Composable
private fun PreviewCodeBlockDark() {
    DevzTheme(themeMode = ThemeMode.DARK) { PreviewCodeBlockContent() }
}

@Preview(name = "CodeBlock — Light")
@Composable
private fun PreviewCodeBlockLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) { PreviewCodeBlockContent() }
}

@Composable
private fun PreviewCodeBlockContent() {
    CodeBlock(
        code = """
def fetch_paged_data(limit: int) -> list:
# Query the database
query = db.collection("items")
    .order_by("timestamp")
    .limit(limit)
return query.get()
        """.trimIndent(),
        language = SyntaxLanguage.PYTHON
    )
}
