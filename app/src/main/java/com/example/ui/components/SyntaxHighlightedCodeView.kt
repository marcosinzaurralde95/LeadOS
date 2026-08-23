package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SyntaxHighlightedCodeView(
    code: String,
    language: String = "yaml",
    isEditable: Boolean = false,
    onCodeChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Surface(
        color = CodeBackground,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Code header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).background(RoseError, shape = RoundedCornerShape(5.dp)))
                    Box(modifier = Modifier.size(10.dp).background(AmberWarning, shape = RoundedCornerShape(5.dp)))
                    Box(modifier = Modifier.size(10.dp).background(EmeraldSuccess, shape = RoundedCornerShape(5.dp)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = language.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Slate400,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(code))
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = Slate400,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Code content with line numbers
            val lines = code.lines()
            val lineNumbers = (1..lines.size).joinToString("\n")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .verticalScroll(verticalScroll)
            ) {
                // Line numbers
                Text(
                    text = lineNumbers,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Slate600
                    ),
                    modifier = Modifier.padding(end = 12.dp)
                )

                Divider(
                    color = Slate800,
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .width(1.dp)
                        .padding(end = 12.dp)
                )

                if (isEditable) {
                    BasicTextField(
                        value = code,
                        onValueChange = onCodeChange,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Slate100
                        ),
                        cursorBrush = SolidColor(CyanBright),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScroll)
                    )
                } else {
                    val highlighted = highlightCode(code, language)
                    Text(
                        text = highlighted,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScroll)
                    )
                }
            }
        }
    }
}

private fun highlightCode(code: String, language: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = code.lines()
        lines.forEachIndexed { index, line ->
            val trimmed = line.trimStart()
            when {
                trimmed.startsWith("#") || trimmed.startsWith("//") -> {
                    append(line)
                    addStyle(SpanStyle(color = CodeComment), length - line.length, length)
                }
                language == "yaml" && trimmed.contains(":") -> {
                    val keyPart = line.substringBefore(":")
                    val valuePart = line.substringAfter(":")
                    append(keyPart)
                    addStyle(SpanStyle(color = CodeProperty), length - keyPart.length, length)
                    append(":")
                    addStyle(SpanStyle(color = Slate400), length - 1, length)
                    append(valuePart)
                    val valueColor = when {
                        valuePart.trim().startsWith("\"") || valuePart.trim().startsWith("'") -> CodeString
                        valuePart.trim() == "true" || valuePart.trim() == "false" -> CodeKeyword
                        else -> CodeString
                    }
                    addStyle(SpanStyle(color = valueColor), length - valuePart.length, length)
                }
                trimmed.startsWith("- ") -> {
                    append(line)
                    addStyle(SpanStyle(color = CyanBright), length - line.length, length)
                }
                trimmed.startsWith("{") || trimmed.startsWith("}") || trimmed.startsWith("[") || trimmed.startsWith("]") -> {
                    append(line)
                    addStyle(SpanStyle(color = ElectricViolet), length - line.length, length)
                }
                else -> {
                    append(line)
                    addStyle(SpanStyle(color = Slate200), length - line.length, length)
                }
            }
            if (index < lines.size - 1) append("\n")
        }
    }
}
