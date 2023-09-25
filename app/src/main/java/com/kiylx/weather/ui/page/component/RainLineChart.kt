package com.kiylx.weather.ui.page.component

import android.annotation.SuppressLint
import android.graphics.CornerPathEffect
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiylx.weather.repo.bean.MinutelyPrecipitationEntity
import java.util.Collections
import android.graphics.Color as Color_N
import android.graphics.LinearGradient as LinearGradient_N
import android.graphics.Paint as Paint_N
import android.graphics.Path as Path_N
import android.graphics.Rect as Rect_N
import android.graphics.Shader as Shader_N
import android.graphics.Typeface as Typeface_N
import androidx.compose.foundation.Canvas as CanvasUi
import androidx.compose.ui.graphics.Canvas as Canvas_C


data class RainLineChartData(
    val title: String? = null,
    val data: List<MinutelyPrecipitationEntity.Minutely>,//柱状图数据
    val xAxisLabels: List<String>,
    val yAxisLabels: List<String>,
) {
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RainLineChart(
    data: RainLineChartData,
    modifier: Modifier = Modifier,
    labelTextSize: TextUnit = 12.sp, //sp
    strokeWidth: Dp = 1.dp, //dp
    axisLineColor: Int = 0xCCD5D5D5.toInt(),
    textColor: Int = 0xFF6750A4.toInt(),
    dataLineColor: Int = Color_N.argb(255, 212, 100, 77),
    gradientLineColor: Int = Color_N.argb(100, 111, 111, 111),
    gradientColor1: Int = Color_N.argb(255, 229, 160, 144),
    gradientColor2: Int = Color_N.argb(255, 251, 244, 240),
) {
    //text paint
    val textPaint: Paint_N by remember {
        mutableStateOf(Paint_N().apply {
            isAntiAlias = true
            isDither = true
            typeface = Typeface_N.create(Typeface_N.SANS_SERIF, Typeface_N.NORMAL)
            color = textColor
        })
    }
    val linePaint: Paint_N by remember {
        mutableStateOf(Paint_N().apply {
            isAntiAlias = true
            isDither = true
            style = Paint_N.Style.STROKE
            color = axisLineColor
            strokeJoin = Paint.Join.ROUND
            val cornerPathEffect = CornerPathEffect(200f)
            pathEffect = cornerPathEffect
        })
    }
    val dataLinePaint: Paint_N by remember {
        mutableStateOf(Paint_N().apply {
            style = Paint_N.Style.STROKE
            isDither = true
            isAntiAlias = true
            color = dataLineColor
            strokeJoin = Paint.Join.ROUND
            val cornerPathEffect = CornerPathEffect(200f)
            pathEffect = cornerPathEffect
        })

    }

    val dataGradientPaint: Paint_N by remember {
        mutableStateOf(Paint_N().apply {
            style = Paint_N.Style.FILL
            color = gradientLineColor
        })

    }

    var chartWidth by remember { mutableStateOf(0F) }
    var chartHeight by remember { mutableStateOf(0F) }

    CanvasUi(
        modifier = modifier
            .onSizeChanged { size ->
                chartWidth = size.width.toFloat()
                chartHeight = size.height.toFloat()
            }
    ) {
        drawIntoCanvas { canvas: Canvas_C ->
            textPaint.textSize = labelTextSize.toPx()
            linePaint.strokeWidth = strokeWidth.toPx()

            val rectText = Rect_N()
            textPaint.getTextBounds("右", 0, 1, rectText)
            val bottomInterval: Float = rectText.height().toFloat() + strokeWidth.toPx()

            //保存画布并将画布原点转为左下角
            canvas.save()
            canvas.translate(0f, size.height)
            canvas.scale(1f, -1f)

            //先向上偏移一个文字的高度，腾出给X轴的空间
            canvas.translate(0f, bottomInterval)
            //设置后面渐变色的着色器
            val linearGradient = LinearGradient_N(
                0f, size.height,
                0f,
                0f,
                gradientColor1,
                gradientColor2,
                Shader_N.TileMode.CLAMP
            )
            dataGradientPaint.shader = linearGradient

            //绘制y轴及文字
            val translate_x =
                drawTextOfYLeft(
                    canvas = canvas, data = data,
                    textPaint = textPaint, upInterval = bottomInterval,
                    linePaint = linePaint
                )

            //向右移动文字和y轴线宽距离，腾出空间绘制内容和x轴及x轴标签
            canvas.translate(
                dx = translate_x + strokeWidth.toPx(),
                dy = 0F
            )

            //绘制x轴及x轴标签和x轴上方的横线
            drawTextDownX(
                canvas,
                data,
                textPaint,
                linePaint,
                translate_x.toFloat(),
                bottomInterval,
                strokeWidth.toPx()
            )

            canvas.translate(0f, strokeWidth.toPx() / 2)//因为画x轴时向上抬升了一半的线宽度，所以，内容也要抬升同样的高度
            //绘制数据曲线
            drawCubtoCircle(
                dataLinePaint,
                dataGradientPaint,
                dataList = data.data.map {
                    it.precip.toDouble()
                },
                canvas = canvas,
                bottomInterval = bottomInterval,
                translate_x = translate_x,
                strokeWidth = strokeWidth
            )
            //还原画布
            canvas.restore()
        }
    }


}

fun DrawScope.drawTextDownX(
    canvas: Canvas,
    data: RainLineChartData,
    textPaint: Paint_N,
    linePaint: Paint_N,
    leftInterval: Float,
    bottomInterval: Float,
    strokeWidth: Float,
): Int {
    val xAxisData: List<String> = data.xAxisLabels
    val yAxisData: List<String> = data.yAxisLabels
    if (xAxisData.isEmpty()) {
        return 0;
    }
    val realHeight = size.height - bottomInterval
    val realWidth = size.width - (1.2 * leftInterval).toFloat()
    var maxYInterval = 0;
    val x_interval = realWidth / (xAxisData.size - 1);//标签数量将长度平分

    val rectText = Rect_N()
    canvas.save()
    //将文字旋转摆正，此时坐标系y向下是正
    canvas.scale(1f, -1f)
    (xAxisData.indices).forEach { index ->
        if (index > 0) {
            canvas.nativeCanvas.translate(x_interval, 0f)
        }
        val strTx = xAxisData[index]
        textPaint.getTextBounds(strTx, 0, strTx.length, rectText)
        if (rectText.height() > maxYInterval) {
            maxYInterval = rectText.height()
        }
        //文字书写位置，最后一个文字不能让他超出屏幕
        val text_x_pos = if (index == 0) {
            0f
        } else if (index == xAxisData.size - 1) {
            -rectText.width().toFloat() - 10
        } else {
            -rectText.width().toFloat() / 2 - 10
        }
        canvas.nativeCanvas.drawText(
            strTx,
            text_x_pos,
            (rectText.height()).toFloat(),
            textPaint
        )
    }
    canvas.restore()

    //==========================x轴及上部的横线=========================

    val onePath = Path_N()
    //使x轴与y轴严丝合缝
    onePath.moveTo(-strokeWidth, 0f)
    onePath.lineTo(realWidth, 0f) //前面以及移动过canvas，所以这里直接从原点画直线

    canvas.save()
    //横线间隔是将内容高度平分，并减去横线的线宽
    val intervalHeight = ((realHeight) / (yAxisData.size - 1))
    //通过平移画布绘制剩余的平行x轴线
    (yAxisData.indices).forEach { index ->
        if (index > 0) {
            if (index == yAxisData.size - 1) {
                canvas.translate(0f, intervalHeight - strokeWidth / 2)
            } else {
                canvas.translate(0f, intervalHeight)
            }
            canvas.nativeCanvas.drawPath(onePath, linePaint)
        } else if (index == 0) {
            canvas.translate(0f, strokeWidth / 2)
            canvas.nativeCanvas.drawPath(onePath, linePaint)
            canvas.translate(0f, -strokeWidth / 2)
        }
    }
    canvas.restore()

    return maxYInterval
}

private fun DrawScope.drawTextOfYLeft(
    canvas: Canvas,
    data: RainLineChartData,
    textPaint: Paint,
    upInterval: Float, //整体向上偏移的距离
    linePaint: Paint,
): Int {
    val yAxisLabels: List<String> = data.yAxisLabels //y轴标签
    if (yAxisLabels.isEmpty()) {
        return 0
    }
    //底部抬升，空出x轴的高度后，实际绘制内容的高度
    val realHeight = size.height - upInterval
    //y轴标签间隔高度
    val intervalHeight = realHeight / (yAxisLabels.size - 1)
    //文字测量工具
    val rectText = Rect_N()
    //因为绘制y轴及y轴的标签，所以x轴及图表应向右偏移，这里记录最大的偏移距离（即最长的文本长度）
    var maxTranslate = 0;

    //==========================y轴上的文字================================
    canvas.save() //保存canvas状态
    //将文字旋转摆正，此时坐标系y向下是正
    (yAxisLabels.indices).forEach { index ->
        val strTx = yAxisLabels[index]
        textPaint.getTextBounds(strTx, 0, strTx.length, rectText)
        if (rectText.width() > maxTranslate) {
            maxTranslate = rectText.width() + 15
        }
        //文字偏移
        if (index > 0) {
            canvas.translate(0f, intervalHeight)
        }
        canvas.save()
        canvas.scale(1f, -1f)
        val y_pos: Float = if (index == 0) {
            -rectText.bottom.toFloat()
        } else if (index == yAxisLabels.size - 1) {
            (rectText.height() - rectText.bottom).toFloat()
        } else {
            -rectText.exactCenterY()
        }
        //文字
        canvas.nativeCanvas.drawText(
            strTx,
            0F,
            y_pos,
            textPaint
        )
        canvas.restore()
    }
    canvas.restore()

    //==========================画y轴及分割线================================
    canvas.save()
    canvas.translate(maxTranslate.toFloat(), 0f)
    //竖线
    canvas.nativeCanvas.drawLine(0f, 0f, 0f, realHeight, linePaint)
    //短横线
    (yAxisLabels.indices).forEach { index ->
        //偏移
        if (index > 0) {
            canvas.translate(0f, intervalHeight)
        }
        //跳过最底下和最上面的短线绘制
        if (index != 0 && index != yAxisLabels.size - 1) {
            canvas.nativeCanvas.drawLine(0f, 0f, 8.dp.toPx(), 0f, linePaint)
        }
    }
    canvas.restore()

    return maxTranslate
}

private fun DrawScope.drawCubtoCircle(
    dataLinePaint: Paint,
    dataGradientPaint: Paint,
    dataList: List<Double>,
    canvas: Canvas,
    bottomInterval: Float,
    translate_x: Int,
    strokeWidth: Dp
) {
    val realWidth: Float = size.width - translate_x
    val canvasPath = Path_N()
    val danweiX = realWidth / dataList.size

    dataGradientPaint.strokeWidth = 2f
    val maxValue = Collections.max(dataList)
    val minValue = Collections.min(dataList)

    for (index in 0 until dataList.size - 1) {
        //从原点开始不停的把点连起来
        canvasPath.lineTo(
            index * danweiX,//x的坐标就是把index均匀放在x轴上
            ((size.height - bottomInterval - strokeWidth.toPx()) * (dataList[index] - minValue) / (maxValue - minValue)).toFloat()
            //这里的求y的坐标算法就是，算出（当前的值减去最小值）占（最大值减去最小值）的比例，用这个比例乘以总的高度
        )
        /*
                val xMoveDistance = 20
                val yMoveDistance = 40

                if (dataList[index] == dataList[index + 1]) {
                    caves_path.lineTo(danweiX * (index + 1), 0f)
                } else if (dataList[index] < dataList[index + 1]) {//y1<y2情况
                    val centerX = (realWidth * index + realWidth * (1 + index)) / 2
                    val centerY =
                        (dataList[index].toFloat() * danweiY + dataList[index + 1].toFloat() * danweiY) / 2
                    val controX0 = (realWidth * index + centerX) / 2
                    val controY0 = (dataList[index].toFloat() * danweiY + centerY) / 2
                    val controX1 = (centerX + realWidth * (1 + index)) / 2
                    val controY1 = (centerY + dataList[index + 1].toFloat() * danweiY) / 2
                    caves_path.cubicTo(
                        controX0 + xMoveDistance,
                        controY0 - yMoveDistance,
                        controX1 - xMoveDistance,
                        controY1 + yMoveDistance,
                        realWidth * (1 + index),
                        dataList[index + 1].toFloat() * danweiY
                    )
                } else {
                    val centerX = (realWidth * index + realWidth * (1 + index)) / 2
                    val centerY =
                        (dataList[index].toFloat() * danweiY + dataList[index + 1].toFloat() * danweiY) / 2
                    val controX0 = (realWidth * index + centerX) / 2
                    val controY0 = (dataList[index].toFloat() * danweiY + centerY) / 2
                    val controX1 = (centerX + realWidth * (1 + index)) / 2
                    val controY1 = (centerY + dataList[index + 1].toFloat() * danweiY) / 2
                    caves_path.cubicTo(
                        controX0 + xMoveDistance,
                        controY0 + yMoveDistance,
                        controX1 - xMoveDistance,
                        controY1 - yMoveDistance,
                        realWidth * (1 + index),
                        dataList[index + 1].toFloat() * danweiY
                    )

                }
        */
    }

    dataLinePaint.strokeWidth = 4f
    //绘制外环红色线
    canvas.nativeCanvas.drawPath(canvasPath, dataLinePaint)

    //绘制闭合渐变曲线
    canvasPath.lineTo(realWidth, 0f)
    canvasPath.lineTo(0f, 0f)
    canvas.nativeCanvas.drawPath(canvasPath, dataGradientPaint)


}