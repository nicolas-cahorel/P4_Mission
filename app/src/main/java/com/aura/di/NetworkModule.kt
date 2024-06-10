import com.aura.data.network.LoginClient
import org.koin.dsl.module
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideLoginClient(get()) }
}

private fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
}

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(getBaseUrl()) // Utilisation de la fonction getBaseUrl pour déterminer l'URL en fonction du contexte d'exécution
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}

private fun provideLoginClient(retrofit: Retrofit): LoginClient {
    return retrofit.create(LoginClient::class.java)
}

private fun getBaseUrl(): String {
    // Vérifie si l'application s'exécute sur un émulateur ou un appareil physique
    val isEmulator = android.os.Build.FINGERPRINT.contains("generic")
    return if (isEmulator) {
        "http://10.0.2.2:8080" // URL pour l'exécution sur un émulateur
    } else {
        // Utilisez l'adresse IP de votre ordinateur lorsque vous exécutez l'application sur un appareil physique connecté au même réseau
        // Remplacez "192.168.X.X" par l'adresse IP de votre ordinateur
        "http://192.168.86.36:8080" // URL pour l'exécution sur un appareil physique
    }
}
