package com.mohamed.devz.feature.job.presentation.job_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.R
import com.mohamed.devz.feature.job.presentation.job_detail.JobApplicationField
import com.mohamed.devz.feature.job.presentation.job_detail.JobDetailAction
import com.mohamed.devz.feature.job.presentation.job_detail.JobDetailState
import com.mohamed.devz.feature.job.presentation.util.onLostFocus
import com.mohamed.devz.ui.components.DevzButton
import com.mohamed.devz.ui.components.DevzTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplySheet(
    state: JobDetailState,
    onAction: (JobDetailAction) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.jobdetail_apply_for, state.job?.title.orEmpty()),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(16.dp))

            DevzTextField(
                value = state.email,
                onValueChange = { onAction(JobDetailAction.EmailChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(onLostFocus {
                        onAction(JobDetailAction.FieldFocusChanged(JobApplicationField.EMAIL, false))
                    }),
                placeholder = stringResource(R.string.apply_email),
                singleLine = true,
                isError = state.emailTouched && state.emailError != null,
                errorMessage = state.emailError?.takeIf { state.emailTouched }?.asString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(12.dp))

            DevzTextField(
                value = state.whatsapp,
                onValueChange = { onAction(JobDetailAction.WhatsAppChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(onLostFocus {
                        onAction(JobDetailAction.FieldFocusChanged(JobApplicationField.WHATSAPP, false))
                    }),
                placeholder = stringResource(R.string.apply_whatsapp),
                singleLine = true,
                isError = state.whatsappTouched && state.whatsappError != null,
                errorMessage = state.whatsappError?.takeIf { state.whatsappTouched }?.asString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )

            Spacer(modifier = Modifier.height(12.dp))

            DevzTextField(
                value = state.coverLetter,
                onValueChange = { onAction(JobDetailAction.CoverLetterChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .then(onLostFocus {
                        onAction(JobDetailAction.FieldFocusChanged(JobApplicationField.COVER_LETTER, false))
                    }),
                placeholder = stringResource(R.string.apply_cover_letter_hint),
                singleLine = false,
                isError = state.coverLetterTouched && state.coverLetterError != null,
                errorMessage = state.coverLetterError?.takeIf { state.coverLetterTouched }?.asString(),
            )

            Spacer(modifier = Modifier.height(20.dp))

            DevzButton(
                label = stringResource(R.string.apply_submit),
                onClick = { onAction(JobDetailAction.SubmitApplication(onSuccess = {})) },
                enabled = state.canSubmit,
                loading = state.isSubmitting,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
