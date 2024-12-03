package com.app.ticket_booking_simulator.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogManager {
    private static List<String> logs = new ArrayList<>();

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
        writeToFile(text);
        System.out.println(text);
    }

    public static void writeToFile(String log){
        try(FileWriter writer = new FileWriter(filename, true)){
            writer.write(log+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearLogFile(){
        try(FileWriter writer = new FileWriter(filename)){
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
