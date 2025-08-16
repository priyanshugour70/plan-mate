package `in`.syncboard.planmate.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Dependency Injection Module
 *
 * This module provides app-wide dependencies that live for the entire app lifecycle
 *
 * @Module - Tells Hilt this class provides dependencies
 * @InstallIn(SingletonComponent::class) - These dependencies live as long as the app
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Future: Provide Room Database
     *
     * @Provides - Tells Hilt how to create this dependency
     * @Singleton - Only one instance will be created
     */
    // @Provides
    // @Singleton
    // fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    //     return Room.databaseBuilder(
    //         context,
    //         AppDatabase::class.java,
    //         "planmate_database"
    //     ).build()
    // }

    /**
     * Future: Provide Retrofit for API calls
     */
    // @Provides
    // @Singleton
    // fun provideRetrofit(): Retrofit {
    //     return Retrofit.Builder()
    //         .baseUrl("https://api.planmate.syncboard.in/")
    //         .addConverterFactory(GsonConverterFactory.create())
    //         .build()
    // }

    /**
     * Future: Provide DataStore for preferences
     */
    // @Provides
    // @Singleton
    // fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
    //     return PreferenceDataStoreFactory.create(
    //         produceFile = { context.preferencesDataStoreFile("planmate_preferences") }
    //     )
    // }

    /**
     * Provide a simple string for now (placeholder)
     * This shows how Hilt dependency injection works
     */
    @Provides
    @Singleton
    fun provideAppName(): String {
        return "PlanMate"
    }
}