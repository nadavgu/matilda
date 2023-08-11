package org.matilda.commands.exceptions

import javax.lang.model.element.Element

class AnnotationProcessingException(message: String, val element: Element) : RuntimeException(message)
