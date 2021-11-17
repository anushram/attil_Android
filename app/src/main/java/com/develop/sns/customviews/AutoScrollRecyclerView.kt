package com.develop.sns.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.Interpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.develop.sns.customviews.AutoScrollRecyclerView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class AutoScrollRecyclerView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(
    context!!, attrs, defStyle
) {
    private var dispose: Disposable? = null

    /**
     * Sliding estimator
     */
    private var mInterpolator: UniformSpeedInterpolator? = null

    /**
     * Dx and dy between units
     */
    private var mSpeedDx: Int = 0

    /**
     * Dx and dy between units
     */
    private var mSpeedDy: Int = 0

    /**
     * Sliding speed, default 100
     */
    private var mCurrentSpeed = SPEED

    /**
     * Whether to display the list infinitely
     */
    private var mLoopEnabled = false

    /**
     * Whether to slide backwards
     */
    var mReverse = false


    /**
     * Whether to turn on automatic sliding
     */
    private var mIsOpenAuto = false

    /**
     * Whether the user can manually slide the screen
     */
    private var mCanTouch = true

    /**
     * Whether the user clicks on the screen
     */
    private var mPointTouch = false

    /**
     * Are you ready for data?
     */
    private var mReady = false

    /**
     * Whether initialization is complete
     */
    private var mInflate = false

    /**
     * Whether to stop scroll
     */
    private var isStopAutoScroll = false

    private var listSize: Int = 0
    private var intervalInMillis: Long = 0

    /**
     * Start sliding
     */
    fun startAutoScroll() {
        isStopAutoScroll = false
        openAutoScroll(mCurrentSpeed, false)
    }

    /**
     * Start sliding
     *
     * @param speed   Sliding distance (determining the sliding speed)
     * @param reverse Whether to slide backwards
     */
    fun openAutoScroll(speed: Int, reverse: Boolean) {
        mReverse = reverse
        mCurrentSpeed = speed
        mIsOpenAuto = true
        notifyLayoutManager()
        startScroll()
    }


    /**
     * Is it possible to manually slide when swiping automatically?
     */
    fun setCanTouch(b: Boolean) {
        mCanTouch = b
    }

    fun canTouch(): Boolean {
        return mCanTouch
    }

    /**
     * Set whether to display the list infinitely
     */
    open fun setLoopEnabled(loopEnabled: Boolean) {
        mLoopEnabled = loopEnabled
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
            //startScroll()
            autoScroll(listSize, intervalInMillis)
        }
    }


    /**
     * Whether to slide infinitely
     */
    fun isLoopEnabled(): Boolean {
        return mLoopEnabled
    }

    /**
     * Set whether to reverse
     */
    fun setReverse(reverse: Boolean) {
        mReverse = reverse
        notifyLayoutManager()
        startScroll()
    }

    /**
     * @param isStopAutoScroll
     */
    fun pauseAutoScroll(isStopAutoScroll: Boolean) {
        this.isStopAutoScroll = isStopAutoScroll
    }


    @JvmName("getReverse1")
    fun getReverse(): Boolean {
        return mReverse
    }

    /**
     * Start scrolling
     */
    private fun startScroll() {
        if (!mIsOpenAuto) return
        if (scrollState == SCROLL_STATE_SETTLING) return
        if (mInflate && mReady) {
            mSpeedDy = 0
            mSpeedDx = mSpeedDy
            smoothScroll()
        }
    }

    private fun smoothScroll() {
        if (!isStopAutoScroll) {
            val absSpeed = Math.abs(mCurrentSpeed)
            val d = if (mReverse) -absSpeed else absSpeed
            smoothScrollBy(d, d, mInterpolator)
        }
    }


    fun autoScroll(listSize: Int, intervalInMillis: Long) {
        this.listSize = listSize
        this.intervalInMillis = intervalInMillis
        dispose?.let {
            if (!it.isDisposed) return
        }
        dispose = Flowable.interval(intervalInMillis, TimeUnit.MILLISECONDS)
            .map { it % listSize + 1 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                smoothScrollToPosition(it.toInt() + 1)
            }
    }

    private fun notifyLayoutManager() {
        val layoutManager = layoutManager
        if (layoutManager is LinearLayoutManager) {
            val linearLayoutManager = layoutManager as LinearLayoutManager?
            if (linearLayoutManager != null) {
                linearLayoutManager.reverseLayout = mReverse
            }
        } else {
            val staggeredGridLayoutManager = layoutManager as StaggeredGridLayoutManager?
            if (staggeredGridLayoutManager != null) {
                staggeredGridLayoutManager.reverseLayout = mReverse
            }
        }
    }

    override fun swapAdapter(adapter: Adapter<*>?, removeAndRecycleExistingViews: Boolean) {
        super.swapAdapter(generateAdapter(adapter!!), removeAndRecycleExistingViews)
        mReady = true
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(generateAdapter(adapter!!))
        mReady = true
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return if (mCanTouch) {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> mPointTouch = true
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> if (mIsOpenAuto) {
                    return true
                }
            }
            super.onInterceptTouchEvent(e)
        } else true
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return if (mCanTouch) {
            when (e.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (mIsOpenAuto) {
                    mPointTouch = false
                    smoothScroll()
                    return true
                }
            }
            super.onTouchEvent(e)
        } else true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        startScroll()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mInflate = true
    }

    override fun onScrolled(dx: Int, dy: Int) {
        if (mPointTouch) {
            mSpeedDx = 0
            mSpeedDy = 0
            return
        }
        val vertical: Boolean
        if (dx == 0) { //Vertical scrolling
            mSpeedDy += dy
            vertical = true
        } else { //Horizontal scrolling
            mSpeedDx += dx
            vertical = false
        }
        if (vertical) {
            if (Math.abs(mSpeedDy) >= Math.abs(mCurrentSpeed)) {
                mSpeedDy = 0
                smoothScroll()
            }
        } else {
            if (Math.abs(mSpeedDx) >= Math.abs(mCurrentSpeed)) {
                mSpeedDx = 0
                smoothScroll()
            }
        }
    }

    private fun generateAdapter(adapter: Adapter<*>): NestingRecyclerViewAdapter<ViewHolder> {
        return NestingRecyclerViewAdapter(this, adapter) as NestingRecyclerViewAdapter<ViewHolder>
    }

    /**
     * Custom estimator
     * Swipe the list at a constant speed
     */
    private class UniformSpeedInterpolator : Interpolator {
        override fun getInterpolation(input: Float): Float {
            return input
        }
    }

    /**
     * Customize the Adapter container so that the list can be displayed in an infinite loop
     */
    private class NestingRecyclerViewAdapter<VH : ViewHolder>(
        private val mRecyclerView: AutoScrollRecyclerView, var mAdapter: Adapter<VH>
    ) :
        Adapter<VH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return mAdapter.onCreateViewHolder(parent, viewType)
        }

        override fun registerAdapterDataObserver(observer: AdapterDataObserver) {
            super.registerAdapterDataObserver(observer)
            mAdapter.registerAdapterDataObserver(observer)
        }

        override fun unregisterAdapterDataObserver(observer: AdapterDataObserver) {
            super.unregisterAdapterDataObserver(observer)
            mAdapter.unregisterAdapterDataObserver(observer)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            mAdapter.onBindViewHolder(holder, generatePosition(position))
        }

        override fun setHasStableIds(hasStableIds: Boolean) {
            super.setHasStableIds(hasStableIds)
            mAdapter.setHasStableIds(hasStableIds)
        }

        override fun getItemCount(): Int {
            //If it is an infinite scroll mode, set an unlimited number of items
            return if (getLoopEnable()) Int.MAX_VALUE else mAdapter.itemCount
        }

        override fun getItemViewType(position: Int): Int {
            return mAdapter.getItemViewType(generatePosition(position))
        }

        override fun getItemId(position: Int): Long {
            return mAdapter.getItemId(generatePosition(position))
        }

        /**
         * Returns the corresponding position according to the current scroll mode
         */
        private fun generatePosition(position: Int): Int {
            return if (getLoopEnable()) {
                getActualPosition(position)
            } else {
                position
            }
        }

        /**
         * Returns the actual position of the item
         *
         * @param position The position after starting to scroll will grow indefinitely
         * @return Item actual location
         */
        private fun getActualPosition(position: Int): Int {
            val itemCount = mAdapter.itemCount
            return if (position >= itemCount) position % itemCount else position
        }

        private fun getLoopEnable(): Boolean {
            return mRecyclerView.mLoopEnabled
        }

        fun getReverse(): Boolean {
            return mRecyclerView.mReverse
        }
    }


    companion object {
        private val TAG = AutoScrollRecyclerView::class.java.simpleName
        private const val SPEED = 2000
    }

    init {
        mInterpolator = UniformSpeedInterpolator()
        mReady = false
    }
}