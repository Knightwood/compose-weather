package com.kiylx.libx.http.kotlin.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 使用方式：
 *  1. viewmodel中定义要分页的数据
 *  ```
 *     var studentPagerInfo = Paging<StudentBean>()
 *  ```
 *  2.调用pager中的加载方法
 *  ```
 *  val loadMore=true //是否是加载更多，如果不是，则会重置分页之类的值
 *  viewModelScope.launch {
 *      studentPagerInfo.loadMore(loadMore) { currentIndex, pageSize ->
 *          //调用网络接口查询分页数据
 *          val data = Repo.queryList(
 *              content,
 *              currentIndex + 1,
 *              pageSize
 *      )
 *      if (data is RawResponse.Success && data.isSuccess()) {
 *          //如果数据加载成功
 *          val result = data.responseData?.data?.list ?: emptyList()
 *          //返回本次的分页数据
 *          Paging.PagerData<MemberBean>(
 *              result,//接口返回的list
 *              Paging.LoadState.NO_LOADING(hasMoreData = result.isNotEmpty()),//状态值，可以监听。（是否还有更多数据）
 *              data.responseData?.data?.total?:-1//总的total值，可以不传
 *          )
 *      } else {
 *          Paging.PagerData(emptyList(), Paging.LoadState.FAILED)
 *      }
 *  }
```
 *
 */
class Paging<T>(
    var pageSize: Int = 10,
) {

    //记录当前的index，初始化时，还没有任何数据，为0
    var currentIndex: Int = 0
        private set

    //所有的数据
    private var _datasList: MutableStateFlow<MutableList<T>> = MutableStateFlow(mutableListOf())
    val datasList: StateFlow<MutableList<T>> get() = _datasList

    //每次请求所得数据
    private var _loadMoreDatasList: MutableStateFlow<List<T>> = MutableStateFlow(listOf())
    val loadMoreDatasList: StateFlow<List<T>> get() = _loadMoreDatasList

    //加载更多数据之前和之后的数据总量大小的记录 <加载数据之前的数据总量，加载数据之后的数据总量>
    var sizeRecord = 0 to 0
        private set



    //记录上一次的index，初始化时为0
    var oldIndex: Int = currentIndex
        private set

    //加载状态
    var loadStatus: MutableStateFlow<LoadState> = MutableStateFlow(LoadState.INIT)

    //总的数据总量
    var totalSize: Int = 0
        private set

    /**
     * 是否有下一页数据，与totalSize可以二选一
     */
    private var hasMoreData: Boolean=false

    /**
     * 判断是否有下一页数据
     * 有两个条件，1，当前的数据集尺寸小于总大小。
     *      或者，2，hasMoreData为true
     */
    fun hasNext(): Boolean {
        return (datasList.value.size < totalSize) || hasMoreData
    }

    /**
     * 自动判断是否有下一页数据，有的话，加载。
     * @param block 返回值 <total,List<T>>
     */
    suspend fun loadMore(
        loadMore: Boolean = true,
        block: suspend (
            currentIndex: Int,//当前已有的数据加载到的index，获取新数据时应该+1
            pageSize: Int
        ) -> PagerData<T>
    ) {
        val oldTotalDatasList: MutableList<T> = if (!loadMore) {//数据刷新，重置所有数据
            currentIndex = 0
            oldIndex = 0
            mutableListOf()
        } else {
            if (!hasNext()) {
                loadStatus.emit(LoadState.NO_LOADING(false))
                return
            }
            val emptyContainer = mutableListOf<T>()
            emptyContainer.addAll(datasList.value)
            emptyContainer
        }

        loadStatus.emit(LoadState.LOADING)
        val pair = block(currentIndex, pageSize)
        val datas = pair.data
        if (datas.isNotEmpty()) {
            sizeRecord =
                oldTotalDatasList.size to (oldTotalDatasList.size + datas.size)//记录加载前和之后的数据总量

            oldTotalDatasList.addAll(datas)
            _datasList.emit(oldTotalDatasList)//更新总数居

            _loadMoreDatasList.emit(datas)//更新本次加载的数据

            oldIndex = currentIndex//记录数据加载前的index
            this.currentIndex += 1//记录当前的index

            if (pair.total>0){
                this.totalSize=pair.total
            }else{
                this.hasMoreData = pair.state is LoadState.NO_LOADING && pair.state.hasMoreData
            }//更新数据总量
//            this.totalSize = if (pair.total != this.totalSize && pair.total > 0) {
//                pair.total
//            } else {
//                this.totalSize //更新数据总量
//            }
        }
        loadStatus.emit(pair.state)//可以令界面监听加载状态
    }

    companion object {
        const val TAG = "tty1-分页组件"
    }

    data class PagerData<T>(
        val data: List<T>,
        val state: LoadState = LoadState.NO_LOADING(true),
        val total: Int = -1,
    ) {
        companion object {

        }
    }

    /**
     * 加载状态
     */
    sealed class LoadState {
        object LOADING : LoadState()
        object FAILED : LoadState()
        object INIT : LoadState()// 初始化
        data class NO_LOADING(val hasMoreData: Boolean) : LoadState()
    }
}