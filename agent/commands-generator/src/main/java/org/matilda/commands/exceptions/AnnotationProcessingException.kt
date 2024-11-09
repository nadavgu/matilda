package org.matilda.commands.exceptions

import androidx.room.compiler.processing.XElement

class AnnotationProcessingException(message: String, val element: XElement) : RuntimeException(message)
