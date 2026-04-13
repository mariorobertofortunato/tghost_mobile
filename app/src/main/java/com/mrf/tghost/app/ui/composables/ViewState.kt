package com.mrf.tghost.app.ui.composables

sealed class ViewState {
    data object None : ViewState()
    data object Loading : ViewState()
    data object Success : ViewState()
    data class Error(
        @Transient val exception: Throwable? = Exception(),
        val code: Int? = null,
        val message: String? = null
    ) : ViewState()
}