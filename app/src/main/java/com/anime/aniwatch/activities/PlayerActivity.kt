package com.anime.aniwatch.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anime.aniwatch.R
import com.anime.aniwatch.network.ApiService
import com.anime.aniwatch.network.EpisodeSourceResponse
import com.anime.aniwatch.network.Track
import com.anime.aniwatch.util.Constants
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.ui.SubtitleView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.anime.aniwatch.fragment.EpisodeFragment
import com.anime.aniwatch.network.AnimeResponse
import com.anime.aniwatch.network.EpisodeResponse

class PlayerActivity : AppCompatActivity() {

    private var exoPlayer: ExoPlayer? = null
    private lateinit var playerView: StyledPlayerView
    private var playbackPosition: Long = 0
    private var playWhenReady: Boolean = true

    private var isFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val animeTitleText: TextView = findViewById(R.id.animeTitleText)
        val episodeTitleText: TextView = findViewById(R.id.episodeTitleText)
        val episodeNumberText: TextView = findViewById(R.id.episodeNumberText)

        playerView = findViewById(R.id.playerView)

        // Set up fullscreen button click listener
        playerView.setControllerOnFullScreenModeChangedListener { isFullScreen ->
            this.isFullscreen = isFullScreen
            if (isFullScreen) {
                enterFullscreen()
            } else {
                exitFullscreen()
            }
        }

        // Rest of your existing onCreate code
        val episodeId = intent.getStringExtra("EPISODE_ID")
        val animeId = intent.getStringExtra("ANIME_ID")
        if (episodeId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid episode ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchEpisodeSources(episodeId)
        loadEpisodeFragment(animeId.toString())
        if (animeId != null && episodeId != null) {
            fetchEpisodeDetails(animeId, episodeId, animeTitleText, episodeTitleText, episodeNumberText)
        }
        Toast.makeText(this, "Anime ID: $animeId", Toast.LENGTH_SHORT).show()
    }

