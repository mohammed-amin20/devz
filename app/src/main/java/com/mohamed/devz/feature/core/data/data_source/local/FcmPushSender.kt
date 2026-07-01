package com.mohamed.devz.feature.core.data.data_source.local

import android.content.Context
import com.mohamed.devz.R
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmPushSender @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val httpClient = HttpClient(Android)

    private var cachedAccessToken: Pair<String, Long>? = null

    private data class ServiceAccountConfig(
        val projectId: String,
        val privateKeyPem: String,
        val clientEmail: String,
        val tokenUri: String,
    )

    private val config: ServiceAccountConfig by lazy {
        val text = context.resources.openRawResource(R.raw.fcm_service_account)
            .bufferedReader().use { it.readText() }
        val obj = json.decodeFromString<JsonObject>(text)
        ServiceAccountConfig(
            projectId = obj["project_id"]!!.jsonPrimitive.content,
            privateKeyPem = obj["private_key"]!!.jsonPrimitive.content,
            clientEmail = obj["client_email"]!!.jsonPrimitive.content,
            tokenUri = obj["token_uri"]!!.jsonPrimitive.content,
        )
    }

    private val privateKey: PrivateKey by lazy {
        val pem = config.privateKeyPem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")
        val der = Base64.getDecoder().decode(pem)
        val keySpec = PKCS8EncodedKeySpec(der)
        KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }

    suspend fun sendPush(
        fcmToken: String,
        title: String,
        body: String,
        questionId: Int?,
        type: String,
        actorId: Int? = null,
    ) = withContext(Dispatchers.IO) {
        try {
            val accessToken = getAccessToken()
            val projectId = config.projectId

            val messageBody = buildJsonObject {
                putJsonObject("message") {
                    put("token", fcmToken)
                    putJsonObject("notification") {
                        put("title", title)
                        put("body", body)
                    }
                    putJsonObject("data") {
                        put("type", type)
                        if (type == "follower" && actorId != null) {
                            put("actorId", actorId.toString())
                        } else {
                            put("questionId", questionId?.toString() ?: "")
                        }
                    }
                }
            }

            httpClient.post("https://fcm.googleapis.com/v1/projects/$projectId/messages:send") {
                header("Authorization", "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(messageBody.toString())
            }
        } catch (_: Exception) {
        }
    }

    private suspend fun getAccessToken(): String {
        val now = System.currentTimeMillis() / 1000
        cachedAccessToken?.let { (token, expiresAt) ->
            if (now < expiresAt - 60) return token
        }

        val jwt = createJwt()
        val response = httpClient.post(config.tokenUri) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$jwt")
        }
        val result = json.decodeFromString<JsonObject>(response.bodyAsText())
        val accessToken = result["access_token"]!!.jsonPrimitive.content
        val expiresIn = result["expires_in"]!!.jsonPrimitive.content.toLong()
        cachedAccessToken = Pair(accessToken, now + expiresIn)
        return accessToken
    }

    private fun createJwt(): String {
        val now = System.currentTimeMillis() / 1000
        val header = buildJsonObject {
            put("alg", "RS256")
            put("typ", "JWT")
        }
        val payload = buildJsonObject {
            put("iss", config.clientEmail)
            put("scope", "https://www.googleapis.com/auth/firebase.messaging")
            put("aud", config.tokenUri)
            put("exp", now + 3600)
            put("iat", now)
        }

        val headerB64 = base64UrlEncode(header.toString().toByteArray())
        val payloadB64 = base64UrlEncode(payload.toString().toByteArray())
        val signingInput = "$headerB64.$payloadB64"

        val signature = Signature.getInstance("SHA256withRSA").apply {
            initSign(privateKey)
            update(signingInput.toByteArray())
        }.sign()
        val signatureB64 = base64UrlEncode(signature)

        return "$signingInput.$signatureB64"
    }

    private fun base64UrlEncode(bytes: ByteArray): String {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
