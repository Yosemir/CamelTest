package com.telefonica.camel.component;

import com.telefonica.camel.common.DecimalFormat;
import com.telefonica.camel.exception.InvalidValueException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public abstract class LoadMapperValidator {

    protected Double toDouble(String value, DecimalFormat format) {
	if (value == null || value.isEmpty()) {
	    return null;
	}
	try {
	    if (format == DecimalFormat.POINT) {
		return Double.valueOf(value.replace(",", ""));
	    }
	    return Double.valueOf(value.replace(",", "."));
	} catch (NumberFormatException e) {
	    throw new InvalidValueException(value, e);
	}
    }

    protected Integer toInteger(String value) {
	try {
	    return value != null && !value.isEmpty() ? Integer.valueOf(value) : null;
	} catch (NumberFormatException e) {
	    throw new InvalidValueException(value, e);
	}
    }

    protected Long toLong(String value) {
	try {
	    return value != null && !value.isEmpty() ? Long.valueOf(value) : null;
	} catch (NumberFormatException e) {
	    throw new InvalidValueException(value, e);
	}
    }

    protected LocalDate toLocalDate(String value, String format) {
	try {
	    return value != null && !value.isEmpty() ? LocalDate.parse(value, DateTimeFormatter.ofPattern(format)) : null;
	} catch (DateTimeParseException e) {
	    throw new InvalidValueException(value, e);
	}
    }

    protected LocalDateTime toLocalDateTime(String value, String format) {
	try {
	    return value != null && !value.isEmpty() ? LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format)) : null;
	} catch (DateTimeParseException e) {
	    throw new InvalidValueException(value, e);
	}
    }

    protected String validPkNotNull(String value) {
	if (value == null || value.isEmpty()) {
	    throw new InvalidValueException("primary key value can't be null");
	}
	return value;
    }

}
