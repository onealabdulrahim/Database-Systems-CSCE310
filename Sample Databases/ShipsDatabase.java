/**
 * Oneal Abdulrahim
 * CSCE 310 - 500
 * Homework 5
 */

import java.sql.*;
import java.lang.*;
import java.util.*;

public class ShipsDatabase {
    // Connection to the database on CS servers (MariaDB)
    public static final String DB_LOCATION = "database-new.cse.tamu.edu";
    public static final String DB_NAME = "oneal.abdulrahim-shipsDB";
    public static final String DB_USER = "oneal.abdulrahim";
    public static final String DB_PASS = ""*******";";
    
        /**
         * Attempts connection to the database given the URL.
         * @param
         * @return connection   Connection datatype containing active connection to DB
         * @throws SQLException To be considered when calling connect() 
         */
        public static Connection connect() throws SQLException {
            Connection connection = null;

            try {
                String connectionString = "jdbc:mysql://"+ DB_LOCATION + "/" + DB_NAME;
                Class.forName ("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(connectionString, DB_USER, DB_PASS);
                //System.out.println ("Database connection established!");
            } catch (Exception e) {
                System.out.println("Connection Issue: " + e.getMessage());
            }
            
            return connection;
        }

        /**
         * Populates an ArrayList with given characteristics for the Classes table
         * @return The List<String> type with 6 fields as strings
         */
        public static List<String> getClassesData() {
            List<String> result = new ArrayList<String>();
            List<String> attributes = Arrays.asList("class", "type", "country",
                                            "numGuns", "bore", "displacement");
            
            Scanner userInput = new Scanner(System.in);

            System.out.println("Now entering data for a new Class tuple...");

            // aha a loop! I cheaped out on the pretty printing this time
            for (String attribute : attributes) {
                System.out.print("Please enter the " + attribute + ": ");
                result.add(userInput.next());
            }

            return result;
        }

        /**
         * Populates an ArrayList with given characteristics for the Ships table
         * @param className  The name of the class of the ship
         * @return           The List<String> type with 6 fields as strings
         */
        public static List<String> getShipsData(String className) {
            List<String> result = new ArrayList<String>();
            List<String> attributes = Arrays.asList("name", "class", "launched");
            
            Scanner userInput = new Scanner(System.in);

            System.out.println("Now entering data for a new Ship tuple...");

            // When inserting the class, use the parameter.
            for (String attribute : attributes) {
                if (attribute.equals("class")) {
                    result.add(className);
                } else {
                    System.out.print("Please enter the " + attribute + ": ");
                    result.add(userInput.next());
                }
            }

            return result;
        }

        /**
         * Given an unverified ArrayList of attributes, insert into Classes.
         * Note: parameters are unchecked
         * @param attributes    Assumed well-formed 6 field arguments
         */
        public static void insertIntoClasses(List<String> attributes) {
            Connection connection;
            try {
                connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO classes (class,type,country,numGuns,bore,disp) "
                  + "VALUES (?, ?, ?, ?, ?, ?)");

                // let's just put it in a loop
                for (int i = 1; i <= attributes.size(); i++) {
                    preparedStatement.setString(i,attributes.get(i - 1));
                }
                
                preparedStatement.executeUpdate();

                connection.close(); // close connection to DB -- always
            } catch (SQLException exception) {
                System.err.println("SQL Exception during connection: ");
                exception.printStackTrace();
            }
        }
        
        /**
         * Given an unverified ArrayList of attributes, insert into Ships.
         * Note: parameters are unchecked
         * @param attributes    Assumed well-formed 3 field arguments
         */
        public static void insertIntoShips(List<String> attributes) {
            Connection connection;
            try {
                connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO ships (name,class,launched) "
                  + "VALUES (?, ?, ?)");

                // let's just put it in a loop
                for (int i = 1; i <= attributes.size(); i++) {
                    preparedStatement.setString(i,attributes.get(i - 1));
                }
                
                preparedStatement.executeUpdate();

                connection.close(); // close connection to DB -- always
            } catch (SQLException exception) {
                System.err.println("SQL Exception during connection: ");
                exception.printStackTrace();
            }
        }

        public static void main(String[] args) {
            Scanner userInput = new Scanner(System.in);
            String next = "";
            
            List<String> classData = new ArrayList<String>();
            List<String> shipsData = new ArrayList<String>();
            
            try {
                System.out.println("Howdy! First, let's get a new class tuple: ");
                classData = getClassesData();

                System.out.println("Now we have created the new class, "
                                + classData.get(0)
                                + ". Inserting this data into classes table...\n\n");
                
                insertIntoClasses(classData);
                
                // unelegant
                while (!next.equals("exit")) {
                    System.out.println("Type add to continue adding ships, or type exit: ");
                    next = userInput.next();
                    if (next.equals("add")) {
                        System.out.println("Adding a new ship...\n\n");
                        shipsData = getShipsData(classData.get(0));
                        System.out.println("Inserting this data into ships table...");
                        insertIntoShips(shipsData);
                    } else {
                        next = "exit";
                    }
                }

                userInput.close(); // keep it clean           

            } catch (Exception e) {
                System.err.println("SQL Exception during connection: ");
                e.printStackTrace();
            }
            
        }
}