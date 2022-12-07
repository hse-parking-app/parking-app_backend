/*
 * Copyright (c) 2021-2021.
 * Written by Maksim Stepanenko <stepanenko-qa@yandex.ru>
 */

package org.example.coursework.error;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private int statusCode;
    private Date timestamp;
    private String message;
    private String description;
}
