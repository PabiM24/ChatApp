/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatapp;

import java.util.Scanner;

/**
 *
 * @author Student
 */
public class ChatApp {

    public static void main(String[] args) {
        Scanner input =new Scanner (System.in);
        
        //holds registred user,to check their details when they try to login
         Login registeredUser = null;
         boolean running = true;
         
         //loop to keep showing the menu until the user chooses to exit
         while (running){
             System.out.println("\nWelcome to the Chat App");
             System.out.println("1.Register");
             System.out.println("2.Login");
             System.out.println("3.Exit");
             System.out.println("Please choose an option:");
             String choice = input.nextLine();
             
             if (choice.equals("1")){  //registering
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
                 
                 //login object for every detail a user entered
                 registeredUser = new Login (firstName, lastName, username, password, cellphoneNumber);
                 
                 //Check the rules or requirements and give a message 
                 String message = registeredUser.registerUser();
                 System.out.println(message);
                 
             }else if (choice.equals("2")){  //Login
                 
                 if (registeredUser == null) {
                     System.out.println("You have not registred. Please register first.");
                 }else{
                     System.out.print("Enter your username: "); 
                     String enteredUsername = input.nextLine();
                     
                      System.out.println("Enter your password: ");
                      String enteredPassword = input.nextLine();
                      
                      boolean success = registeredUser.loginUser(enteredUsername, enteredPassword);
                      String statusMessage = registeredUser.returnLoginStatus(success);
                      System.out.println(statusMessage);
                 }
             }else if (choice.equals("3")){
                 running = false;
                 System.out.println("Goodbye!");
             }else{
                 System.out.println("Invalid option, please choose 1, 2 or 3.");
                 
                 }
                 
             }
         input.close();
         }
    }

