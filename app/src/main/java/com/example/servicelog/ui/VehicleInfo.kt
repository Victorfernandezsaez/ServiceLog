package com.example.servicelog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servicelog.domain.DueStatus
import com.example.servicelog.domain.Vehicle
import com.example.servicelog.domain.icon
@Composable
fun VehicleInfo(vehicle: Vehicle, dueStatuses: List<DueStatus>) {

    var expanded by remember { mutableStateOf(false) }
    val visible = if (expanded) dueStatuses else dueStatuses.take(4)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFEBE0FF),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = vehicle.type.icon,
                        contentDescription = vehicle.type.label,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = listOf(vehicle.alias, vehicle.brand, vehicle.model)
                            .filter { it.isNotBlank() }
                            .joinToString(" "),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${numberFormat.format(vehicle.currentKm)} km",
                    fontSize = 14.sp
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Text("Next service", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                if (dueStatuses.isEmpty()) {
                    Text("No intervals defined yet", fontSize = 13.sp)
                } else {
                    visible.forEach { status ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .size(8.dp)
                                    .background(status.urgency.color(), CircleShape)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(status.interval.title, fontSize = 14.sp)
                                Text(
                                    status.nextText(),
                                    fontSize = 12.sp,
                                    color = status.urgency.color()
                                )
                                status.lastText()?.let {
                                    Text("Last: $it", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                    if (dueStatuses.size > 4) {
                        Text(
                            text = if (expanded) "Show less" else "+${dueStatuses.size - 4} more",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable { expanded = !expanded }
                        )
                    }
                }
            }
        }
    }
}