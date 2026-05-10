package com.mohamed.devz.feature.question.presentation.question_details.compose.foundation.Image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mohamed.devz.R
import com.mohamed.devz.feature.question.presentation.question_details.components.ActionPill
import com.mohamed.devz.feature.question.presentation.question_details.components.AnswerCard
import com.mohamed.devz.feature.question.presentation.question_details.components.AnswerInputBar
import com.mohamed.devz.feature.question.presentation.question_details.components.Bg
import com.mohamed.devz.feature.question.presentation.question_details.components.Breadcrumb
import com.mohamed.devz.feature.question.presentation.question_details.components.CodeBlock
import com.mohamed.devz.feature.question.presentation.question_details.components.OnSurface
import com.mohamed.devz.feature.question.presentation.question_details.components.OnSurfaceVariant
import com.mohamed.devz.feature.question.presentation.question_details.components.Outline
import com.mohamed.devz.feature.question.presentation.question_details.components.OutlineVariant
import com.mohamed.devz.feature.question.presentation.question_details.components.Primary
import com.mohamed.devz.feature.question.presentation.question_details.components.QuestionContent
import com.mohamed.devz.feature.question.presentation.question_details.components.SurfaceHigh
import com.mohamed.devz.feature.question.presentation.question_details.components.SurfaceLow
import com.mohamed.devz.feature.question.presentation.question_details.components.TagChip
import com.mohamed.devz.feature.question.presentation.question_details.components.TopBar
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.TextWhite

data class QuestionDetailUiModel(
    val title: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val timeAgo: String,
    val tags: List<String>,
    val body: String,
    val language: SyntaxLanguage,
    val code: String,
    val likes: Int,
    val answersCount: Int
)

data class AnswerUiModel(
    val authorName: String,
    val timeAgo: String,
    val avatarUrl: String,
    val body: String,
    val likes: Int,
    val isAccepted: Boolean = false
)

@Composable
fun QuestionDetailScreen(
    modifier: Modifier = Modifier
) {
    val question = remember {
        QuestionDetailUiModel(
            title = "How to implement a clean architecture with React and Tailwind while keeping components reusable?",
            authorName = "Alex Rivers",
            authorAvatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDY38hTlTpmk7GzVMpcfJjQRzRbD6kdEfj-SvitVErUuBwPRNCeRUa-EzzpaRMsxSvpi2MYrZSBTKMWuunI7s1MWwbFQ8EpD2FfpMuouD2QP4-LSjkIOYuGPdMO4w4EN28ElVsCDia7cS5GvOsrerZ1-YdLDNWhLgyPdlAicxJORTHRA6D_m6ARH-N2Za3vxwigpz85C3ctlCAStgWsf_ombYmwWxIXZ3AEBuG-HswR-IHdpq5NuUWOHDrPW3xFkw0TpMfErzmJOZg",
            timeAgo = "2 hours ago",
            tags = listOf("ARCH", "REACT"),
            body = "I'm struggling to find the right balance between strict separation of concerns and the pragmatic approach of Tailwind's utility-first styling. I've noticed that my component logic often gets cluttered with long class strings, making it hard to maintain the 'clean architecture' principles.",
            language = SyntaxLanguage.JAVASCRIPT,
            code = """
import React from 'react';
import { cva, type VariantProps } from 'class-variance-authority';

// Defining the styling layer outside the logic
const buttonVariants = cva(
  "inline-flex items-center justify-center rounded-md font-medium",
  {
    variants: {
      variant: {
        primary: "bg-blue-600 text-white hover:bg-blue-700",
        outline: "border-2 border-slate-200 bg-transparent",
      },
      size: {
        default: "h-10 px-4 py-2",
        sm: "h-9 px-3",
      },
    },
    defaultVariants: {
      variant: "primary",
      size: "default",
    },
  }
);
            """.trimIndent(),
            likes = 124,
            answersCount = 8
        )
    }

    val answers = remember {
        listOf(
            AnswerUiModel(
                authorName = "Sarah Chen",
                timeAgo = "Replied 32 minutes ago",
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBG5INegJV563dcp4CwqiQB1PphB3tvtELFyAHX6PbxOZMFUTp7BzdLJwtTQTZ7xSOTnLSsxBUCJ6bAm1i6vGdh2ytSNMQPdXj-2tiasexm3O2yYMuEOwQ9G_ZAEucRKyQp_B1guQ_7uaRXGeVckTMTIvNzL9zgmSrsGr_7iUsCcecQCjVT6EbK_BHG78fJyWmHhcJBLTY8mdFW_Wh-UadS7ef8Dw07aAb0emYfXX6XNMHrl1yCD45my7UKzAumJ75qh6CLGl0eqOo",
                body = "Your use of class-variance-authority is actually the industry standard right now for this exact problem.\n\nTo go a step further into 'Clean Architecture', you should keep your UI components completely \"dumb.\" They should only receive props and the styling logic. Move any business logic or data fetching into Custom Hooks or Container Components.",
                likes = 42,
                isAccepted = true
            ),
            AnswerUiModel(
                authorName = "Marcus V.",
                timeAgo = "Replied 1 hour ago",
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjqlZGMrdweexciumvkrx0UA4ur2Ms5pBkY7UCNfZUYf-WqeVLkgHsYVRT_hTsig4TNs7FCX5DQkT9k_vSQ_wAWO-zUs31RC2AjF2QMGZ_QZOt0hd7IAJdmxLfcTGO3s6bcFJAPMszcQ2itvMzW_uD5MMXmUOm7dslwk5M3jK6AXJ8G4zL7A5ZnRKbXLbT2Yl5AB3GcP8MHAcupaweIWxhtviNY4PrZP_t0JuuQKn6yyiVJvX9gFM9y-wa5HHFl-ZREONd_RNzLMU",
                body = "I would also suggest looking into the Atomic Design pattern. It complements Tailwind beautifully. Atoms are your basic components (buttons, inputs) where the utility classes live. Organisms combine them into larger pieces where the layout logic resides.",
                likes = 12
            )
        )
    }

    var answerText by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        containerColor = Bg,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TopBar()

            QuestionContent(
                question = question,
                answers = answers,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            AnswerInputBar(
                answerText = answerText,
                onAnswerChange = { answerText = it },
                onPost = {}
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewQuestionDetailScreen() {
    DevzTheme {
        QuestionDetailScreen()
    }
}