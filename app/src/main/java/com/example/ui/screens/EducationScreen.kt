package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodGroup
import com.example.ui.components.BloodGroupBadge
import com.example.ui.theme.BloodRed10
import com.example.ui.theme.BloodRed20
import com.example.ui.theme.BloodRed40
import com.example.ui.theme.BloodRed90
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusEligible

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EducationScreen(
    modifier: Modifier = Modifier
) {
    var selectedGroupForMatrix by remember { mutableStateOf(BloodGroup.O_NEG) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("education_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Medical Guidelines & Education",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Scientific donor criteria, the strict 90-day cooldown rationale, and myth busters.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Eligibility Rules Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = BloodRed40, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Core Eligibility Rules", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BloodRed20)
                    }

                    RuleItem(
                        title = "Minimum 90 Days Gap",
                        desc = "The body requires 90 days (12 weeks) between successive whole blood donations to replenish iron stores and red blood cell count fully."
                    )
                    RuleItem(
                        title = "Age Limit: 18 - 65 Years",
                        desc = "Healthy donors between 18 and 65 years old are eligible. First-time donors are accepted up to age 60."
                    )
                    RuleItem(
                        title = "Body Weight: Minimum 50.0 kg",
                        desc = "Standard blood collection volume is 350ml or 450ml. Donors under 50 kg risk hypovolemia and fainting."
                    )
                    RuleItem(
                        title = "Hemoglobin Level >= 12.5 g/dL",
                        desc = "Ensures the donor will not become anemic post-donation. Verified prior to venipuncture."
                    )
                }
            }
        }

        // Interactive Compatibility Matrix
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bloodtype, contentDescription = null, tint = BloodRed40, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Interactive Compatibility Matrix", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BloodRed20)
                    }

                    Text(
                        text = "Tap a blood group to see whom it can safely donate to and receive from:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Grid of 8 Blood Groups
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BloodGroup.entries.forEach { bg ->
                            val isSelected = selectedGroupForMatrix == bg
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BloodRed40 else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { selectedGroupForMatrix = bg }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = bg.label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Donate To list
                    val canDonateTo = BloodGroup.entries.filter { selectedGroupForMatrix.canDonateTo(it) }
                    val canReceiveFrom = BloodGroup.entries.filter { selectedGroupForMatrix.canReceiveFrom(it) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BloodRed90)
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "${selectedGroupForMatrix.label} CAN DONATE TO:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BloodRed20
                            )
                            Text(
                                text = canDonateTo.joinToString(", ") { it.label } + if (selectedGroupForMatrix == BloodGroup.O_NEG) " (Universal Red Cell Donor!)" else "",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = BloodRed10
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "${selectedGroupForMatrix.label} CAN RECEIVE FROM:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = canReceiveFrom.joinToString(", ") { it.label } + if (selectedGroupForMatrix == BloodGroup.AB_POS) " (Universal Recipient!)" else "",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Myth Busters
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Blood Donation Myth Busters", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    }

                    MythBusterItem(
                        myth = "Myth: Donating blood makes you physically weak or reduces immunity.",
                        fact = "Fact: The body replenishes lost fluid volume within 24-48 hours. Red blood cells are regenerated in 4 to 6 weeks with zero reduction in immune strength."
                    )
                    MythBusterItem(
                        myth = "Myth: It takes too long and causes severe pain.",
                        fact = "Fact: The actual blood collection takes only 8-10 minutes. The slight pinch is temporary and painless thereafter."
                    )
                    MythBusterItem(
                        myth = "Myth: People with high blood pressure can never donate.",
                        fact = "Fact: As long as your systolic pressure is under 180 and diastolic under 100 on the day of donation, you can donate safely."
                    )
                }
            }
        }
    }
}

@Composable
fun RuleItem(title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StatusEligible, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun MythBusterItem(myth: String, fact: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = myth, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StatusCritical)
        Text(text = fact, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
