package org.example.businessLogic;

import org.example.businessLogic.records.Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class AnalyzerLogicTest {

    @Test
    void processInputDataHappyTest() throws IOException {
        String input = "4\n" +
                "C 1.1 8.15.1 P 15.10.2012 83\n" +
                "C 1 10.1 P 01.12.2012 65\n" +
                "C 1 10.1 N 01.12.2012 65\n" +
                "D 1.1 8 P 01.01.2012-01.12.2012\n";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        int numLines = Integer.parseInt(reader.readLine().trim());
        Map<String, List<Service>> map = new HashMap<>();
        AnalyzerLogic.processInputData(reader, numLines, map);
        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals(2, map.get("P").size());
        Assertions.assertEquals(1, map.get("N").size());
    }

    @Test
    void processInputDataUnHappyTest() throws IOException {
        String input = "4\n" +
                "C 15.1 8.15.1 P 15.10.2012 83\n" + // Bad Service number
                "C 1 10.1 P 01.12.2012 65\n" +
                "C 1 10.1 N 01.12.2012 65\n" +
                "D 1.1 8 P 01.01.2012-01.12.2012\n";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        int numLines = Integer.parseInt(reader.readLine().trim());
        Map<String, List<Service>> map = new HashMap<>();
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            AnalyzerLogic.processInputData(reader, numLines, map);
        }, "Service must be in range from 1 to 10. Value: \"15\" is incorrect");
    }

    @Test
    void createServiceRecordTest() {
        String input = "C 1 10.1 P 01.12.2012 65";
        String[] parts = input.split(" ");
        Map<String, List<Service>> map = new HashMap<>();
        AnalyzerLogic.createServiceRecord(parts, map);

        Service service = map.get("P").get(0);
        Assertions.assertEquals("1", service.service());
        Assertions.assertEquals("10.1", service.questionType());
        Assertions.assertEquals("P", service.response());
        Assertions.assertEquals(LocalDate.of(2012, 12, 1), service.date());
        Assertions.assertEquals(65, service.time());
    }

    @Test
    void calculateAverageWaitingTimeTest0() {
        String input = "C 1 10.1 P 01.11.2012 65";
        String[] parts = input.split(" ");
        Map<String, List<Service>> map = new HashMap<>();
        AnalyzerLogic.createServiceRecord(parts, map);

        String d = "D 1 * P 8.10.2012-20.11.2012";
        String[] dParts = d.split(" ");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        AnalyzerLogic.calculateAverageWaitingTime(dParts, map);
        String consoleOutput = outputStream.toString();

        Assertions.assertEquals(consoleOutput, "65\r\n");
    }

    @Test
    void calculateAverageWaitingTimeTest1() {
        String input = "C 3 10.1 P 01.11.2012 65";
        String[] parts = input.split(" ");
        Map<String, List<Service>> map = new HashMap<>();
        AnalyzerLogic.createServiceRecord(parts, map);

        String d = "D 1 * P 8.10.2012-20.11.2012";
        String[] dParts = d.split(" ");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        AnalyzerLogic.calculateAverageWaitingTime(dParts, map);
        String consoleOutput = outputStream.toString();

        Assertions.assertEquals(consoleOutput, "-\r\n");
    }

    @Test
    void calculateAverageWaitingTimeUnhappyTest() {
        String input = "C 1 10.1 P 01.10.2012 65";
        String[] parts = input.split(" ");
        Map<String, List<Service>> map = new HashMap<>();
        AnalyzerLogic.createServiceRecord(parts, map);

        String d = "D 1 * P 8.10.2012-20.11.2012";
        String[] dParts = d.split(" ");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        AnalyzerLogic.calculateAverageWaitingTime(dParts, map);
        String consoleOutput = outputStream.toString();

        Assertions.assertNotEquals(consoleOutput, "65\r\n");
    }

    @Test
    void matchesHappyTest() {
        String input = "C 1 10.1 P 01.12.2012 65";
        String[] parts = input.split(" ");
        Map<String, List<Service>> map = new HashMap<>();
        AnalyzerLogic.createServiceRecord(parts, map);
        Service serviceRecord = map.get("P").get(0);

        LocalDate date = LocalDate.of(2012, 12, 1);
        Assertions.assertTrue(AnalyzerLogic.matches(serviceRecord, "1", "10", date, date));
    }

    @Test
    void matchesUnHappyTest() {
        String input = "C 1 10.1 P 01.12.2012 65";
        String[] parts = input.split(" ");
        Map<String, List<Service>> map = new HashMap<>();
        AnalyzerLogic.createServiceRecord(parts, map);
        Service serviceRecord = map.get("P").get(0);

        LocalDate date = LocalDate.of(2012, 12, 1);
        Assertions.assertFalse(AnalyzerLogic.matches(serviceRecord, "1", "5", date, date));
    }

    @Test
    void getAverageWaitingTimeHappyTest() throws IOException {
        String input = "3\n" +
                "C 1.1 8.15.1 P 15.10.2012 83\n" +
                "C 1.1 8.2 P 01.12.2012 65\n" +
                "C 1 10.1 N 01.12.2012 65\n";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        int numLines = Integer.parseInt(reader.readLine().trim());
        Map<String, List<Service>> map = new HashMap<>();
        AnalyzerLogic.processInputData(reader, numLines, map);
        LocalDate dateFrom = LocalDate.of(2012, 10, 1);
        LocalDate dateTo = LocalDate.of(2012, 12, 12);

        Assertions.assertEquals(AnalyzerLogic.getAverageWaitingTime(map, "1.1", "8", "P", dateFrom, dateTo), 74);
    }

    @Test
    void getAverageWaitingTimeUnHappyTest() throws IOException {
        String input = "3\n" +
                "C 1.1 8.15.1 P 15.10.2012 83\n" +
                "C 1.1 8.2 P 01.12.2012 65\n" +
                "C 1 10.1 N 01.12.2012 65\n";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        int numLines = Integer.parseInt(reader.readLine().trim());
        Map<String, List<Service>> map = new HashMap<>();
        AnalyzerLogic.processInputData(reader, numLines, map);
        LocalDate dateFrom = LocalDate.of(2012, 10, 1);
        LocalDate dateTo = LocalDate.of(2012, 12, 12);

        Assertions.assertNotEquals(AnalyzerLogic.getAverageWaitingTime(map, "1.1", "10", "P", dateFrom, dateTo), 74);
    }
}