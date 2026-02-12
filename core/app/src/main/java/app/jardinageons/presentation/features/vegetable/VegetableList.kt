package app.jardinageons.presentation.features.vegetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.jardinageons.data.services.RetrofitClient.vegetableService


var vegtableList = vegetableService.listVegetables(0,10)

@Composable
fun VegetableInventory(){

    LazyColumn(Modifier.padding(10.dp)) {
        /*items(items = vegtableList){
            Box{
                Text(text = it.name)

            }
        }*/
    }
}