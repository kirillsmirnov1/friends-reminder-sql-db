package com.trulden;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;

import static com.trulden.TableName.INTERACTION_TYPES;
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
            System.out.println("\nEnter " +
                    "\n 0 to exit" +
                    "\n Persons:      1 : list; 2 : add;" +
                    "\n Interactions: 3 : list; 4 : add;");
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
                case "3":
                    interactionsCycle();
                    break;
                case "4":
                    addInteraction();
                    break;
                default:
                    System.out.println("Wrong input, mate");
            }
        }
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

        //TODO Добавление связей взаимодействий

        System.out.println("That's all, folks\nHaven't finished yet");
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
            System.out.println("Type «" + type + "» doesn't exist\nEnter 1 to create or 2 to write something else");
            switch(Integer.parseInt(inScan.nextLine())){
                case 1:
                    addInteractionType(type);
                    return type;
                case 2:
                default:
                    return readType();
            }
        }
    }

    private static void addInteractionType(String type) {
        if(interactionTypeExists(type)){
            System.out.println("Interaction type «" + type + "» already exists");
        } else {
            if(sqlHandler.addInteractionType(type))
                System.out.println("Interaction type «" + type + "» added");
            else
                System.out.println("Couldn't add «" + type + "»");
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
        // TODO
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
