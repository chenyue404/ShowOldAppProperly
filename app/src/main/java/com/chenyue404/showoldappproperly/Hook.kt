package com.chenyue404.showoldappproperly

import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2023/4/14 0014.
 */
class Hook : IXposedHookLoadPackage {
    private val HOOK_TARGET_PACKAGENAME = "android"
    private val TAG = "showoldappproperly"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != HOOK_TARGET_PACKAGENAME) {
            return
        }

//        try {
//            XposedHelpers.findClass("com.android.server.wm.ActivityRecord", classLoader)
//                .methods.forEach {
//                    val parameters = it.parameters.map { it.type }.joinToString()
//                    log("${it.name}: $parameters")
//                }
//        } catch (e: Exception) {
//            log(e.toString())
//            e.printStackTrace()
//        }
        val hookFun = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val info =
                    XposedHelpers.getObjectField(param.thisObject, "info") as ActivityInfo
                val maxAspectRatio =
                    XposedHelpers.callMethod(info, "getMaxAspectRatio").toString()
                Log.d(TAG, "maxAspectRatio: $maxAspectRatio")
                log("maxAspectRatio: $maxAspectRatio")
                if (maxAspectRatio == "1.86") {
                    param.result = false
                }
            }
        }
        try {
            XposedHelpers.findAndHookMethod(
                "com.android.server.wm.ActivityRecord",
                classLoader,
                "applyAspectRatio",
                Rect::class.java,
                Rect::class.java,
                Rect::class.java,
                Float::class.java,
                Boolean::class.java,
                hookFun
            )
        } catch (e: XposedHelpers.ClassNotFoundError) {
            log(e.toString())
            e.printStackTrace()
        } catch (e: NoSuchMethodError) {
            log(e.toString())
            e.printStackTrace()
        }
        try {
            XposedHelpers.findAndHookMethod(
                "com.android.server.wm.ActivityRecord",
                classLoader,
                "applyAspectRatio",
                Rect::class.java,
                Rect::class.java,
                Rect::class.java,
                Float::class.java,
                hookFun
            )
        } catch (e: XposedHelpers.ClassNotFoundError) {
            log(e.toString())
            e.printStackTrace()
        } catch (e: NoSuchMethodError) {
            log(e.toString())
            e.printStackTrace()
        }
    }

    private fun log(str: String) {
//        XposedBridge.log("$TAG: $str")
    }
}