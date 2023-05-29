package edu.touro.cs.mcon364;

import java.sql.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseConnection {
    public static void ConnectingDB(Set<String> emails) throws ClassNotFoundException {
        Logger.getLogger(WebScraping.class.getName()).log(Level.INFO, "Connecting to DB");
        Map<String, String> env = System.getenv();
        String endpoint = env.get("dbendpoint");
        System.out.println(endpoint);
        Logger.getLogger(WebScraping.class.getName()).log(Level.INFO, endpoint);
        String connectionUrl = // specifies how to connect to the database
                "jdbc:sqlserver://" + endpoint + ";"
                        + "database=Aaronson_Yonathan;"
                        + "user=admin;"
                        + "password=mcon364_417;"
                        + "encrypt=false;"
                        + "trustServerCertificate=false;"
                        + "loginTimeout=30;";
        ResultSet resultSet = null;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        Logger.getLogger(WebScraping.class.getName()).log(Level.INFO, "*** INSERTING INTO DB***");
        StringBuilder builder = new StringBuilder("INSERT INTO Emails (EmailAddress) VALUES (?)");
        int emailCount = emails.size();
        for (int i = 0; i < emails.size() - 1; i++) {
            builder.append(",(?)");
        }

        Logger.getLogger(WebScraping.class.getName()).log(Level.INFO, "*** STRING BUILDER ***");
        String insertSql2 = builder.toString();
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             PreparedStatement prepsInsertProduct = connection.prepareStatement(insertSql2, Statement.RETURN_GENERATED_KEYS);) {
            {
                Iterator<String> set = emails.iterator();
                for (int i = 0; i < emails.size(); i++){
                    prepsInsertProduct.setString(i+1, set.next());
                }
                prepsInsertProduct.execute();

                resultSet = prepsInsertProduct.getGeneratedKeys();
                while (resultSet.next()) {
                    System.out.println(resultSet.getInt(1)); //number of emails inserted into DB
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
