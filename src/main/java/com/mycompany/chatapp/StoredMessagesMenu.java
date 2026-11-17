/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author pabee
 */
public class StoredMessagesMenu {
    
   
    // shows the Stored Messages menu until the user chooses "Back"
    public static void run(Scanner input) {
        // read the JSON file into the Stored Messages array every time the menu opens
        Message.loadStoredMessagesFromFile();

        boolean running = true;
        while (running) {
            // show the menu
            System.out.println("\n===== Stored Messages =====");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a recipient");
            System.out.println("5) Delete a message using the message hash");
            System.out.println("6) Display a report of all the messages");
            System.out.println("7) Back to the main menu");
            System.out.println("Please choose an option:");
            String choice = input.nextLine().trim();

            if (choice.equals("1")) {
                // 2a. sender and recipient of every stored message
                System.out.println(Message.displaySenderAndRecipient());
            } else if (choice.equals("2")) {
                // 2b. the longest stored message
                System.out.println("Longest stored message: " + Message.getLongestStoredMessage());
            } else if (choice.equals("3")) {
                // 2c. find a message with its ID
                System.out.println("Enter the message ID:");
                String id = input.nextLine().trim();
                System.out.println(Message.searchByMessageID(id));
            } else if (choice.equals("4")) {
                // 2d. find all the messages for one recipient
                System.out.println("Enter the recipient's cell phone number:");
                String recipient = input.nextLine().trim();
                ArrayList<String> results = Message.searchByRecipient(recipient);
                if (results.isEmpty()) {
                    System.out.println("No messages were found for " + recipient + ".");
                } else {
                    for (String text : results) {
                        System.out.println("Message: " + text);
                    }
                }
            } else if (choice.equals("5")) {
                // 2e. delete a message with its hash
                System.out.println("Enter the message hash:");
                String hash = input.nextLine().trim();
                System.out.println(Message.deleteByMessageHash(hash));
            } else if (choice.equals("6")) {
                // 2f. the full report
                System.out.println(Message.displayReport());
            } else if (choice.equals("7")) {
                // go back to the main menu
                running = false;
            } else {
                System.out.println("Invalid option, please choose 1 to 7.");
            }
        }
    }
}

