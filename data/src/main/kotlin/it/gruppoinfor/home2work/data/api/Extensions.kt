package it.gruppoinfor.home2work.data.api

inline fun <reified S> APIService.get(): S = createService(S::class.java)