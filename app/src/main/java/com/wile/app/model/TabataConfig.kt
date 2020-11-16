package com.wile.app.model

data class TabataConfig(
    var cycles: Int = TRAINING_DEFAULT_TABATA_REPEAT,
    var mainDuration: Int = TRAINING_DEFAULT_TABATA_1_DURATION,
    var alterDuration: Int = TRAINING_DEFAULT_TABATA_2_DURATION,
    var mainName: String = "",
    var alterName: String = ""
)

const val TRAINING_DEFAULT_TABATA_1_DURATION = 20
const val TRAINING_DEFAULT_TABATA_2_DURATION = 10
const val TRAINING_DEFAULT_TABATA_REPEAT = 8