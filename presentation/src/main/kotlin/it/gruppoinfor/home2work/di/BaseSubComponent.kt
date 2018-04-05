package it.gruppoinfor.home2work.di


interface BaseSubComponent<in C: Any>{
    fun inject(component: C)
}