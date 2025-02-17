/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.media3.demo.shortform.viewpager

import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.demo.shortform.PlayerPool
import androidx.media3.demo.shortform.R
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView

@OptIn(UnstableApi::class)
class ViewPagerMediaHolder(itemView: View, private val playerPool: PlayerPool) :
  RecyclerView.ViewHolder(itemView), View.OnAttachStateChangeListener {
  private val playerView: PlayerView = itemView.findViewById(R.id.player_view)
  private var exoPlayer: ExoPlayer? = null
  private var isInView: Boolean = false
  private var pendingPlayRequestUponSetupPlayer: Boolean = false

  private lateinit var mediaSource: MediaSource

  companion object {
    private const val TAG = "TEST"
  }

  init {
    // Define click listener for the ViewHolder's View
    playerView.findViewById<PlayerView>(R.id.player_view).setOnClickListener {
      if (it is PlayerView) {
        it.player?.run { playWhenReady = !playWhenReady }
      }
    }
  }

  private val player: ExoPlayer?
    get() {
      return exoPlayer
    }

  override fun onViewAttachedToWindow(view: View) {
    Log.d(TAG, "onViewAttachedToWindow: $bindingAdapterPosition")
    isInView = true
    if (player == null) {
      playerPool.acquirePlayer(bindingAdapterPosition, ::setupPlayer)
    }
  }

  override fun onViewDetachedFromWindow(view: View) {
    Log.d(TAG, "onViewDetachedFromWindow: $bindingAdapterPosition")
    isInView = false
    releasePlayer(exoPlayer)
  }

  fun bindData(mediaSource: MediaSource) {
    this.mediaSource = mediaSource
  }

  fun playIfPossible() {
    player?.let { playerPool.play(it) }
    if (player == null) {
      Log.d(TAG, "playIfPossible: The player hasn't been setup yet")
      pendingPlayRequestUponSetupPlayer = true
    }
  }

  private fun releasePlayer(player: ExoPlayer?) {
    playerPool.releasePlayer(bindingAdapterPosition, player ?: exoPlayer)
    this.exoPlayer = null
    playerView.player = null
  }

  private fun setupPlayer(player: ExoPlayer) {
    Log.d(TAG, "setupPlayer isInView: $isInView")
    if (!isInView) {
      releasePlayer(player)
    } else {
      if (player != exoPlayer) {
        releasePlayer(exoPlayer)
      }

      player.addListener(

        object : Player.Listener {
          override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
              ExoPlayer.STATE_IDLE -> "1ExoPlayer.STATE_IDLE      -"
              ExoPlayer.STATE_BUFFERING -> "1ExoPlayer.STATE_BUFFERING -"
              ExoPlayer.STATE_READY -> "1ExoPlayer.STATE_READY     -"
              ExoPlayer.STATE_ENDED -> "1ExoPlayer.STATE_ENDED     -"
              else -> "1UNKNOWN_STATE             -"
            }
            Log.d("TEST", "1changed state to $stateString current: ${player.contentPosition}")
          }
        }

      )

      player.run {
        repeatMode = ExoPlayer.REPEAT_MODE_ALL
        setMediaSource(mediaSource)

//        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
//        val hlsMediaSource =
//          HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri("http://210.109.108.113:18080/AD-slate-10s_11.mp4/master.m3u8"))
//
//
//
//        val mediaItem = MediaItem.Builder()
//          .setUri("http://210.109.108.113:18080/AD-slate-10s_11.mp4/master.m3u8")
//          .setMimeType(MimeTypes.APPLICATION_M3U8) //m3u8 is the extension used with HLS sources
//          .build()
//
//        player.setMediaItem(mediaItem)

//        player.setMediaSource(hlsMediaSource)
//        seekTo(currentPosition)

//        Log.d("TEST", "currentPosition: ${currentPosition} / mediaSource: $mediaSource")

        this@ViewPagerMediaHolder.exoPlayer = player
        player.prepare()
        player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        playerView.player = player


        player.videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT

        if (pendingPlayRequestUponSetupPlayer) {
          playerPool.play(player)
          pendingPlayRequestUponSetupPlayer = false
        }
      }




    }
  }
}
