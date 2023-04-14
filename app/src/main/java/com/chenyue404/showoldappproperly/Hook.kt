package com.chenyue404.showoldappproperly

import android.content.pm.ActivityInfo
import android.graphics.Rect
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2023/4/14 0014.
 */
class Hook : IXposedHookLoadPackage {
    private val HOOK_TARGET_PACKAGENAME = "android"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != HOOK_TARGET_PACKAGENAME) {
            return
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
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val info =
                            XposedHelpers.getObjectField(param.thisObject, "info") as ActivityInfo
                        val maxAspectRatio =
                            XposedHelpers.callMethod(info, "getMaxAspectRatio").toString()
                        if (maxAspectRatio == "1.86") {
                            param.result = false
                        }
                    }
                }
            )
        } catch (e: XposedHelpers.ClassNotFoundError) {
            e.printStackTrace()
        } catch (e: NoSuchMethodError) {
            e.printStackTrace()
        }
    }
}