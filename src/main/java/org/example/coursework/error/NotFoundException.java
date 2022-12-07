/*
 * Copyright (c) 2021.
 * Written by Maksim Stepanenko <stepanenko-qa@yandex.ru>
 */

package org.example.coursework.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
