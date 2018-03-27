package it.gruppoinfor.home2work.match

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import it.gruppoinfor.home2work.domain.usecases.EditMatch
import it.gruppoinfor.home2work.domain.usecases.GetMatch
import it.gruppoinfor.home2work.domain.usecases.GetMatchList
import it.gruppoinfor.home2work.entities.Match


class MatchVMFactory(
        private val getMatch: GetMatch,
        private val getMatchList: GetMatchList,
        private val editMatch: EditMatch,
        private val mapper: Mapper<MatchEntity, Match>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MatchViewModel(getMatch, getMatchList, editMatch, mapper) as T
    }
}