package com.study.tedkim.imageloading;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by tedkim on 2017. 9. 17..
 */

public abstract class EndlessGridRecyclerViewScrollListner extends RecyclerView.OnScrollListener {

    private int mVisibleThreshold = 5;
    private int mCurrentPage = 0;
    private int mPreviousTotalItemCount = 0;
    private int mStartingPageIndex = 0;

    private boolean loading = true;

    GridLayoutManager mLayoutManager;

    public EndlessGridRecyclerViewScrollListner(GridLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        mVisibleThreshold = mVisibleThreshold * layoutManager.getSpanCount();
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {

        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
        int totalItemCount = mLayoutManager.getItemCount();

        if (totalItemCount < mPreviousTotalItemCount) {
            mCurrentPage = mStartingPageIndex;
            mPreviousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                loading = true;
            }
        }

        if (loading && (totalItemCount > mPreviousTotalItemCount)) {
            loading = false;
            mPreviousTotalItemCount = totalItemCount;
        }

        if (!loading && (lastVisibleItemPosition + mVisibleThreshold) > totalItemCount) {
            mCurrentPage++;
            onLoadMore(mCurrentPage, totalItemCount);
            loading = true;
        }
    }

    public abstract void onLoadMore(int page, int totalItemsCount);

}
