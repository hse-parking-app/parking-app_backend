/*
 * Copyright (c) 2021.
 * Written by Maksim Stepanenko <stepanenko-qa@yandex.ru>
 */

package org.example.coursework.error;

public class EngagedException extends RuntimeException {
    public EngagedException(String message) {
        super(message);
    }
}
