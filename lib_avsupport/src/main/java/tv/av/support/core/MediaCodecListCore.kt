package tv.av.support.core

import android.media.MediaCodecList
import android.os.Build
import tv.av.support.model.CodecSupport

internal class MediaCodecListCore {

    /**
     * @param
     * avc/hevc
     */
    fun codecSupport(mime: String): CodecSupport {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (info in MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos) {
                    if (info.name.contains("decoder") && info.name.contains(mime)) {
                        return CodecSupport("MediaList: success", true)
                    }
                }
            } else {
                val count = MediaCodecList.getCodecCount()
                for (i in 0 until count) {
                    val info = MediaCodecList.getCodecInfoAt(i)
                    if (info.name.contains("decoder") && info.name.contains(mime)) {
                        return CodecSupport("MediaList: success", true)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return CodecSupport("MediaList: ${e.printStackTrace()}", false)
        }
        return CodecSupport("MediaList: ", false)
    }
}