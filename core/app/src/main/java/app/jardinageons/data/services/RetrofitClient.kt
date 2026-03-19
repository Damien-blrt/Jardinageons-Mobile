    package app.jardinageons.data.services

    import app.jardinageons.BuildConfig
    import app.jardinageons.data.interceptors.AuthInterceptor
    import okhttp3.OkHttpClient
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory


    object RetrofitClient {

        private const val BASE_URL =
            "https://codefirst.iut.uca.fr/kubernetes/iut-inf63-projets-etudiants-jardinageons/jardinageons/api/"
        val tokenProvider: () -> String? = {
            BuildConfig.API_TOKEN
        }

        private const val BASE_URL_WEATHER = "https://api.openweathermap.org/"

        /*
            """""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
                Supprimez les commentaires quand tout le monde a vu
            """""""""""""""""""""""""""""""""""""""""""""""""""""""""""
         */
        val seedService: ISeedService by lazy { // Crée une propriété seedService de type ISeedService initialisée seulement à la première utilisation

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenProvider))
                .build()

            val retrofit = Retrofit.Builder() // Crée un constructeur Retrofit
                .baseUrl(BASE_URL) // Définit l'URL de base de l API, tous les endpoints
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                // Ajoute un convertisseur JSON pour transformer les objets Kotlin en JSON et inversement
                .build() // Construit l'instance Retrofit

            retrofit.create(ISeedService::class.java)
            // Crée automatiquement une implémentation de l'interface ISeedService
            // on pourra appeler seedService.listSeeds(...)
        }

        val vegetableService: IVegetableService by lazy {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenProvider))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit.create(IVegetableService::class.java)
        }

        val weatherService: IWeatherService by lazy {
            val okHttpClient = OkHttpClient.Builder().build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_WEATHER) // On utilise l'URL d'OpenWeatherMap
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()) // Gson pour traduire le JSON
                .build()

            retrofit.create(IWeatherService::class.java)
        }

    }
