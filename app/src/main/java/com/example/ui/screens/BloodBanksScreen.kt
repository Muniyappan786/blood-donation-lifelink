package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodBank
import com.example.data.model.BloodStock
import com.example.ui.components.StockStatusBadge
import com.example.ui.theme.BloodRed20
import com.example.ui.theme.BloodRed40
import com.example.ui.theme.BloodRed90
import com.example.ui.theme.StatusCritical
import com.example.util.HaversineUtil
import com.example.viewmodel.BloodViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BloodBanksScreen(
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val banks by viewModel.bloodBanks.collectAsState()
    val stocks by viewModel.stocks.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedComponent by viewModel.selectedComponent.collectAsState()
    val selectedBloodGroup by viewModel.selectedBloodGroup.collectAsState()

    val cities = listOf("All", "Salem", "Chennai", "Coimbatore", "Bengaluru")
    val components = listOf("All", "Whole Blood", "Packed RBC", "Platelets", "Fresh Frozen Plasma")
    val bloodGroups = listOf("All", "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

    val filteredBanks = banks.filter { bank ->
        selectedCity == "All" || bank.city.equals(selectedCity, ignoreCase = true)
    }.sortedBy { bank ->
        HaversineUtil.calculateDistanceKm(
            viewModel.userLatitude, viewModel.userLongitude,
            bank.latitude, bank.longitude
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("blood_banks_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Accredited Blood Banks & Stock",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Verified live inventory with Haversine distance proximity and emergency hotlines.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // City Filters
        item {
            Text(
                text = "Filter by City:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cities) { city ->
                    FilterChip(
                        selected = selectedCity == city,
                        onClick = { viewModel.setSelectedCity(city) },
                        label = { Text(city, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BloodRed40,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Blood Group Filters
        item {
            Text(
                text = "Target Blood Group:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(bloodGroups) { bg ->
                    FilterChip(
                        selected = selectedBloodGroup == bg,
                        onClick = { viewModel.setSelectedBloodGroup(bg) },
                        label = { Text(bg, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BloodRed20,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Component Filters
        item {
            Text(
                text = "Component Type:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(components) { comp ->
                    FilterChip(
                        selected = selectedComponent == comp,
                        onClick = { viewModel.setSelectedComponent(comp) },
                        label = { Text(comp, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                }
            }
        }

        // Bank Cards List
        items(filteredBanks) { bank ->
            val dist = HaversineUtil.calculateDistanceKm(
                viewModel.userLatitude, viewModel.userLongitude,
                bank.latitude, bank.longitude
            )
            val bankStocks = stocks.filter { stock ->
                stock.bloodBankId == bank.id &&
                (selectedComponent == "All" || stock.component.equals(selectedComponent, ignoreCase = true)) &&
                (selectedBloodGroup == "All" || stock.bloodGroup.equals(selectedBloodGroup, ignoreCase = true))
            }

            BloodBankCard(
                bank = bank,
                distanceKm = dist,
                stocks = bankStocks,
                onCall = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${bank.contactPhone.replace(" ", "")}")
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BloodBankCard(
    bank: BloodBank,
    distanceKm: Double,
    stocks: List<BloodStock>,
    onCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bank_card_${bank.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = BloodRed40,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = bank.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Lic: ${bank.licenseNumber} • ${bank.city}, ${bank.state}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Haversine Distance Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BloodRed90)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = HaversineUtil.formatDistance(distanceKm),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BloodRed20
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${bank.address}, ${bank.pincode}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Live Blood Stock Inventory:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Stock grid
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (s in stocks.take(8)) {
                    StockPill(stock = s)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hotline: ${bank.contactPhone}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = onCall,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BloodRed40)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Call Center", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StockPill(
    stock: BloodStock,
    modifier: Modifier = Modifier
) {
    val isCritical = stock.unitsAvailable <= 3
    val bgColor = if (isCritical) StatusCritical.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (isCritical) StatusCritical.copy(alpha = 0.5f) else Color.Transparent

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stock.bloodGroup,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isCritical) StatusCritical else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${stock.unitsAvailable}u",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                color = BloodRed40
            )
        }
    }
}
