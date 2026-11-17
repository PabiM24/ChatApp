package com.mycompany.chatapp;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 3 unit tests. They use the five test messages from the brief.
 * Each test saves to a temporary JSON file, so your real messages.json is not touched.
 */
public class StoredMessagesTest {

    // Test Data Message 1 (Sent)
    private static final String RECIPIENT_1 = "+27834557896";
    private static final String TEXT_1 = "Did you get the cake?";

    // Test Data Message 2 (Stored)
    private static final String RECIPIENT_2 = "+27838884567";
    private static final String TEXT_2 = "Where are you? You are late! I have asked you to be on time.";

    // Test Data Message 3 (Disregard)
    private static final String RECIPIENT_3 = "+27834484567";
    private static final String TEXT_3 = "Yohoooo, I am at your gate.";

    // Test Data Message 4 (Sent)
    private static final String RECIPIENT_4 = "0838884567";
    private static final String TEXT_4 = "It is dinner time!";

    // Test Data Message 5 (Stored)
    private static final String RECIPIENT_5 = "+27838884567";
    private static final String TEXT_5 = "Ok, I am leaving without you.";

    // fixed IDs so the message hashes are the same every time the tests run
    private static final String ID_1 = "1000000001";
    private static final String ID_2 = "1000000002";
    private static final String ID_3 = "1000000003";
    private static final String ID_4 = "1000000004";
    private static final String ID_5 = "1000000005";

    // runs before every test so each test starts fresh and uses a temporary file
    @BeforeEach
    public void setUp(@TempDir Path tempFolder) {
        Message.resetForTesting();   // empty all the arrays
        File jsonFile = new File(tempFolder.toFile(), "messages.json");
        Message.setJsonFileName(jsonFile.getPath());
        Message.setSender("Paballo Maluleke");   // the logged-in user
    }

    // sends, stores or disregards each of the 5 test messages (flags from the brief)
    // 1 = Sent, 2 = Disregard, 3 = Stored
    private void loadTestData() {
        new Message(ID_1, 0, RECIPIENT_1, TEXT_1).sentMessage(1);   // Sent
        new Message(ID_2, 1, RECIPIENT_2, TEXT_2).sentMessage(3);   // Stored
        new Message(ID_3, 2, RECIPIENT_3, TEXT_3).sentMessage(2);   // Disregard
        new Message(ID_4, 3, RECIPIENT_4, TEXT_4).sentMessage(1);   // Sent
        new Message(ID_5, 4, RECIPIENT_5, TEXT_5).sentMessage(3);   // Stored
    }

    // ===== Sent Messages array correctly populated =====
    @Test
    public void testSentMessagesArrayPopulated() {
        loadTestData();
        // messages 1 and 4 were sent, so only they must be in the array
        ArrayList<String> expected = new ArrayList<>(Arrays.asList(TEXT_1, TEXT_4));
        assertEquals(expected, Message.getSentMessageTexts());
        assertEquals("Did you get the cake?", Message.getSentMessageTexts().get(0));
        assertEquals("It is dinner time!", Message.getSentMessageTexts().get(1));
    }

    // ===== Disregarded Messages array correctly populated =====
    @Test
    public void testDisregardedMessagesArrayPopulated() {
        loadTestData();
        // only message 3 was disregarded
        ArrayList<String> expected = new ArrayList<>(Arrays.asList(TEXT_3));
        assertEquals(expected, Message.getDisregardedMessageTexts());
    }

    // ===== Stored Messages array is filled by reading the JSON file =====
    @Test
    public void testStoredMessagesReadFromJsonFile() {
        loadTestData();
        Message.loadStoredMessagesFromFile();   // clears the array and reads the file again
        ArrayList<String> expected = new ArrayList<>(Arrays.asList(TEXT_2, TEXT_5));
        assertEquals(expected, Message.getStoredMessageTexts());
    }

