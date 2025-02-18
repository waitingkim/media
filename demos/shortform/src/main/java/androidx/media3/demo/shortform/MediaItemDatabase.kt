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
package androidx.media3.demo.shortform

import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes

class MediaItemDatabase {
    private var prefix = "http://125.159.54.5:1554/"
    private var postfix = "?AdaptiveType=HLS"
    private val mediaUris =
        mutableListOf(
            "${prefix}M49P100AGGL1500001.mpg$postfix",
            "${prefix}M64OA0T8GGL1500001.mpg$postfix",
            "${prefix}M64OA0THGGL1500001.mpg$postfix",
            "${prefix}M64N30C9CNL1500001.mpg$postfix",
            "${prefix}M64N30CBCNL1500001.mpg$postfix",
            "${prefix}M64N30CFCNL1500001.mpg$postfix",
            "${prefix}M64N30CLCNL1500001.mpg$postfix",
            "${prefix}M64LA08TSGL1500001.mpg$postfix",
            "${prefix}M64N30JKCNL1500001.mpg$postfix",
            "${prefix}M64N30EECNL1500001.mpg$postfix"

//      "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8"
//      "https://storage.googleapis.com/exoplayer-test-media-0/shortform_1.mp4",
//      "https://storage.googleapis.com/exoplayer-test-media-0/shortform_2.mp4",
//      "https://storage.googleapis.com/exoplayer-test-media-0/shortform_3.mp4",
//      "https://storage.googleapis.com/exoplayer-test-media-0/shortform_4.mp4",
//      "https://storage.googleapis.com/exoplayer-test-media-0/shortform_6.mp4",
        )

    fun size(): Int {
        return mediaUris.size;
    }

    fun get(index: Int): MediaItem {
        val uri = mediaUris.get(index.mod(mediaUris.size))
        return MediaItem.Builder().setUri(uri).setMediaId(index.toString())
            .setMimeType(MimeTypes.APPLICATION_M3U8).build()
    }

}
