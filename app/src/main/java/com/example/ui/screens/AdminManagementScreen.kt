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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.model.ActivityLog
import com.example.data.model.BloodBank
import com.example.data.model.BloodGroup
import com.example.data.model.Donor
import com.example.ui.components.BloodGroupBadge
import com.example.ui.theme.BloodRed20
import com.example.ui.theme.BloodRed40
import com.example.ui.theme.BloodRed90
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusEligible
import com.example.viewmodel.BloodViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManagementScreen(
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Stock Adjuster, 1: Donor Controllers, 2: Activity Log Telemetry

    val banks by viewModel.bloodBanks.collectAsState()
    val stocks by viewModel.stocks.collectAsState()
    val donors by viewModel.donors.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_screen")
    ) {
        // Header
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = BloodRed40,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Admin Governance Suite",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Atomic stock adjustments, user status controls, and immutable activity telemetry logs.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BloodRed40
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Stock Adjuster") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("User Controls") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Audit Logs") }
            )
        }

        when (selectedTab) {
            0 -> AdminStockAdjustmentTab(banks = banks, onAdjust = { bankId, bankName, group, comp, delta ->
                viewModel.adjustStock(bankId, bankName, group, comp, delta)
            })
            1 -> AdminUserControllersTab(
                donors = donors,
                onToggleStatus = { viewModel.toggleDonorStatus(it) },
                onToggleVerification = { viewModel.toggleDonorVerification(it) }
            )
            2 -> AdminActivityLogsTab(logs = activityLogs)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStockAdjustmentTab(
    banks: List<BloodBank>,
    onAdjust: (bankId: Long, bankName: String, group: String, comp: String, delta: Int) -> Unit
) {
    var selectedBank by remember { mutableStateOf(banks.firstOrNull()) }
    var selectedGroup by remember { mutableStateOf("O+") }
    var selectedComponent by remember { mutableStateOf("Whole Blood") }
    var deltaStr by remember { mutableStateOf("2") }

    var bankExpanded by remember { mutableStateOf(false) }
    var groupExpanded by remember { mutableStateOf(false) }
    var compExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                        text = "Atomic Stock Adjustment Controller",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BloodRed20
                    )
                    Text(
                        text = "Real-time thread-safe increments/decrements for hospital inventory with audit tracking.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Bank selector
                    ExposedDropdownMenuBox(
                        expanded = bankExpanded,
                        onExpandedChange = { bankExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedBank?.name ?: (banks.firstOrNull()?.name ?: "Select Blood Bank"),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Blood Bank") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bankExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = bankExpanded,
                            onDismissRequest = { bankExpanded = false }
                        ) {
                            banks.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text("${b.name} (${b.city})") },
                                    onClick = {
                                        selectedBank = b
                                        bankExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Blood group
                        ExposedDropdownMenuBox(
                            expanded = groupExpanded,
                            onExpandedChange = { groupExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedGroup,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Group") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = groupExpanded,
                                onDismissRequest = { groupExpanded = false }
                            ) {
                                BloodGroup.entries.forEach { bg ->
                                    DropdownMenuItem(
                                        text = { Text(bg.label) },
                                        onClick = {
                                            selectedGroup = bg.label
                                            groupExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Component
                        ExposedDropdownMenuBox(
                            expanded = compExpanded,
                            onExpandedChange = { compExpanded = it },
                            modifier = Modifier.weight(1.5f)
                        ) {
                            OutlinedTextField(
                                value = selectedComponent,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Component") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = compExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = compExpanded,
                                onDismissRequest = { compExpanded = false }
                            ) {
                                listOf("Whole Blood", "Packed RBC", "Platelets", "Fresh Frozen Plasma").forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c) },
                                        onClick = {
                                            selectedComponent = c
                                            compExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Delta value input
                    OutlinedTextField(
                        value = deltaStr,
                        onValueChange = { deltaStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Units to Adjust") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val b = selectedBank ?: banks.firstOrNull()
                                if (b != null) {
                                    val delta = deltaStr.toIntOrNull() ?: 1
                                    onAdjust(b.id, b.name, selectedGroup, selectedComponent, delta)
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusEligible)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Units")
                        }

                        Button(
                            onClick = {
                                val b = selectedBank ?: banks.firstOrNull()
                                if (b != null) {
                                    val delta = deltaStr.toIntOrNull() ?: 1
                                    onAdjust(b.id, b.name, selectedGroup, selectedComponent, -delta)
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BloodRed40)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Deduct Units")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUserControllersTab(
    donors: List<Donor>,
    onToggleStatus: (Donor) -> Unit,
    onToggleVerification: (Donor) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Registered Donors & Account Status",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(donors) { donor ->
            val isBlocked = donor.accountStatus == "BLOCKED"

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BloodGroupBadge(bloodGroup = donor.bloodGroup)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${donor.fullName} (${donor.donorCode})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${donor.city} • ${donor.phoneNumber} • ${donor.accountStatus}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { onToggleVerification(donor) }
                        ) {
                            Icon(
                                imageVector = if (donor.verifiedByAdmin) Icons.Default.VerifiedUser else Icons.Default.Shield,
                                contentDescription = "Verification",
                                tint = if (donor.verifiedByAdmin) StatusEligible else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { onToggleStatus(donor) }
                        ) {
                            Icon(
                                imageVector = if (isBlocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Status",
                                tint = if (isBlocked) StatusCritical else StatusEligible
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminActivityLogsTab(logs: List<ActivityLog>) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Immutable Activity Telemetry Logs",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Capturing administrative adjustments, stock variances, and SOS dispatches with IP origin tracking.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(logs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = log.actionType,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = BloodRed40
                        )
                        Text(
                            text = sdf.format(Date(log.timestampEpoch)),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = log.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Origin IP: ${log.ipAddress}",
                        fontSize = 9.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
