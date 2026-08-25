package org.robojackets.apiary.base.ui.nfc

sealed interface BuzzCardTapSource {
    data object Nfc : BuzzCardTapSource
    data object Keyboard : BuzzCardTapSource
    data class Mrd5(val source: String) : BuzzCardTapSource {
        override fun toString() = "MRD5 - $source"
    }
    data object Debug : BuzzCardTapSource
}
