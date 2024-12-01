package com.app.ticket_booking_simulator;

import java.util.ArrayList;
import java.util.List;

public class LogManager {
    private static List<String> logs = new ArrayList<>();

    public static List<String> getLogs() {
        return logs;
    }

    public static void clearLogs() {
        LogManager.logs = new ArrayList<>();
    }

    public static void log(String text){
        logs.add(text);
        System.out.println(text);
    }
}
