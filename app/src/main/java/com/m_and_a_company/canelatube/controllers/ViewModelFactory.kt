package com.m_and_a_company.canelatube.controllers

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.m_and_a_company.canelatube.domain.repository.song.SongsDataSource
import com.m_and_a_company.canelatube.domain.repository.song.SongsRepository
import com.m_and_a_company.canelatube.ui.svdn.DownloadViewModel
import com.m_and_a_company.canelatube.ui.svdn.SVDNViewModel
import com.m_and_a_company.canelatube.usesCases.DownloadSongUseCase
import com.m_and_a_company.canelatube.usesCases.GetIdSongUseCase
import com.m_and_a_company.canelatube.usesCases.GetInfoSongFromUrlUseCase

object ViewModelFactory {

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(private val context: Context): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            when {

                modelClass.isAssignableFrom(SVDNViewModel::class.java) -> {
                    return SVDNViewModel() as T
                }

                modelClass.isAssignableFrom(DownloadViewModel::class.java) -> {
                    return DownloadViewModel(
                        providesGetInfoSongFromUrlUseCase(providesSongRepository(context)),
                        providesGetIdSongUseCase(providesSongRepository(context)),
                        providesDownloadSongUseCase(providesSongRepository(context))
                    ) as T
                }

                else -> throw IllegalArgumentException("Unknown ViewModel class")

            }
        }
    }

    fun providesViewModelFactory(context: Context): ViewModelFactory = ViewModelFactory(context)

    private fun providesGetInfoSongFromUrlUseCase(songsRepository: SongsDataSource): GetInfoSongFromUrlUseCase {
        return GetInfoSongFromUrlUseCase(songsRepository)
    }

    private fun providesDownloadSongUseCase(songsRepository: SongsDataSource): DownloadSongUseCase {
        return DownloadSongUseCase(songsRepository)
    }

    private fun providesSongRepository(context: Context): SongsDataSource {
        return SongsRepository(context)
    }

    private fun providesGetIdSongUseCase(songsRepository: SongsDataSource): GetIdSongUseCase {
        return GetIdSongUseCase(songsRepository)
    }

}