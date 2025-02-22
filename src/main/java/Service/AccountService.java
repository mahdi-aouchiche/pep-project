package Service;

import Model.Account;
import DAO.AccountDAO;

public class AccountService {

    private AccountDAO accountDAO;

    /**
     * No argument constructor to create a new AccountService with a new AccountDAO object.
    */
    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    /**
     * Constructor for a AccountService given an exesting AccountDAO
     * @param accountDAO The AccountDAO object to be used by the AccountService
    */
    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }
    
    /**
     * Create a new account
     * requirements: 
     *  1. username is not blank
     *  2. password is at least 4 characters long
     *  3. an Account with that username does not already exist
     * 
     * @param account The account to create
     * @return The account that was created, or null if the account was not created
    */
    public Account createNewAccount(Account account) {
        String u_name = account.getUsername();
        String p_word = account.getPassword();
        int p_wordLength = p_word.strip().length();
        Account existing_account = accountDAO.findAccountByUsername(u_name);
        
        if (u_name.isBlank() || p_wordLength < 4 || existing_account != null) {
            return null;
        }

        return accountDAO.insertAccount(account);
    }

    /**
     * Find an account in the database by username
     * @param account The account to verify if it exists in database  
     * @return The existing account, or null if no account is found
    */
    public Account getAccountByUsername(Account account) {
        Account existignAccount = accountDAO.findAccountByUsername(account.getUsername());
        if (existignAccount == null || !existignAccount.getPassword().equals(account.getPassword())) {
            return null;
        }

        return existignAccount;
    }

    /**
     * Find an account in the database by account_id
     * @param account_id The account_id to verify if it exists in database  
     * @return The existing account, or null if no account is found
    */
    public Account getAccountByAccoutId(int account_id) {
        Account existignAccount = accountDAO.findAccountByAccount_Id(account_id);
        if (existignAccount == null){
            return null;
        }

        return existignAccount;
    }
}

