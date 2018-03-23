package it.gruppoinfor.home2work.di.match

import dagger.Subcomponent
import it.gruppoinfor.home2work.match.MatchesFragment


@MatchScope
@Subcomponent(modules = [MatchModule::class])
interface MatchSubComponent {
    fun inject(matchesFragment: MatchesFragment)
}