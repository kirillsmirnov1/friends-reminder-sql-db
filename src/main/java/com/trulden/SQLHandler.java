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

    boolean changeFieldValue(TableName tableName, int id, String fieldName, String newValue){

        String sql =
                "UPDATE " + tableName.toString() + " " +
                "SET "+ fieldName +" = '" + newValue + "' " +
                "WHERE id = " + id + ";";

        return executeUpdate(sql);
    }

    private boolean executeUpdate(String sql){
        try (Connection conn = DriverManager.getConnection(databaseURL);
             PreparedStatement updateStatement = conn.prepareStatement(sql)) {

            updateStatement.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    int getIdByField(TableName tableName, String fieldName, String fieldValue){

        String selectStatement =
                "SELECT id FROM " + tableName.toString() +
                " WHERE " + fieldName + " = '" + fieldValue + "'";

        try (Connection conn = DriverManager.getConnection(databaseURL);
             PreparedStatement select = conn.prepareStatement(selectStatement)) {

            ResultSet rs = select.executeQuery();
            rs.next();
            int id = rs.getInt("id");

            return id;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return -1;
    }

    String[] listTable(TableName tableName){
        ArrayList<String> result = new ArrayList<>();
        String selectStatementStr;

        switch (tableName){
            case PERSONS:
                selectStatementStr = "SELECT name FROM " + PERSONS.toString() + ";";
                break;
            default:
                throw new IllegalArgumentException(tableName.toString() + " is not supported yet");
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
                        throw new IllegalArgumentException(tableName.toString() + " is not supported yet");
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
