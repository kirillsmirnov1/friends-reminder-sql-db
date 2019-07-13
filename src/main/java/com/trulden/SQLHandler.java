package com.trulden;

import java.sql.*;
import java.util.ArrayList;

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

    String[] listTable(TableName tableName){
        ArrayList<String> result = new ArrayList<>();
        String selectStatementStr;

        switch (tableName){
            case PERSONS:
                selectStatementStr = "SELECT name FROM " + PERSONS.toString() + ";";
                break;
            default:
                throw new IllegalArgumentException();
        }

        try(Connection conn = DriverManager.getConnection(databaseURL);
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(selectStatementStr)){

            while(rs.next()){
                switch (tableName){
                    case PERSONS:
                        result.add(" " + rs.getString("name"));
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return result.toArray(new String[0]);
    }

    private void createTable(String sqlCreateStatement) {
        try (Connection conn = DriverManager.getConnection(databaseURL);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sqlCreateStatement);
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
