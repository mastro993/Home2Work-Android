package it.gruppoinfor.home2work.entities


import java.util.*

class Profile(
        var exp: Karma,
        var stats: Statistics,
        var activity: Map<Int, SharingActivity>,
        var regdate: Date
)
