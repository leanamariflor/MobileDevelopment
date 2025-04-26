package com.anime.aniwatch.player

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.anime.aniwatch.network.Track
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes

class PlayerManager(private val context: Context, private val playerView: StyledPlayerView) {
    
    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition: Long = 0
    private var playWhenReady: Boolean = true
    private var mediaSourceUrl: String? = null
    private var subtitleTracks: List<Track> = emptyList()
    private var referer: String = "https://megacloud.blog/"
    
    fun preparePlayer(hlsUrl: String, subtitleTracks: List<Track>, referer: String, savedWatchedTime: Long = 0) {
        mediaSourceUrl = hlsUrl
        this.subtitleTracks = subtitleTracks
        this.referer = referer

        if (exoPlayer != null) {
            exoPlayer?.stop()
            exoPlayer?.release()
        }
        exoPlayer = ExoPlayer.Builder(context).build()
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

        // Start playback from savedWatchedTime
        exoPlayer?.seekTo(savedWatchedTime)
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
    
    fun releasePlayer() {
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }
    
    fun resumePlayer() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
            playerView.player = exoPlayer
            exoPlayer?.playWhenReady = playWhenReady
            exoPlayer?.seekTo(playbackPosition)

            mediaSourceUrl?.let { url ->
                preparePlayer(url, subtitleTracks, referer, playbackPosition)
            }
        }
    }
    
    fun stopPlayer() {
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
    }
    
    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0
    
    fun getDuration(): Long = exoPlayer?.duration ?: 0
    
    fun isPlaying(): Boolean = exoPlayer?.isPlaying ?: false
    
    fun getExoPlayer(): ExoPlayer? = exoPlayer
}