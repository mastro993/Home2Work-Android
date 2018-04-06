package it.gruppoinfor.home2work.di.settings

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.mappers.MatchEntityMatchMapper
import it.gruppoinfor.home2work.domain.usecases.EditMatch
import it.gruppoinfor.home2work.domain.usecases.GetMatch
import it.gruppoinfor.home2work.domain.usecases.GetMatchList
import it.gruppoinfor.home2work.match.MatchVMFactory
import it.gruppoinfor.home2work.settings.SettingsVMFactory


@Module
@SettingsScope
class SettingsModule {
    @Provides
    fun provideSettingsVMFactory(): SettingsVMFactory {
        return SettingsVMFactory()
    }
}