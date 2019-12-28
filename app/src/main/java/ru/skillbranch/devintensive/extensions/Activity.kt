package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    if (imm != null) {
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
        imm = null
    }
}

fun Activity.showKeyboard() {
    var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    if (imm != null) {
        imm.showSoftInput(currentFocus, InputMethodManager.SHOW_IMPLICIT)
        imm = null
    }
}

fun Activity.isKeyboardOpen(): Boolean {
    val r = Rect()
    window.decorView.rootView.getWindowVisibleDisplayFrame(r)
    val screenHeight = window.decorView.rootView.height

    val keypadHeight = screenHeight - r.bottom

    return keypadHeight > screenHeight * 0.15
}

fun Activity.isKeyboardClosed(): Boolean {
    val r = Rect()
    window.decorView.rootView.getWindowVisibleDisplayFrame(r)
    val screenHeight = window.decorView.rootView.height

    val keypadHeight = screenHeight - r.bottom

    return keypadHeight <= screenHeight * 0.15
}