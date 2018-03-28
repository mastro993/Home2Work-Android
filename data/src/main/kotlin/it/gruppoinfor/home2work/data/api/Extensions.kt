package it.gruppoinfor.home2work.data.api

inline fun <reified S> APIServiceGenerator.getService(): S = createService(S::class.java)