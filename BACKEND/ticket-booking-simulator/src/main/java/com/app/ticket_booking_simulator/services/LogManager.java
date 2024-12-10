package com.app.ticket_booking_simulator.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that manages the logs.
 */
public class LogManager {
    private static List<String> logs = new ArrayList<>();// list containing all the logs

    private static String filename = "DataFiles/Logs.txt";

    /**
     *
     * @return The logs in a List
     */
    public static List<String> getLogs() {
        return logs;
    }

    public static void clearLogs() {
        LogManager.logs = new ArrayList<>();
        clearLogFile();

    }

    /**
     * Adds the log to a List and prints the log to the screen.
     * @param text - log text
     */
    public static void log(String text){
        logs.add(text);
        System.out.println(text);
    }

    /**
     * Writes the contents of the logs list to a file.
     */
    public static void writeToFile(){ // writes a log to log file
        try(FileWriter writer = new FileWriter(filename)){
            for(String log: getLogs()) {
                writer.write(log + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * clears the logs list.
     */
    public static void clearLogFile(){// creates or clears log file
        try(FileWriter writer = new FileWriter(filename)){
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
