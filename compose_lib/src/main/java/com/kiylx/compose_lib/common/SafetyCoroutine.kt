package com.kiylx.compose_lib.common

import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * 我们自己实现一个launch不就完了么。😊
 * 仿照官方的实现方法
 * ```
 * // launch 系统源码
 * public fun CoroutineScope.launch(
 *     context: CoroutineContext = EmptyCoroutineContext,
 *     start: CoroutineStart = CoroutineStart.DEFAULT,
 *     block: suspend CoroutineScope.() -> Unit
 * ): Job {
 *     val newContext = newCoroutineContext(context)
 *
 *     // 重点在这里，不使用下面两个，自己实现一个 Coroutine
 *     val coroutine = if (start.isLazy)
 *         LazyStandaloneCoroutine(newContext, block) else
 *         StandaloneCoroutine(newContext, active = true)
 *
 *     coroutine.start(start, coroutine, block)
 *     return coroutine
 * }
 * 第一步，先实现一个自定义 Coroutine 类（直接看注释吧）：
 * ```
 */
@OptIn(InternalCoroutinesApi::class)
class SafetyCoroutine<T>(
    parentContext: CoroutineContext
) : AbstractCoroutine<T>(parentContext + CoroutineExceptionHandler { _, error ->
    // 这里打印日志，想写就写
    error.printStackTrace()
}, initParentJob = true, active = true) {

    /**
     * 协程异常回调
     * （数组定义为0是为了节省内存，定义为0的情况下，初始状态下不会分配内存，添加数据后 ArrayList 扩容比较收敛，
     * 具体自己看源码，不要相信百度、CSDN教程，太老了！
     * 这里添加的方法回掉不会很多的，不需要扩容大量内存）
     */
    private var catchBlock = ArrayList<((Throwable) -> Unit)>(0)

    /**
     * 执行成功
     * （没太必要添加，这里主要是为了展示。因为 launch 里的代码执行完毕一定是成功的）
     */
    private var successBlock = ArrayList<((T) -> Unit)>(0)

    /**
     * 执行取消
     */
    private var cancellBlock = ArrayList<((Throwable) -> Unit)>(0)

    /**
     * 执行完成
     */
    private var completeBlock = ArrayList<((T?) -> Unit)>(0)


    /**
     * 代码发生异常，才会执行此方法
     */
    override fun handleJobException(exception: Throwable): Boolean {
        handleCoroutineException(context, exception)
        if (exception !is CancellationException) { // CancellationException 的不处理
            catchBlock.forEach { it.invoke(exception) }
        }
        return true
    }

    /**
     * 只有代码正常执行完毕，才会执行此方法
     * （一定是成功后才会走，协程被取消情况不会走这里）
     */
    override fun onCompleted(value: T) {
        super.onCompleted(value)
        successBlock.forEach { it.invoke(value) }
        completeBlock.forEach { it.invoke(value) }
        removeCallbacks()
    }

    /**
     * 协程被取消，会执行此方法
     */
    override fun onCancelled(cause: Throwable, handled: Boolean) {
        super.onCancelled(cause, handled)
        cancellBlock.forEach { it.invoke(cause) }
        completeBlock.forEach { it.invoke(null) }
        removeCallbacks()
    }

    private fun removeCallbacks() {
        successBlock.clear()
        catchBlock.clear()
        cancellBlock.clear()
        completeBlock.clear()
    }

    fun onCatch(catch: (e: Throwable) -> Unit) = apply {
        catchBlock.add(catch)
    }

    fun onSuccess(success: (T) -> Unit) = apply {
        successBlock.add(success)
    }

    fun onCancell(cancell: (Throwable) -> Unit) = apply {
        cancellBlock.add(cancell)
    }

    fun onComplete(complete: (T?) -> Unit) = apply {
        completeBlock.add(complete)
    }
}

// 自己的launch扩展
/**
 * 用法
 * ```
 * // 链式调用
 * lifecycleScope.launchSafety {
 *     // 这里能执行完的代码，一定是成功的
 * }.onCatch {
 *     // 想来几个就来几个，不想处理就一个都不写
 * }.onCatch {
 *
 * }.onComplete {
 *
 * }.onComplete {
 *
 * }
 *
 *
 * // 修改执行线程，和官方的用法一摸一样，没有区别
 * lifecycleScope.launchSafety(Dispatchers.IO) {
 *
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <Result> CoroutineScope.launchSafety(
    context: CoroutineContext = EmptyCoroutineContext,
): SafetyCoroutine<Result> {
    val newContext = newCoroutineContext(context)
    val coroutine = SafetyCoroutine<Result>(newContext)
    return coroutine
}





/**
 * 在一个协程作用域里，使用SupervisorJob启动新的协程
 */
fun CoroutineScope.launchSupervisor(block: suspend CoroutineScope.() -> Unit) {
    launch(SupervisorJob(), block = block)
}

/**
 * 使用SupervisorJob的协程作用域
 */
fun MainScope(name: String) =
    CoroutineScope(Dispatchers.Main) + SupervisorJob() + CoroutineName(name)

/**
 * 使用SupervisorJob的协程作用域
 */
fun IoScope(name: String) =
    CoroutineScope(Dispatchers.IO) + SupervisorJob() + CoroutineName(name)

/**
 * 使用SupervisorJob的协程作用域
 */
fun DefaultScope(name: String) =
    CoroutineScope(Dispatchers.Default) + SupervisorJob() + CoroutineName(name)
