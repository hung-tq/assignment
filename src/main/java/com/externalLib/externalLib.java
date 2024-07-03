package com.externalLib;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * This library contains many helpful function to help me to simplify some works
 * in writing code
 * 
 * @author hung-tq
 * @since 31/05/2024
 */
public class externalLib
{
    public static String nomalizeName(String name)
    {
        name = name.replaceAll("[^A-Z a-z]", "");

        name = name.trim();
        name = name.toLowerCase();
        name = name.replaceAll("\\s+", " ");
        String[] words = name.split(" ");
        name = "";
        for (String word : words)
        {
            name += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
        }
        return name.trim();
    }

    /**
     * This method let user to input string from terminal
     * 
     * @param prompt             (String) Prompt message
     * @param isRemoveWhitespace (Boolean) Set to true if want string input has no
     *                           whitespaces, false for including whitespaces
     * @return String without whitespaces if {@code isRemoveWhitespace} set to false
     *         and vice vera.
     */
    public static String consoleInput(String prompt, boolean isRemoveWhitespace)
    {
        System.out.print(prompt);
        @SuppressWarnings("resource")
        Scanner input = new Scanner(System.in);
        if (isRemoveWhitespace)
        {
            return removeWhiteSpace(input.nextLine());
        }
        else
        {
            return input.nextLine();
        }
    }

    /**
     * Methods to remove every whitesapces in given string
     * 
     * @param str (String) Input string
     * @return String with no whitespaces
     */
    public static String removeWhiteSpace(String str)
    {
        return str.replaceAll("\\s+", "");
    }

    /**
     * Method to let user to retry doing something
     * 
     * @return {@code true} if user press Y button only, {@code false} if user press
     *         other keys
     */
    public static boolean isTryAgain()
    {
        boolean TryAgain = consoleInput("\nDo you want to try again? (Y/All):  ", true).equalsIgnoreCase("Y");
        if (!TryAgain)
        {
            return false;
        }
        return true;
    }

    /**
     * This function is to check whether if the input that match the condition, also
     * remove all whitespaces between characters
     * 
     * @param prompt    (String) Prompt message
     * @param Condition (String) ">0i" for all positive integer, "ddmmyyyyy" for Date string,
     *                  ">0d" for all positive double
     * @param optionToGoBack (integer) Pass a number for an option to go back, -1 for nothing
     * @return {@code null} if the input not match the condition, a formatted string if match
     */
    public static String consoleInput(String prompt, String Condition, int optionToGoBack)
    {
        while (true)
        {
            switch (Condition)
            {
                case ">0i":
                {
                    try
                    {
                        String original = consoleInput(prompt, true);
                        if (original.equals(String.valueOf(optionToGoBack)))
                            return "0";
                            
                        int number = Integer.parseInt(original);
                        if (number <= 0)
                        {
                            System.out.println("You must enter a number greater than 0.");
                            if (!isTryAgain())
                                return null;
                        }
                        else
                        {
                            return original;
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println("You must enter a number.");
                        if (!isTryAgain())
                        {
                            return null;
                        }
                    }
                    break;
                }
                case "ddmmyyyy":
                {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String tempInput = consoleInput(prompt, true);
                    if (tempInput.equals(String.valueOf(optionToGoBack)))
                    {
                        return tempInput;
                    }
                    try
                    {
                        LocalDate date = LocalDate.parse(tempInput, formatter);
                        return date.format(formatter);
                    }
                    catch (DateTimeParseException e)
                    {
                        System.out.println("Invalid date format: " + e.getMessage());
                        if (!isTryAgain())
                        {
                            return null;
                        }
                    }
                    break;
                }

                case ">0d":
                {
                    try
                    {
                        String original = consoleInput(prompt, true);
                        double number = Double.parseDouble(original);
                        if (number <= 0)
                        {
                            System.out.println("You must enter a number greater than 0.");
                            if (!isTryAgain())
                            {
                                return null;
                            }
                        }
                        else
                        {
                            return original;
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println("You must enter a number.");
                        if (!isTryAgain())
                        {
                            return null;
                        }
                    }
                    break;
                }
            }
        }
    }

    public static boolean checkInput(String checkString, String Condition)
    {
        switch (Condition)
        {
            case ">0i":
            {
                try
                {
                    int number = Integer.parseInt(checkString);
                    if (number <= 0)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
                catch (NumberFormatException e)
                {
                    return false;
                }
            }
            case "ddmmyyyy":
            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try
                {
                    LocalDate.parse(checkString, formatter);
                    return true;
                }
                catch (DateTimeParseException e)
                {
                    return false;
                }
            }
            case ">0d":
            {
                try
                {
                    double number = Double.parseDouble(checkString);
                    if (number <= 0)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
                catch (NumberFormatException e)
                {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * This is a method to check the input whether it in range {@code start} and
     * {@code end}
     * 
     * @param prompt (String) Prompt message
     * @param start  (Integer) Start number
     * @param end    (Integer) End number
     * @return {@code null} if outside, a digit string if valid
     */
    public static String consoleInput(String prompt, int start, int end)
    {
        while (true)
        {
            try
            {
                int number = Integer.parseInt(consoleInput(prompt, true));
                if (number <= start - 1 || number >= end + 1)
                {
                    System.out.printf("\nYou must enter a number in range %d and %d.\n", start, end);
                    if (!isTryAgain())
                    {
                        return null;
                    }
                }
                else
                {
                    return String.valueOf(number);
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("You must enter a number.");
                if (!isTryAgain())
                {
                    return null;
                }
            }
        }
    }

    public static boolean isDigit(String str)
    {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * This methoid is used to clear screen terminal in Windows only
     * <p>
     * Using command {@code cls} from Windows
     */
    public static void clearScreen()
    {
        try
        {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to pause the program until user press Enter key
     */
    @SuppressWarnings("resource")
    public static void pressEnterToContinue()
    {
        System.out.println("\nPress Enter key to continue...");
        try
        {
            System.in.read();
        }
        catch (Exception e)
        {
        }
        new Scanner(System.in).nextLine();
    }

    /**
     * This method is used to pause the program for ... seconds.
     * 
     * @param second (Integer) The second that program will be paused
     */
    public static void SystemSleep(int second)
    {
        try
        {
            TimeUnit.SECONDS.sleep(second);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Display the quit message and terminate the program
     */
    public static void QuitTheProgram()
    {
        System.out.print("\nExit the program !!!");
        System.exit(0);
    }
}