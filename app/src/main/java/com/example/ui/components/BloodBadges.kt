package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BloodRed20
import com.example.ui.theme.BloodRed40
import com.example.ui.theme.BloodRed90
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusEligible
import com.example.ui.theme.StatusIneligible
import com.example.ui.theme.StatusNormal
import com.example.ui.theme.StatusUrgent

@Composable
fun BloodGroupBadge(
    bloodGroup: String,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false
) {
    val sizeDp = if (isLarge) 46.dp else 34.dp
    val fontSize = if (isLarge) 18.sp else 13.sp

    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(CircleShape)
            .background(BloodRed90)
            .border(1.5.dp, BloodRed40, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bloodGroup,
            color = BloodRed20,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun UrgencyBadge(
    urgency: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (urgency.uppercase()) {
        "CRITICAL" -> Triple(StatusCritical.copy(alpha = 0.15f), StatusCritical, "CRITICAL")
        "URGENT" -> Triple(StatusUrgent.copy(alpha = 0.15f), StatusUrgent, "URGENT")
        else -> Triple(StatusNormal.copy(alpha = 0.15f), StatusNormal, "NORMAL")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (urgency.equals("CRITICAL", ignoreCase = true)) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EligibilityBadge(
    isEligible: Boolean,
    statusText: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = if (isEligible) {
        Pair(StatusEligible.copy(alpha = 0.15f), StatusEligible)
    } else {
        Pair(StatusIneligible.copy(alpha = 0.15f), StatusIneligible)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusText,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun StockStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        "Critical Low" -> Pair(StatusCritical.copy(alpha = 0.15f), StatusCritical)
        "Low" -> Pair(StatusUrgent.copy(alpha = 0.15f), StatusUrgent)
        "Normal" -> Pair(StatusNormal.copy(alpha = 0.15f), StatusNormal)
        else -> Pair(Color(0xFF00695C).copy(alpha = 0.15f), Color(0xFF00695C))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
