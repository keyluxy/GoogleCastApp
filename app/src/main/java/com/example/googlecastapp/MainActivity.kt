package com.example.googlecastapp

import androidx.mediarouter.app.MediaRouteButton
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var castContext: CastContext
    private lateinit var sessionManager: SessionManager
    private lateinit var mediaRouteButton: MediaRouteButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        castContext = CastContext.getSharedInstance(this)
        sessionManager = castContext.sessionManager

        mediaRouteButton = findViewById(R.id.media_route_button)
        CastButtonFactory.setUpMediaRouteButton(applicationContext, mediaRouteButton)

        val castButton = findViewById<Button>(R.id.cast_button)
        castButton.setOnClickListener {
            val castSession: CastSession? = sessionManager.currentCastSession
            if (castSession == null || !castSession.isConnected) {
                mediaRouteButton.performClick()
            } else {
                launchVideo(castSession)
            }
        }
    }

    private fun launchVideo(castSession: CastSession) {
        val remoteMediaClient = castSession.remoteMediaClient
        if (remoteMediaClient == null) {
            Toast.makeText(this, "Ошибка: не удалось получить RemoteMediaClient", Toast.LENGTH_SHORT).show()
            return
        }

        val metadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
        metadata.putString(MediaMetadata.KEY_TITLE, "Test Video")

        val mediaInfo = MediaInfo.Builder("https://videolink-test.mycdn.me/?pct=1&sig=6QNOvp0y3BE&ct=0&clientType=45&mid=193241622673&type=5")
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType("video/mp4")
            .setMetadata(metadata)
            .build()

        val requestData = MediaLoadRequestData.Builder()
            .setMediaInfo(mediaInfo)
            .build()

        remoteMediaClient.load(requestData)
    }
}
