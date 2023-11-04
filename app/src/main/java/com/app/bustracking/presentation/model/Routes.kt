package com.app.bustracking.presentation.model

import java.io.Serializable

data class Routes(
    val icon: Int,
    val header: String,
    val msgIcon: Int,
    val msg: String,
): Serializable
