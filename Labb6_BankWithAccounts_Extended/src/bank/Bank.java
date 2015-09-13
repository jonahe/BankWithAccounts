package bank;

import java.io.File;

public class Bank {
	
	/*
	 *   Bank should take care of
	 *   - showing, creating, deleting accounts
	 *   - holding accounts
	 *   - searching / filtering accounts
	 *   - transferring money to / from accounts
	 *   
	 *   - An account file writing class? 
	 *   
	 *   - some kind of staring menu (options: search, create, transfer, change) 
	 * 
	 */
	
	private String bankName; 
	private BankMenu menu; // object containing methods for all option menus
	private AccountManager accountManager; // handles existing Accounts + their creation/deletion/modification
	
	
	Bank(String bankName, File accountFolder){
		this.bankName = bankName;
		this.accountManager = new AccountManager(accountFolder);
		this.menu = new BankMenu(accountManager);
	}
	
	public void visitBank(){
		// welcome message
		System.out.println("Hello and welcome to " + bankName);
		
		// start main menu
		menu.mainMenu();
		
		// if we end up here, user has exited main menu and wants to leave
		exitBank();
	}
	
	public void exitBank(){
		System.out.println("Godbye!");
	}

}
