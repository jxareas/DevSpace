package com.flexcode.devspace.remote.quotes

import android.content.Context
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.flexcode.devspace.quotes.data.remote.QuotesApi
import com.flexcode.devspace.quotes.data.remote.dto.QuotesDto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import java.io.InputStream
import java.net.HttpURLConnection

class QuotesApiTest {

    private var context: Context? = null
    private var mockWebServer = MockWebServer()
    private lateinit var api: QuotesApi

    @Before
    fun setUp() {
        mockWebServer.start()

        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        context = InstrumentationRegistry.getInstrumentation().targetContext

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()


        val contentType = "application/json".toMediaType()
        val converterFactory = Json.asConverterFactory(contentType)

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(converterFactory)
            .client(client)
            .build()
            .create(QuotesApi::class.java)

        val jsonStream: InputStream = context!!.resources.assets.open("quotes.json")
        val jsonBytes: ByteArray = jsonStream.readBytes()

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(String(jsonBytes))
        mockWebServer.enqueue(response)
    }


    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testQuotesApi() : Unit = runBlocking {
        val data = api.getAllQuotes()
        ViewMatchers.assertThat(data,CoreMatchers.equalTo(QuotesDto))
    }
}