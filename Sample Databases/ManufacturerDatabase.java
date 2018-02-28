/**
 * Oneal Abdulrahim
 * CSCE 310 - 500
 * Homework 5
 */

import java.sql.*;
import java.lang.*;
import java.util.*;

public class ManufacturerDatabase {

    // Connection to the database is here on my computer
    // Please adjust accordingly!
    public static final String URL = "jdbc:derby:C:\\Apache\\db-derby-10.14.1.0-bin\\bin\\manfDB;create=false";

    /**
     * Attempts connection to the database given the URL.
     * @param
     * @return connection   Connection datatype containing active connection to DB
     * @throws SQLException To be considered when calling connect() 
     */
    public static Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        // System.out.println("Connection to manfdb successful! \n...\n"); // yay
        return connection;
    }

    /**
     * Given an active connection to the manfDB, pretty prints laptop data
     * given the model number.
     * @param model      The integer model number of the laptop
     * @param connection Connection datatype containing active connection to DB
     */
    public static void printLaptopData(int model, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM LAPTOP WHERE MODEL=?"); // SQL query
            preparedStatement.setInt(1, model);
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) { // for as long as there are results...
                StringBuilder sb = new StringBuilder(); // my favorite function in lang
                sb.append("Laptop product detail: model # " + model + "\n")
                  .append("\tCPU:\t" + resultSet.getString("speed") + " MHz\n")
                  .append("\tRAM:\t" + resultSet.getString("ram") + " Gb\n")
                  .append("\tHDD:\t" + resultSet.getString("hd") + " Gb\n")
                  .append("-->$" + resultSet.getString("price") + "\n");
                System.out.println(sb.toString());
            }
        } catch (SQLException exception) {
            System.err.println("SQL Exception during connection: ");
            exception.printStackTrace();
        }
    }

    /**
     * Given an active connection to the manfDB, pretty prints PC data
     * given the model number.
     * @param model      The integer model number of the PC
     * @param connection Connection datatype containing active connection to DB
     */
    public static void printPCData(int model, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM PC WHERE MODEL=?");
            preparedStatement.setInt(1, model);
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) { // for as long as there are results...
                StringBuilder sb = new StringBuilder();
                sb.append("Desktop PC product detail: model # " + model + "\n")
                  .append("\tCPU:\t" + resultSet.getString("speed") + " MHz\n")
                  .append("\tRAM:\t" + resultSet.getString("ram") + " Gb\n")
                  .append("\tHDD:\t" + resultSet.getString("hd") + " Gb\n")
                  .append("-->$" + resultSet.getString("price") + "\n");
                System.out.println(sb.toString());
            }
        } catch (SQLException exception) {
            System.err.println("SQL Exception during connection: ");
            exception.printStackTrace();
        }
    }

    /**
     * Given an active connection to the manfDB, pretty prints printer data
     * given the model number.
     * @param model      The integer model number of the printer
     * @param connection Connection datatype containing active connection to DB
     */
    public static void printPrinterData(int model, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM PRINTER WHERE MODEL=?");
            preparedStatement.setInt(1, model);
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) { // for as long as there are results...
                StringBuilder sb = new StringBuilder();
                sb.append("Desktop PC product detail: model # " + model + "\n")
                  .append("\tColor:\t" + resultSet.getString("color") + "\n")
                  .append("\tType:\t" + resultSet.getString("type") + "\n")
                  .append("-->$" + resultSet.getString("price") + "\n");
                System.out.println(sb.toString());
            }
        } catch (SQLException exception) {
            System.err.println("SQL Exception during connection: ");
            exception.printStackTrace();
        }
    }

    /**
     * Given an active connection to the manfDB, searches and pretty prints
     * information about all products given the current manufacturer from the
     * PRODUCTS table in the database.
     * @param manufacturer String containing the "make" of the product
     */
    public static void printMFData(String manufacturer) {
        Connection connection;
        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM PRODUCT WHERE MAKER=?");
            preparedStatement.setString(1, manufacturer);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) { // check each type, corresponding function call
                if (resultSet.getString("type").equals("laptop")) {
                    printLaptopData(resultSet.getInt("model"), connection);
                } else if (resultSet.getString("type").equals("pc")) {
                    printPCData(resultSet.getInt("model"), connection);
                } else if (resultSet.getString("type").equals("printer")) {
                    printPrinterData(resultSet.getInt("model"), connection);
                } else {
                    System.out.println("No products found! Try another maker");
                }
            }
            connection.close(); // close connection to DB -- always
        } catch (SQLException exception) {
            System.err.println("SQL Exception during connection: ");
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        String next = "";

        while (!next.equals("exit")) { 
            System.out.print("Howdy! Please enter a manufacturer to search" +
                               " for products or \"exit\"! Maker name: ");
            next = userInput.next();
            printMFData(next); // input sanitized with PreparedStatements :)
        }

        userInput.close(); // keep it clean
    }
}