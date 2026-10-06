package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodComponent
import com.example.data.model.BloodGroup
import com.example.data.model.EmergencyRequest
import com.example.data.model.UrgencyLevel
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.theme.BloodRed20
import com.example.ui.theme.BloodRed40
import com.example.ui.theme.BloodRed90
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusEligible
import com.example.viewmodel.BloodViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosBroadcastsScreen(
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allRequests by viewModel.allRequests.collectAsState()
    val revealedRequestIds by viewModel.revealedRequestIds.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showCreateSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val filteredRequests = when (selectedTabIndex) {
        0 -> allRequests.filter { it.status == "OPEN" }
        1 -> allRequests.filter { it.status == "ACCEPTED" }
        else -> allRequests.filter { it.status == "FULFILLED" }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateSheet = true },
                containerColor = BloodRed40,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_sos_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New SOS")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Post SOS", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("sos_broadcasts_screen")
        ) {
            // Header
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
                Text(
                    text = "Emergency SOS Broadcasts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "High-urgency blood requests. Accepting an alert reveals direct family/hospital contact.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BloodRed40
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Active (${allRequests.count { it.status == "OPEN" }})") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Accepted (${allRequests.count { it.status == "ACCEPTED" }})") }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Fulfilled (${allRequests.count { it.status == "FULFILLED" }})") }
                )
            }

            // Request List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (filteredRequests.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No requests in this category.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredRequests) { req ->
                        val isRevealed = revealedRequestIds.contains(req.id) || req.status != "OPEN"

                        EmergencyRequestCard(
                            request = req,
                            isContactRevealed = isRevealed,
                            onAccept = { viewModel.acceptAlert(req.id, req.patientName) },
                            onFulfill = { viewModel.fulfillRequest(req.id, req.patientName) },
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${req.contactPhone.replace(" ", "")}")
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateSheet = false },
            sheetState = sheetState
        ) {
            CreateSosSheetContent(
                onSubmit = { name, bg, comp, units, hosp, city, phone, urg, notes ->
                    viewModel.createEmergencyRequest(name, bg, comp, units, hosp, city, phone, urg, notes)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showCreateSheet = false
                    }
                },
                onCancel = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showCreateSheet = false
                    }
                }
            )
        }
    }
}

@Composable
fun EmergencyRequestCard(
    request: EmergencyRequest,
    isContactRevealed: Boolean,
    onAccept: () -> Unit,
    onFulfill: () -> Unit,
    onCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sos_card_${request.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BloodGroupBadge(bloodGroup = request.bloodGroup, isLarge = true)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = request.patientName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${request.unitsNeeded} Units • ${request.component}",
                            fontSize = 12.sp,
                            color = BloodRed40,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                UrgencyBadge(urgency = request.urgencyLevel)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${request.hospital}, ${request.city}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (request.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"${request.notes}\"",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact / Consent Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isContactRevealed) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isContactRevealed) StatusEligible else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        val maskedPhone = if (request.contactPhone.length >= 8) {
                            request.contactPhone.substring(0, request.contactPhone.length - 5) + "XXXXX"
                        } else "93812XXXXX"

                        Text(
                            text = if (isContactRevealed) request.contactPhone else maskedPhone,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isContactRevealed) "Contact Unmasked (SOS Confirmed)" else "Accept Alert to Reveal Contact",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isContactRevealed) {
                    Button(
                        onClick = onCall,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BloodRed40),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Action Buttons
            if (request.status == "OPEN") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BloodRed40)
                    ) {
                        Text("Accept Alert & Unmask", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onFulfill,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Fulfilled", fontSize = 12.sp)
                    }
                }
            } else if (request.status == "ACCEPTED") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StatusEligible, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Accepted by donor. Awaiting donation.", fontSize = 11.sp, color = StatusEligible, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = onFulfill,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Mark Done", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSosSheetContent(
    onSubmit: (name: String, bg: String, comp: String, units: Int, hosp: String, city: String, phone: String, urg: String, notes: String) -> Unit,
    onCancel: () -> Unit
) {
    var patientName by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var component by remember { mutableStateOf("Whole Blood") }
    var unitsNeeded by remember { mutableStateOf("2") }
    var hospital by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Chennai") }
    var contactPhone by remember { mutableStateOf("+91 ") }
    var urgencyLevel by remember { mutableStateOf("Critical") }
    var notes by remember { mutableStateOf("") }

    var bgExpanded by remember { mutableStateOf(false) }
    var compExpanded by remember { mutableStateOf(false) }
    var urgExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            .testTag("create_sos_sheet"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Post Emergency SOS Broadcast",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BloodRed20
            )
            IconButton(onClick = onCancel) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        OutlinedTextField(
            value = patientName,
            onValueChange = { patientName = it },
            label = { Text("Patient Full Name *") },
            modifier = Modifier.fillMaxWidth().testTag("patient_name_input"),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Blood Group selector
            ExposedDropdownMenuBox(
                expanded = bgExpanded,
                onExpandedChange = { bgExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = bloodGroup,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Blood Group") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bgExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = bgExpanded,
                    onDismissRequest = { bgExpanded = false }
                ) {
                    BloodGroup.entries.forEach { bg ->
                        DropdownMenuItem(
                            text = { Text(bg.label) },
                            onClick = {
                                bloodGroup = bg.label
                                bgExpanded = false
                            }
                        )
                    }
                }
            }

            // Units Needed
            OutlinedTextField(
                value = unitsNeeded,
                onValueChange = { unitsNeeded = it.filter { ch -> ch.isDigit() } },
                label = { Text("Units Needed") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        // Component & Urgency
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = compExpanded,
                onExpandedChange = { compExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = component,
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
                                component = c
                                compExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = urgExpanded,
                onExpandedChange = { urgExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = urgencyLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Urgency") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = urgExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = urgExpanded,
                    onDismissRequest = { urgExpanded = false }
                ) {
                    listOf("Critical", "Urgent", "Normal").forEach { u ->
                        DropdownMenuItem(
                            text = { Text(u) },
                            onClick = {
                                urgencyLevel = u
                                urgExpanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = hospital,
            onValueChange = { hospital = it },
            label = { Text("Hospital Name & Department *") },
            modifier = Modifier.fillMaxWidth().testTag("hospital_input"),
            singleLine = true
        )

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
                value = contactPhone,
                onValueChange = { contactPhone = it },
                label = { Text("Emergency Contact Phone *") },
                modifier = Modifier.weight(1.5f),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Clinical Notes / Instructions") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = {
                val units = unitsNeeded.toIntOrNull() ?: 2
                onSubmit(patientName, bloodGroup, component, units, hospital, city, contactPhone, urgencyLevel, notes)
            },
            enabled = patientName.isNotBlank() && hospital.isNotBlank() && contactPhone.length >= 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_sos_button"),
            colors = ButtonDefaults.buttonColors(containerColor = BloodRed40),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Broadcast SOS Immediately", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
