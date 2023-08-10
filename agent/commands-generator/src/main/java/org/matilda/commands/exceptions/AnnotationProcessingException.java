package org.matilda.commands.exceptions;

import javax.lang.model.element.Element;

public class AnnotationProcessingException extends RuntimeException {
    public final Element element;

    public AnnotationProcessingException(String message, Element element) {
        super(message);
        this.element = element;
    }
}
