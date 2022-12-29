package org.matrix.chromext.proxy

import android.content.Context

class GestureNavProxy(ctx: Context, split: Boolean) {

  // The main class to hook is
  // org/chromium/chrome/browser/gesturenav/HistoryNavigationCoordinator.java

  private var HISTORY_NAVIGATION_COORDINATOR = "d31"
  // But we need several other classes to identify it
  // It has following fields:
  // private final Runnable mUpdateNavigationStateRunnable = this::onNavigationStateChanged;
  // private ViewGroup mParentView;
  // private HistoryNavigationLayout mNavigationLayout;
  // private InsetObserverView mInsetObserverView;
  // private CurrentTabObserver mCurrentTabObserver;
  // private ActivityLifecycleDispatcher mActivityLifecycleDispatcher;
  // private BackActionDelegate mBackActionDelegate;
  // private Tab mTab;
  // private boolean mEnabled;
  // We start with its HistoryNavigationLayout

  // private val HISTORY_NAVIGATION_LAYOUT = "g31"
  // which has two fields of Ljava/lang/Runnable,
  // one field of Lorg/chromium/base/Callback
  // And it has a Field SideSlideLayout

  // private val SIDE_SLIDE_LAYOUT = "vR2"
  // which can be identified with having many fields, such as
  // android/view/animation/Animation$AnimationListener,
  // android/view/animation/Animation,
  // android/view/animation/DecelerateInterpolator,
  // org/chromium/chrome/browser/gesturenav/NavigationBubble.

  // Once the class HistoryNavigationCoordinator is found, we care about its filed mEnabled
  // and its method isFeatureEnabled() with a Boolean return value
  var historyNavigationCoordinator: Class<*>
  // val ENABLE_FIELD = "s"
  var IS_FEATURE_ENABLED = "b"

  // val decorView: Class<*>? = null
  // val sideSlideLayout: Class<*>? = null
  var chromeTabbedActivity: Class<*>
  // Even though it exposes the onLayout method, it is not the correct Layout to hook

  // val UPDATE_NAVIGATION_HANDLER = "g"
  // identified since it contains WebContents class

  init {
    if (!split) {
      // private val SIDE_SLIDE_LAYOUT = "E83"
      // private val HISTORY_NAVIGATION_LAYOUT = "Xb1"
      HISTORY_NAVIGATION_COORDINATOR = "Ub1"
      IS_FEATURE_ENABLED = "b"
    }

    historyNavigationCoordinator = ctx.getClassLoader().loadClass(HISTORY_NAVIGATION_COORDINATOR)
    chromeTabbedActivity =
        ctx.getClassLoader().loadClass("org.chromium.chrome.browser.ChromeTabbedActivity")
    // decorView = ctx.getClassLoader().loadClass("com.android.internal.policy.DecorView")
    // sideSlideLayout = ctx.getClassLoader().loadClass(SIDE_SLIDE_LAYOUT)
  }
}
