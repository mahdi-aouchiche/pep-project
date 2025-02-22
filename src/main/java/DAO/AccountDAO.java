package DAO;

import Model.Account;
import Util.ConnectionUtil;
import java.sql.*;

public class AccountDAO {

    /**
     * Check if a an account_id already exists in the database
     * 
     * @param account_id The account_idd to check
     * @return Account if the account_id exists, null otherwise
    */
    public Account findAccountByAccount_Id(int account_id) {
        // get a connection to the database
        Connection conn = ConnectionUtil.getConnection();
        
        // SQL query to get all accounts with username
        String sql = "SELECT * FROM account WHERE account_id = ?";

        try {
            // Create a prepared statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, account_id);

            // Execute the query
            ResultSet rs = ps.executeQuery();

            // If the result set has a next value, then the username exists
            if(rs.next()){
                String username = rs.getString("username");
                String password = rs.getString("password");

                return new Account(account_id, username, password);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Check if a username already exists in the database
     * 
     * @param username The username to check
     * @return Account if the username exists, null otherwise
    */
    public Account findAccountByUsername(String username) {
        // get a connection to the database
        Connection conn = ConnectionUtil.getConnection();
        
        // SQL query to get all accounts with username
        String sql = "SELECT * FROM account WHERE username = ?";

        try {
            // Create a prepared statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            // Execute the query
            ResultSet rs = ps.executeQuery();

            // If the result set has a next value, then the username exists
            if(rs.next()){
                int account_id = rs.getInt("account_id");
                String password = rs.getString("password");

                return new Account(account_id, username, password);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    /**
     * Insert a new account to the Account table
     * @param account The account to create
     * @return The account that was created with account id, or null if the account was not created
    */
    public Account insertAccount(Account account) {
        // get a connection to the database
        Connection conn = ConnectionUtil.getConnection();
        
        // SQL query to insert a new account
        String sql = "INSERT INTO account(username, password) VALUES(?, ?);";

        try {
            // Create a prepared statement
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            // Execute the query
            ps.executeUpdate();

            ResultSet pkeyResultSet = ps.getGeneratedKeys();

            if(pkeyResultSet.next()){
                int generated_account_id = (int) pkeyResultSet.getLong(1);
              
                return new Account(generated_account_id, account.getUsername(), account.getPassword());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());         
        }
        return null;
    }
}
