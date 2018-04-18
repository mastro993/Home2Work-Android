package it.gruppoinfor.home2work.common

import android.support.v4.widget.NestedScrollView
import android.view.View

abstract class NestedScrollViewEndlessScrollListener : NestedScrollView.OnScrollChangeListener {

    var isLoading = false
    var isLastPage = false

    private var currentPage = 1

    override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {

/*        if (scrollY > oldScrollY) {
        }

        if (scrollY < oldScrollY) {
        }

        if (scrollY == 0) {
        }*/

        if (scrollY == (v!!.getChildAt(0).measuredHeight - v.measuredHeight) && !isLoading && !isLastPage) {
            loadMoreItems(++currentPage)
        }

    }

    abstract fun loadMoreItems(page: Int)
}