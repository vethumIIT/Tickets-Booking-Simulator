package com.app.ticket_booking_simulator.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogManager {
    private static List<String> logs = new ArrayList<>();// list containing all the logs

    private static String filename = "DataFiles/Logs.txt";

    public static List<String> getLogs() {
        return logs;
    }

    public static void clearLogs() {
        LogManager.logs = new ArrayList<>();
        clearLogFile();

    }

    public static void log(String text){
        logs.add(text);
        System.out.println(text);
    }

    public static void writeToFile(){ // writes a log to log file
        try(FileWriter writer = new FileWriter(filename)){
            for(String log: getLogs()) {
                writer.write(log + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearLogFile(){// creates or clears log file
        try(FileWriter writer = new FileWriter(filename)){
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
