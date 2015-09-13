package bank;

import java.util.Locale;
import java.util.Scanner;

/**
 * Class for showing option menus and starting the appropriate process
 * @author Erik
 *
 */

public class BankMenu {
	
	private final double QUIT = 5;
	
	private AccountManager accountManager;
	private Scanner stringScanner = new Scanner(System.in);
	private Scanner numberScanner = new Scanner(System.in);
	
	BankMenu(AccountManager accountManager){
		this.accountManager = accountManager;
		stringScanner.useLocale(Locale.US);
	}
	
	/**
	 * Main menu asking to create account or do something with existing account. Branches out to other methods
	 */
	public void mainMenu(){
		// options to choose from
		double optionUseExistingAccount = 1;
		double optionNewAccount = 2;
		double choice = 0;
		String mainMessage = "What do you want to do?\n1) Do something with an existing account, 2) Create a new account, or 5) Exit bank";
		
		boolean accountSelected = false;
		do {
			choice = askForAndGetNextDouble(mainMessage);
			if(choice == optionUseExistingAccount){
				// make user select account, or search for an account (and select it for use)
				accountSelected = selectAccount();
				if(accountSelected){
					accountOptions();
				} else {
					continue;
				}
			} else if (choice == optionNewAccount){
				createNewAccountOptions();
			}
			
		} while(choice != QUIT);
		// user wants to quit - make sure everything is ok
		accountManager.closeAccountManager();
	}
	
	
	/**
	 * Options for current account (Transactions, Edit, Delete, Show) 
	 */
	private void accountOptions() {
		// options to choose from
		double oTransaction = 1;
		double oEdit = 2;
		double oDelete = 3;
		double showDetailedInfo = 4;

		double choice = 0;
		String accountMessage = 	"What do you want to do?\n"
								+ "1) Make a transaction\n"
								+ "2) Edit the account\n"
								+ "3) Delete the account\n"
								+ "4) Show detailed info\n"
								+ "5) Exit to main menu";
		do {
			choice = askForAndGetNextDouble(accountMessage);
			
			// only follow through if an account was selected
			if(choice == oTransaction){
				transaction();
			} else if (choice == oEdit){
				// edit();
			} else if (choice == oDelete){
				delete();
				// if account was deleted, this menu is irrelevant so quit it
				break;
			} else if (choice == showDetailedInfo){
				// search();
			}

		} while(choice != QUIT);
		
	}
	private void delete() {
		System.out.println("Currently selected account is : " + accountManager.getCurrentAccount());
		
		double delete = 1;
		String message = "Are you sure that you want to delete this account?\n"
						+ "1) Yes, delete it, 5) Cancel ";
		double choice  = 0;
		do {
			choice  = askForAndGetNextDouble(message);
			if(choice == delete){
				accountManager.deleteAccount(accountManager.getCurrentAccount());
				break;
			} else if(choice == QUIT){
				break;
			} else {
				continue; // invalid input, ask again
			}
			
		} while (true);
		
	}

	/**
	 * Transaction menu - deposit or withdraw
	 */
	
	public void transaction(){
		double deposit = 1;
		double withdraw = 2;
		double choice = 0;
		String message = "What kind of transaction? 1) Deposit, 2) Withdraw, 5) Go back to previous menu";
		do {
			choice = askForAndGetNextDouble(message);
			if(choice == deposit){
				deposit();
			} else if (choice == withdraw){
				withdraw();
			}
		} while(choice != QUIT);
	}
	
	
	/**
	 * Deposit menu
	 */
	private void deposit() {
		String message = "Enter an amount: ";
		boolean depositOK = false;
		double amount = 0;
		do {
			amount = askForAndGetNextDouble(message);
			depositOK = accountManager.deposit(amount);
		} while (depositOK == false);
		
	}
	
