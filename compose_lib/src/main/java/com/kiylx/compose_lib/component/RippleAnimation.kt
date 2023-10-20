package com.kiylx.compose_lib.component

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView

/*
关于偏移、缩放与旋转，我们建议的调用顺序是 rotate -> scale -> offset

若offset发生在rotate之前时，rotate会对offset造成影响。具体表现为当出现拖动手势时，组件会以当前角度为坐标轴进行偏移。

若offset发生在scale之前时，scale也会对offset造成影响。具体表现为UI组件在拖动时不跟手


 */


class RippleAnimationState {
    var animMode: AnimMode = AnimMode.expend
    var animTime: Long = 500
    internal var innerPos = mutableStateOf(Offset.Zero)

    /**
     * 绘制时是否向上移动一个状态栏高度
     */
    var moveUpSystemBarInsts: Boolean = true

    //when need change theme,will switch to true,when change done,will restore to false
    var runAnim = false
    internal var offset = mutableStateOf(Offset.Zero)

    /**
     * 切换主题的方法块
     */
    internal var block: () -> Unit = {}

    /**
     * 设置主题切换的逻辑，传入坐标，以运行动画以及触发主题切换
     * 若使用了[autoRippleAnimation]，可以不传坐标。
     */
    fun change(pointerOffset: Offset = innerPos.value, func: () -> Unit) {
        if (runAnim) {
            return
        }
        if (pointerOffset == Offset.Zero) {
            throw IllegalArgumentException("坐标错误")
        }
        offset.value = pointerOffset
        block = func
    }


    enum class AnimMode {
        shrink, expend
    }
}

@Composable
fun rememberRippleAnimationState(block: RippleAnimationState.() -> Unit = {}): RippleAnimationState {
    return remember {
        RippleAnimationState().also(block)
    }
}

private const val TAG = "rippleAnimation"

