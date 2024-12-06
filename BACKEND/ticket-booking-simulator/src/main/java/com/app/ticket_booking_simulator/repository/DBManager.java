package com.app.ticket_booking_simulator.repository;

/*
 *
 * The xerial dependency from https://github.com/xerial/sqlite-jdbc was used
 * for the SQLite database management.
 *
 * */

import com.app.ticket_booking_simulator.services.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteErrorCode;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager {
    private static final Logger logger = LoggerFactory.getLogger(DBManager.class);
    private String url = "jdbc:sqlite:DataFiles/database.db";

    public DBManager(){}

    public DBManager(String url){
        this.url=url;
    }

    public String writeDatabase(String SQLCommand, List<Object> parameters){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(this.url);

            PreparedStatement stmt = c.prepareStatement(SQLCommand);

            if (parameters!=null) {
                for (int p = 1; p <= parameters.size(); p++) {

                    Object param = parameters.get(p - 1);

                    if (param instanceof Long) {
                        stmt.setLong(p, (Long) param);
                    } else if (param instanceof String) {
                        stmt.setString(p, (String) param.toString());
                    } else if (param instanceof Integer) {
                        stmt.setInt(p, (Integer) param);
                    } else if (param instanceof Double) {
                        stmt.setDouble(p, (Double) param);
                    }
                }
            }
            stmt.executeUpdate();
            stmt.close();
            c.close();

            return "Success";
        }catch (SQLException e){
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code){
                logger.error(e.getMessage());
                return "Exists";
            }else {
                logger.error("Error: {}",e.getMessage());
                throw new RuntimeException(e.getMessage());
                //return "Error";
            }
        }
        catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return "Error";
        }
    }

    public List<Map<String,Object>> readDatabase(String SQLCommand, List<Object> parameters){
        List<Map<String, Object>> resultList = new ArrayList<>();
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(this.url);

            PreparedStatement stmt = c.prepareStatement(SQLCommand);

            if (parameters!=null) {
                for (int p = 1; p <= parameters.size(); p++) {

                    Object param = parameters.get(p - 1);

                    if (param instanceof Long) {
                        stmt.setLong(p, (Long) param);
                    } else if (param instanceof String) {
                        stmt.setString(p, (String) param);
                    } else if (param instanceof Integer) {
                        stmt.setInt(p, (Integer) param);
                    } else if (param instanceof Double) {
                        stmt.setDouble(p, (Double) param);
                    }
                }
            }

            ResultSet rs = stmt.executeQuery();

            ResultSetMetaData rsMetaData = rs.getMetaData();
            int column_count = rsMetaData.getColumnCount();

            while (rs.next()){

                Map<String, Object> rowMap = new HashMap<>();

                for (int i=1; i<=column_count; i++){

                    String columnName = rsMetaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);

                    rowMap.put(columnName, columnValue);
                }
                resultList.add(rowMap);

            }
            if(resultList.isEmpty()){
                return null;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return resultList;
    }

    public void setup(){
        Connection c;
        Statement stmt;

        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(this.url);
            LogManager.log("Database Opened successfully!");

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS tickets (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    vendor_id INTEGER, \n" +
                    "    customer_id INTEGER, \n" +
                    "    isAvailable BOOLEAN \n" +
                    ");\n";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
            LogManager.log("Table Created Successfully!");

            this.writeDatabase("DELETE FROM tickets",null);

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

    }
}
