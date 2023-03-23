// This code implements a WebhostingAnalyzer class that reads input data from the command line,
// stores the data in a Map, and then performs calculations based on the stored data.
package org.example.businessLogic;

import org.example.businessLogic.records.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyzerLogic {

    public static void startApp() throws IOException {
        // Create a BufferedReader object to read input from the command line
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Read the number of input lines to be processed

        int numLines = getNumLines(reader);

        // Create a Map to store the service data
        Map<String, List<Service>> serviceMap = new HashMap<>();

        // Call processInputData function to process the input data and store it in the Map
        processInputData(reader, numLines, serviceMap);
    }

    //this need to ensure that the first line is digit format
    private static int getNumLines(BufferedReader reader) throws IOException {
        int numLines = 0;
        boolean isNumberFormatException = true;
        while (isNumberFormatException) {
            try {
                numLines = Integer.parseInt(reader.readLine().trim());
                isNumberFormatException = false; // Exit the loop if successful
            } catch (NumberFormatException e) {
                System.out.println(e+". Enter a digit");
            }
        }
        return numLines;
    }

    // Function to process the input data and store it in the Map
    protected static void processInputData(BufferedReader reader, int numLines, Map<String, List<Service>> serviceMap) throws IOException {
        for (int i = 0; i < numLines; i++) {
            // Read the input line
            String line = reader.readLine();
            String[] parts = line.split(" ");
            String type = parts[0];

            // If the input line is of type "C", create a service record and add it to the Map
            if (type.equals("C")) { // Processing Waiting TimeLine
                createServiceRecord(parts, serviceMap);
            }
            // If the input line is of type "D", calculate the average waiting time and print it to the console
            else if (type.equals("D")) {// Processing Query line
                calculateAverageWaitingTime(parts, serviceMap);
            }
        }
    }

    // Function to create a service record and add it to the Map
    protected static void createServiceRecord(String[] parts, Map<String, List<Service>> serviceMap) {
        String service = parts[1];
        String questionType = parts[2];
        String responseType = parts[3];
        LocalDate date = LocalDate.parse(parts[4], DateTimeFormatter.ofPattern("d.MM.yyyy"));
        int time = Integer.parseInt(parts[5]);
        Service serviceRecord = new Service(service, questionType, responseType, date, time);
        List<Service> list = serviceMap.computeIfAbsent(responseType, k -> new ArrayList<>());
        list.add(serviceRecord);
    }

    // Function to calculate the average waiting time and print it to the console
    protected static void calculateAverageWaitingTime(String[] parts, Map<String, List<Service>> serviceMap) {
        String service = parts[1];
        String questionType = parts[2];
        String responseType = parts[3];
        String[] dateRangeParts = parts[4].split("-");

        LocalDate fromDate = LocalDate.parse(dateRangeParts[0], DateTimeFormatter.ofPattern("d.MM.yyyy"));
        LocalDate toDate = dateRangeParts.length > 1 ? LocalDate.parse(dateRangeParts[1], DateTimeFormatter.ofPattern("d.MM.yyyy")) : fromDate;
        int average = getAverageWaitingTime(serviceMap, service, questionType, responseType, fromDate, toDate);
        System.out.println(average >= 0 ? Math.round(average) : "-");
    }

    //Compare criteria of Service QuestionType and Date
    protected static boolean matches(Service record, String service, String questionType, LocalDate fromDate, LocalDate toDate) {

        String recordService = record.service();
        String recordQuestionType = record.questionType();
        LocalDate recordDate = record.date();

        if (!("*".equals(service) ||
                (recordService.length() > service.length() && recordService.startsWith(service)) ||
                (recordService.length() == service.length() && recordService.equals(service)))) {
            return false;
        }
        if (!("*".equals(questionType) ||
                (recordQuestionType.length() > questionType.length() && recordQuestionType.startsWith(questionType)) ||
                (recordQuestionType.length() == questionType.length() && recordQuestionType.equals(questionType)))) {
            return false;
        }

        if (recordDate.isBefore(fromDate) || recordDate.isAfter(toDate)) {
            return false;
        }
        return true;
    }

    //Calculating Average Waiting Time
    protected static int getAverageWaitingTime(Map<String, List<Service>> records, String service, String questionType, String responseType, LocalDate fromDate, LocalDate toDate) {
        List<Service> list = records.get(responseType);

        if (list == null) {
            return -1;
        }
        int total = 0;
        int count = 0;

        for (Service record : list) {
            if (matches(record, service, questionType, fromDate, toDate)) {
                total += record.time();
                count++;
            }
        }
        return count > 0 ? total / count : -1;
    }

}
