package ru.skillbranch.devintensive.extensions

import android.content.res.Resources

fun Int.dp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.sp(): Int = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()

