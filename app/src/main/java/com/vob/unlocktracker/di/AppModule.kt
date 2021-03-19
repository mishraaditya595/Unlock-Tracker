package com.vob.unlocktracker.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.vob.unlocktracker.db.RunDB
import com.vob.unlocktracker.util.Constants.KEY_FIRST_TIME_TOGGLE
import com.vob.unlocktracker.util.Constants.KEY_NAME
import com.vob.unlocktracker.util.Constants.KEY_WEIGHT
import com.vob.unlocktracker.util.Constants.RUNNING_DATABASE_NAME
import com.vob.unlocktracker.util.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunDatabase(
            @ApplicationContext app: Context
    ) = Room.databaseBuilder(
            app,
            RunDB::class.java,
            RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunDB) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(KEY_NAME, "")?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
}