package app.jardinageons.data.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object RetrofitClient {

    private const val BASE_URL = "https://codefirst.iut.uca.fr/kubernetes/iut-inf63-projets-etudiants-jardinageons/jardinageons/api/"

    /*
        """""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
            Supprimez les commentaires quand tout le monde a vu
        """""""""""""""""""""""""""""""""""""""""""""""""""""""""""
     */
    val seedService: ISeedService by lazy { // Crée une propriété seedService de type ISeedService initialisée seulement à la première utilisation

        val retrofit = Retrofit.Builder() // Crée un constructeur Retrofit
            .baseUrl(BASE_URL) // Définit l'URL de base de l API, tous les endpoints
            .addConverterFactory(GsonConverterFactory.create())
            // Ajoute un convertisseur JSON pour transformer les objets Kotlin en JSON et inversement
            .build() // Construit l'instance Retrofit

        retrofit.create(ISeedService::class.java)
        // Crée automatiquement une implémentation de l'interface ISeedService
        // on pourra appeler seedService.listSeeds(...)
    }

    val vegetableService: IVegetableService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(IVegetableService::class.java)
    }

}
