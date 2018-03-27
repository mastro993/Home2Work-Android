package it.gruppoinfor.home2work.common.extensions

import it.gruppoinfor.home2work.entities.Match


fun Match.getScore(): Int? {

    this.homeScore?.let { home ->
        this.jobScore?.let { job ->
            this.timeScore?.let { time ->
                return (home + job + time) / 3
            }
            return null
        }
        return null
    }
    return null
}