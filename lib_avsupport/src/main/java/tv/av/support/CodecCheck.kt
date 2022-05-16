package tv.av.support

import android.media.MediaFormat
import android.view.Surface
import tv.av.support.core.FrameFilter
import tv.av.support.core.MediaCodecCore
import tv.av.support.core.MediaCodecListCore
import tv.av.support.model.CodecSupport
import java.io.File

class CodecCheck {

    private val mMediaCodecListCore = MediaCodecListCore()
    private val mMediaCodecCore = MediaCodecCore()
    private val mLock = Object()

    fun supportByList(mime: String): CodecSupport {
        return mMediaCodecListCore.codecSupport(mime)
    }

    fun supportByCodec(
        file: File,
        oneTap: Boolean,
        mtf: MediaFormat?,
        surface: Surface?,
        filter: FrameFilter?
    ): CodecSupport {
        synchronized(mLock) {
            return mMediaCodecCore.codecSupport(file, oneTap, mtf, surface, filter)
        }
    }
}