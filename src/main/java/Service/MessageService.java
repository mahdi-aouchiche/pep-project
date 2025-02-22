package Service;

import Model.Message;
import DAO.MessageDAO;
import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;

    /**
     * No argument constructor to create a new MessageService with a new MessageDAO object.
    */
    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    /**
     * Constructor for a MessageService given an exesting MessageDAO
     * @param messageDAO The MessageDAO object to be used by the MessageService
    */
    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    /**
     * Posts a new message to the message database.
     * The creation of the message will be successful if and only if:
     *  - The message_text is not blank.
     *  - Is not over 255 characters.
     *  - Assumes that Posted_by refers to a real, existing user.
     * @param message The message to create
     * @return the inserted message
    */
    public Message createNewMessage(Message message) {
        String message_text = message.getMessage_text();
        int message_textLength = message_text.strip().length();
        
        if (message_text.isBlank() || message_textLength > 255) {
            return null;
        }
        return messageDAO.insertMessage(message);
    }

    /**
     * Find a message in the database by message_id
     * @param message
     * @return The message object that was found in the database
    */
    public Message getMessagebyId(int message_id) {
        return messageDAO.findMessageByMessageId(message_id);
    } 

    /**
     * Retrieve all existing messages.
     * @return List of all messages in the database
    */
    public List<Message> getAllMessages() {
        return messageDAO.findAllMessages();
    }

    /**
     * Retrieve all messages written by a user 
     * @param posted_by which is a foreign-key as account_id 
     * @return List of messages which are posted by a user
    */
    public List<Message> getMessagesWrittenByUser(int posted_by) {
        return messageDAO.findAllMessagesWrittenByUser(posted_by);
    }

    /**
     * Delete a message in the database by message_id
     * The deletion of an existing message should remove an existing message from the database.
     * @param message_id The message_id to search for in the database
     * @return The message object that was deleted from the database
     *         or null if no message was found
    */
    public Message deleteMessageByMessageId(int message_id) {
        // find the message by message_id
        Message message = messageDAO.findMessageByMessageId(message_id);

        // delete the message if it exists
        if(message != null) {
            messageDAO.deleteMessage(message_id);
        }
        
        // return the message that was deleted or null if no message was found
        return message;
    }

    /**
     * Update a message in the database using a message_id
     * The update of a message should be successful if and only if:
     *  - The message id already exists.
     *  - The new message_text is not blank.
     *  - The new message_text is not over 255 characters.
     * @param message_id The message_id to search for in the database
     * @param new message which has the the message_text to update in the database
     * @return The message object that was updated in the database
     *         or null if no message was updated
    */
    public Message updateMessageByMessageId(int message_id, Message new_message) {
        // check if message_id exists in database
        Message oldMessage = messageDAO.findMessageByMessageId(message_id);
        if(oldMessage == null) {
            return null;
        }
        // check if new message text is valid
        String newMessageText = new_message.getMessage_text();
        if(newMessageText.isBlank() || newMessageText.length() >= 255){
            return null;
        }

        // update the message_id in the message database with the new message text
        Boolean updated = messageDAO.updateMessageText(message_id, newMessageText);

        if(updated) {
            // get the updated message and return it
            return messageDAO.findMessageByMessageId(message_id);
        } else {
            return null;
        }
    }
}