	/**
	 * Withdraw menu
	 */
	private void withdraw() {
		String message = "Enter an amount: ";
		boolean depositOK = false;
		double amount = 0;
		do {
			amount = askForAndGetNextDouble(message);
			depositOK = accountManager.withdraw(amount);
		} while (depositOK == false);
		
	}

	/**
	 * Menu for selecting account - makes that account the current account in AccountManager
	 */
	private boolean selectAccount(){
		Account[] accounts = accountManager.getAllAccounts();
		System.out.println("The existing " + accounts.length + " accounts are: ");
		for(Account account : accounts){
			System.out.println(account);
		}
		// options 
		String oSearch = "search";
				
		String message = "Which account do you want to work with? To choose: \n"
						+ "Enter the account name of one of the existing accounts above,\n"
						+ "Or enter 'search' to search for account or account info,\n"
						+ "Or enter 5 to go back to the previous menu: ";
		String choice ="";
		do {
			choice = askForAndGetNextString(message);
			Account choosenAccount = accountManager.getAccountByAccountName(choice);
			if(choosenAccount != null){
				// we don't know what option we made before
				accountManager.setCurrentAccount(choosenAccount);
				// account was chosen, return true
				return true;
			} else {
				try{
					if(Double.parseDouble(choice) == QUIT){ // Throws exception if it cannot be parsed as double
						// user don't want to select an account. no account selected return false
						return false;
					} else if(choice.equalsIgnoreCase(oSearch)){
						// user wants to search for account ( then do something )
						search();
					}else {
						// user just failed to enter a valid account - try again
						System.out.println("No account with that name was found.");
						continue;
					}
				} catch(NumberFormatException ex ){
					// ignore. We know that the exception is not important
					System.out.println("Unimportant NumberFormatException..");
				}
			}
		} while(true);
		
	}

	//TODO: make this a boolean to see if an account was eventually chosen.
	private void search() {
		// options
		double byOwnerName = 1;
		double byAccountName = 2;
		double byAccountId = 3;
		
		String message = "Search by 1) Owner name, 2) Account name, 3) Account id (or enter 5 to exit) ";
		double choice = 0;
		do {
			choice = askForAndGetNextDouble(message);
			if(choice == byOwnerName){
				//TODO: methods for search (in AccountManager)
				// that return an array with 0 or more Accounts in it? 
			} else if(choice == byAccountName){
				
			}  else if(choice == byAccountId){
				
			}
		} while(choice != QUIT);
		
	}

	/**
	 * Menu for account creation
	 */
	public void createNewAccountOptions(){
		
		String message = "Enter the name of the account owner: ";
		String accountOwnerName = askForAndGetNextString(message);

		message = "Enter a name for the account :";
		String accountName = askForAndGetNextString(message);

		// just use one constructor. if user enters no balance, just send 0

		// create it now? Or enter balance first?
		message = "Do you want to 1) Enter the starting amount of money first, 2) Create (empty) account now :";
		double choice  = askForAndGetNextDouble(message);


		if(choice == 1){
			message = "How much should this account have to start with?";
			double startBalance = askForAndGetNextDouble(message);
			// use account manager to create the new Account
			accountManager.createNewAccount(startBalance, accountOwnerName, accountName);
			// let user choose what to do with this account
			accountOptions();

		} else {
			// use account manager to create the new Account - with zero as startBalance
			accountManager.createNewAccount(0, accountOwnerName, accountName);
			// let user choose what to do with this account
			accountOptions();
		}
	}
	
	public double askForAndGetNextDouble(String askMessage){
		
		while(true){
			System.out.println(askMessage);
			// if user input can be read as a double
			if(numberScanner.hasNextDouble()){
				return numberScanner.nextDouble();
			} else {
				// input can not be read as as double. 
				// blame the user. remove the input and try again
				System.out.println("That's not a valid input!");
				numberScanner.nextLine();
				continue; 
				
			}
		}
	}
	
	public String askForAndGetNextString(String askMessage){
		System.out.println(askMessage);
		return stringScanner.nextLine();
	}
}
