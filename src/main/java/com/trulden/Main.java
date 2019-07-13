package com.trulden;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static Scanner inScan = new Scanner(System.in);
    private static SQLHandler sqlHandler;

    public static void main(String[] args) {

        sqlHandler = new SQLHandler("friends.db");
        sqlHandler.initDB();

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

        try(Connection conn = DriverManager.getConnection(sqlHandler.getDatabaseURL());
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
                "\nPersons" +
                "\n 1 : add; 2 : rename; 3 : remove");

            switch (inScan.nextLine()){
                case "0" :
                    stayInPersonsCycle = false;
                    break;
                case "1" :
                    addPerson();
                    break;
                case "2" :
                    renamePerson();
                    break;
                case "3" :
                    removePerson();
                    break;
                default:
                    System.out.println("Wrong input, mate");
            }
        }
    }

    private static void removePerson() {
        System.out.print("Who do you wish to remove? ");
        String name = inScan.nextLine();
        if(personExists(name)) {
            int personId = getPersonId(name);
            if(removePerson(personId))
                System.out.println("«" + name + "» removed");
            else
                System.out.println("Removing failed miserably");
        } else {
            System.out.println("Person «" + name + "» doesn't exist");
        }
    }

    private static boolean removePerson(int personId) {
        String sql = "DELETE FROM persons WHERE id = " + personId + ";";
        try (Connection conn = DriverManager.getConnection(sqlHandler.getDatabaseURL());
             PreparedStatement updateStatement = conn.prepareStatement(sql)) {

            updateStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    private static void renamePerson() {
        System.out.print("Who do you wish to rename? ");
        String oldName = inScan.nextLine();
        if(personExists(oldName)){
            int personId = getPersonId(oldName);

            System.out.print("How do you want to call «" + oldName + "» from now on? ");

            String newName = inScan.nextLine();

            if(renamePerson(personId, newName))
                System.out.println("«" + oldName + "» renamed to «" + newName + "»");
            else
                System.out.println("Renaming failed miserably");

        } else {
            System.out.println("Person «" + oldName + "» doesn't exist");
        }
    }

    private static boolean renamePerson(int personId, String newName) {
        String sql = "UPDATE persons SET name = '" + newName + "' WHERE id = " + personId + ";";
        try (Connection conn = DriverManager.getConnection(sqlHandler.getDatabaseURL());
             PreparedStatement updateStatement = conn.prepareStatement(sql)) {

            updateStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    private static int getPersonId(String name) {

        String selectStatement = "SELECT id FROM persons WHERE name = '" + name + "'";

        try (Connection conn = DriverManager.getConnection(sqlHandler.getDatabaseURL());
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

        try (Connection conn = DriverManager.getConnection(sqlHandler.getDatabaseURL());
             PreparedStatement select = conn.prepareStatement(selectStatement)) {

            return !select.executeQuery().isClosed();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
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
            try (Connection conn = DriverManager.getConnection(sqlHandler.getDatabaseURL());
                 PreparedStatement add = conn.prepareStatement(addStatement)) {
                add.executeUpdate();
                System.out.println("Person «" + name + "» added");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }




}
