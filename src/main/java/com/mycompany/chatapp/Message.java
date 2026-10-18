/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * JSON layout follows: JSON.org. n.d. Introducing JSON.
 * https://www.json.org/json-en.html [Accessed 18 October 2026].
 */

public class Message {
    private String messageID;
    private int messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;

    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();
    private static String jsonFileName = "messages.json";

    public Message(int messageNumber, String recipient, String messageText) {
        this(makeMessageID(), messageNumber, recipient, messageText);
    }

    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    public String getMessageID() {
        return messageID;
    }

    // random 10-digit ID: the loop picks one digit at a time with substring
    public static String makeMessageID() {
        String digits = "0123456789";
        Random random = new Random();
        String id = "";
        for (int counter = 0; counter < 10; counter++) {
            int position = random.nextInt(10);
            id = id + digits.substring(position, position + 1);
        }
        return id;
    }

    public String getMessageIDStatus() {
        return "Message ID generated: " + messageID;
    }

    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    // reuses the cell number check from Part 1
    public static boolean isRecipientValid(String cellNumber) {
        Login loginChecker = new Login("", "", "", "", cellNumber);
        return loginChecker.checkCellphoneNumber();
    }

    public String checkRecipientCell() {
        if (isRecipientValid(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.";
    }

    public static boolean isMessageLengthValid(String text) {
        return text.length() <= 250;
    }

    public static String checkMessageLength(String text) {
        if (isMessageLengthValid(text)) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (text.length() - 250)
                + "; please reduce the size.";
    }

    // example: 00:0:HITONIGHT
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");
        String firstWord = removeSymbols(words[0]);
        String lastWord = removeSymbols(words[words.length - 1]);
        String hash = messageID.substring(0, 2) + ":" + messageNumber + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }

    private static String removeSymbols(String word) {
        String cleanWord = "";
        for (int counter = 0; counter < word.length(); counter++) {
            char letter = word.charAt(counter);
            if (Character.isLetterOrDigit(letter)) {
                cleanWord = cleanWord + letter;
            }
        }
        return cleanWord;
    }

    // 1 = send, 2 = disregard, 3 = store
    public String sentMessage(int choice) {
        if (choice == 1) {
            sentMessages.add(this);
            return "Message successfully sent.";
        } else if (choice == 2) {
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            storeMessage();
            return "Message successfully stored.";
        }
        return "Invalid choice. Please choose 1, 2 or 3.";
    }

    public String getFullDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText + "\n";
    }

    public static String printMessages() {
        String allMessages = "";
        for (Message message : sentMessages) {
            allMessages = allMessages + message.getFullDetails() + "\n";
        }
        return allMessages;
    }

    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // saves all stored messages in the JSON file
    public void storeMessage() {
        storedMessages.add(this);

        String json = "[\n";
        for (int counter = 0; counter < storedMessages.size(); counter++) {
            Message m = storedMessages.get(counter);
            json = json + "  {\n"
                    + "    \"messageID\": \"" + clean(m.messageID) + "\",\n"
                    + "    \"messageNumber\": " + m.messageNumber + ",\n"
                    + "    \"recipient\": \"" + clean(m.recipient) + "\",\n"
                    + "    \"messageText\": \"" + clean(m.messageText) + "\",\n"
                    + "    \"messageHash\": \"" + clean(m.messageHash) + "\"\n"
                    + "  }";

            if (counter < storedMessages.size() - 1) {
                json = json + ",";
            }

            json = json + "\n";
        }
        json = json + "]";


        try (FileWriter writer = new FileWriter(jsonFileName)) {
            writer.write(json);
        } catch (IOException e) {
            System.out.println("Could not save the message: " + e.getMessage());
        }
    }


    // puts a backslash before quotes so the JSON stays correct
    private static String clean(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // used by the unit tests only
    public static void setJsonFileName(String fileName) {
        jsonFileName = fileName;
    }
    public static void resetForTesting() {

        sentMessages.clear();

        storedMessages.clear();

        jsonFileName = "messages.json";
    }
}