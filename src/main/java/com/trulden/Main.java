package com.trulden;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static String databaseURL;
    private static Scanner inScan = new Scanner(System.in);

    public static void main(String[] args) {

        initDB();

        mainCycle();
    }

    private static void mainCycle() {
        while(true){
            System.out.println("\nEnter \n 0 to exit\n Persons: 1 : list; 2 : add;");
            switch(inScan.nextLine()){
                case "0" :
                    System.exit(0);
                    break;
                case "1" :
                    personsCycle();
                    break;
                case "2" :
                    addPerson();
                    break;
                default:
                    System.out.println("Wrong input, mate");
            }
        }
    }

    private static void listPersons() {
        String getNames = "SELECT name FROM persons";

        System.out.println("\nPersons: ");

        try(Connection conn = DriverManager.getConnection(databaseURL);
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(getNames)){

            while(rs.next()){
                System.out.println(" " + rs.getString("name"));
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void personsCycle() {
        boolean stayInPersonsCycle = true;
        while(stayInPersonsCycle){
            listPersons();

            System.out.println("\nEnter" +
                "\n 0 to go back" +
                "\n 1 : add; 2 : rename; 3 : remove Person"); // TODO 2, 3

            switch (inScan.nextLine()){
                case "0" :
                    stayInPersonsCycle = false;
                    break;
                case "1" :
                    addPerson();
                    break;
                case "2" :
                    changePerson();
                    break;
                default:
                    System.out.println("Wrong input, mate");
            }
        }
    }

    private static void changePerson() {
        System.out.print("Who do you wish to rename? ");
        String oldName = inScan.nextLine();
        if(personExists(oldName)){
            int personId = getPersonId(oldName);

        } else {
            System.out.println("Person «" + oldName + "» doesn't exist");
        }
        // TODO
        // ввод нового имени
        // обновление таблицы
    }

    private static int getPersonId(String name) {

        String selectStatement = "SELECT id FROM persons WHERE name = '" + name + "'";

        try (Connection conn = DriverManager.getConnection(databaseURL);
             PreparedStatement select = conn.prepareStatement(selectStatement)) {

            ResultSet rs = select.executeQuery();
            rs.next();
            int personId = rs.getInt("id");

            return personId;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return -1;
    }

    // TODO проверять через id
    private static boolean personExists(String name) {
        String selectStatement = "SELECT name FROM persons WHERE name = '" + name + "'";

        try (Connection conn = DriverManager.getConnection(databaseURL);
             PreparedStatement select = conn.prepareStatement(selectStatement)) {

            return !select.executeQuery().isClosed();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    private static void initDB() {
        setDatabaseURLName("friends.db");
        connectToDatabase();

        String createPersons =
                "CREATE TABLE IF NOT EXISTS persons (\n" +
                        " id integer PRIMARY KEY,\n" +
                        " name text NOT NULL\n" +
                        ");";

        createTable(createPersons);
    }

    private static void addPerson(){
        System.out.print("Enter persons name: ");
        addPerson(inScan.nextLine());
    }

    private static void addPerson(String name) {
        String addStatement = "INSERT INTO persons(name) VALUES('" + name + "')";

        if(personExists(name)){
            System.out.println("Person «" + name + "» already exists");
        } else {
            try (Connection conn = DriverManager.getConnection(databaseURL);
                 PreparedStatement add = conn.prepareStatement(addStatement)) {
                add.executeUpdate();
                System.out.println("Person «" + name + "» added");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void createTable(String sqlCreateStatement) {
        try (Connection conn = DriverManager.getConnection(databaseURL);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sqlCreateStatement);
            System.out.println("Table created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void setDatabaseURLName(String dbName) {
        databaseURL = "jdbc:sqlite:db\\" + dbName;
    }

    public static void connectToDatabase(){
        try(Connection conn = DriverManager.getConnection(databaseURL)){
            if(conn != null){
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("A new database «" + databaseURL + "» has been created");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
}
