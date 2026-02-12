package app.jardinageons.presentation.features.seed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.jardinageons.data.models.Seed
import app.jardinageons.data.stub.SeedStub

var seeds = SeedStub.seeds
@Composable
fun SeedInventory(){

    LazyColumn(Modifier.padding(10.dp)) {
        items(items = seeds){
            Box{
                Text(text = it.name)

            }
        }
    }
}