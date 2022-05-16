package com.example.hevccodec

import android.app.Application
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class MApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UMConfigure.preInit(this, "6278861130a4f67780cf2624", null)
        UMConfigure.init(
            this,
            "6278861130a4f67780cf2624",
            null,
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }
}