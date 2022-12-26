# ChromeXt

Add UserScript support to Chrome using Xposed framework

##  How does it work?

We hook a `onUpdateUrl` function in [UserScript.kt](app/src/main/java/org/matrix/chromext/hook/UserScript.kt),
add URL comparison there and evaluate JavaScript using the `javascript:` scheme.

### Adapt to your Chrome version

We pay our main efforts to support the latest stable version of Android Chrome.
And usually the `beta` or `dev` versions are supported as well, but not guaranteed.

Recently, the author has tested `ChromeXt` with the latest `Android Chrome 108.0.5359.128`, and it works well.
Please consider update your Android Chrome first before proceeding.

For other versions, it might not work.
To adapt to those versions, one only need to find out one method name in its [smali](https://github.com/JesusFreke/smali/wiki) code.
Here is how to do that.
First use `apktool` to decompile the `split_chrome.apk` file pulled from the installation of Chrome on your phone,
then follow the hints in [UserScript.kt](app/src/main/java/org/matrix/chromext/proxy/UserScript.kt) to get the correct name
and modify it in the [SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences) of Chrome at `/data/data/com.android.chrome/shared_prefs/ChromeXt.xml`.

## Usage

This project requires **Xposed framework** installed.
You can try the following implements of it, depending on your Android version or whether having root enabled:
[LSPosed](https://github.com/LSPosed/LSPosed), [LSPatch](https://github.com/LSPosed/LSPatch),
[EdXposed](https://github.com/ElderDrivers/EdXposed), [TaiChi](https://github.com/taichi-framework/TaiChi),
[VirtualXposed](https://github.com/android-hacker/VirtualXposed), [Dreamland](https://github.com/canyie/Dreamland).

Pick up the latest built APK from my repo's [GitHub Action](https://github.com/JingMatrix/ChromeXt/actions/workflows/android.yml) and install it.
The author upload releases to [Xposed-Modules-Repo](https://github.com/Xposed-Modules-Repo/org.matrix.chromext/releases) when needed, but not that frequently.

You can then install UserScripts from popular sources: URLs that ends with `.user.js`.
However, this fails for scripts from some domains like `raw.githubusercontent.com`.
For them, please download those scripts using the download button on the top of Chrome's three dot menu.
And then if you open your downloaded scripts in Chrome, the installation prompt should show up again.


### Supported API

Currently, ChromeXt supports only the following APIs since they are everything the author needs to perform all sort of tasks.

1. @name (colons and backslashes not allowed), @namespace (backslashes not allowed), other similar properties' implements depends on the manage front end
2. @match (must present and conform to the [Chrome Standard](https://developer.chrome.com/docs/extensions/mv2/match_patterns/))
3. @include = @match, @exclude
4. @run-at: document-start, document-end, document-idle (the default and fallback value)
5. @grant GM_addStyle, GM_addElement, GM_xmlhttpRequest, GM_openInTab, unsafeWindow (= window)
6. @require

These APIs are implemented differently from the official ones, see the source file [LocalScripts.kt](app/src/main/java/org/matrix/chromext/script/LocalScripts.kt) if you have doubts or questions.

### UserScripts manager front end

To manage scripts installed by `ChromeXt`, here are a simple front end hosted on [github.io](https://jingmatrix.github.io/ChromeXt/) and two mirrors of it (in case that you have connection issues): [onrender.com](https://jianyu-ma.onrender.com/ChromeXt/), [netlify.app](https://jianyu-ma.netlify.app/ChromeXt/).

### Edit scripts before installing them

If you cancel the prompt to install a new UserScript, you can then edit it directly in Chrome.
To commit your modifications, long press on some text and follow with a click somewhere, the installation prompt should appear again.

### Limitations

A valid UserScript would fail if the following two conditions hold _at the same time_:

1. The matched website has disabled `script: 'unsafe-eval';` by [Content Security Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP);
2. The script size is nearly 2M, or it escapes \` with backslash.

Please note that one can, of course, use \` normally. 
To deal with this extremely rare case, one should
```
use multiple scripts of normal sizes instead of a giant script, and avoid using \` in the code.
```

## Bonus

### Solution of system gesture conflicts

The forward and backward gestures of Chrome are now available near the vertical center of screen.
On other areas, only the system gesture is available.
<!-- To enable forward gesture in chrome, with the help of this module, -->
<!-- one only needs to disable the right back gesture by -->
<!-- ```sh -->
<!-- adb shell settings put secure back_gesture_inset_scale_right -1 -->
<!-- ``` -->

### Dev Tools for developers

<!-- Currently, I implement this feature only for Android 11+. -->
<!-- I might implement it for older Android versions if there are some requests for doing so. -->

Tap five times on the Chrome version from the Chrome settings, you will see the `Developer options` menu.
After restarting Chrome, ChromeXt offers you the `Developer tools` page menu.

## Contribute to this project

Before you submit your pull-requests, please ensure that the command
`./gradlew build` or `gradlew.bat build` produces no warnings and no errors.

Here are corresponding files you might want / need to change:
1. Front end: [ChromeXt.vue](https://github.com/JingMatrix/viteblog/tree/master/components/ChromeXt.vue)
2. Tampermonkey API: [LocalScripts.kt](app/src/main/java/org/matrix/chromext/script/LocalScripts.kt)

## Development plans

- [x] Make it possible to pass intents to Chrome with `file` scheme
- [x] Fix encoding problem for Chrome downloaded Javascript files
- [x] Inject module resource into Chrome
- [x] Implement developer tools
- [x] Use local versions of [eruda](https://github.com/liriliri/eruda)
- [x] Improve eruda incorporation with Chrome
- [x] Add more information in the preference screen
- [x] Support more [Tampermonkey API](https://www.tampermonkey.net/documentation.php)s
- [x] Find elegant way to support Dev Tools for Android 11-
- [x] Improve front end
- [ ] Fix general text document encoding problem for Chrome Custom Tab
