package zero.network.petgarden.util

operator fun String.times(times: Int) = StringBuilder().apply {
    repeat(times){ append(this@times) }
}.toString()