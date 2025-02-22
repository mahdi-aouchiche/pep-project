package Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Service.AccountService;
import Service.MessageService;
import Model.Account;
import Model.Message;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;


/**
 * Write endpoints and handlers for the social media controller.
*/
public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;
    ObjectMapper objMapper = new ObjectMapper();

    /**
     * Constructor for the SocialMediaController
    */
    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    /**
     * Write the endpoints in the startAPI() method.
     * The test suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
    */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        app.post("/register", this::postRegisterHandler);
        app.post("/login", this::postLoginHandler);               
        
        app.post("/messages", this::postMessagesHandler);
        app.get("/messages", this::getMessagesHandler);
        app.get("messages/{message_id}", this::getMessageByMessageIdHandler);
        app.delete("messages/{message_id}", this::deleteMessageByMessageIdHandler);
        app.patch("messages/{message_id}", this::patchMessageByMessageIdHandler);

        app.get("accounts/{account_id}/messages", this::getMessagesByAccountIdHandler);
        
        return app;
    }
        
    /**
     * This is a post register handler for the /register endpoint.
     * Returns a JSON object of the account. The response status should be 200 OK.
     * If it doesn't add a user successfully, the response status should be 400. (Client error)
     * @param ctx The Javalin Context object
     * @throws JsonProcessingException
    */
    private void postRegisterHandler(Context ctx) throws JsonProcessingException {          
        Account account = objMapper.readValue(ctx.body(), Account.class);
        Account newAccount = accountService.createNewAccount(account);
        
        if (newAccount != null) {
            ctx.status(200).json(objMapper.writeValueAsString(newAccount));
        } else {
            ctx.status(400);
        }
    }

    /**
     * This is a post login handler for the /login endpoint.
     * The login will be successful if and only if :
     *  - The username and password provided in the request body JSON match a real account existing on the database.
     * Returns a JSON object of the account. The response status should be 200 OK.
     * If the login is not successful, the response status should be 401. (Unauthorized)
     * @param ctx The Javalin Context object
    */
    private void postLoginHandler(Context ctx) throws JsonProcessingException {
        Account account = objMapper.readValue(ctx.body(), Account.class);
        Account existingAccount = accountService.getAccountByUsername(account);

        if(existingAccount != null) {
            ctx.status(200).json(objMapper.writeValueAsString(existingAccount));
        } else {
            ctx.status(401);
        }
    }
    
    /**
     * This is a post messages handler for the /messages endpoint.
     * Handler to post a new message to the message database.
     * The Jackson ObjectMapper should be used to convert the JSON  of the POST request into a Message object.
     * 
     * Checks if Posted_by refers to a real, existing user in the database.
     * 
     * If successful, the response body should contain:
     *  - A JSON of the message, including its message_id.
     *  - The response status should be 200, which is the default.
     *  - The new message should be persisted to the database.
     * If the creation of the message is not successful: 
     *  - the response status should be 400. (Client error)
     * 
     * @param ctx The Javalin Context object handles the information HTTP requests and generates response objects.
     *            it is made available by the app.post() method.
     * @throws JsonProcessingException will be thrown if there is an error in the JSON conversion to an object.
    */
    private void postMessagesHandler(Context ctx) throws JsonProcessingException {
        Message message = objMapper.readValue(ctx.body(), Message.class);
        
        // check if posted_by refers to a real, existing user.
        Account account = this.accountService.getAccountByAccoutId(message.getPosted_by());
        
        Message newMessage = null;

        if (account != null) {
            newMessage = messageService.createNewMessage(message);
        }

        if (newMessage != null) {
            ctx.status(200).json(newMessage);
        } else {
            ctx.status(400);
        }
    }

    /**
     * This is a get messages handler for the /messages endpoint.
     * 
     * The response body should contain a JSON representation of a list containing all messages retrieved from the database.
     * It is expected for the list to simply be empty if there are no messages.
     * The response status should always be 200, which is the default.
     * @param ctx The Javalin Context object
     */
    private void getMessagesHandler(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.status(200).json(messages);
    }

    /**
     * This is a get message by message_id handler for the /messages/{message_id} endpoint.
     * The response body should contain a JSON representation of the message identified by the message_id.
     * It is expected for the response body to simply be empty if there is no such message.
     * The response status should always be 200, which is the default.
     * @param ctx The Javalin Context object
     */
    private void getMessageByMessageIdHandler(Context ctx) {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));

        Message message = messageService.getMessagebyId(message_id);
        if (message != null) {
            ctx.status(200).json(message);
        } else {
            ctx.status(200);
        }
    }

    /**
     * This is a delete message by message_id handler for the /messages/{message_id} endpoint.
     * Our API should be able to delete a message identified by a message ID.
     * If the message existed:
     *  - the response body should contain the now-deleted message.
     *  - the response status should be 200, which is the default.
     * If the message did not exist:
     *  - the response status should be 200.
     *  - but the response body should be empty.
     * This is because the DELETE verb is intended to be idempotent,
     * ie, multiple calls to the DELETE endpoint should respond with the same type of response.
     * @param ctx The Javalin Context object
     */
    private void deleteMessageByMessageIdHandler(Context ctx) {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.deleteMessageByMessageId(message_id);

        if (message != null) {
            ctx.status(200).json(message);
        } else {
            ctx.status(200);
        }
    }

    /**
     * This is a patch message by message_id handler for the /messages/{message_id} endpoint.
     * This handler should update a message text identified by a message ID.
     * The request body should contain a new message_text values to replace the message identified by message_id.
     * The request body can not be guaranteed to contain any other information.
     * 
     * If the update is successful:
     *  - The response body should contain the message object (including message_id, posted_by, message_text, and time_posted_epoch), 
     *  - The response status should be 200, which is the default. 
     *  - The message existing on the database should have the updated message_text.
     * 
     * If the update of the message is not successful for any reason:
     *  - The response status should be 400. (Client error)
     * 
     * @param ctx The Javalin Context object
     */
    private void patchMessageByMessageIdHandler(Context ctx) throws JsonProcessingException  {
        // get the message id we want to update
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        
        // get the new message we want to use to update the old message
        Message new_message = objMapper.readValue(ctx.body(), Message.class);
       
        // updated message 
        Message updatedMessage = messageService.updateMessageByMessageId( message_id, new_message);
        
        if(updatedMessage == null) {
            ctx.status(400);
        } else {
            ctx.status(200).json(updatedMessage);
        }   
    }

    /**
     * This is a get messages by account_id handler for the /accounts/{account_id}/messages endpoint.
     * The response body should contain a JSON representation of a list containing all messages posted by a particular user,
     * which is retrieved from the database. 
     * It is expected for the list to simply be empty if there are no messages.
     * The response status should always be 200, which is the default.
     * 
     * @param ctx The Javalin Context object
     */
    private void getMessagesByAccountIdHandler(Context ctx) {
        int account_id = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getMessagesWrittenByUser(account_id);

        ctx.status(200).json(messages);
    }
}