package com.mohamed.devz.feature.question.presentation.util

data class FormatterConfig(
    val indentUnit: String = "    ",
    val openingTokens: Set<Char> = setOf('{', '[', '('),
    val closingTokens: Set<Char> = setOf('}', ']', ')'),
)

class IndentationFormatter(
    private val config: FormatterConfig = FormatterConfig()
) {
    fun format(input: String): String {
        val lines = input.lines()
        val result = StringBuilder()
        var indentLevel = 0

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) {
                result.appendLine()
                continue
            }

            val leadingClosers = countLeadingClosers(line)
            val effectiveIndent = (indentLevel - leadingClosers).coerceAtLeast(0)

            repeat(effectiveIndent) {
                result.append(config.indentUnit)
            }
            result.appendLine(line)

            indentLevel += countOpeningTokens(line)
            indentLevel -= countClosingTokens(line)
            if (indentLevel < 0) indentLevel = 0
        }

        return result.toString().trimEnd()
    }

    private fun countLeadingClosers(line: String): Int {
        var count = 0
        for (ch in line) {
            if (ch.isWhitespace()) continue
            if (ch in config.closingTokens) count++
            else break
        }
        return count
    }

    private fun countOpeningTokens(line: String): Int {
        var count = 0
        for (ch in line) {
            if (ch in config.openingTokens) count++
        }
        return count
    }

    private fun countClosingTokens(line: String): Int {
        var count = 0
        for (ch in line) {
            if (ch in config.closingTokens) count++
        }
        return count
    }
}

data class PythonFormatterConfig(
    val indentUnit: String = "    ",
    val dedentBeforeLine: Set<String> = setOf("elif", "else", "except", "finally"),
)

class PythonIndentFormatter(
    private val config: PythonFormatterConfig = PythonFormatterConfig(),
) {
    fun format(input: String): String {
        val lines = input.lines()
        val output = StringBuilder()

        var indentLevel = 0
        var continuationDepth = 0
        var inTripleQuotedString = false
        var tripleQuoteDelimiter: String? = null

        for (rawLine in lines) {
            val line = rawLine.trim()

            if (line.isEmpty()) {
                output.appendLine()
                continue
            }

            val dedentFirst = shouldDedentBeforeLine(line)
            if (dedentFirst) {
                indentLevel = (indentLevel - 1).coerceAtLeast(0)
            }

            val effectiveIndent = (indentLevel + continuationDepth).coerceAtLeast(0)
            repeat(effectiveIndent) {
                output.append(config.indentUnit)
            }
            output.appendLine(line)

            val stringState =
                updateTripleQuoteState(line, inTripleQuotedString, tripleQuoteDelimiter)
            inTripleQuotedString = stringState.first
            tripleQuoteDelimiter = stringState.second

            if (!inTripleQuotedString) {
                continuationDepth = countContinuationDepth(line)
                if (endsWithBlockColon(line)) {
                    indentLevel++
                }
            } else {
                continuationDepth = 0
            }
        }

        return output.toString().trimEnd()
    }

    private fun shouldDedentBeforeLine(line: String): Boolean {
        val firstWord = line
            .trimStart()
            .takeWhile { !it.isWhitespace() && it != ':' && it != '(' && it != '[' && it != '{' }

        return firstWord in config.dedentBeforeLine
    }

    private fun endsWithBlockColon(line: String): Boolean {
        val trimmed = line.trimEnd()

        if (!trimmed.endsWith(":")) return false
        if (trimmed.startsWith("#")) return false

        // Avoid counting slices like a[1:3] or dictionary values if colon is internal.
        // This is a simple heuristic: only treat trailing colon as a block starter.
        return true
    }

    private fun countContinuationDepth(line: String): Int {
        var depth = 0
        for (ch in line) {
            when (ch) {
                '(', '[', '{' -> depth++
                ')', ']', '}' -> if (depth > 0) depth--
            }
        }

        val trimmed = line.trimEnd()
        return if (depth > 0 || endsWithLineContinuation(trimmed)) 1 else 0
    }

    private fun endsWithLineContinuation(line: String): Boolean {
        return line.endsWith("\\")
    }

    private fun updateTripleQuoteState(
        line: String,
        currentlyInString: Boolean,
        currentDelimiter: String?,
    ): Pair<Boolean, String?> {
        val tripleQuotes = listOf("\"\"\"", "'''")
        var inString = currentlyInString
        var delimiter = currentDelimiter

        for (quote in tripleQuotes) {
            val count = line.windowed(3, 1, partialWindows = false).count { it == quote }
            if (count % 2 != 0) {
                if (!inString) {
                    inString = true
                    delimiter = quote
                } else if (delimiter == quote) {
                    inString = false
                    delimiter = null
                }
                break
            }
        }

        return inString to delimiter
    }
}