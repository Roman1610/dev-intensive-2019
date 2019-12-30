package ru.skillbranch.devintensive.extensions

fun CharSequence.containsOneOf(list: List<String>): Boolean {
    for (value in list) {
        if (contains(value)) {
            return true
        }
    }

    return false
}