package tv.av.support.core

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import java.nio.ByteBuffer


class MediaCodecPlayer {

    private enum class Track {
        AUDIO,
        VIDEO
    }

    fun play(path: String, surface: Surface?) {
        Thread {
            try {
                // extractor
                val mMediaFmt = arrayOf(
                    MediaFormat(),
                    MediaFormat()
                )
                val mExtractors = arrayOf(
                    MediaExtractor(),
                    MediaExtractor()
                )
                mExtractors[Track.AUDIO.ordinal].setDataSource(path)
                mExtractors[Track.VIDEO.ordinal].setDataSource(path)
                for (i in 0 until mExtractors[Track.AUDIO.ordinal].trackCount) {
                    val fmt = mExtractors[Track.AUDIO.ordinal].getTrackFormat(i)
                    if (fmt.getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true) {
                        mMediaFmt[Track.AUDIO.ordinal] = fmt
                        mExtractors[Track.AUDIO.ordinal].selectTrack(i)
                    } else if (fmt.getString(MediaFormat.KEY_MIME)?.startsWith("video/") == true) {
                        mMediaFmt[Track.VIDEO.ordinal] = fmt
                        mExtractors[Track.VIDEO.ordinal].selectTrack(i)
                    }
                }
                // mediacodec
                val mMediaCodec = arrayOf(
                    MediaCodec.createDecoderByType(
                        mMediaFmt[Track.AUDIO.ordinal].getString(MediaFormat.KEY_MIME)!!
                    ),
                    MediaCodec.createDecoderByType(
                        mMediaFmt[Track.VIDEO.ordinal].getString(MediaFormat.KEY_MIME)!!
                    )
                )
                mMediaCodec[Track.AUDIO.ordinal].configure(
                    mMediaFmt[Track.AUDIO.ordinal], null, null, 0
                )
                mMediaCodec[Track.VIDEO.ordinal].configure(
                    mMediaFmt[Track.VIDEO.ordinal], surface, null, 0
                )
                mMediaCodec[Track.AUDIO.ordinal].start()
                mMediaCodec[Track.VIDEO.ordinal].start()
                val mBufferInfo = arrayOf(
                    MediaCodec.BufferInfo(),
                    MediaCodec.BufferInfo()
                )
                val mSampleTimeStart = SystemClock.currentThreadTimeMillis()
                var eos = false
                while (!eos) {
                    var mSampleTimePast = SystemClock.currentThreadTimeMillis() - mSampleTimeStart
                    var mTrackOrdinal = 0
                    if (
                        mExtractors[Track.AUDIO.ordinal].sampleTime >=
                        mExtractors[Track.VIDEO.ordinal].sampleTime
                    ) {
                        mTrackOrdinal = Track.VIDEO.ordinal
                    } else if (
                        mExtractors[Track.AUDIO.ordinal].sampleTime <
                        mExtractors[Track.VIDEO.ordinal].sampleTime
                    ) {
                        mTrackOrdinal = Track.AUDIO.ordinal
                    }
                    // input
                    val mInBufferIndex = mMediaCodec[mTrackOrdinal].dequeueInputBuffer(
                        1000 * 10
                    )
                    val mInBuffer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mMediaCodec[mTrackOrdinal].getInputBuffer(mInBufferIndex)
                    } else {
                        mMediaCodec[mTrackOrdinal].inputBuffers[mInBufferIndex]
                    }
                    val mSampleSize: Int = mExtractors[mTrackOrdinal].readSampleData(
                        mInBuffer!!, 0
                    )
                    if (mSampleSize < 0) {
                        mMediaCodec[mTrackOrdinal].queueInputBuffer(
                            mInBufferIndex,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        eos = true
                    } else {
                        mMediaCodec[mTrackOrdinal].queueInputBuffer(
                            mInBufferIndex,
                            0,
                            mSampleSize,
                            mExtractors[mTrackOrdinal].sampleTime,
                            0
                        )
                        mExtractors[mTrackOrdinal].advance()
                    }
                    // output
                    var mOutBufferIndex: Int = mMediaCodec[mTrackOrdinal].dequeueOutputBuffer(
                        mBufferInfo[mTrackOrdinal], 1000 * 10
                    )
                    while (mOutBufferIndex >= 0) {
                        /*val mOutBuffer =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                val outBuffer =
                                    mMediaCodec[mTrackOrdinal].getOutputBuffer(mOutBufferIndex)
                            } else {
                                val outBuffer =
                                    mMediaCodec[mTrackOrdinal].outputBuffers[mOutBufferIndex]
                            }*/
                        mMediaCodec[mTrackOrdinal].releaseOutputBuffer(
                            mOutBufferIndex,
                            mTrackOrdinal == Track.VIDEO.ordinal
                        )
                        mOutBufferIndex = mMediaCodec[mTrackOrdinal].dequeueOutputBuffer(
                            mBufferInfo[mTrackOrdinal], 1000 * 10
                        )
                    }
                }
                // release
                for (i in 0 until Track.values().size) {
                    mMediaCodec[i].stop()
                    mMediaCodec[i].release()
                    mExtractors[i].release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}