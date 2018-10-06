package com.gcr.acm.common.utils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import javax.validation.ValidationException;

import org.springframework.util.CollectionUtils;

/**
 * Generic utility methods for simple validations
 * @author Razvan Dani.
 */
public class ValidationUtils {

    public static void validateRequiredObject(Object object, String fieldName) {
        validateRequiredObject(object, fieldName, -1);
    }

    public static void validateRequiredObject(Object object, String fieldName, int maxLength) {
        validateRequiredObject(object, fieldName, -1, maxLength);
    }

    public static void validateRequiredObject(Object object, String fieldName, int minLength, int maxLength) {
        if (!isObjectSet(object)) {
            throw new ValidationException(fieldName + " required");
        }
        if (object instanceof String) {
            validateMinLength((String) object, fieldName, minLength);
            validateMaxLength((String) object, fieldName, maxLength);
        }
    }

    private static boolean isObjectSet(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof String) {
            String stringValue = (String) object;

            if (stringValue.isEmpty()) {
                return false;
            }
        }

        if (object instanceof Collection) {
            Collection collection = (Collection) object;

            if (collection.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static void validateRequiredCollection(Collection collection, String fieldName) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new ValidationException(fieldName + " required");
        }
    }

    public static void validateMutuallyExclusiveObjects(Object object1, String fieldName1, Object object2, String fieldName2) {
        if (!isObjectSet(object1) && !isObjectSet(object2)) {
            throw new ValidationException(fieldName1 + " or" + fieldName2 + " are required");
        }

        if (isObjectSet(object1) && isObjectSet(object2)) {
            throw new ValidationException(fieldName1 + " or" + fieldName2 + " cannot be specified at the same time");
        }
    }

    public static void validateBothOrNoneObjectsRequired(Object object1, String fieldName1, Object object2,
                                                       String fieldName2) {
        if (!isObjectSet(object1) && isObjectSet(object2) || isObjectSet(object1) && !isObjectSet(object2)) {
            throw new ValidationException("Both or none: " + fieldName1 + " and" + fieldName2 + " required");
        }
    }

    public static void validateMinLength(String fieldValue, String fieldName, int minLength) {
        if (minLength > 0 && fieldValue.length() < minLength) {
            throw new ValidationException(fieldName + " min length is " + minLength);
        }
    }

    public static void validateMaxLength(String fieldValue, String fieldName, int maxLength) {
        if (fieldValue != null && maxLength > 0 && fieldValue.length() > maxLength) {
            throw new ValidationException(fieldName + " max length is " + maxLength);
        }
    }

    public static void validateRangeLength(String fieldValue, String fieldName, int minLength, int maxLength) {
        validateMinLength(fieldValue, fieldName, minLength);
        validateMaxLength(fieldValue, fieldName, maxLength);
    }

    public static void validateNotDesiredObject(Object object, String fieldName) {
        if (object != null) {
            throw new ValidationException(fieldName + " should NOT be provided on save");
        }
    }

    public static String message(String message, Object... args) {
        String msg;
        try {
            msg = new MessageFormat(message).format(args);
        } catch (IllegalArgumentException e) {
            return message;
        }
        return msg;
    }

    public static void validateValueBetweenMinMax(String fileName, BigDecimal value, BigDecimal min, BigDecimal max){
        if(value != null && ( value.compareTo(min) < 0 || value.compareTo(max) > 0 )){
            throw new ValidationException( fileName + " should be between " + min + " and " + max);
        }
    }

    public static void validateMaxValue(Integer value,String fileName ,Integer max) {
        if (value != null && value.compareTo(max) > 0) {
            throw new ValidationException(fileName + " min value is " + max);
        }
    }

    public static void validateAtLeastOneIsSet(List attributeValueList, String errorMessage) {
        boolean atLeastOneIsSet = false;

        for (Object attributeValue : attributeValueList) {
            if (isObjectSet(attributeValue)) {
                atLeastOneIsSet = true;
                break;
            }
        }

        if (!atLeastOneIsSet) {
            throw new ValidationException(errorMessage);
        }
    }
}
