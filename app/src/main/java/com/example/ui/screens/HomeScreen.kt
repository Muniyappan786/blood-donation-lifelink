package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.EmergencyRequest
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.theme.BloodRed10
import com.example.ui.theme.BloodRed20
import com.example.ui.theme.BloodRed40
import com.example.ui.theme.BloodRed80
import com.example.ui.theme.BloodRed90
import com.example.ui.theme.StatusCritical
import com.example.viewmodel.BloodViewModel

@Composable
fun HomeScreen(
    viewModel: BloodViewModel,
    onNavigateToSos: () -> Unit,
    onNavigateToDonors: () -> Unit,
    onNavigateToBanks: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToApiEngine: () -> Unit,
    onNavigateToEducation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val banks by viewModel.bloodBanks.collectAsState()
    val stocks by viewModel.stocks.collectAsState()
    val donors by viewModel.donors.collectAsState()
    val openRequests by viewModel.openRequests.collectAsState()

    val totalUnits = stocks.sumOf { it.unitsAvailable }
    val eligibleDonorsCount = donors.count { it.isEligible() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card with Generated Graphic
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card")
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_blood_donation_1791315775152),
                        contentDescription = "Blood Donation Hero",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xCC7F0000),
                                        Color(0xF04A0000)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF5252))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE EMERGENCY NETWORK",
                                color = Color(0xFFFF8A80),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Every Drop Counts in Seconds",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Find compatible donors & reserve hospital blood units instantly.",
                            color = Color(0xFFE0E0E0),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Emergency Disclaimer Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StatusCritical.copy(alpha = 0.10f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StatusCritical.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .testTag("emergency_disclaimer_banner")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StatusCritical.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = StatusCritical,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Strict Medical 90-Day Protocol",
                            fontWeight = FontWeight.Bold,
                            color = StatusCritical,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Donors with donations in last 90 days are locked in cooldown. Phone numbers masked until emergency match consent.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Primary Action Grid (SOS Broadcast + Register + Mode A API Inspector)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Urgent SOS Button
                Button(
                    onClick = onNavigateToSos,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("sos_broadcast_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BloodRed40,
                        contentColor = Color.White
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddAlert,
                            contentDescription = "SOS",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Post SOS Request",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Register Donor Button
                OutlinedButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("donor_intake_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BloodRed40
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = "Intake",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Donor Screening",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Live Telemetry Metrics
        item {
            Text(
                text = "Live Availability Telemetry",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatMetricCard(
                    title = "Available Units",
                    value = "$totalUnits",
                    sub = "Across 5 Banks",
                    icon = Icons.Default.Bloodtype,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Eligible Donors",
                    value = "$eligibleDonorsCount",
                    sub = "Ready & Screened",
                    icon = Icons.Default.PersonAdd,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Active SOS",
                    value = "${openRequests.size}",
                    sub = "Emergency Broadcast",
                    icon = Icons.Default.Warning,
                    iconTint = StatusCritical,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Critical SOS Alerts Header & Horizontal Cards
        if (openRequests.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(StatusCritical)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Active Emergency Broadcasts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "View All (${openRequests.size})",
                        color = BloodRed40,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onNavigateToSos() }
                            .padding(4.dp)
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(openRequests) { req ->
                        HomeSosBroadcastCard(
                            request = req,
                            onRespond = { viewModel.acceptAlert(req.id, req.patientName) },
                            onFulfill = { viewModel.fulfillRequest(req.id, req.patientName) }
                        )
                    }
                }
            }
        }

        // Quick Navigation Tiles (Blood Banks, Donors, API Engine, Education)
        item {
            Text(
                text = "System Operational Hub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HubNavCard(
                    title = "Blood Bank Live Stock Catalogs",
                    description = "Salem, Chennai, Coimbatore, Bengaluru hospitals & units per group",
                    icon = Icons.Default.LocalHospital,
                    badgeText = "${banks.size} Centers",
                    onClick = onNavigateToBanks
                )
                HubNavCard(
                    title = "Emergency Donors Directory",
                    description = "90-day cooldown screening, Haversine proximity, privacy phone masking",
                    icon = Icons.Default.VolunteerActivism,
                    badgeText = "${donors.size} Profiles",
                    onClick = onNavigateToDonors
                )
                HubNavCard(
                    title = "Mode A: Backend API Structured Data",
                    description = "Inspect real-time parsable JSON schema outputs & query parameters",
                    icon = Icons.Default.Code,
                    badgeText = "Raw JSON API",
                    onClick = onNavigateToApiEngine
                )
                HubNavCard(
                    title = "Medical Compatibility & Myth Busters",
                    description = "Universal donor matrix, fluid replenishment facts, donation gap criteria",
                    icon = Icons.Default.Shield,
                    badgeText = "Rules & Facts",
                    onClick = onNavigateToEducation
                )
            }
        }
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconTint: Color = BloodRed40
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = sub,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun HomeSosBroadcastCard(
    request: EmergencyRequest,
    onRespond: () -> Unit,
    onFulfill: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .border(1.dp, BloodRed80, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BloodGroupBadge(bloodGroup = request.bloodGroup, isLarge = true)
                UrgencyBadge(urgency = request.urgencyLevel)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = request.patientName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${request.unitsNeeded} Units needed • ${request.component}",
                fontSize = 12.sp,
                color = BloodRed40,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${request.hospital}, ${request.city}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRespond,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BloodRed40)
                ) {
                    Text("Accept SOS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HubNavCard(
    title: String,
    description: String,
    icon: ImageVector,
    badgeText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BloodRed90),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BloodRed20,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = badgeText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BloodRed40,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}
