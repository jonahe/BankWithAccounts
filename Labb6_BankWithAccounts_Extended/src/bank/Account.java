package bank;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class Account {
	
	public static int ID_COUNTER;
	
	private int accountID;
	private String owner;
	private String accountName;
	private double balance;
	private double[] latest10transactions = new double[10];
	private File accountFile;
	private boolean startingBalanceProvided = false;
	
	// Reader
	private FileReader reader;
	private BufferedReader readBuffer;
	// Writer
	public static FileWriter writer;
	public static BufferedWriter writeBuffer;
	
	Account(File accountFile, String owner, String accountName){
		this.owner = owner;
		this.accountName = accountName;
		this.accountFile = accountFile;
		this.accountID = ++ID_COUNTER;
		// make ready for use
		setupReader();
	
		// setupWriter();
		
	}
	
	
	
	Account(File accountFile, String owner, String accountName, double startBalance){
		this.owner = owner;
		this.accountName = accountName;
		this.accountFile = accountFile;
		this.balance = startBalance;
		this.accountID = ++ID_COUNTER;
		startingBalanceProvided = true;
		System.out.println("Accountfile of account nr " + accountID + " is :"  + accountFile.getPath());
		// make ready for use
		setupReader();
		//TODO: see if this setup is better in another place? maybe wait until it's needed
		// setupWriter();
		
	}
	
	public File getAccountFile(){
		return accountFile;
	}
	
	public int getAccountID(){
		return accountID;
	}
	
	public String getAccountName(){
		return accountName;
	}
	
	public void setBalance(double newBalance){
		// figure out what kind of transaction was made.
		// if newBlance - balance is a positive number, a deposit was made
		// if it's a negative, a withdraw was made for amount of 
		double latestTransaction = newBalance - balance;
		updateTransactionHistory(latestTransaction);
		balance = newBalance;
	}
	public double getBalance(){
		return balance;
	}
	
	private void setupReader(){
		
		try {
			boolean fileCreated = false;
			// check if file exists, else create it
			if(!accountFile.exists()){
				System.out.println("File '" + accountFile.getPath() + "' doesn't exists yet. Will try to create it:");
				fileCreated = accountFile.createNewFile();
				System.out.println("File created = " + fileCreated);
			}
			
			reader = new FileReader(accountFile);
			readBuffer = new BufferedReader(reader);
			
			
			// if file was not created earlier, it means it's an old file
			// so we should try to read values.
			// but only if we also didn't get a start value in the constructor
			if(!fileCreated && !startingBalanceProvided){
				// read the first line and get balance (first value): 
				String firstLine = readBuffer.readLine();
				// get comma separated values
				String[] valuesInFirstLine = firstLine.split(",");
				balance = Double.parseDouble(valuesInFirstLine[0]);
			} else {
				// must still read a line, so that file pointer is at line 2 for the next step
				readBuffer.readLine();
			}

//			// interest rate 7.93%
//			balance *= 1.0793;


			// read in saved transactions
			int index = 0;
			while(readBuffer.ready()){
				if(index < latest10transactions.length){
					double transValue = Double.parseDouble(readBuffer.readLine());
					latest10transactions[index] = transValue;
					index++;
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getFormattedBalance(){
		return String.format(Locale.US, "%.2f", balance);
	}

	
	
	
	// new value is first value in the new array, the last old value is ignored/removed and every other value
	// gets index + 1
	public void updateTransactionHistory(double latestValue){
		double[] newTransactionsHistory = new double[10];
		// first index get the new value
		newTransactionsHistory[0] = latestValue;
		// The new value in index 1 - 9 is same as the old values in index 0 - 8 
		int oldIndex = 0;
		for(int index = 1; index < newTransactionsHistory.length; index++){
			newTransactionsHistory[index] = latest10transactions[oldIndex];
			oldIndex++;
		}
		// old transaction array to new one
		latest10transactions = newTransactionsHistory;
	}
	
	public void setupWriter(){
		try{
			
			writer = new FileWriter(accountFile);
			writeBuffer = new BufferedWriter(writer);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void insertMoney(double value){
		updateTransactionHistory(value);
		balance += value;
		System.out.println("OK! Balance is now: " + getFormattedBalance());
	}
	
	public void takeOutMoney(double value){
		
		if(value > balance){
			System.out.println(	"You don't have that kind of money! "
								+ "Choose a smaller amount or insert money first");
		} else {
			updateTransactionHistory(-value);
			balance -= value;
			System.out.println("Ok! Your new balance is: " + getFormattedBalance());
		}
	}
	
	public void printLatestTransactions(){
		System.out.println("Your latest transactions were: ");
		for(int i = 0; i < latest10transactions.length; i++){
			double transValue = latest10transactions[i];
			if(transValue != 0){
				if(transValue > 0){
					System.out.println("+" + transValue);
				} else {
					System.out.println(transValue);
				}
			}
		}
	}
	
	public void saveBalanceOwnerAndAccName(){
		try{
			System.out.println("Saving to file " + accountFile.getPath());
			// first line in file will be balance, then new line. (transaction history will follow)
			writeBuffer.write(String.valueOf(balance));
			writeBuffer.write(",");
			writeBuffer.write(owner);
			writeBuffer.write(",");
			writeBuffer.write(accountName);
			writeBuffer.newLine();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void saveTransactionsHistory(){
		try{
			// continues where the saveSaldo() write left off. from line 2 and on
			for(int i = 0; i < latest10transactions.length; i++){
				double transValue = latest10transactions[i];
				if(transValue != 0.0){
					writeBuffer.write(String.valueOf(transValue));
					writeBuffer.newLine();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void closeResources(){
		try {
			if(readBuffer != null){
				readBuffer.close();				
			}
			if(writeBuffer != null){
				writeBuffer.close();				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public String toString(){
		return String.format(	Locale.US, 
								"Account [id: %d, owner: %s, account name: %s, balance: %.2f]", 
								accountID,
								owner,
								accountName,
								balance);
	}
}
