// Path: app/src/main/java/in/syncboard/planmate/PlanMateApplication.kt

package `in`.syncboard.planmate

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * PlanMate Application Class
 * Updated to properly initialize with Hilt and Room database
 */
@HiltAndroidApp
class PlanMateApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // All Hilt setup is handled automatically by the @HiltAndroidApp annotation
        // Room database is initialized lazily when first accessed through DI

        // Future: Initialize other app-wide components here
        // - Timber for logging
        // - Crash reporting (Firebase Crashlytics)
        // - Analytics (Firebase Analytics)
        // - Push notifications
        // - App update checker
    }
}
