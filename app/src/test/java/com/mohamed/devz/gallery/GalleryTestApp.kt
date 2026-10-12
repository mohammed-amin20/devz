package com.mohamed.devz.gallery

import android.app.Application

/**
 * Minimal application for the Robolectric screenshot gallery. Using a plain
 * [Application] (instead of the real @HiltAndroidApp [com.mohamed.devz.DevZApp])
 * avoids booting the Hilt dependency graph in a unit-test JVM.
 */
class GalleryTestApp : Application()
