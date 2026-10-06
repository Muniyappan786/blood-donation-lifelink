package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodGroup
import com.example.ui.theme.BloodRed20
import com.example.ui.theme.BloodRed40
import com.example.ui.theme.BloodRed90
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusEligible
import com.example.viewmodel.BloodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorRegistrationScreen(
    viewModel: BloodViewModel,
    onRegistrationSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) } // 1: Personal, 2: Physical, 3: Medical, 4: Consent

    // Step 1: Personal
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+91 ") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var city by remember { mutableStateOf("Salem") }
    var address by remember { mutableStateOf("") }

    // Step 2: Physical & Cooldown
    var ageStr by remember { mutableStateOf("28") }
    var weightStr by remember { mutableStateOf("65.0") }
    var daysSinceLastDonationStr by remember { mutableStateOf("120") }
    var isFirstTimeDonor by remember { mutableStateOf(false) }

    // Step 3: Medical checklist
    var hasChronicIllness by remember { mutableStateOf(false) }
    var takesMedication by remember { mutableStateOf(false) }
    var hasRecentSurgery by remember { mutableStateOf(false) }

    // Step 4: Consent & Privacy
    var consentGiven by remember { mutableStateOf(true) }
    var termsAccepted by remember { mutableStateOf(true) }

    var submittedSuccessfully by remember { mutableStateOf(false) }

    val age = ageStr.toIntOrNull() ?: 0
    val weight = weightStr.toDoubleOrNull() ?: 0.0
    val daysSinceDonation = if (isFirstTimeDonor) 365 else (daysSinceLastDonationStr.toIntOrNull() ?: 0)

    val isAgeValid = age in 18..65
    val isWeightValid = weight >= 50.0
    val isCooldownCleared = daysSinceDonation >= 90
    val isHealthCleared = !hasChronicIllness && !hasRecentSurgery
    val overallEligible = isAgeValid && isWeightValid && isCooldownCleared && isHealthCleared

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("donor_registration_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (submittedSuccessfully) {
            item {
                SuccessSubmissionView(
                    donorName = fullName,
                    bloodGroup = bloodGroup,
                    isEligible = overallEligible,
                    onDone = onRegistrationSuccess
                )
            }
        } else {
            // Header & Step indicator
            item {
                Column {
                    Text(
                        text = "Donor Intake Screening",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Mandatory medical criteria validation adhering to blood donation safety guidelines.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    StepProgressBar(currentStep = step, totalSteps = 4)
                }
            }

            // Step Content
            when (step) {
                1 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Step 1: Identity & Blood Profile",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = BloodRed20
                                )

                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    label = { Text("Full Name *") },
                                    modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email Address *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = { Text("Phone Number (will be masked in public view) *") },
                                    modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
                                    singleLine = true
                                )

                                // Blood Group Picker
                                var bgExpanded by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = bgExpanded,
                                    onExpandedChange = { bgExpanded = it },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = bloodGroup,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Blood Group *") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bgExpanded) },
                                        modifier = Modifier.fillMaxWidth().menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = bgExpanded,
                                        onDismissRequest = { bgExpanded = false }
                                    ) {
                                        BloodGroup.entries.forEach { bg ->
                                            DropdownMenuItem(
                                                text = { Text("${bg.label} (${bg.name.replace("_POS", " Positive").replace("_NEG", " Negative")})") },
                                                onClick = {
                                                    bloodGroup = bg.label
                                                    bgExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = city,
                                        onValueChange = { city = it },
                                        label = { Text("City *") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = address,
                                        onValueChange = { address = it },
                                        label = { Text("Locality / Area *") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { step = 2 },
                                    enabled = fullName.isNotBlank() && phone.length >= 8 && city.isNotBlank(),
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BloodRed40)
                                ) {
                                    Text("Next: Physical Criteria", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                2 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Step 2: Physical & Cooldown Validation",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = BloodRed20
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = ageStr,
                                        onValueChange = { ageStr = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Age (18-65) *") },
                                        modifier = Modifier.weight(1f).testTag("reg_age_input"),
                                        singleLine = true,
                                        isError = !isAgeValid && ageStr.isNotBlank(),
                                        supportingText = {
                                            if (!isAgeValid && ageStr.isNotBlank()) {
                                                Text("Must be 18 to 65 years", color = StatusCritical)
                                            }
                                        }
                                    )

                                    OutlinedTextField(
                                        value = weightStr,
                                        onValueChange = { weightStr = it },
                                        label = { Text("Weight in kg *") },
                                        modifier = Modifier.weight(1f).testTag("reg_weight_input"),
                                        singleLine = true,
                                        isError = !isWeightValid && weightStr.isNotBlank(),
                                        supportingText = {
                                            if (!isWeightValid && weightStr.isNotBlank()) {
                                                Text("Min 50.0 kg required", color = StatusCritical)
                                            }
                                        }
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = isFirstTimeDonor,
                                        onCheckedChange = { isFirstTimeDonor = it }
                                    )
                                    Text("I am a first-time donor (No prior donations)", fontSize = 13.sp)
                                }

                                if (!isFirstTimeDonor) {
                                    OutlinedTextField(
                                        value = daysSinceLastDonationStr,
                                        onValueChange = { daysSinceLastDonationStr = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Days Since Last Donation *") },
                                        modifier = Modifier.fillMaxWidth().testTag("reg_cooldown_input"),
                                        singleLine = true,
                                        supportingText = {
                                            if (!isCooldownCleared) {
                                                val left = 90 - daysSinceDonation
                                                Text("Under 90-day cooldown ($left days remaining before next safe donation)", color = StatusCritical)
                                            } else {
                                                Text("Safe interval cleared (> 90 days)", color = StatusEligible)
                                            }
                                        }
                                    )
                                }

                                // Medical notice box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (overallEligible) StatusEligible.copy(alpha = 0.1f) else StatusCritical.copy(alpha = 0.1f))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = if (isCooldownCleared && isAgeValid && isWeightValid)
                                            "Physical eligibility check passed."
                                        else
                                            "Status alert: Ineligible profiles can still register but will be paused in standby until cooldown/criteria are met.",
                                        fontSize = 11.sp,
                                        color = if (isCooldownCleared && isAgeValid && isWeightValid) StatusEligible else StatusCritical
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { step = 1 },
                                        modifier = Modifier.weight(1f).height(48.dp)
                                    ) {
                                        Text("Back")
                                    }
                                    Button(
                                        onClick = { step = 3 },
                                        enabled = ageStr.isNotBlank() && weightStr.isNotBlank(),
                                        modifier = Modifier.weight(1.5f).height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BloodRed40)
                                    ) {
                                        Text("Next: Medical Check", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "Step 3: Medical Health Checklist",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = BloodRed20
                                )

                                MedicalChecklistRow(
                                    title = "Chronic Illness",
                                    desc = "Diabetes (on insulin), heart conditions, hepatitis, or cancer",
                                    checked = hasChronicIllness,
                                    onCheckedChange = { hasChronicIllness = it }
                                )

                                MedicalChecklistRow(
                                    title = "Active Prescription Medication",
                                    desc = "Blood thinners, antibiotics in the last 72 hours",
                                    checked = takesMedication,
                                    onCheckedChange = { takesMedication = it }
                                )

                                MedicalChecklistRow(
                                    title = "Recent Major Surgery",
                                    desc = "Major operation or tattoo/piercing within the last 6 months",
                                    checked = hasRecentSurgery,
                                    onCheckedChange = { hasRecentSurgery = it }
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { step = 2 },
                                        modifier = Modifier.weight(1f).height(48.dp)
                                    ) {
                                        Text("Back")
                                    }
                                    Button(
                                        onClick = { step = 4 },
                                        modifier = Modifier.weight(1.5f).height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BloodRed40)
                                    ) {
                                        Text("Next: Privacy Consent", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "Step 4: Emergency Consent & Terms",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = BloodRed20
                                )

                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = consentGiven,
                                        onCheckedChange = { consentGiven = it }
                                    )
                                    Column {
                                        Text("Emergency Alert Consent", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            "I consent to receive high-urgency notifications for patients in need of my blood group in my geographical zone.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = termsAccepted,
                                        onCheckedChange = { termsAccepted = it }
                                    )
                                    Column {
                                        Text("Privacy Matrix Acknowledgment", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            "I understand my phone number and address are masked (\"98765XXXXX\") until I confirm acceptance of a specific hospital emergency broadcast.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Status Summary
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (overallEligible) StatusEligible.copy(alpha = 0.12f) else StatusCritical.copy(alpha = 0.12f))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (overallEligible) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (overallEligible) StatusEligible else StatusCritical,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (overallEligible) "Ready as Active Emergency Donor" else "Registration Status: Ineligible / Cooldown",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (overallEligible) StatusEligible else StatusCritical
                                            )
                                            Text(
                                                text = if (overallEligible)
                                                    "All criteria passed: Age $age, Weight ${weight}kg, Gap ${daysSinceDonation}d."
                                                else
                                                    "Will be registered in standby until medical restrictions expire.",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { step = 3 },
                                        modifier = Modifier.weight(1f).height(48.dp)
                                    ) {
                                        Text("Back")
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.registerDonor(
                                                fullName = fullName,
                                                email = email,
                                                phone = phone,
                                                bloodGroup = bloodGroup,
                                                city = city,
                                                address = address,
                                                age = age,
                                                weightKg = weight,
                                                daysSinceLastDonation = daysSinceDonation,
                                                chronicIllness = hasChronicIllness,
                                                medication = takesMedication,
                                                recentSurgery = hasRecentSurgery,
                                                consentGiven = consentGiven
                                            )
                                            submittedSuccessfully = true
                                        },
                                        enabled = consentGiven && termsAccepted,
                                        modifier = Modifier.weight(1.5f).height(48.dp).testTag("complete_registration_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BloodRed40)
                                    ) {
                                        Text("Complete Registration", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepProgressBar(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..totalSteps) {
            val isActive = i <= currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isActive) BloodRed40 else MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

@Composable
fun MedicalChecklistRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SuccessSubmissionView(
    donorName: String,
    bloodGroup: String,
    isEligible: Boolean,
    onDone: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(BloodRed90),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = BloodRed40,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = "Donor Profile Registered!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Welcome, $donorName. Your blood group $bloodGroup has been recorded. ${
                    if (isEligible) "You are listed as an active emergency donor ready for matching."
                    else "You are recorded in the medical cooldown pool until eligible."
                }",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BloodRed40)
            ) {
                Text("Return to Hub", fontWeight = FontWeight.Bold)
            }
        }
    }
}
