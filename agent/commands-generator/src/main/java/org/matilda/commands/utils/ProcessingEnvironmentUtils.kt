package org.matilda.commands.utils

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XProcessingEnv

@OptIn(ExperimentalProcessingApi::class)
fun XProcessingEnv.option(option: String) =
    options[option] ?: throw IllegalArgumentException("Option $option not passed to annotation processor")
