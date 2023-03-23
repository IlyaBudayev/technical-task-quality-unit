package org.example.businessLogic.records;

import java.time.LocalDate;
import java.util.Optional;

public record Service(String service, String questionType, String response, LocalDate date, int time) {

    private static final int MIN_VALUE = 1;
    private static final int MAX_SERVICE = 10;
    private static final int MAX_SERVICE_VARIATION = 3;
    private static final int MAX_QUESTION_TYPE = 10;
    private static final int MAX_QUESTION_CATEGORIES = 20;
    private static final int MAX_QUESTION_SUBCATEGORIES = 5;
    // Defines a record class Service with five fields: service, questionType, response, date and time
    public Service {

        // split the service string into parts (e.g. "1.2" -> ["1", "2"])
        String[] serviceParts = service.split("\\.");
        String serviceVar = serviceParts[0];
        int serviceInt = Integer.parseInt(serviceVar);
        String serviceVariation = serviceParts.length > 1 ? serviceParts[1] : null;

        validateInputValue(serviceInt, MIN_VALUE, MAX_SERVICE, "Service"); // Service


        validateInputValueForNullable(serviceVariation, MAX_SERVICE_VARIATION, "Service Variation");// Service Variation


        String[] questionPart = questionType.split("\\.");
        String questionTypeVar = questionPart[0];
        int questionTypeInt = Integer.parseInt(questionTypeVar);
        String questionCategory = questionPart.length > 1 ? questionPart[1] : null;
        String questionSubCategory = questionPart.length > 2 ? questionPart[2] : null;

        validateInputValue(questionTypeInt, MIN_VALUE, MAX_QUESTION_TYPE, "Question Type"); // Question Type

        validateInputValueForNullable(questionCategory, MAX_QUESTION_CATEGORIES, "Question Category");//Question Category
        validateInputValueForNullable(questionSubCategory, MAX_QUESTION_SUBCATEGORIES, "Question Subcategory");//Question Subcategory
    }

    private static void validateInputValueForNullable(String serviceVariation, int maxServiceVariation, String serviceVariation1) {
        Optional.ofNullable(serviceVariation)
                .map(Integer::parseInt)
                .ifPresent(serviceVariationInt ->
                        validateInputValue(serviceVariationInt, Service.MIN_VALUE, maxServiceVariation, serviceVariation1));
    }

    //Validate to ensure that the line is according to number limit
    private static void validateInputValue(int inputNumber, int min, int max, String instance) {
        if (inputNumber < min || inputNumber > max) {
            throw new IllegalArgumentException(instance + " must be in range from " + min + " to " + max + ". Value: \"" + inputNumber + "\" is incorrect");
        }
    }
}
