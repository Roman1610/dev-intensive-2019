package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1_000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

enum class TimeUnits {
    SECOND {
        override fun plural(value: Int): String {
            return plural(value = value, units = secondsPluralList)
        }
    },
    MINUTE {
        override fun plural(value: Int): String {
            return plural(value = value, units = minutesPluralList)
        }
    },
    HOUR {
        override fun plural(value: Int): String {
            return plural(value = value, units = hoursPluralList)
        }
    },
    DAY {
        override fun plural(value: Int): String {
            return plural(value = value, units = daysPluralList)
        }
    };

    abstract fun plural(value: Int): String

    val secondsPluralList = listOf("секунду", "секунды", "секунд")
    val minutesPluralList = listOf("минуту", "минуты", "минут")
    val hoursPluralList = listOf("час", "часа", "часов")
    val daysPluralList = listOf("день", "дня", "дней")

    fun plural(value: Int, units: List<String>): String {
        return when {
            value % 10 == 1 && value % 100 != 11 -> "$value ${units[0]}"
            value % 10 in 2 .. 4 -> "$value ${units[1]}"
            else -> "$value ${units[2]}"
        }
    }
}

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    return SimpleDateFormat(pattern, Locale("ru")).format(this)
}

fun Date.add(value: Int, units: TimeUnits): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val diff = date.time - this.time
    if (diff < 0) {
        return when (diff) {
            in -360 * DAY .. -26 * HOUR  -> "через ${TimeUnits.DAY.plural((-diff / DAY).toInt())}"
            in -26 * HOUR .. -22 * HOUR -> "через день"
            in -22 * HOUR .. -75 * MINUTE -> "через ${TimeUnits.HOUR.plural((-diff / HOUR).toInt())}"
            in -45 * MINUTE .. -75 * SECOND -> "через ${TimeUnits.MINUTE.plural((-diff / MINUTE).toInt())}"
            in -75 * SECOND .. -45 * SECOND -> "через минуту"
            in -45 * SECOND .. -SECOND -> "через несколько секунд"
            in -SECOND .. 0 -> "через несколько секунд"
            else -> "более чем через год"
        }
    } else {
        return when (diff) {
            in 0 .. SECOND -> "только что"
            in SECOND .. 45 * SECOND -> "несколько секунд назад"
            in 45 * SECOND .. 75 * SECOND -> "минуту назад"
            in 75 * SECOND .. 45 * MINUTE -> "${TimeUnits.MINUTE.plural((diff / MINUTE).toInt())} назад"
            in 75 * MINUTE .. 22 * HOUR -> "${TimeUnits.HOUR.plural((diff / HOUR).toInt())} назад"
            in 22 * HOUR .. 26 * HOUR -> "день назад"
            in 26 * HOUR .. 360 * DAY -> "${TimeUnits.DAY.plural((diff / DAY).toInt())} назад"
            else -> "более года назад"
        }
    }

}