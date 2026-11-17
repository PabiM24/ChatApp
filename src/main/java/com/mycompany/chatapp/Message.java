/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

    // ===== The five arrays for Part 3 =====
    private static ArrayList<Message> sentMessages = new ArrayList<>();         // Sent Message array
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();  // Disregarded Messages array
    private static ArrayList<Message> storedMessages = new ArrayList<>();       // Stored Messages array (read from the JSON file)
    private static ArrayList<String> messageHashes = new ArrayList<>();        // Message Hash array
    private static ArrayList<String> messageIDs = new ArrayList<>();            // Message ID array

    private static String jsonFileName = "messages.json";
    private static String currentSender = "Unknown sender";

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

    // ===== Getters =====
    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
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
            refreshHashAndIdArrays();
            return "Message successfully sent.";
        } else if (choice == 2) {
            disregardedMessages.add(this);
            refreshHashAndIdArrays();
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

    // ===================================================================
    // PART 3: storing, reading and displaying messages
    // ===================================================================

    // saves a message in the JSON file (the file is read first so older messages are kept)
    public void storeMessage() {
        loadStoredMessagesFromFile();
        storedMessages.add(this);
        saveStoredMessagesToFile();
        refreshHashAndIdArrays();
    }

    // writes every message in the stored array to the JSON file
    private static void saveStoredMessagesToFile() {
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

    // reads the JSON file and fills the Stored Messages array (no hard-coding)
    public static void loadStoredMessagesFromFile() {
        storedMessages.clear();

        Path filePath = Paths.get(jsonFileName);
        if (!Files.exists(filePath)) {
            refreshHashAndIdArrays();
            return;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);

            String id = null;
            int number = 0;
            String recipientValue = "";
            String text = "";

            for (String line : lines) {
                String trimmed = line.trim();

                if (trimmed.startsWith("\"messageID\"")) {
                    id = readJsonValue(trimmed);
                } else if (trimmed.startsWith("\"messageNumber\"")) {
                    number = Integer.parseInt(readJsonValue(trimmed));
                } else if (trimmed.startsWith("\"recipient\"")) {
                    recipientValue = readJsonValue(trimmed);
                } else if (trimmed.startsWith("\"messageText\"")) {
                    text = readJsonValue(trimmed);
                } else if (trimmed.startsWith("}") && id != null) {
                    // end of one message object, so build it and add it to the array
                    storedMessages.add(new Message(id, number, recipientValue, text));
                    id = null;
                    number = 0;
                    recipientValue = "";
                    text = "";
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Could not read the stored messages: " + e.getMessage());
        }

        refreshHashAndIdArrays();
    }

    // takes the value from a line such as:   "recipient": "+27838884567",
    private static String readJsonValue(String line) {
        String value = line.substring(line.indexOf(":") + 1).trim();

        if (value.endsWith(",")) {
            value = value.substring(0, value.length() - 1).trim();
        }

        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1);
        }

        return unclean(value);
    }

    // puts a backslash before quotes so the JSON stays correct
    private static String clean(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // undoes clean() when the text is read back from the file
    private static String unclean(String text) {
        String result = "";
        for (int counter = 0; counter < text.length(); counter++) {
            char letter = text.charAt(counter);
            if (letter == '\\' && counter + 1 < text.length()) {
                counter++;
                result = result + text.charAt(counter);
            } else {
                result = result + letter;
            }
        }
        return result;
    }

    // refills the Message Hash and Message ID arrays from the three message arrays
    private static void refreshHashAndIdArrays() {
        messageHashes.clear();
        messageIDs.clear();

        for (Message m : sentMessages) {
            messageHashes.add(m.messageHash);
            messageIDs.add(m.messageID);
        }
        for (Message m : disregardedMessages) {
            messageHashes.add(m.messageHash);
            messageIDs.add(m.messageID);
        }
        for (Message m : storedMessages) {
            messageHashes.add(m.messageHash);
            messageIDs.add(m.messageID);
        }
    }

    // ===== Array getters (the arrays stay private) =====
    public static ArrayList<String> getSentMessageTexts() {
        ArrayList<String> texts = new ArrayList<>();
        for (Message m : sentMessages) {
            texts.add(m.messageText);
        }
        return texts;
    }

    public static ArrayList<String> getDisregardedMessageTexts() {
        ArrayList<String> texts = new ArrayList<>();
        for (Message m : disregardedMessages) {
            texts.add(m.messageText);
        }
        return texts;
    }

    public static ArrayList<String> getStoredMessageTexts() {
        ArrayList<String> texts = new ArrayList<>();
        for (Message m : storedMessages) {
            texts.add(m.messageText);
        }
        return texts;
    }

    public static ArrayList<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static ArrayList<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    // ===== 2a. Sender and recipient of all stored messages =====
    public static void setSender(String senderName) {
        currentSender = senderName;
    }

    public static String displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }
        String result = "";
        for (Message m : storedMessages) {
            result = result + "Sender: " + currentSender + " | Recipient: " + m.recipient + "\n";
        }
        return result;
    }

    // ===== 2b. Longest stored message =====
    public static String getLongestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }
        Message longest = storedMessages.get(0);
        for (Message m : storedMessages) {
            if (m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        return longest.messageText;
    }

    // ===== 2c. Search for a message ID =====
    // looks in the sent, disregarded and stored arrays; returns null if it is not found
    public static Message findMessageByID(String id) {
        for (Message m : sentMessages) {
            if (m.messageID.equals(id)) {
                return m;
            }
        }
        for (Message m : disregardedMessages) {
            if (m.messageID.equals(id)) {
                return m;
            }
        }
        for (Message m : storedMessages) {
            if (m.messageID.equals(id)) {
                return m;
            }
        }
        return null;
    }

    public static String searchByMessageID(String id) {
        Message found = findMessageByID(id);
        if (found == null) {
            return "No message was found with the message ID " + id + ".";
        }
        return "Recipient: " + found.recipient + "\n"
                + "Message: " + found.messageText;
    }

    // ===== 2d. All messages (sent or stored) for one recipient =====
    public static ArrayList<String> searchByRecipient(String recipientNumber) {
        ArrayList<String> results = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.recipient.equals(recipientNumber)) {
                results.add(m.messageText);
            }
        }
        for (Message m : storedMessages) {
            if (m.recipient.equals(recipientNumber)) {
                results.add(m.messageText);
            }
        }
        return results;
    }

    // ===== 2e. Delete a stored message using its hash =====
    public static String deleteByMessageHash(String hash) {
        for (int counter = 0; counter < storedMessages.size(); counter++) {
            Message m = storedMessages.get(counter);
            if (m.messageHash.equals(hash)) {
                storedMessages.remove(counter);
                saveStoredMessagesToFile();
                refreshHashAndIdArrays();
                return "Message: \"" + m.messageText + "\" successfully deleted.";
            }
        }
        return "No stored message was found with the message hash " + hash + ".";
    }

    // ===== 2f. Report with the full details of the messages =====
    public static String displayReport() {
        String report = "========== MESSAGE REPORT ==========\n";

        report = report + "\n--- Sent messages ---\n";
        if (sentMessages.isEmpty()) {
            report = report + "No sent messages.\n";
        }
        for (Message m : sentMessages) {
            report = report + reportLines(m) + "\n";
        }

        report = report + "\n--- Stored messages ---\n";
        if (storedMessages.isEmpty()) {
            report = report + "No stored messages.\n";
        }
        for (Message m : storedMessages) {
            report = report + reportLines(m) + "\n";
        }

        return report;
    }

    private static String reportLines(Message m) {
        return "Message Hash: " + m.messageHash + "\n"
                + "Recipient: " + m.recipient + "\n"
                + "Message: " + m.messageText + "\n";
    }

    // used by the unit tests only
    public static void setJsonFileName(String fileName) {
        jsonFileName = fileName;
    }

    public static void resetForTesting() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        currentSender = "Unknown sender";
        jsonFileName = "messages.json";
    }
}