/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

/**
 *
 * @author Student
 */
public class Login {
    public String firstName;
    public String lastName;
    public String username;
    public String password;
    public String cellphoneNumber; //it is stored a string 
    //becuase it can start with 0 or +
    
    //Constructorsthat will run automattically when 
    //a new login object takes what the user typed and saves it  above
    public Login(String firstName, String lastName, String username, String password, String cellphoneNumber){
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellphoneNumber = cellphoneNumber;
        
        
    }
    //this will check if the username has an underscore and is 5 charcter long 
    public boolean checkUserName(){
        return username.contains("_")&& username.length() <= 5;
    }
    
    
    // Cellphone number should start with +27 and not more than 10 numbers
    public boolean checkCellphoneNumber(){
        if (!cellphoneNumber.startsWith("+27")) {
            return false;
        }
        String numberPart = cellphoneNumber.substring(3);
        return numberPart.length() <= 10 && numberPart.matches("\\d+");
    }
       
    //password should be atleast 8 characters long with a number, special character and capital
    public boolean checkPassword(){
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;
        
        for (int i=0; i< password.length(); i++){
            char c = password.charAt(i);
            
            if (Character.isUpperCase(c)){
                hasCapital = true;
            }
            if (Character.isDigit(c)){
                hasNumber = true;
            }
            if (!Character.isLetterOrDigit(c)){
                hasSpecialCharacter = true;
            }
        }
       return password.length() >= 8 && hasCapital && hasNumber && hasSpecialCharacter;
    }
    
    
    //a method that will verify if the requirements were met
    public String registerUser(){
        
    if(!checkUserName()){
        return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
    }
    if(!checkPassword()){
        return "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number and a special character.";
    }
    if(!checkCellphoneNumber()){
        return "Cell number is incorrectly formatted or does not contain an international code, please correct the number and try again.";
    }
    return "Username successfully captured. Password successfully captured. Cell number successfully captured.";

    }
    
    
        //Check if requirements of the username and password entered in the login are correct
        //to match what was saved when the user was registering
    public boolean loginUser (String enteredUserName, String enteredPassword){
            return username.equals(enteredUserName) && password.equals(enteredPassword);
    }
    
    
    //the user authentication messages
    public String returnLoginStatus(boolean loginSuccess){
        if (loginSuccess){
            return "Welcome " + firstName + "," + lastName + " it is great to see you again.";
                    }else{
            return "Username or password is incorrect, please try again.";
        }
        }

   
    }

