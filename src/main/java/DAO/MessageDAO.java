package DAO;

import Model.Message;
import Util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    /**
     * Get all messages from the message database
     * @return List of all messages in the database
    */
    public List<Message> findAllMessages() {
        List<Message> messages = new ArrayList<Message>();
        
        // get a connection to the database
        Connection conn = ConnectionUtil.getConnection();
        
        // SQL query to get all messages
        String sql = "SELECT * FROM message;";

        try {
            // Create a prepared statement
            PreparedStatement ps = conn.prepareStatement(sql);

            // Execute the query
            ResultSet rs = ps.executeQuery();

            // Iterate through the result set
            while (rs.next()) {
                Message message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setMessage_text(rs.getString("message_text"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                messages.add(message);
            }

            return messages;
        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    } 

    /**
     * Retrieve all messages written by a particular user (account_id)
     * @return List of all messages in the database written by posted_by
    */
    public List<Message> findAllMessagesWrittenByUser(int posted_by) {  
        List<Message> messages = new ArrayList<Message>();
        
        // get a connection to the database
        Connection conn = ConnectionUtil.getConnection();
        
        // SQL query to get all messages
        String sql = "SELECT * FROM message Where posted_by = ?;";

        Message message = null;

        try {
            // Create a prepared statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, posted_by);

            // Execute the query
            ResultSet rs = ps.executeQuery();

            // Iterate through the result set
            while (rs.next()) {
                message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setMessage_text(rs.getString("message_text"));
                message.setPosted_by(posted_by);
                message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
                   
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    } 

    /**
     * Find a message in the database by message_id
     * @param message_id The message_id to search for in the database
     * @return The message object that was found in the database
     *         or null if no message was found
     */
    public Message findMessageByMessageId(int message_id) {
        // get a connection to the database
        Connection conn = ConnectionUtil.getConnection();
        
        // SQL query to get all messages with message_id
        String sql = "SELECT * FROM message WHERE message_id = ?;";

        Message message = null;

        try {
            // Create a prepared statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, message_id);

            // Execute the query
            ResultSet rs = ps.executeQuery();

            // Iterate through the result set
            if (rs.next()) {
                message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setMessage_text(rs.getString("message_text"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
            }        
        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return message;
    }

    /**
     * Insert a new message to the Massage table
     * @param message The message to create
     * @return The message object that was added to the database
     *         or null if the message was not added.
    */
    public Message insertMessage(Message message) {
        // get a connection to the database
        Connection conn = ConnectionUtil.getConnection();
        
        // SQL query to insert a new message
        String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);";

        try {
            // Create a prepared statement
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
           
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            // Execute the query
            ps.executeUpdate();

            ResultSet pkeyResultSet = ps.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generated_message_id = (int) pkeyResultSet.getLong(1);
                return new Message(generated_message_id, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }
        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Delete a message in the message database by message_id
     * @param message_id The message_id to search for in the database
    */
    public void deleteMessage(int message_id) {
        // get a connection to the database
        Connection conn = ConnectionUtil.getConnection();
        
        // SQL query to delete a message with message_id
        String sql = "DELETE FROM message WHERE message_id = ?;";

        try {
            // Create a prepared statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, message_id);

            // Execute the query
            ps.executeUpdate();
        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Update the message_text of the given message_id
     * @param message_id The message_id of the message we want to update
     * @param new message text to replace the old text message
     * @return true if message updated succefully, false otherwise
    */
    public Boolean updateMessageText(int message_id, String newMessageText) {
        // get a connection to the database
        Connection conn = ConnectionUtil.getConnection();

        // SQL query to update a message_id with the new message text
        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?;";

        try {
            // Create a prepared statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newMessageText);
            ps.setInt(2, message_id);
            int rowsUpdated = ps.executeUpdate();
            
            if(rowsUpdated > 0) {
                return true;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
