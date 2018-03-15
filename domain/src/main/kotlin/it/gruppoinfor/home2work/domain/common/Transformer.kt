package it.gruppoinfor.home2work.domain.common

import io.reactivex.ObservableTransformer


abstract class Transformer<T> : ObservableTransformer<T, T>