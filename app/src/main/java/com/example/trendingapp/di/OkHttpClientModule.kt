package com.example.trendingapp.di

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.example.trendingapp.BuildConfig
import com.example.trendingapp.network.utils.APIConstants
import com.example.trendingapp.utils.ConfigUtil
import com.example.trendingapp.utils.DeviceUtil
import com.example.trendingapp.utils.SharedPreferenceUtil
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.net.SocketTimeoutException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object OkHttpClientModule {
    private const val TAG = "OkHttpClientModule"

    @Provides
    @HeaderHttpLoggingInterceptor
    fun headerHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)
    }

    @Provides
    @BodyHttpLoggingInterceptor
    fun bodyHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun getOkHttpClientBuilder(
        @ApplicationContext context: Context?,
        @HeaderHttpLoggingInterceptor headerHttpLoggingInterceptor: HttpLoggingInterceptor?,
        @BodyHttpLoggingInterceptor bodyHttpLoggingInterceptor: HttpLoggingInterceptor?,
        @ApiRetrialInterceptor apiRetrialInterceptor: Interceptor?,
        @DefaultInterceptor defaultInterceptor: Interceptor?,
    ): OkHttpClient {
        val okHttpClientBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            OkHttpClient().newBuilder()
                .connectTimeout(ConfigUtil.CONNECTION_TIMEOUT_IN_MINS.toLong(), TimeUnit.MINUTES)
                .readTimeout(ConfigUtil.READ_TIMEOUT_IN_MINS.toLong(), TimeUnit.MINUTES)
                .writeTimeout(ConfigUtil.WRITE_TIMEOUT_IN_MINS.toLong(), TimeUnit.MINUTES)
                .retryOnConnectionFailure(false)
                .addNetworkInterceptor(StethoInterceptor())
                .addInterceptor(defaultInterceptor!!)
        } else {
            TODO("VERSION.SDK_INT < GINGERBREAD")
        }
        val domainUrl: String =
            SharedPreferenceUtil.getStringSharedPreference(context, SharedPreferenceUtil.DOMAIN_URL)
        val disableSSLPinnig: Boolean = SharedPreferenceUtil.getBooleanSharedPreference(
            context,
            SharedPreferenceUtil.DISABLE_SSL_PINNIG
        )
        if (domainUrl != null && domainUrl.startsWith("https://")) {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    //nothing
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    //nothing
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext
                .socketFactory
            if (sslSocketFactory != null) {
                okHttpClientBuilder.sslSocketFactory(
                    sslSocketFactory!!,
                    trustAllCerts[0] as X509TrustManager
                )
            }
        }
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder
                .addInterceptor(headerHttpLoggingInterceptor as Interceptor)
                .addInterceptor(bodyHttpLoggingInterceptor as Interceptor)
        }
        return okHttpClientBuilder.build()
    }

    /**
     * Provides the logic for API retrial if the Request failed due to some reasons (Network error/Server error)
     * API Retry happens for the mentioned number of times specified in ConfigUtil.java
     *
     * @return mentioned Interceptor through DI.
     */
    @Provides
    @ApiRetrialInterceptor
    fun provideApiRetrialInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            var response: Response? = null
            var shouldRetry = false
            var retryCount = 0
            do {
                Log.d(TAG, request.url.toString() + " Requesting..")
                try {
                    response = chain.proceed(request)
                    Log.d(TAG, request.url.toString() + " SUCCESSFUL at Retry: " + retryCount)
                } catch (e: IOException) {
                    if (e is SocketTimeoutException) {
                        Log.d(TAG, request.url.toString() + " Timeout at Retry: " + retryCount)
                        shouldRetry = true
                    }
                } finally {
                    retryCount++
                }
            } while (shouldRetry && retryCount < ConfigUtil.API_RETRY_THRESHOLD)
            response!!
        }
    }

    @Provides
    @DefaultInterceptor
    fun provideDefaultInterceptor(@ApplicationContext context: Context?): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder().method(original.method, original.body)
            val authToken: String = SharedPreferenceUtil.getStringSharedPreference(
                context,
                SharedPreferenceUtil.AUTHORIZATION_TOKEN
            )
            if (!TextUtils.isEmpty(authToken)) {
                builder.addHeader(APIConstants.AUTHORIZATION, authToken)
            }
            val request: Request = builder.build()
            if (DeviceUtil.hasInternetConnection(context)) {
                chain.proceed(request)
            } else {
                throw IOException("No internet connectivity")
            }
        }
    }
}