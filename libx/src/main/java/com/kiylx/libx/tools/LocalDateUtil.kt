package com.kiylx.libx.tools

import java.time.format.DateTimeFormatter

object LocalDateUtil {
    val ymdhmsFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val ymdFormatter =DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val mdFormatter =DateTimeFormatter.ofPattern("MM月dd")



}