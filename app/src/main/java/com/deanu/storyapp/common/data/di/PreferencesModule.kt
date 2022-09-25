package com.deanu.storyapp.common.data.di

import com.deanu.storyapp.common.data.preferences.Preferences
import com.deanu.storyapp.common.data.preferences.StoryAppPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {
    @Binds
    abstract fun providePreferences(preferences: StoryAppPreferences): Preferences
}