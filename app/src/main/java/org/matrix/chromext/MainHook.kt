package org.matrix.chromext

import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.matrix.chromext.hook.BaseHook
import org.matrix.chromext.hook.GestureNavHook
import org.matrix.chromext.hook.IntentHook
import org.matrix.chromext.hook.MenuHook
import org.matrix.chromext.hook.UserScriptHook
import org.matrix.chromext.utils.Log
import org.matrix.chromext.utils.findMethod
import org.matrix.chromext.utils.hookAfter

val supportedPackages =
    arrayOf(
        "com.android.chrome",
        "com.chrome.beta",
        "com.chrome.dev",
        "com.chrome.canary",
        "org.bromite.bromite",
        "com.microsoft.emmx",
        "com.brave.browser",
        "com.brave.browser_beta")

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
  override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    if (supportedPackages.contains(lpparam.packageName)) {
      var entryPoint = "org.chromium.chrome.browser.base.SplitChromeApplication"
      runCatching { lpparam.classLoader.loadClass(entryPoint) }
          .onFailure { entryPoint = "org.chromium.chrome.browser.base.SplitMonochromeApplication" }
      findMethod(lpparam.classLoader.loadClass(entryPoint)) { name == "attachBaseContext" }
          .hookAfter {
            val ctx = (it.args[0] as Context).createContextForSplit("chrome")
            Chrome.init(ctx)
            initHooks(UserScriptHook, GestureNavHook, MenuHook, IntentHook)
          }
    }
  }

  override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
    ResourceMerge.init(startupParam.modulePath)
  }

  private fun initHooks(vararg hook: BaseHook) {
    hook.forEach {
      runCatching {
            if (it.isInit) return@forEach
            it.init()
            it.isInit = true
            Log.i("Inited hook: ${it.javaClass.simpleName}")
          }
          .onFailure { Log.ex(it) }
    }
  }
}
