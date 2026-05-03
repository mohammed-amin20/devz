package com.mohamed.devz.feature.question.presentation.add_edit_qestion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.feature.question.presentation.util.tokenize

@Composable
fun CodeEditorField(
    modifier: Modifier = Modifier,
//    code: String = "",
    language: SyntaxLanguage = SyntaxLanguage.KOTLIN,
    minLines: Int = 1,
    placeholder: String = "// Paste your code here...",
) {
    var code by remember { mutableStateOf("") }
    val highlightedCode = remember(code, language) {
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
                    .fillMaxHeight()
                    .defaultMinSize(minHeight = (minLines * 20).dp)
            ) {
                val lineCount = highlightedCode.text.lines().size.coerceAtLeast(minLines)
                Column(
                    modifier = Modifier
                        .background(Color(0xFF0A0A0A))
                        .padding(horizontal = 10.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    repeat(lineCount) { index ->
                        Text(
                            text = (index + 1).toString(),
                            color = Color(0xFF3C494C),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 20.sp
                        )
                    }
                }
                if(highlightedCode.text.isBlank()) {
                    TextField(
                        value = "",
                        onValueChange = { code = it.reversed() },
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = (minLines * 20).dp)
                            .horizontalScroll(rememberScrollState())
                        /*.padding(horizontal = 12.dp, vertical = 16.dp)*/,
                        singleLine = false,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFF44D8F1)
                        ),
                        placeholder = {
                            Text(
                                text = placeholder,
                                color = Color(0xFF3C494C),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 20.sp
                            )
                        }
                    )
                } else {
                    TextField(
                        value = TextFieldValue(highlightedCode),
                        onValueChange = {
                            code = it.text.reversed()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = (minLines * 20).dp)
                            .horizontalScroll(rememberScrollState())
                        /*.padding(horizontal = 12.dp, vertical = 16.dp)*/,
                        singleLine = false,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFF44D8F1)
                        ),
                        textStyle = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 20.sp
                        )
                    )
                }

                /*BasicTextField(
                    state = codeInputState,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = (minLines * 20).dp)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    cursorBrush = SolidColor(Color(0xFF44D8F1)),
                    lineLimits = TextFieldLineLimits.MultiLine(),
                    textStyle = TextStyle(
                        color = Color.Transparent
                    ),
                    decorator = { innerTextField ->
                        Box {
                            if (codeInputState.text.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = Color(0xFF3C494C),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 20.sp
                                )
                            } else {
                                Text(
                                    text = highlightedCode,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 20.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )*/
            }
        }
    }
}