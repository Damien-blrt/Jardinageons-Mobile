package app.jardinageons.data.services

import app.jardinageons.data.interceptors.AuthInterceptor
import app.jardinageons.data.interceptors.TokenAuthenticator
import app.jardinageons.data.storage.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL =
        "https://codefirst.iut.uca.fr/kubernetes/iut-inf63-projets-etudiants-jardinageons/jardinageons/"
    private const val BASE_URL_WEATHER = "https://api.openweathermap.org/"

    val tokenProvider: () -> String? = {
        TokenManager.accessToken
    }

    private fun buildRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun buildAuthorizedClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .authenticator(TokenAuthenticator())
            .build()
    }

    private fun buildPublicClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    val seedService: ISeedService by lazy {
        val retrofit = buildRetrofit(BASE_URL, buildAuthorizedClient())
        retrofit.create(ISeedService::class.java)
    }

    val vegetableService: IVegetableService by lazy {
        val retrofit = buildRetrofit(BASE_URL, buildAuthorizedClient())
        retrofit.create(IVegetableService::class.java)
    }

    val loginQService: ILoginQService by lazy {
        val retrofit = buildRetrofit(BASE_URL, buildPublicClient())
        retrofit.create(ILoginQService::class.java)
    }

    val harvestService: HarvestService by lazy {
        val retrofit = buildRetrofit(BASE_URL, buildAuthorizedClient())
        retrofit.create(HarvestService::class.java)
    }

    val growService: IGrowService by lazy {
        val retrofit = buildRetrofit(BASE_URL, buildAuthorizedClient())
        retrofit.create(IGrowService::class.java)
    }

    val gardenService: IGardenService by lazy {
        val retrofit = buildRetrofit(BASE_URL, buildAuthorizedClient())
        retrofit.create(IGardenService::class.java)
    }

    val weatherService: IWeatherService by lazy {
        val retrofit = buildRetrofit(BASE_URL_WEATHER, buildPublicClient())
        retrofit.create(IWeatherService::class.java)
    }

    val adviceService: IAdviceService by lazy {
        val retrofit = buildRetrofit(BASE_URL, buildAuthorizedClient())
        retrofit.create(IAdviceService::class.java)
    }
}
