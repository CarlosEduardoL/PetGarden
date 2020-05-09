package zero.network.petgarden.tools

private const val normal = "\\e[0m"
private const val gren = "\\e[32m"
private const val red = "\\e[31m"

fun logError(message: String) = println("${red}Error: $message$normal")
fun logMessage(message: String) = println("${gren}Log: $message$normal")
