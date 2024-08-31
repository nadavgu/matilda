package org.matilda.commands.utils

import java.lang.IllegalArgumentException
import javax.annotation.processing.ProcessingEnvironment

fun ProcessingEnvironment.option(option: String) =
    options[option] ?: throw IllegalArgumentException("Option $option not passed to annotation processor")
