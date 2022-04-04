package com.example.getyoursale

import android.app.PendingIntent.getActivity
import android.content.Context
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.gmail.GmailScopes
import java.io.File
import java.io.InputStreamReader
import java.util.*

private const val TOKENS_DIRECTORY_PATH = "/tokens"
private const val CREDENTIALS_FILE_PATH = "credentials.json"
val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
val SCOPES: List<String> = Collections.singletonList(GmailScopes.GMAIL_LABELS)
class GmailApi {
    fun getCredentials(httpTransport: NetHttpTransport, activity: Context): Credential? {
        val inputStream = activity.assets.open(CREDENTIALS_FILE_PATH)
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))
        val flow = GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File(activity.filesDir.absolutePath + TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}