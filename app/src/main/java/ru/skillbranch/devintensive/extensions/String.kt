package ru.skillbranch.devintensive.extensions

fun String.truncate(amount: Int = 16): String {
    if (amount < length) {
        var truncated = substring(0, amount).trim()
        truncated += "..."
        return truncated
    }
    return this.trim()
}

fun String.stripHtml(): String {
    return replace(regex = Regex("<.*?>"), replacement = "")
        .replace(Regex("[ ]{2,}"), replacement = " ")
        .replace(Regex("[&<>\'\"]+"), replacement = "")
}