package com.wile.app.model

class ErrorModels {
    data class Error(
        val message: String
    ) : WileMessage
}