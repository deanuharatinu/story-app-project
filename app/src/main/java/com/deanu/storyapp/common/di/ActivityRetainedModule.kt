package com.deanu.storyapp.common.di

import com.deanu.storyapp.common.data.StoryAppPagingRepoImpl
import com.deanu.storyapp.common.data.StoryAppRepoImpl
import com.deanu.storyapp.common.domain.repository.StoryAppPagingRepo
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.deanu.storyapp.common.utils.CoroutineDispatchersProvider
import com.deanu.storyapp.common.utils.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityRetainedModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindStoryAppRepository(repository: StoryAppRepoImpl): StoryAppRepository

    @Binds
    abstract fun bindStoryAppPagingRepository(repoImpl: StoryAppPagingRepoImpl): StoryAppPagingRepo

    @Binds
    abstract fun bindDispatchersProvider(dispatchersProvider: CoroutineDispatchersProvider): DispatchersProvider
}