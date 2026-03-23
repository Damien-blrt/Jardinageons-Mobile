package app.jardinageons.presentation.features.vegetable.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.jardinageons.R
import app.jardinageons.data.models.Vegetable

@Composable
fun Info(
    modifier: Modifier,
    icon: @Composable () -> Unit,
    title: String,
    value: String,
    backgroundColor: Color
) {
    Box(modifier = modifier
        .background(backgroundColor, RoundedCornerShape(16.dp))
        .padding(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(Modifier.width(8.dp))
                Text(title, color = Color.Gray, fontSize = 14.sp)
            }
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A1C1E))
        }
    }
}

@Composable
fun VegetableCard(vegetable: Vegetable, color: Color, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .border(5.dp, Color.LightGray, RoundedCornerShape(20.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color, RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.tomate),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                vegetable.name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Color.DarkGray)
                Spacer(Modifier.width(8.dp))
                Text(vegetable.description)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Info(
                    Modifier.weight(1f),
                    { Icon(Icons.Default.Star, null, tint = Color(0xFFD6A21A)) },
                    "Semis",
                    "${vegetable.sowingStart.take(5)} au ${vegetable.sowingEnd.take(5)}",
                    Color(0xFFFFF8E1)
                )
                Info(
                    Modifier.weight(1f),
                    { Icon(Icons.Default.DateRange, null, tint = Color(0xFF46A24A)) },
                    "Récolte",
                    "${vegetable.harvestStart.take(5)} au ${vegetable.harvestEnd.take(5)}",
                    Color(0xFFF0FFF0)
                )
            }
        }
    }
}