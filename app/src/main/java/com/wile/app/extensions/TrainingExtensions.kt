package com.wile.app.extensions

import com.wile.database.model.Training

fun List<Training>.duration() = map { training -> training.duration }.sum()
