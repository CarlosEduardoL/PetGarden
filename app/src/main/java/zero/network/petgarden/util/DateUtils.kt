package zero.network.petgarden.util

import java.util.*

fun Date.endOfDay() = Calendar.getInstance().apply {
    time = this@endOfDay
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}.time

fun Date.startOfDay() = Calendar.getInstance().apply {
    time = this@startOfDay
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.time

fun monthToText(month: Int) = when(month){
    1 -> "Enero"
    2 -> "Febrero"
    3 -> "Marzo"
    4 -> "Abril"
    5 -> "Mayo"
    6 -> "Junio"
    7 -> "Julio"
    8 -> "Agosto"
    9 -> "Septiembre"
    10 -> "Obtubre"
    11 -> "Noviembre"
    12 -> "Diciembre"
    else -> throw Exception("Internal date error")
}