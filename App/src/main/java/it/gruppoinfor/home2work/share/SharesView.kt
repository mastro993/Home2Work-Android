package it.gruppoinfor.home2work.share


interface SharesView {

    fun setItems(list: List<Share>)
    fun onLoadingError(errorMessage: String)
    fun onLoading()

}