package com.trulden;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;

import static com.trulden.TableName.*;

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
            System.out.println("\nEnter " +
                    "\n 0 to exit" +
                    "\n 1 to show interactions a while ago" +
                    "\n Persons:      2 : list; 3 : add;" +
                    "\n Interactions: 4 : list; 5 : add;");
            switch(inScan.nextLine()){
                case "0" :
                    System.exit(0);
                    break;
                case "1":
                    showInteractionsAWhileAgo();
                    break;
                case "2" :
                    personsCycle(); // TODO из этих циклов нужно сделать выход
                    break;
                case "3" :
                    addPerson();
                    break;
                case "4":
                    interactionsCycle();
                    break;
                case "5":
                    addInteraction();
                    break;
                default:
                    System.out.println("Wrong input, mate");
            }
        }
    }

    private static void showInteractionsAWhileAgo() { // TODO
        // Map [ Type [ person [ date ] ]
        // for every person
            // check every type
            // get last meeting of type, if it have been long enough time ago
    }

    private static void addInteraction() {
        HashSet<String> names = new HashSet<>();
        String type;
        Date date = new Date();
        String comment;

        //Добавление / Проверка друзей
        listPersons();

        System.out.println("\nTo whom you wish to add interaction?\nEnter names divided by comma");

        String[] enteredNames = inScan.nextLine().split(", ");

        for(String name : enteredNames) {
            checkAndAddName(name, names);
        }

        //Добавление / Проверка типов взаимодействий

        type = readType();

        // Date

        date = readDate();

        // Interaction comment

        System.out.println("Tell me about your interaction\n");

        comment = inScan.nextLine();

        //Добавление взаимодействий
        int interactionId = sqlHandler.addInteraction(date, getInteractionTypeId(type), comment);

        for(String name : names){
            sqlHandler.addPersonInteraction(interactionId, name);
        }
    }

    private static Date readDate() {
        System.out.print("Enter date of interaction in «" + Util.dateFormat.toPattern() + "» format: ");

        try {
            String inputDateLine = inScan.nextLine();

            if(inputDateLine.length() != 10)
                throw new ParseException("Wrong length", 0);

            return Util.dateFormat.parse(inputDateLine);
        } catch (ParseException e){
            System.out.println("There was some mistake. Try again");
            return readDate();
        }
    }

    private static String readType() {
        System.out.println("Enter type of meeting. \nYou can choose one of the following or enter a new one\n");
        listTypesOfInteractions();
        String type = inScan.nextLine();
        if(interactionTypeExists(type)){
            return type;
        } else {
            return addInteractionType(type);
        }
    }

    private static String addInteractionType(String type){
        System.out.println("Type «" + type + "» doesn't exist\nEnter 1 to create or 2 to write something else");
        switch(Integer.parseInt(inScan.nextLine())){
            case 1:
                System.out.println("And how often should «" + type + "» be in days?");
                int freq = Integer.parseInt(inScan.nextLine());
                if(addInteractionType(type, freq))
                    return type;
                else {
                    System.out.println("Something went wrong, try again");
                    return readType();
                }
            case 2:
            default:
                return readType();
        }
    }

    private static boolean addInteractionType(String type, int freq) {
        if(interactionTypeExists(type)){
            System.out.println("Interaction type «" + type + "» already exists");
            return true;
        } else {
            if(sqlHandler.addInteractionType(type, freq)) {
                System.out.println("Interaction type «" + type + "» added");
                return true;
            }
            else {
                System.out.println("Couldn't add «" + type + "»");
                return false;
            }
        }
    }

    private static boolean interactionTypeExists(String type) {
        return getInteractionTypeId(type) >= 0;
    }

    private static int getInteractionTypeId(String type){
        return sqlHandler.getIdByField(INTERACTION_TYPES, "typeName", type);
    }

    private static void listTypesOfInteractions() {
        printTable(INTERACTION_TYPES);
    }

    private static void checkAndAddName(String name, HashSet<String> names) {
        if (personExists(name)) {
            names.add(name);
        } else {
            System.out.println("You don't have friend named «" + name + "»" +
                    "\nEnter 1 to create new friend, 2 to change entered name, 3 to forget about this misunderstanding");
            switch(Integer.parseInt(inScan.nextLine())){
                case 1:
                    addPerson(name);
                    names.add(name);
                    System.out.println("«" + name + "» created");
                    break;
                case 2:
                    System.out.print("Enter name instead of «" + name + "»: " );
                    checkAndAddName(inScan.nextLine(), names);
                    break;
                case 3:
                    System.out.println("«" + name + "» is forgotten");
                    break;
                default:
                    System.out.println("Wrong input, mate");
                    checkAndAddName(name, names);
            }
        }
    }

    private static void listPersons() {
        printTable(PERSONS);
    }

    private static void interactionsCycle() {
        boolean stayInInteractionsCycle = true;
        while(stayInInteractionsCycle){
            listInteractions();

            System.out.println("\nEnter" +
                "\n 0 to go back" +
                "\n" + INTERACTIONS.toString() +
                "\n 1 : add; 2: change; 3 : remove");

            switch(inScan.nextLine()){
                case "0" :
                    stayInInteractionsCycle = false;
                    break;
                case "1" :
                    addInteraction();
                    break;
                case "2": // TODO
                    System.out.println("TODO");
                    break;
                case "3":
                    removeInteraction();
                    break;
                default:
                    System.out.println("Wrong input, mate");
            }

        }
    }

    private static void removeInteraction() {
        System.out.print("Enter id of interaction to be removed: ");
        int id = Integer.parseInt(inScan.nextLine());
        if(interactionExists(id)){
            removeInteraction(id);
        } else {
            System.out.println("Interaction [" + id + "] doesn't exist");
        }
    }

    private static void removeInteraction(int id) {
        sqlHandler.removeById(INTERACTIONS, id);
    }

    private static boolean interactionExists(int id) {
        return sqlHandler.getFieldById(INTERACTIONS, id, "date") != null;
    }

    private static void listInteractions() {
        printTable(INTERACTIONS);
    }

    private static void personsCycle() {
        boolean stayInPersonsCycle = true;
        while(stayInPersonsCycle){
            listPersons();

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
        return sqlHandler.removeById(PERSONS, personId);
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
        return sqlHandler.changeFieldValue(PERSONS, personId, "name", newName);
    }

    private static boolean personExists(String name) {
        return sqlHandler.getIdByField(PERSONS, "name", name) >= 0;
    }

    private static void addPerson(){
        System.out.print("Enter persons name: ");
        addPerson(inScan.nextLine());
    }

    private static void addPerson(String name) {
        if(personExists(name)){
            System.out.println("Person «" + name + "» already exists");
        } else {
            if(sqlHandler.addPerson(name))
                System.out.println("Person «" + name + "» added");
            else
                System.out.println("Couldn't add «" + name + "»");
        }
    }




}
