package com.mrf.tghost.app.utils.enums

sealed class PopupType {
    object Error : PopupType()
    data class Warning(val type: WarningType) : PopupType()
}

enum class WarningType {
    DeleteTransaction,
    DeleteWallet
}