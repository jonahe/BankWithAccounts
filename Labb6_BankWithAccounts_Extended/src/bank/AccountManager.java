package bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AccountManager {
	
	/*
	 * manage / create / edit / delete Accounts
	 * 
	 * methods like getAllAccounts, and getAccount(int id)
	 * 
	 */
	
	private File accountFolder;
	private Account[] accounts;
	private Account currentAccount; // the account we're dealing with right now
	
	
	
	AccountManager(File accountFolder){
		this.accountFolder = accountFolder;
		
		setupFolder();
		loadExistingAccounts();
	}
	
	public Account getCurrentAccount(){
		return currentAccount;
	}
	
	
	/**
	 * Returns all the accounts
	 * @return array with all accounts
	 */
	public Account[] getAllAccounts(){
		return accounts;
	}
	
	/**
	 * Deposit an amount to the current account
	 * @param amountToDeposit
	 * @return
	 */
	public boolean deposit(double amountToDeposit){
		if(amountToDeposit >= 0){
			double oldBalance = currentAccount.getBalance();
			currentAccount.setBalance(oldBalance + amountToDeposit);
			System.out.println("Deposit OK. Current balance is " + currentAccount.getFormattedBalance());
			return true;
		} else {
			System.out.println("You tried to deposit a negative value.");
			return false;
		}
	}
	
	/**
	 * Tries to withdraw an amount from the current account
	 * @param amountToWithdraw
	 * @return false if withdraw failed, else true
	 */
	public boolean withdraw(double amountToWithdraw){
		double currentBalance = currentAccount.getBalance();
		if(currentBalance >= amountToWithdraw){
			currentAccount.setBalance(currentBalance - amountToWithdraw); 
			System.out.println("Withdraw OK. Current balance is " + currentAccount.getFormattedBalance());
			return true;
		} else {
			System.out.println("Sorry, you don't have that kind of money.");
			return false;
		}
	}
	
	/**
	 * Does this account manager have an active account?
	 * @return false if currentAccount == null, else true.
	 */
	private boolean currentAccountIsSet(){
		return currentAccount != null;
	}
	
	
	/**
	 * Creates account from provided account info
	 * @param startBalance balance that new account should have
	 * @param accountOwnerName
	 * @param accountName
	 */
	public void createNewAccount(double startBalance, String accountOwnerName, String accountName){
		// the name of the file should be aN where N is the same number as account id
		String fileName = "a" + (Account.ID_COUNTER + 1);
		File accountFile = new File(accountFolder.getPath() + "//" + fileName + ".txt");
		
		Account newAccount = new Account(accountFile, accountOwnerName, accountName, startBalance);
		addToAccountArray(newAccount);
		// set this account as the current
		setCurrentAccount(newAccount);
		
	}
	
	/**
	 * Updates the list of accounts to include a newly created account
	 * @param accountToAdd
	 */
	private void addToAccountArray(Account accountToAdd){
		Account[] updatedAccountArray = new Account[accounts.length + 1];
		// copy every one of the previous ones
		for(int i = 0; i < accounts.length; i++){
			updatedAccountArray[i] = accounts[i];
		}
		// there is empty one slot at the end, left in the new array
		updatedAccountArray[updatedAccountArray.length -1] = accountToAdd;
		
		// Replace old with new
		accounts = updatedAccountArray;
	}
	
	/**
	 * Removes an Account from the account array.
	 * @param accountToRemove
	 */
	private void removeFromAccountArray(Account accountToRemove){
		Account[] updatedAccountArray = new Account[accounts.length - 1];
		// add every one from the old list, except the one we're supposed to remove
		int newIndex = 0;
		for(int i = 0; i < accounts.length; i++){
			Account ac = accounts[i];
			// most should be copied
			if(!ac.equals(accountToRemove)){
				updatedAccountArray[newIndex] = ac;
				newIndex++;
			} else {
				// skip the account. no increase in newIndex this time
				continue;
			}
		}
		// replace old with new
		accounts = updatedAccountArray;
	}
	
	/**
	 * Makes sure the account folder exists, else creates it
	 */
	private void setupFolder() {
		
		if(!accountFolder.exists()){
			System.out.println("The provided account folder didn't exist. Trying to create it..");
			boolean wasCreated = accountFolder.mkdirs();
			if(wasCreated){
				System.out.println("Account folder was created!");
			} else {
				System.out.println("Something whent wrong. Folder not created. Program abort!");
				System.exit(0);
			}
		}
	}
	
	/**
	 * Loads (creates) all the accounts in the specified folder, saves them in array
	 */
	private void loadExistingAccounts() {
		File[] filesInFolder = accountFolder.listFiles();
		
		// how many of the files are actual files (not directories)
		// we assume every file is a 
		int numberOfAccounts = 0;
		for(File file : filesInFolder){
			if(file.isFile()){
				numberOfAccounts++;
			}
		}
		
		accounts = new Account[numberOfAccounts];
		// now that the know the size of the array we can populate it
		int index = 0;
		for(File accountFile : filesInFolder){
			if(accountFile.isFile()){
				accounts[index] = createAccount(accountFile);
				index++;
			}
		}	
		
	}
	
	/**
	 * Takes an existing file for a account and creates an Account from it
	 * @param accountFile file containing account info
	 * @return an Account object
	 */
	private Account createAccount(File accountFile){
		// Read the basic information from the file first.
		// the first line of the file contains balance, ownerName, accountName
		// separated by a comma
		String firstLineOfFile = getFirstLineOfAccountFile(accountFile);
		String[] accountValues  = firstLineOfFile.split(",");
		double balance = Double.valueOf(accountValues[0]);
		String owerName = accountValues[1];
		String accountName = accountValues[2];
		
		return new Account(accountFile, owerName, accountName, balance); 
	}
	
	/**
	 *  Reads first line from accountFile - containing balance, ownerName, accountName
	 * @param accountFile
	 * @return first line of text from file
	 */
	private String getFirstLineOfAccountFile(File accountFile){
		String firstLine = "";
		try {
			FileReader reader = new FileReader(accountFile);
			BufferedReader bufferedReader = new BufferedReader(reader);
			firstLine = bufferedReader.readLine();
			bufferedReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return firstLine;	
	}
	
	/**
	 * Returns account with matching account name, or null if no match
	 * @param accountName
	 * @return account with matching account name, or null if no match
	 */
	public Account getAccountByAccountName(String accountName){
		for(Account a : accounts){
			if(a.getAccountName().equalsIgnoreCase(accountName)){
				return a;
			}
		}
		// if no match is found: return null
		return null;
	}
	
	/**
	 * Changes the current account - also saves changes to the previously active account
	 * @param newCurrentAccount
	 */
	public void setCurrentAccount(Account newCurrentAccount){
		// before we switch account, we want to save the changes made to the old one
		// TODO: see why first account is not saved
		System.out.println("Setting current account to account : " + newCurrentAccount.getAccountID());
		
		if(currentAccountIsSet()){
			saveChangesToCurrentAccount();			
		} else {
			System.out.println("No current account is set, so we won't save any changes there this time.");
		}
		
		currentAccount = newCurrentAccount;
		System.out.println("Current account is: " + currentAccount);
	}
	
	/**
	 * 
	 * @param accountToDelete
	 */
	public void deleteAccount(Account accountToDelete){
		// close resources, then delete the actual file
		accountToDelete.closeResources();
		boolean deleted = accountToDelete.getAccountFile().delete();
		System.out.println("Physical file deleted : " + deleted);
		// destroy all references:
		// remove from accounts array
		removeFromAccountArray(accountToDelete);
		if(currentAccountIsSet()){
			if(currentAccount.equals(accountToDelete)){
				// remove from currentAccount
				currentAccount = null;
			}
		}
	}
	
	/**
	 * Saves changes to latest current account, closes all open resources
	 */
	public void closeAccountManager(){
		if(currentAccountIsSet()){
			saveChangesToCurrentAccount();
		}
		closeAllAccountResources();
	}
	
	/**
	 * Saves the changes of the current account - is run each time setCurrentAccount() is used.
	 */
	private void saveChangesToCurrentAccount(){
		//TODO: figure out why all changes saves to the last file only
		System.out.println("Saving changes to account: " + currentAccount.getAccountID());
		// TODO: see if setupWriter fits better here.. see effect of also closing the resources here
		// Figure out if it's the setup or the closing that solves the problem
		currentAccount.setupWriter();
		currentAccount.saveBalanceOwnerAndAccName();
		currentAccount.saveTransactionsHistory();
		currentAccount.closeResources();
	}
	
	/**
	 * Go through all accounts and close readers/writers - should be done as late as possible (on bank exit?)
	 */
	private void closeAllAccountResources(){
		for(Account acc : accounts){
//			acc.saveBalanceOwnerAndAccName();
//			acc.saveTransactionsHistory();
			System.out.println("Closing account: " + acc.getAccountID());
			acc.closeResources();
		}
	}
	
	
}
