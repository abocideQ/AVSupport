package com.example.hevccodec

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import tv.av.support.CodecCheck
import java.io.File

class MainActivity : AppCompatActivity() {

    private val mPermissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val mCodecCheck = CodecCheck()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(mPermissions, 100)
        } else {
            initConfig(264, false)
        }
    }

    override fun onRequestPermissionsResult(code: Int, per: Array<out String>, ret: IntArray) {
        super.onRequestPermissionsResult(code, per, ret)
        if (code == 100) initConfig(264, false)
    }

    @SuppressLint("SetTextI18n")
    private fun initConfig(h26: Int, onTap: Boolean) {
        val mContentView: LinearLayout = findViewById(R.id.ll_content)
        mContentView.removeAllViews()
        //265
        val button265a = Button(baseContext)
        button265a.text = "h265 首帧"
        mContentView.addView(button265a)
        button265a.setOnClickListener { initConfig(265, true) }
        //265
        val button265 = Button(baseContext)
        button265.text = "h265"
        mContentView.addView(button265)
        button265.setOnClickListener { initConfig(265, false) }
        //264
        val button264 = Button(baseContext)
        button264.text = "h264"
        mContentView.addView(button264)
        button264.setOnClickListener { initConfig(264, false) }
        //view
        val surfaceView = SurfaceView(baseContext)
        surfaceView.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Thread {
                    ZE.assets2Sd(baseContext, "hevc", cacheDir.absolutePath)
                    val file = if (h26 == 265) File("${cacheDir.absoluteFile}/hevc/1s265.h265")
                    else File("${cacheDir.absoluteFile}/hevc/17k264.h264")
                    //mediacodecList
                    val h265MediaList = mCodecCheck.supportByList("hevc")
                    //mediacodec
                    val h265MediaDecode =
                        mCodecCheck.supportByCodec(file, onTap, null, holder.surface, null)
                    //===========================UI


                    runOnUiThread {
                        val viewMediaList = TextView(baseContext)
                        viewMediaList.textSize = 18f
                        viewMediaList.setTextColor(Color.RED)
                        viewMediaList.text = h265MediaList.exception + "\n" +
                                "h265 support： ${h265MediaList.support}" + "\n" +
                                "==========================" + "\n"
                        mContentView.addView(viewMediaList)
                        val viewMediaDecode = TextView(baseContext)
                        viewMediaDecode.textSize = 18f
                        viewMediaDecode.setTextColor(Color.RED)
                        viewMediaDecode.text = h265MediaDecode.exception + "\n" +
                                "h265 support： ${h265MediaDecode.support}" + "\n" +
                                "==========================" + "\n"
                        mContentView.addView(viewMediaDecode)
                    }
                }.start()
            }

            override fun surfaceChanged(s: SurfaceHolder, f: Int, w: Int, h: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })
        mContentView.addView(
            surfaceView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            500
        )
    }
}