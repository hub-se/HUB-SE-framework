package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.util.Scanner;

public final class UserCommunicationUtils {
	
	//suppress default constructor (class should not be instantiated)
	private UserCommunicationUtils() {
		throw new AssertionError();
	}
	
	/**
	 * Prints the given question to the console and queries the user to type
	 * 'y' or 'n' to answer the question. Repeats querying until either answer 
	 * is typed correctly.
	 * @param question
	 * the question to ask to the user
	 * @return
	 * the answer (true if the user typed 'y', false if he typed 'n')
	 */
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