    private fun enterFullscreen() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        playerView.subtitleView?.apply {
            setPadding(0, 0, 0, 150)
        }
    }

    private fun exitFullscreen() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        playerView.subtitleView?.apply {
            setPadding(0, 0, 0, 50)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    private fun fetchEpisodeSources(episodeId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.getEpisodeSources(episodeId).enqueue(object : Callback<EpisodeSourceResponse> {
            override fun onResponse(call: Call<EpisodeSourceResponse>, response: Response<EpisodeSourceResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val sourceData = response.body()?.data
                    if (sourceData != null) {
                        val hlsUrl = sourceData.sources.firstOrNull { it.type == "hls" }?.url

                        if (hlsUrl != null) {
                            val referer = sourceData.headers["Referer"] ?: "https://megacloud.blog/"
                            preparePlayer(hlsUrl, sourceData.tracks, referer)
                        } else {
                            Toast.makeText(this@PlayerActivity, "No playable sources found", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this@PlayerActivity, "Failed to load episode sources", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<EpisodeSourceResponse>, t: Throwable) {
                Toast.makeText(this@PlayerActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun preparePlayer(hlsUrl: String, subtitleTracks: List<Track>, referer: String) {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.playWhenReady = playWhenReady
        playerView.player = exoPlayer

        playerView.subtitleView?.apply {
            val captionStyle = CaptionStyleCompat(
                android.graphics.Color.WHITE,
                android.graphics.Color.parseColor("#33000000"),
                CaptionStyleCompat.EDGE_TYPE_NONE,
                CaptionStyleCompat.EDGE_TYPE_NONE,
                android.graphics.Color.WHITE,
                null
            )
            setStyle(captionStyle)
            setFractionalTextSize(SubtitleView.DEFAULT_TEXT_SIZE_FRACTION)
        }

        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf("Referer" to referer))

        val videoSource = HlsMediaSource.Factory(defaultHttpDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(hlsUrl))

        val defaultTrack = subtitleTracks.find { it.default }

        if (defaultTrack != null) {
            exoPlayer?.trackSelectionParameters = exoPlayer?.trackSelectionParameters
                ?.buildUpon()
                ?.setPreferredTextLanguage(defaultTrack.label)
                ?.build()!!
        }

        if (subtitleTracks.isNotEmpty()) {
            val mediaSources = mutableListOf<MediaSource>(videoSource)

            subtitleTracks.forEach { track ->
                val subtitleSource = SingleSampleMediaSource.Factory(defaultHttpDataSourceFactory)
                    .createMediaSource(
                        MediaItem.SubtitleConfiguration.Builder(Uri.parse(track.file))
                            .setMimeType(MimeTypes.TEXT_VTT)
                            .setLanguage(track.label)
                            .setLabel(track.label)
                            .setSelectionFlags(if (track.default) C.SELECTION_FLAG_DEFAULT else 0)
                            .build(),
                        C.TIME_UNSET
                    )
                mediaSources.add(subtitleSource)
            }

            val mergedSource = MergingMediaSource(*mediaSources.toTypedArray())
            exoPlayer?.setMediaSource(mergedSource)
        } else {
            exoPlayer?.setMediaSource(videoSource)
        }

        exoPlayer?.seekTo(playbackPosition)
        exoPlayer?.prepare()

        exoPlayer?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                if (events.contains(Player.EVENT_TRACKS_CHANGED)) {
                    val trackGroups = player.currentTracks.groups
                    val subtitleTracks = trackGroups.filter { group ->
                        group.type == C.TRACK_TYPE_TEXT
                    }
                    if (subtitleTracks.isEmpty()) {
                        println("No subtitle tracks found.")
                    } else {
                        println("${subtitleTracks.size} subtitle tracks loaded successfully.")
                    }
                }
            }
        })
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            val episodeId = it.getStringExtra("EPISODE_ID")
            val animeId = it.getStringExtra("ANIME_ID")

            if (!episodeId.isNullOrEmpty()) {
                fetchEpisodeSources(episodeId)
                if (animeId != null) {
                    fetchEpisodeDetails(animeId, episodeId, findViewById(R.id.animeTitleText), findViewById(R.id.episodeTitleText), findViewById(R.id.episodeNumberText))
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Change orientation to portrait
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            super.onBackPressed()
        }
    }

    private fun loadEpisodeFragment(animeId: String) {
        val episodeFragment = EpisodeFragment().apply {
            arguments = Bundle().apply {
                putString("ANIME_ID", animeId)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.episodeFragmentContainer, episodeFragment)
            .commit()
    }

    private fun fetchEpisodeDetails(
        animeId: String,
        episodeId: String,
        animeTitleText: TextView,
        episodeTitleText: TextView,
        episodeNumberText: TextView
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Fetch anime details
        apiService.getAnimeDetails(animeId).enqueue(object : Callback<AnimeResponse> {
            override fun onResponse(call: Call<AnimeResponse>, response: Response<AnimeResponse>) {
                if (response.isSuccessful) {
                    val animeName = response.body()?.data?.anime?.info?.name
                    animeTitleText.text = "$animeName"
                }
            }

            override fun onFailure(call: Call<AnimeResponse>, t: Throwable) {
                Toast.makeText(this@PlayerActivity, "Failed to load anime details", Toast.LENGTH_SHORT).show()
            }
        })

        apiService.getAnimeEpisodes(animeId).enqueue(object : Callback<EpisodeResponse> {
            override fun onResponse(call: Call<EpisodeResponse>, response: Response<EpisodeResponse>) {
                if (response.isSuccessful) {
                    val episodes = response.body()?.data?.episodes ?: return
                    val episode = episodes.find { it.episodeId == episodeId }

                    if (episode != null) {
                        episodeTitleText.text = "${episode.title}"
                        episodeNumberText.text = "Episode: ${episode.number}"
                    }
                }
            }

            override fun onFailure(call: Call<EpisodeResponse>, t: Throwable) {
                Toast.makeText(this@PlayerActivity, "Failed to load episode details", Toast.LENGTH_SHORT).show()
            }
        })
    }
}