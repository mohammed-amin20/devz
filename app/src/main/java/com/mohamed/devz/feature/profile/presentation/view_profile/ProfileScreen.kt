package com.mohamed.devz.feature.profile.presentation.view_profile

import androidx.compose.foundation.Image
import com.mohamed.devz.feature.profile.presentation.view_profile.components.ProfileAnswerCard
import com.mohamed.devz.feature.profile.presentation.view_profile.components.ProfileQuestionCard
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.TextGray
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.profile.presentation.view_profile.components.ProfileViewModel
import com.mohamed.devz.feature.profile.presentation.view_profile.components.StatCard
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.TextWhite

@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onQuestionClick: (String) -> Unit,
    onHome: () -> Unit,
    onAsk: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("MY QUESTIONS", "MY ANSWERS")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0D3333),
                        Color(0xFF0A1A1A),
                        Color(0xFF060D0D)
                    ),
                    center = Offset(0.5f, 0.45f),
                    radius = 1200f
                )
            )
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            ) { append("dev") }
                            withStyle(
                                SpanStyle(
                                    color = CyanPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            ) { append("Z") }
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                    Icon(
                        Icons.Filled.Notifications,
                        null,
                        tint = TextGray,
                        modifier = Modifier.size(22.dp)
                    )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Avatar ────────────────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CyanPrimary.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(DevzCard)
                        .border(2.dp, CyanPrimary.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Name + username + points ──────────────────────────────────
        item {
            Text(
                text = uiState.profile?.fullName ?: "Alex Rivera",
                color = TextWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "@${uiState.profile?.username ?: "arivera_dev"}",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(CyanPrimary.copy(alpha = 0.15f))
                        .border(1.dp, CyanPrimary.copy(alpha = 0.4f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Star,
                        null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "${uiState.profile?.points ?: "2,480"} PTS",
                        color = CyanPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── Edit Profile button ───────────────────────────────────────
        item {
            Button(
                onClick = onEditProfile,
                modifier = Modifier
                    .width(180.dp)
                    .height(44.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color.Black
                )
            ) {
                Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Stats grid ────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    label = "ANSWERS",
                    value = "${uiState.profile?.answerCount ?: 142}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "QUESTIONS",
                    value = "${uiState.profile?.questionCount ?: 28}",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    label = "ACCEPTED",
                    value = "${uiState.profile?.acceptedRate ?: "86%"}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "GLOBAL RANK",
                    value = "#${uiState.profile?.globalRank ?: "1,204"}",
                    valueColor = CyanPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Specialized skills ────────────────────────────────────────
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SPECIALIZED SKILLS",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                val skills = uiState.profile?.skills
                    ?: listOf("TypeScript", "React", "Next.js", "Rust", "GraphQL", "WebAssembly", "PostgreSQL", "Tailwind CSS")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    skills.forEachIndexed { index, skill ->
                        val isHighlighted = index == 0 || index == 3
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isHighlighted) CyanPrimary.copy(alpha = 0.15f)
                                    else DevzCard
                                )
                                .border(
                                    1.dp,
                                    if (isHighlighted) CyanPrimary.copy(alpha = 0.5f)
                                    else Color(0xFF2A3A3A),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = skill,
                                color = if (isHighlighted) CyanPrimary else TextGray,
                                fontSize = 13.sp,
                                fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // ── Tabs ──────────────────────────────────────────────────────
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTab == index
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = index },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) CyanPrimary else TextGray,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(if (isSelected) CyanPrimary else Color.Transparent)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF2A3A3A))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Tab content ───────────────────────────────────────────────
        if (selectedTab == 0) {
            items(uiState.myQuestions, key = { it.id }) { question ->
                ProfileQuestionCard(
                    question = question,
                    onClick = { onQuestionClick(question.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        } else {
            items(uiState.myAnswers, key = { it.id }) { answer ->
                ProfileAnswerCard(answer = answer)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
@Preview
@Composable
private fun PrevProfile() {
    ProfileScreen(
        onEditProfile = {},
        onQuestionClick = {},
        onAsk = {},
        onHome = {}
    )
}
