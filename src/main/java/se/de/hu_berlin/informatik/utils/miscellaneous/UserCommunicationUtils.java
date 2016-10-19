package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.util.Scanner;

public final class UserCommunicationUtils {
	
	//suppress default constructor (class should not be instantiated)
	private UserCommunicationUtils() {
		throw new AssertionError();
	}
	
	public static boolean askUser(String question) {
		System.out.print(question + "(y/n): ");
		Scanner scan = new Scanner(System.in);
		String input;
		while(!(input = scan.nextLine()).equals("n") && !input.equals("y")) {
			System.out.print("Please type 'y' or 'n': ");
		}
		scan.close();
		if (input.equals("y")) {
			return true;
		} else {
			return false;
		}
	}

}
