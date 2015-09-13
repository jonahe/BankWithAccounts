package bank;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		
		
		File accountFolder = new File("accounts\\");
		Bank bankSEB = new Bank("SEB", accountFolder);
		bankSEB.visitBank();
		

	}

}
