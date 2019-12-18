package com.arctouch.codechallenge.util

import android.app.Activity
import android.view.inputmethod.InputMethodManager

fun Activity.getInputMethodManager(): InputMethodManager {
   return (getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager)
}