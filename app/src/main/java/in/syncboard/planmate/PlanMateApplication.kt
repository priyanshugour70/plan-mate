package `in`.syncboard.planmate

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * PlanMate Application Class
 *
 * @HiltAndroidApp - This annotation is required for Hilt to work
 * It generates all the necessary Hilt components and sets up dependency injection
 *
 * This class is the entry point for our entire application and must be registered
 * in AndroidManifest.xml as android:name=".PlanMateApplication"
 */
@HiltAndroidApp
class PlanMateApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Future: Initialize any app-wide components here
        // - Timber for logging
        // - Crash reporting
        // - Analytics
        // - Database initialization

        // For now, we'll keep it simple
        // All Hilt setup is handled automatically by the @HiltAndroidApp annotation
    }
}