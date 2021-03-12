package com.vob.unlocktracker.di

import android.content.Context
import androidx.room.Room
import com.vob.unlocktracker.db.RunDB
import com.vob.unlocktracker.util.Constants.RUNNING_DATABASE_NAME
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
}