package tv.av.support.nulls

import android.media.MediaCodecList
import android.os.Build
import tv.av.support.model.CodecSupport

internal object MediaList {

    fun codecSupport(): CodecSupport {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (info in MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos) {
                    if (info.name.contains("decoder") && info.name.contains("hevc")) {
                        return CodecSupport("MediaList: success", true)
                    }
                }
            } else {
                val count = MediaCodecList.getCodecCount()
                for (i in 0 until count) {
                    val info = MediaCodecList.getCodecInfoAt(i)
                    if (info.name.contains("decoder") && info.name.contains("hevc")) {
                        return CodecSupport("MediaList: success", true)
                    }
                }
            }
        } catch (e: Exception) {
            return CodecSupport("MediaList: ${e.printStackTrace()}", false)
        }
        return CodecSupport("MediaList: ", false)
    }

}