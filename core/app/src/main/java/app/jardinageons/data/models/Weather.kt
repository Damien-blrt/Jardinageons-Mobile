package app.jardinageons.data.models
import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("name") val name: String
)


data class WeatherResponse(
    @SerializedName("list") val list: List<ForeCast>,
    @SerializedName("city") val city: City?
)

data class Wind(
    @SerializedName("speed") val speed: Double // en mètre par seconde
)

data class ForeCast(
    @SerializedName("dt_txt") val dt_txt: String,
    @SerializedName("main") val main: MainWeather,
    @SerializedName("rain") val rain: Rain?,
    @SerializedName("wind") val wind: Wind?
)

data class MainWeather(
    @SerializedName("temp") val temp: Double,
    @SerializedName("humidity") val humidity: Int
)

data class Rain(
    @SerializedName("3h") val threeHour: Double?
)

