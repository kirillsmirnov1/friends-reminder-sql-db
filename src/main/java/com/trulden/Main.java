package com.trulden;

import java.sql.*;
import java.util.Scanner;

import static com.trulden.TableName.PERSONS;

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

    private static void personsCycle() {
        boolean stayInPersonsCycle = true;
        while(stayInPersonsCycle){
            printTable(PERSONS);

            System.out.println("\nEnter" +
                "\n 0 to go back" +
                "\n" + PERSONS.toString() +
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

    private static void printTable(TableName tableName) {
        System.out.println(tableName.toString() + ": ");
        for(String str : sqlHandler.listTable(tableName))
            System.out.println(str);
    }

    private static void removePerson() {
        System.out.print("Who do you wish to remove? ");
        String name = inScan.nextLine();
        if(personExists(name)) {
            int personId = sqlHandler.getIdByField(PERSONS, "name", name);
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
            int personId = sqlHandler.getIdByField(PERSONS, "name", oldName);

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
