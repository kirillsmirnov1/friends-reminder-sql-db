package com.trulden;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

import static com.trulden.TableName.*;

class SQLHandler {

    private String databaseURL;

    SQLHandler(String dbName){
        databaseURL = "jdbc:sqlite:db\\" + dbName;
    }

    void initDB() {
        connectToDatabase();

        createTable("CREATE TABLE IF NOT EXISTS " +
                PERSONS.toString() + " (\n" +
                " id integer PRIMARY KEY,\n" +
                " name text NOT NULL\n" +
                ");");

        // FIXME встречи и переписки добавлять по умолчанию? При первом старте?
        createTable("CREATE TABLE IF NOT EXISTS " +
                INTERACTION_TYPES.toString() + "(\n" +
                " id integer PRIMARY KEY,\n" +
                " typeName text NOT NULL\n" +
                ");");

        createTable("CREATE TABLE IF NOT EXISTS " +
                INTERACTIONS.toString() + "(\n" +
                " id integer PRIMARY KEY,\n" +
                " typeId integer NOT NULL,\n" +
                " date text NOT NULL,\n" +
                " comment text NOT NULL\n" +
                ");");

        // FIXME не нравится название. Мб связи?
        createTable("CREATE TABLE IF NOT EXISTS " +
                PERSON_INTERACTIONS.toString() + "(\n" +
                " personId integer NOT NULL, \n" +
                " interactionId integer NOT NULL\n" +
                ");");
    }

    boolean removeById(TableName tableName, int id){
        String sql = "DELETE FROM " + tableName.toString() + " WHERE id = " + id + ";";
        try (Connection conn = DriverManager.getConnection(databaseURL);
             PreparedStatement updateStatement = conn.prepareStatement(sql)) {

            updateStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    boolean addPerson(String name){
        String addStatement = "INSERT INTO " + PERSONS.toString() + "(name) VALUES('" + name + "')";
        return executeUpdate(addStatement);
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

        return getIdByStatement(selectStatement);
    }

    int getMaxIdOfTable(TableName tableName){
        String sql = "SELECT MAX(id) FROM " + tableName.toString() + ";";

        return getIdByStatement(sql);
    }

    int getIdByStatement(String statement){
        try (Connection conn = DriverManager.getConnection(databaseURL);
             PreparedStatement select = conn.prepareStatement(statement)) {

            ResultSet rs = select.executeQuery();

            if(rs.isClosed())
                return -1;

            rs.next();
            //FIXME если полей больше одного, брать по названию "id"
            int id = rs.getInt(1);

            return id;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return -1;
    }

    String[] listTable(TableName tableName){ // TODO Interactions
        ArrayList<String> result = new ArrayList<>();
        String sql;

        switch (tableName){
            case PERSONS:
                sql = "SELECT name FROM " + PERSONS.toString() + ";";
                try(Connection conn = DriverManager.getConnection(databaseURL);
                    Statement statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery(sql)){
                    while(rs.next()){
                        result.add(" " + rs.getString("name"));
                    }
                } catch (SQLException e){ System.out.println(e.getMessage()); }
                break;
            //////////////////////////////////////////////////////////////////////////////
            case INTERACTION_TYPES:
                sql = "SELECT typeName FROM " + INTERACTION_TYPES.toString() + ";";
                try(Connection conn = DriverManager.getConnection(databaseURL);
                    Statement statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery(sql)){
                    while(rs.next()){
                        result.add(" " + rs.getString("typeName"));
                    }
                } catch (SQLException e){ System.out.println(e.getMessage()); }
                break;
            //////////////////////////////////////////////////////////////////////////////
            default:
                throw new IllegalArgumentException(tableName.toString() + " is not supported yet");
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

    public boolean addInteractionType(String type) {
        String addStatement = "INSERT INTO " + INTERACTION_TYPES.toString() + "(typeName) VALUES('" + type + "')";
        return executeUpdate(addStatement);
    }

    public int addInteraction(Date date, int typeId, String comment) {
        String addStatement =
                "INSERT INTO " + INTERACTIONS.toString() +
                "(typeId, date, comment) VALUES(" +
                typeId + ", '" + Util.dateFormat.format(date) + "', '" + comment + "');";
        executeUpdate(addStatement);

        return getMaxIdOfTable(INTERACTIONS);
    }

    public void addPersonInteraction(int interactionId, String name) {
        String addStatement =
                "INSERT INTO " + PERSON_INTERACTIONS.toString() +
                "(personId, interactionId) VALUES(" +
                getIdByField(PERSONS, "name", name) + ", " + interactionId + ");";
        executeUpdate(addStatement);
    }
}
