package com.mohamed.devz.feature.admin.presentation.manage_companies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCompaniesScreen(
    onNavigateUp: () -> Unit,
    viewModel: ManageCompaniesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = QBg,
        topBar = {
            TopAppBar(
                title = { Text("Manage Companies", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite, modifier = Modifier.size(24.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = QBg),
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = CyanPrimary)
                }
            }
            uiState.companies.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No companies registered", color = QOnSurfaceVariant, fontSize = 16.sp)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.companies, key = { it.id }) { company ->
                        CompanyCard(
                            company = company,
                            onToggle = { activate ->
                                viewModel.onAction(ManageCompaniesAction.ToggleSubscription(company.accountId, activate))
                            },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun CompanyCard(
    company: CompanyUiModel,
    onToggle: (Boolean) -> Unit,
) {
    val isActive = company.subscriptionStatus == "active"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DevzCard,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (company.logoUrl.isNotBlank()) {
                AsyncImage(
                    model = company.logoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyanPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(44.dp),
                ) {
                    Icon(
                        Icons.Filled.Business,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = company.companyName,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (company.website.isNotBlank()) {
                    Text(
                        text = company.website,
                        color = CyanPrimary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isActive) "Active" else "Inactive",
                    color = if (isActive) Color(0xFF4CAF50) else TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(
                            if (isActive) Color(0xFF4CAF50).copy(alpha = 0.15f)
                            else TextGray.copy(alpha = 0.15f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { onToggle(!isActive) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) Color(0xFFFF5252) else Color(0xFF4CAF50),
                ),
            ) {
                Text(
                    text = if (isActive) "Deactivate" else "Activate",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite,
                )
            }
        }
    }
}
