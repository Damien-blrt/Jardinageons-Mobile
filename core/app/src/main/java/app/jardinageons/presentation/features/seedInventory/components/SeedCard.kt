package app.jardinageons.presentation.features.seedInventory.components

import androidx.compose.foundation.layout.fillMaxSize
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.jardinageons.R
import app.jardinageons.data.models.Seed

@Composable
fun SeedCard(
    seed: Seed,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(shape = RoundedCornerShape(20.dp), color = Color.LightGray, width = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp), color = color)
                .padding(20.dp),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.width(256.dp)
            ) {
                AsyncImage(
                    model = R.drawable.seed,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = seed.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .border(
                        width = 4.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
                    .width(82.dp)
                    .height(82.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = seed.quantity.toString(),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Graines".uppercase(),
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.DarkGray
                )
                Text(seed.description)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            )
            {
                Info(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.Blue
                        )
                    },
                    title = "Germination",
                    value = "${seed.germinationTime} jours",
                    backgroundColor = Color(0xFFF0F4FF)
                )

                Info(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF46A24A)
                        )
                    },
                    title = "Expiration",
                    value = seed.expiryDate,
                    backgroundColor = Color(0xFFF0FFF0)
                )
            }

        }
    }
}