@Composable
fun Modifier.rippleAnimation(
    window: Window,
    state: RippleAnimationState
): Modifier {
    val mRootView = LocalView.current.rootView
//    val yTranslate = if (state.moveUpSystemBarInsts) {
//        WindowInsets.statusBars.getTop(Density(LocalDensity.current.density))
//    } else {
//        0
//    }
    var mMaxRadius = 0

    var mBackground by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var anim: ValueAnimator?

    var mRadius by remember {
        mutableStateOf(0f)
    }

    val mPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    /**
     * 根据起始点将屏幕分成4个小矩形,mMaxRadius就是取它们中最大的矩形的对角线长度
     * 这样的话, 无论起始点在屏幕中的哪一个位置上, 我们绘制的圆形总是能覆盖屏幕
     */
    fun updateMaxRadius(offset: Offset) {
        //将屏幕分成4个小矩形
        val leftTop = RectF(0f, 0f, offset.x, offset.y)
        val rightTop = RectF(leftTop.right, 0f, mRootView.right.toFloat(), leftTop.bottom)
        val leftBottom = RectF(0f, leftTop.bottom, leftTop.right, mRootView.bottom.toFloat())
        val rightBottom =
            RectF(leftBottom.right, leftTop.bottom, mRootView.right.toFloat(), leftBottom.bottom)
        //分别获取对角线长度
        val leftTopHypotenuse = Math.sqrt(
            Math.pow(leftTop.width().toDouble(), 2.0) + Math.pow(
                leftTop.height().toDouble(), 2.0
            )
        )
        val rightTopHypotenuse = Math.sqrt(
            Math.pow(rightTop.width().toDouble(), 2.0) + Math.pow(
                rightTop.height().toDouble(), 2.0
            )
        )
        val leftBottomHypotenuse = Math.sqrt(
            Math.pow(leftBottom.width().toDouble(), 2.0) + Math.pow(
                leftBottom.height().toDouble(), 2.0
            )
        )
        val rightBottomHypotenuse = Math.sqrt(
            Math.pow(rightBottom.width().toDouble(), 2.0) + Math.pow(
                rightBottom.height().toDouble(), 2.0
            )
        )
        //取最大值
        mMaxRadius = Math.max(
            Math.max(leftTopHypotenuse, rightTopHypotenuse),
            Math.max(leftBottomHypotenuse, rightBottomHypotenuse)
        ).toInt()
    }

    /**
     * 更新屏幕截图
     */
    fun updateBackground(success:(bitmap:Bitmap)->Unit) {
        if (mBackground != null && !mBackground!!.isRecycled) {
            mBackground!!.recycle()
        }

//        val bitmap = Bitmap.createBitmap(mRootView.width, mRootView.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        canvas.translate(0f, (-yTranslate).toFloat())
//        mRootView.draw(canvas)
//        mBackground=bitmap

        // 避免Software rendering doesn't support hardware bitmaps
        val bounds = Rect()
        mRootView.getDrawingRect(bounds)
        try {
            val bitmap = Bitmap.createBitmap(
                bounds.width().toInt(),
                bounds.height().toInt(),
                Bitmap.Config.ARGB_8888,
            )

            PixelCopy.request(
                window,
                bounds,
                bitmap,
                {
                    when (it) {
                        PixelCopy.SUCCESS -> {
                            success(bitmap)
                        }

                        PixelCopy.ERROR_DESTINATION_INVALID -> {
                            Log.e(TAG, "updateBackground: ERROR_DESTINATION_INVALID")
                        }

                        PixelCopy.ERROR_SOURCE_INVALID -> {
                            Log.e(TAG, "updateBackground: ERROR_SOURCE_INVALID")
                        }

                        PixelCopy.ERROR_TIMEOUT -> {
                            Log.e(
                                TAG, "A timeout occurred while trying to acquire a buffer " +
                                        "from the source to copy from."
                            )
                        }

                        PixelCopy.ERROR_SOURCE_NO_DATA -> {
                            Log.e(TAG, "updateBackground: ERROR_SOURCE_NO_DATA")
                        }

                        else -> {
                            Log.e(TAG, "updateBackground: UN_KNOW_ERR")

                        }
                    }
                },
                android.os.Handler(Looper.getMainLooper())
            )
        } catch (e: Exception) {
            Log.e(TAG, "updateBackground: $e")
        }
    }

    val mAnimatorListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {
            state.block.invoke()
        }
        override fun onAnimationEnd(p0: Animator) {
            if (mBackground != null && !mBackground!!.isRecycled) {
                mBackground!!.recycle()
                mBackground = null
            }
            state.runAnim = false
        }

        override fun onAnimationCancel(p0: Animator) {}
        override fun onAnimationRepeat(p0: Animator) {}
    }

    /**
     * 点击时，触发动画，更新圆的半径，触发绘制
     */
    LaunchedEffect(key1 = state.offset.value, block = {
        if (!state.runAnim && state.offset.value != Offset.Zero) {
            state.runAnim = true
            updateMaxRadius(state.offset.value)//获取圆形的最大半径
            updateBackground{
                mBackground=it
                anim = when (state.animMode) {
                    RippleAnimationState.AnimMode.shrink -> {
                        ValueAnimator.ofFloat(mMaxRadius.toFloat(), 0f)
                    }

                    RippleAnimationState.AnimMode.expend -> {
                        ValueAnimator.ofFloat(0f, mMaxRadius.toFloat())
                    }
                }.apply {
                    duration = state.animTime
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener { valueAnimator ->
                        mRadius = valueAnimator.animatedValue as Float
                    }
                    addListener(mAnimatorListener)
                }
                anim?.start()
            }
        }
    })

    val result = this.drawWithCache {
        this.onDrawWithContent {
            //绘制内容
            drawContent()
            if (mBackground != null) {
                val mStartX = state.offset.value.x
                val mStartY = state.offset.value.y
                //绘制遮罩层
                drawIntoCanvas {
                    it.nativeCanvas.run {
                        val layer: Int =
                            saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
                        when (state.animMode) {
                            RippleAnimationState.AnimMode.shrink -> {
                                mPaint.xfermode = null
                                drawCircle(mStartX, mStartY, mRadius, mPaint)
                                mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                                drawBitmap(mBackground!!, 0f,0f, mPaint)
                            }

                            RippleAnimationState.AnimMode.expend -> {
                                mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                                drawBitmap(mBackground!!, 0f,0f, null)
                                drawCircle(mStartX, mStartY, mRadius, mPaint)
                            }
                        }
                        restoreToCount(layer)
                    }
                }
            }

        }
    }
    return result
}

/**
 * 使用此modifier，可以省略获取手动点击位置
 */
@Composable
fun Modifier.autoRippleAnimation(window: Window,state: RippleAnimationState): Modifier {
    return this
        .rippleAnimation(window,state)
        .extendClick {
            state.innerPos.value = it.position
        }
}


/**
 * 在button这种有clickable的控件上使用时，可以提供点击事件处理的同时
 * 还可以提供这次的点击事件信息，之后的clickable则失效
 */
@Composable
fun Modifier.extendClick(
    consumed: Boolean = false,
    onClick: (firstDown: PointerInputChange) -> Unit
): Modifier {
    return this.pointerInput(Unit) {
        this.awaitEachGesture {
            //pass指定为Initial，最先处理事件
            val firstDown: PointerInputChange = awaitFirstDown(pass = PointerEventPass.Initial)
            onClick(firstDown)
            if (consumed) {
                firstDown.consume() //消费了之后，其余的不再响应事件
            }
        }
    }
}
