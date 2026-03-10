package app.jardinageons.data.models

/**
 * Contient le résumé utile de la météo pour l'interface
 */
data class WeatherSummary(
    val rainTotal24h: Double,  // Total de pluie (mm)
    val currentTemp: Double?,   // Température actuelle (°C)
    val locationName: String, // Nom de la ville
    val humidity: Int?, // Humidité
    val windSpeedKmh: Double? // Vitesse du vent (km/h)
)