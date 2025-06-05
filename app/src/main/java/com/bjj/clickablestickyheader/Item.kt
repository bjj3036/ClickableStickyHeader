package com.bjj.clickablestickyheader

sealed interface Item {
    val text: String

    data class Header(override val text: String) : Item
    data class Content(override val text: String) : Item
}