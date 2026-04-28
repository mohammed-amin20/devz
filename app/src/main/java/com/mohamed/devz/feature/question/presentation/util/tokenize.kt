package com.mohamed.devz.feature.question.presentation.util

import androidx.compose.ui.graphics.Color

private object SyntaxColors {
    val keyword = Color(0xFFFFB87B)
    val string = Color(0xFFA1EFFF)
    val comment = Color(0xFF869396)
    val function = Color(0xFF44D8F1)
    val number = Color(0xFFF19640)
    val annotation = Color(0xFFFFD700)
    val type = Color(0xFF82AAFF)
    val plain = Color(0xFFE5E2E1)
}

private object SyntaxKeywords {
    val kotlinKeywords = setOf(
        "fun", "val", "var", "class", "object", "interface", "data",
        "sealed", "enum", "return", "if", "else", "when", "for", "while",
        "import", "package", "override", "private", "public", "internal",
        "suspend", "companion", "by", "in", "is", "as", "null", "true",
        "false", "this", "super", "init", "constructor", "throw", "try",
        "catch", "finally", "lateinit", "lazy", "it", "typealias"
    )

    val jsKeywords = setOf(
        "import", "export", "from", "const", "let", "var", "function",
        "return", "if", "else", "for", "while", "class", "extends",
        "new", "this", "null", "undefined", "true", "false", "type",
        "interface", "async", "await", "default", "typeof", "instanceof"
    )

    val pythonKeywords = setOf(
        "def", "class", "import", "from", "return", "if", "elif", "else",
        "for", "while", "in", "is", "not", "and", "or", "True", "False",
        "None", "with", "as", "pass", "break", "continue", "lambda",
        "try", "except", "finally", "raise", "yield", "async", "await"
    )
}

fun tokenize(code: String, language: SyntaxLanguage): List<Token> {
    val indentedCode = if (language == SyntaxLanguage.PYTHON)
        PythonIndentFormatter().format(code)
    else
        IndentationFormatter().format(code)

    val keywords = when (language) {
        SyntaxLanguage.KOTLIN -> SyntaxKeywords.kotlinKeywords
        SyntaxLanguage.JAVASCRIPT -> SyntaxKeywords.jsKeywords
        SyntaxLanguage.PYTHON -> SyntaxKeywords.pythonKeywords
        SyntaxLanguage.GENERIC -> emptySet()
    }

    val tokens = mutableListOf<Token>()
    val lines = indentedCode.lines()

    for ((lineIndex, line) in lines.withIndex()) {
        var i = 0
        while (i < line.length && (line[i] == ' ' || line[i] == '\t')) {
            i++
        }
        if (i > 0) {
            tokens.add(Token(line.substring(0, i), SyntaxColors.plain))
        }

        while (i < line.length) {
            when {
                line.startsWith("//", i) -> {
                    tokens.add(Token(line.substring(i), SyntaxColors.comment))
                    i = line.length
                }

                line[i] == '#' && language == SyntaxLanguage.PYTHON -> {
                    tokens.add(Token(line.substring(i), SyntaxColors.comment))
                    i = line.length
                }

                line[i] == '"' -> {
                    val end = line.indexOf('"', i + 1).takeIf { it != -1 } ?: (line.length - 1)
                    tokens.add(Token(line.substring(i, end + 1), SyntaxColors.string))
                    i = end + 1
                }

                line[i] == '\'' -> {
                    val end = line.indexOf('\'', i + 1).takeIf { it != -1 } ?: (line.length - 1)
                    tokens.add(Token(line.substring(i, end + 1), SyntaxColors.string))
                    i = end + 1
                }

                line[i] == '@' && language == SyntaxLanguage.KOTLIN -> {
                    var end = i + 1
                    while (end < line.length && (line[end].isLetterOrDigit() || line[end] == '_')) end++
                    tokens.add(Token(line.substring(i, end), SyntaxColors.annotation))
                    i = end
                }

                line[i].isDigit() -> {
                    var end = i
                    while (end < line.length && (line[end].isDigit() || line[end] == '.')) end++
                    tokens.add(Token(line.substring(i, end), SyntaxColors.number))
                    i = end
                }

                line[i] == ' ' || line[i] == '\t' -> {
                    var end = i
                    while (end < line.length && (line[end] == ' ' || line[end] == '\t')) end++
                    tokens.add(Token(line.substring(i, end), SyntaxColors.plain))
                    i = end
                }

                line[i].isLetter() || line[i] == '_' -> {
                    var end = i
                    while (end < line.length && (line[end].isLetterOrDigit() || line[end] == '_')) end++
                    val word = line.substring(i, end)
                    val color = when {
                        word in keywords -> SyntaxColors.keyword
                        end < line.length && line[end] == '(' -> SyntaxColors.function
                        word.first().isUpperCase() -> SyntaxColors.type
                        else -> SyntaxColors.plain
                    }
                    tokens.add(Token(word, color))
                    i = end
                }

                else -> {
                    tokens.add(Token(line[i].toString(), SyntaxColors.plain))
                    i++
                }
            }
        }

        if (lineIndex < lines.size - 1) {
            tokens.add(Token("\n", SyntaxColors.plain))
        }
    }

    return tokens
}