/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatapp;

import java.util.Scanner;

public class ChatApp {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        Login registeredUser = null;

        boolean running = true;
        while (running) {
            System.out.println("\n=============================");
            System.out.println("Welcome to the Chat App");
            System.out.println("1.Register");
            System.out.println("2.Login");
            System.out.println("3.Exit");
            System.out.println("Please choose an option:");
            String choice = input.nextLine();

            if (choice.equals("1")) {
                registeredUser = registerNewUser(input);
            } else if (choice.equals("2")) {
                if (registeredUser == null) {

                    System.out.println("You have not registered. Please register first.");
                } else if (loginExistingUser(input, registeredUser)) {
                    runQuickChat(input);
                }
            } else if (choice.equals("3")) {
                running = false;
                System.out.println("Goodbye!");

            } else {

                System.out.println("Invalid option, please choose 1, 2 or 3.");
            }
        }
        input.close();
    }


    // ===== Part 1: register and login =====
    private static Login registerNewUser(Scanner input) {
        System.out.println("Enter your first name: ");
        String firstName = input.nextLine();
        
        System.out.println("Enter your last name: ");
        String lastName = input.nextLine();
        
        System.out.println("Enter a username: ");
        String username = input.nextLine();
        
        System.out.println("Enter a password");
        String password = input.nextLine();
        
        System.out.println("Enter a cellphone number");
        String cellphoneNumber = input.nextLine();


        Login newUser = new Login(firstName, lastName, username, password, cellphoneNumber);
        System.out.println(newUser.registerUser());
        return newUser;

    }

    private static boolean loginExistingUser(Scanner input, Login registeredUser) {
        System.out.print("Enter your username: ");
        String enteredUsername = input.nextLine();

        System.out.println("Enter your password: ");
        String enteredPassword = input.nextLine();


        boolean success = registeredUser.loginUser(enteredUsername, enteredPassword);
        System.out.println(registeredUser.returnLoginStatus(success));

        return success;
    }


    // Part 2: ChatApp 

    private static void runQuickChat(Scanner input) {
        System.out.println("\nWelcome to QuickChat.");
        int numberOfMessages = askNumberOfMessages(input);

        boolean messagesDone = false;
        boolean quickChatRunning = true;


        while (quickChatRunning) {
            System.out.println("\n--- QuickChat Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("Please choose an option:");

            String choice = input.nextLine();
            if (choice.equals("1")) {
                if (messagesDone) {

                    System.out.println("You have already entered all your messages.");

                } else {
                    enterAllMessages(input, numberOfMessages);
                    messagesDone = true;
                }
            } else if (choice.equals("2")) {
                System.out.println("Coming Soon.");

            } else if (choice.equals("3")) {
                quickChatRunning = false;
                System.out.println("Leaving QuickChat. Goodbye!");

            } else {
                System.out.println("Invalid option, please choose 1, 2 or 3.");
            }
        }
    }

    private static int askNumberOfMessages(Scanner input) {
        int number = 0;
        while (number <= 0) {
            System.out.println("How many messages do you want to enter?");
            try {
                number = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {

                number = 0;
            }
            if (number <= 0) {
                System.out.println("Please enter a whole number greater than 0.");
            }
        }
        return number;
    }

    // the for loop counter is the message number: 0, 1, 2...
    private static void enterAllMessages(Scanner input, int numberOfMessages) {
        for (int messageNumber = 0; messageNumber < numberOfMessages; messageNumber++) {

            System.out.println("\nMessage " + (messageNumber + 1) + " of " + numberOfMessages);
            enterOneMessage(input, messageNumber);

        }
        System.out.println("\nTotal number of messages sent: " + Message.returnTotalMessages());
    }

    
    private static void enterOneMessage(Scanner input, int messageNumber) {
        String recipient = askRecipient(input);
        String text = askMessageText(input);


        Message message = new Message(messageNumber, recipient, text);
        System.out.println(message.getMessageIDStatus());


        int option = askSendOption(input);
        System.out.println(message.sentMessage(option));


        if (option == 1) {
            System.out.println("\n--- Message details ---");
            System.out.println(message.getFullDetails());
        } else if (option == 2) {

            System.out.println("Enter 0 to delete the message:");

            if (input.nextLine().equals("0")) {

                System.out.println("Message deleted.");
            } else {

                System.out.println("Message disregarded.");
            }
        }
    }


    private static String askRecipient(Scanner input) {
        String recipient = "";
        boolean correct = false;
        while (!correct) {

            System.out.println("Enter the recipient's cell number (for example +27718693002):");
            recipient = input.nextLine().trim();

            if (Message.isRecipientValid(recipient)) {

                System.out.println("Cell phone number successfully captured.");

                correct = true;
            } else {
                System.out.println("Cell phone number is incorrectly formatted or does not "
                        + "contain an international code. Please correct the number and try again.");
            }
        }
        return recipient;
    }


    private static String askMessageText(Scanner input) {
        String text = "";
        boolean correct = false;

        while (!correct) {
            System.out.println("Enter your message:");
            text = input.nextLine();
            if (text.trim().isEmpty()) {

                System.out.println("The message cannot be empty.");
            } else if (Message.isMessageLengthValid(text)) {

                System.out.println(Message.checkMessageLength(text));
                correct = true;

            } else {

                System.out.println(Message.checkMessageLength(text));
            }
        }
        return text;
    }


    private static int askSendOption(Scanner input) {
        String answer = "";
        while (!answer.equals("1") && !answer.equals("2") && !answer.equals("3")) {
            System.out.println("What would you like to do with this message?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");

            answer = input.nextLine().trim();

            if (!answer.equals("1") && !answer.equals("2") && !answer.equals("3")) {

                System.out.println("Invalid option, please choose 1, 2 or 3.");
            }
        }
        return Integer.parseInt(answer);
    }
}