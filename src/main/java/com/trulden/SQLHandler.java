package com.trulden;

import java.sql.*;

import static com.trulden.TableName.PERSONS;

class SQLHandler {

    private String databaseURL;

    SQLHandler(String dbName){
        databaseURL = "jdbc:sqlite:db\\" + dbName;
    }

    void initDB() {
        connectToDatabase();

        String createPersons =
                "CREATE TABLE IF NOT EXISTS " + PERSONS.toString() + " (\n" +
                        " id integer PRIMARY KEY,\n" +
                        " name text NOT NULL\n" +
                        ");";

        createTable(createPersons);
    }

    private void createTable(String sqlCreateStatement) {
        try (Connection conn = DriverManager.getConnection(databaseURL);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sqlCreateStatement);
            System.out.println("TableName created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void connectToDatabase(){
        try(Connection conn = DriverManager.getConnection(databaseURL)){
            if(conn != null){
                System.out.println("Successfully connected to «" + databaseURL + "» database");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    String getDatabaseURL() {
        return databaseURL;
    }
}
