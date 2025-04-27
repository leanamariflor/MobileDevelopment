package com.anime.aniwatch.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ui.StyledPlayerView

class FullscreenManager(
    private val activity: Activity,
    private val playerView: StyledPlayerView
) {
    private var isFullscreen = false

    fun enterFullscreen() {
        isFullscreen = true
        
        activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        playerView.subtitleView?.apply {
            setPadding(0, 0, 0, 150)
        }
    }

    fun exitFullscreen() {
        isFullscreen = false
        
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        playerView.subtitleView?.apply {
            setPadding(0, 0, 0, 50)
        }
    }

    fun handleConfigurationChange(newConfig: Configuration) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    fun handleBackPressed(): Boolean {
        return if (isFullscreen) {
            exitFullscreen()
            true
        } else {
            false
        }
    }

    fun isFullscreen(): Boolean = isFullscreen

    fun toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen()
        } else {
            enterFullscreen()
        }
    }
}