    // ===== Message Hash and Message ID arrays =====
    @Test
    public void testMessageHashAndIdArrays() {
        loadTestData();
        // the order is: sent messages, then disregarded, then stored
        ArrayList<String> expectedHashes = new ArrayList<>(Arrays.asList(
                "10:0:DIDCAKE", "10:3:ITTIME", "10:2:YOHOOOOGATE",
                "10:1:WHERETIME", "10:4:OKYOU"));
        ArrayList<String> expectedIds = new ArrayList<>(Arrays.asList(
                ID_1, ID_4, ID_3, ID_2, ID_5));
        assertEquals(expectedHashes, Message.getMessageHashes());
        assertEquals(expectedIds, Message.getMessageIDs());
    }

    // ===== Display the longest message =====
    @Test
    public void testLongestMessage() {
        loadTestData();
        // message 2 is the longest of the stored messages
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.getLongestStoredMessage());
    }

    // ===== Search for a message ID (Test Data message 4) =====
    @Test
    public void testSearchForMessageID() {
        loadTestData();
        // look for message 4 with its ID
        Message found = Message.findMessageByID(ID_4);
        assertEquals("It is dinner time!", found.getMessageText());
        assertEquals("0838884567", found.getRecipient());
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                Message.searchByMessageID(ID_4));
    }

    @Test
    public void testSearchForMessageID_notFound() {
        loadTestData();
        // an ID that does not exist must give the "not found" message
        assertEquals("No message was found with the message ID 9999999999.",
                Message.searchByMessageID("9999999999"));
    }

    // ===== Search all the messages sent or stored for a recipient =====
    @Test
    public void testSearchByRecipient() {
        loadTestData();
        // messages 2 and 5 were both for +27838884567
        ArrayList<String> expected = new ArrayList<>(Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."));
        assertEquals(expected, Message.searchByRecipient("+27838884567"));
    }

    // ===== Delete a message using the message hash (Test Message 2) =====
    @Test
    public void testDeleteByMessageHash() throws Exception {
        loadTestData();
        // get the hash of Test Message 2
        String hash = new Message(ID_2, 1, RECIPIENT_2, TEXT_2).getMessageHash();

        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\""
                + " successfully deleted.", Message.deleteByMessageHash(hash));

        // the message is gone from the array and from the JSON file
        assertEquals(new ArrayList<>(Arrays.asList(TEXT_5)), Message.getStoredMessageTexts());
        Message.loadStoredMessagesFromFile();
        assertEquals(new ArrayList<>(Arrays.asList(TEXT_5)), Message.getStoredMessageTexts());
    }

    @Test
    public void testDeleteByMessageHash_notFound() {
        loadTestData();
        // a hash that does not exist must give the "not found" message
        assertEquals("No stored message was found with the message hash 00:0:NOPE.",
                Message.deleteByMessageHash("00:0:NOPE"));
    }

    // ===== Display the report =====
    @Test
    public void testDisplayReport() {
        loadTestData();
        String report = Message.displayReport();

        // the sent messages must show the hash, recipient and message
        assertTrue(report.contains("Message Hash: 10:0:DIDCAKE"));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: Did you get the cake?"));
        assertTrue(report.contains("Message Hash: 10:3:ITTIME"));
        assertTrue(report.contains("Recipient: 0838884567"));
        assertTrue(report.contains("Message: It is dinner time!"));

        // the stored messages are in the report too
        assertTrue(report.contains("Message Hash: 10:1:WHERETIME"));
        assertTrue(report.contains("Message: Ok, I am leaving without you."));

        // the disregarded message is not in the report
        assertFalse(report.contains("Yohoooo"));
    }

    // ===== Sender and recipient of all stored messages =====
    @Test
    public void testSenderAndRecipient() {
        loadTestData();
        // two stored messages, so two lines
        assertEquals("Sender: Paballo Maluleke | Recipient: +27838884567\n"
                + "Sender: Paballo Maluleke | Recipient: +27838884567\n",
                Message.displaySenderAndRecipient());
    }

    // ===== Message text with quotes must survive the JSON file =====
    @Test
    public void testQuotesSurviveTheJsonFile() {
        // store a message with quotation marks, then read it back from the file
        new Message(ID_1, 0, RECIPIENT_1, "He said \"hello\" to me").storeMessage();
        Message.loadStoredMessagesFromFile();
        assertEquals(new ArrayList<>(Arrays.asList("He said \"hello\" to me")),
                Message.getStoredMessageTexts());
    }
}