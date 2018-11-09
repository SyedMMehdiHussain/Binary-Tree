import java.util.InputMismatchException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;


/**
 * Project 4 Binary Search Tree
 * @author Syed Muhammad Mehdi Hussain, E01472939
 *
 *The Objective of this Program is to implement a binary search tree 
 *using a text file and then streaming into randomaccess file and then creating a binary search tree
 *using the random access file
 */
public class Driver {

	static File temp;
	static RandomAccessFile raf;
	static Student rec;

	
	public static <E> void main(String[] args) throws IOException {
		System.out.println("*****Welcome to Binary Search Tree****");

	//we initialize here			
	
	File temp = null;
	RandomAccessFile raf = null;
	Student rec = null;
		
BinarySearchTree<Pair> bst = new BinarySearchTree<Pair>();

menu(raf,rec, bst, temp);	
	}//end of main menu
	
	/***
	 * Main menu where user will be able to access the functions	
	 * @param raf
	 * @param rec
	 * @param bst
	 * @param temp
	 * @throws IOException
	 */
	private static void menu(RandomAccessFile raf, Student rec,BinarySearchTree<Pair> bst,File temp) throws IOException {
		/*
		 * Asks the user for input
		 * we have a method for each of the 9 options 
		 */
		System.out.println("\n1- Make a random-access file\n"
				+ "2- Display a random-access file\n"
				+ "3- Build the index\n"
				+ "4- Display the index\n"
				+ "5- Retrieve a record\n"
				+ "6- Modify a record\n"
				+ "7- Add a new record\n"
				+ "8- Delete a record\n"
				+ "9- Exit ");

		/*
		 *  it takes the user input and goes to that method, if any invalid input is made,
		 *  the message will be given and the menu be displayed again		
		 */
		Scanner scan = new Scanner(System.in);
		try {
		boolean bError = true;
		int	 var = scan.nextInt();
		switch (var) {
		case 1:
		{
			createRandomAccessFile(bst, raf, rec, temp);
			break;
		}

		case 2:
		{
			displayRandomAccessFile(raf, rec,bst, temp);
			break;
		}

		case 3:
		{
			createIndex(raf,rec, bst, temp);
			break;
		}

		case 4:
		{
			displayIndex(raf,rec, bst, temp);
			break;
		}

		case 5:
		{
			retrieve(raf,rec, bst, temp);
			break;	
		}

		case 6:
		{
			updateRecord(raf,rec, bst,temp);
			break;
		}
		case 7:
		{
			addRecord(raf,rec, bst, temp);
			break;
		}		
		case 8:
		{
			deleteRecord(raf,rec,bst, temp);
			break;
		}
		case 9:
		{
			exit(temp);

		}
		bError = false;	
		}
		}
		catch (InputMismatchException e) {
			System.out.println("Invalid Value");
			menu(raf, rec, bst, temp);
		}

	}	
	/***********************************************************************************
	 **********(1) Making a Random access file from a text file ****************************
	 * @throws IOException
	 */
	
	private static void createRandomAccessFile(BinarySearchTree<Pair> bst,RandomAccessFile raf,Student rec, File temp) throws IOException {
		
	try {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("Input filename:");
		String filename = scan.nextLine();

		//take input from user the text file name
		Scanner fin = new Scanner(new FileInputStream(filename));

		System.out.print("Name the Randomaccess File you want to create: ");


			String rafName= scan.next();

			temp = new File(rafName);
			if (temp.exists()){
			     temp.delete();
			 }  
			

			raf = new RandomAccessFile(temp, "rw");

			rec = new Student();
			while (fin.hasNext()) {
				rec.readFromTextFile(fin);
				rec.writeToFile(raf);
				}
	} catch (FileNotFoundException e) {
	
		System.out.println("File is not Found");
		menu(raf,rec, bst, temp);
	}
	System.out.println("Back To Menu");
	menu(raf, rec, bst, temp);
}	
	
	/************************************************************************************************
	 * 2- Display the random-access file: here I have displayed the RandomAccess File
	 * like in the previous project
	 * @param raf
	 * @param rec
	 * @throws IOException
	 */
	private static void displayRandomAccessFile(RandomAccessFile raf,Student rec,BinarySearchTree<Pair> bst,File temp) throws IOException {
		
		System.out.println("******RandomAccesFile:********");			
		try {
			
			//printing the records from the random access file one by one
			
			
			for (int i=0; i<raf.length()/92;i++)
			{	
			raf.seek(i*92);
				rec.readFromFile(raf);	
				System.out.println(rec);
			}
			
		menu(raf, rec, bst, temp);	//back to menu
		 
			}catch (Exception e) {
		
			System.out.println("Back To Menu");
			menu(raf, rec, bst, temp);	//back to menu
	}	
}
	
/**
 * 3)Build the index: It asks the user to enter a database name (a random-access file). It reads
	the database records sequentially one at a time, and builds a binary search tree in the
	memory. Every node of the tree will contain a pair (KEY, ADDRESS) as well as two
	references to its subtrees, where KEY is the student ID field and the ADDRESS is the
	position of the record containing the ID in the database (first record in the database is at
	 zero, the second record is at position 1, and so on).
 * @param raf
 * @param rec
 * @param bst
 * @throws IOException
 */
	public static void createIndex(RandomAccessFile raf,Student rec, BinarySearchTree<Pair> bst,File temp) throws IOException{
		
	try {
		bst.reset(); // clear the tree of all the nodes
	
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the name of database file (randomaccess):");
		String dbName = scan.nextLine();
		
		temp = new File(dbName);
		if (temp.exists()){
		     temp.delete();
		     System.out.println("Creating Index is successfull");
		
		//making pairs of (KEY , ADDRESS) using the pair class, and then the binary search tree is made of type <Pair>
		  	
		for (int i = 0; i < raf.length()/92; i++) {
  				raf.seek((i * 92) + 80); 
   				Pair myPair =	new Pair(raf.readInt(),i);
  				
   				//check for deleted records, the deleted records will be omitted
   				
   				if(raf.readInt()==0){
  					continue;
  				}
   				//adding Node<E> to the binary search tree
  				bst.add(myPair);
			}
		}	else{
			System.out.println("enter correct database name");
			
			createIndex(raf, rec, bst, temp);
		}
	} catch (Exception e) {
		//will handle any exceptions in building the index
		System.out.println("Index not built");
		displayRandomAccessFile(raf, rec, bst, temp);
		createIndex(raf, rec, bst, temp);
	}
	menu(raf,rec, bst, temp);
}

	/**
	 * Display the index: It asks the user to indicate whether (s)he wishes to see the entire index or
	part of the index. If the entire index is desired, it displays all the KEY/ADDRESS pairs starting
	with the one at the root of the tree and then uses a breadth first traversal (level-by-level
	traversal) to display the remaining pairs (visits the nodes at each level left to right). If only part of
	the index is desired, it prompts the user to enter the starting key. It searches the binary search
	tree to find the node containing that key. If the search is successful, it displays all the
	KEY/ADDRESS pairs in the subtree rooted at that node using breadth first traversal. Otherwise it
	displays a message indicating the failure of the search.
	 * @param raf
	 * @param rec
	 * @param bst
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private static void displayIndex(RandomAccessFile raf,Student rec, BinarySearchTree<Pair> bst,File temp) throws IOException {
		
		try { 
			System.out.println("******* DISPLAY INDEX MENU ********");
			
			//asks the user to select to see the full index or part of the index till the last node
					
			System.out.println("All Indexes: A"+'\n' + "Partial Indexes: B");
			
			Scanner scan = new Scanner(System.in);
			String var = scan.nextLine();
			
			//this will print whole index from createIndex method 

			if (var.equalsIgnoreCase("A")){					
		
				bst.breadthFirstTraversal();
				
				//now going back to main menu
		
				menu(raf,rec,bst, temp);
			}else if (var.equalsIgnoreCase("B")) {
			
				// here part of the key will be taken in and matched with the possible keys
				try{
				
					System.out.println("Enter the starting Key of the Index:");
					Scanner scan1 = new Scanner(System.in);
					
					int startingKey = scan1.nextInt();	
					

				//takes the input from where the rest of the index should be printed 
					
					try {

						Pair startPair = bst.get(startingKey);
						bst.BFSTraversal(startPair);
						
					}	catch (InputMismatchException e)
						{
						System.out.println("Invalid Key,Please input correct Key!");
						}

					// this exception will catch the null pointer because the last node's address points at null 						//

					System.out.println("End of Tree!\n");
					menu(raf,rec, bst, temp);
					
					}	catch (InputMismatchException e) {

				// this exception will catch the wrong input		
					System.out.println("Failure of search!");
					menu(raf,rec, bst, temp);	
					//going back to main menu
				} 
				System.out.println("Back To Menu");
				menu(raf, rec, bst, temp);
			}
			menu(raf, rec, bst, temp);
		}	catch (InputMismatchException e) {

			System.out.println("Failure of search!Input Correct value");
			menu(raf,rec, bst, temp);	
			}
	}
	

	
	/**
	 * 5) Retrieve a record: It asks the user to enter a student ID (a key value). It searches the index
(the binary search tree) for the key, and if the search is successful it uses the ADDRESS
associated with that key to retrieve and then display the corresponding record. If the search is
unsuccessful, it prints a message indicating the failure of the search.
	 * @param <E>
	 * @param raf
	 * @param rec
	 * @param bst
	 * @throws IOException
	 */

	@SuppressWarnings("resource")
	private static <E> void retrieve(RandomAccessFile raf,Student rec, BinarySearchTree<Pair> bst,File temp) throws IOException {
		System.out.println("******Retrieve a record:*****");
		try {
			try {
				//ask the user to input key to be retrieved
				
				System.out.println("Enter the Student Id (Key) to be retrieved:");	
				Scanner scan = new Scanner(System.in);
				int num	=scan.nextInt();
			
				// finds the key and returns the address where that record is in the random access file

				int address=find(num,bst); //
				
				System.out.println(address);
				
				raf.seek(address*92);	//points to the starting of the record in random access file
				rec.readFromFile(raf);	//reads that particular record 
				System.out.println(rec); //prints the retrieved record
				
				menu(raf, rec, bst, temp);
			} catch (InputMismatchException  e) {
				
				System.out.println("Search Failure, enter correct key");
				menu(raf, rec, bst, temp);
			}
		} catch (IOException e) {
			System.out.println("Search Failure, enter correct key");
			menu(raf, rec, bst, temp);
		}	
	}

	// this is a function to return the ADDRESS of a Pair(node) by taking the Key

	
	public static int find(int id,BinarySearchTree<Pair> bst){
	{	
	if	(bst.get(id).getKey()==id){
				return bst.get(id).getAddress();
			}
		}
		return -1;
	}
	
	
/**
 * 6) Modify a record: It asks the user to enter a student ID (a key value). It searches the index (the
binary search tree) for the key, and if the search is successful it uses the ADDRESS associated
with that key to retrieve the corresponding record. It allows the user to modify any fields of this
record, except the student ID field, and then it writes the modified record over the original record
in the database. Note that modifying a record doesn’t require any changes to the index (the
binary search tree) because the user is not allowed to change the ID field.
 * @param raf
 * @param rec
 * @param bst
 * @throws IOException 
 */
@SuppressWarnings("resource")
private static void updateRecord(RandomAccessFile raf, Student rec,BinarySearchTree<Pair> bst,File temp) throws IOException {
	System.out.println("******Modify a Record:******");

	try {
		//ask the user to input key to be modified
		
		System.out.println("Enter the Student Id (Key) to be Modified:");	
		Scanner scan = new Scanner(System.in);
		int num	= scan.nextInt();
		
		int address = find(num,bst);
		
		//prints the address where the record is on the random access file
		
		System.out.println(address);
		
		raf.seek(address*92);
		rec.readFromFile(raf);
		
		//prints the record before being modified
		
		System.out.println(rec);

while(true){		
		Scanner scan1 = new Scanner(System.in);
		System.out.println("\nFor modifying First name enter F\n "
				+ "\nFor modifying Last name enter L\n "
				+ "\nFor modifying GPA enter G");
		String field = scan1.next();
					// Prompt for new first name
			if(field.equalsIgnoreCase("f")){ 
					System.out.println("Please Enter the new First Name:");
					String newFirstName = new String();
					newFirstName = scan.next();
					rec.setfName(newFirstName);
				
				}
					// Prompt for new last name
			if(field.equalsIgnoreCase("l")){
					System.out.println("Please Enter the new Last Name:");
					String newLastName = new String();
					newLastName = scan.next();
					rec.setlName(newLastName);
					
			}
					// Prompt for new gpa
			if(field.equalsIgnoreCase("g")){
					System.out.println("Please Enter the new GPA number:");
					Scanner scan2 = new Scanner(System.in);
					double newGpa = scan2.nextDouble();
					rec.setGPA(newGpa);
			}
			
			System.out.println("Do you want to modify any other field?"
					+ "\nPress any key for YES, and N for NO");
			String input = scan.next();
			
			if(input.equalsIgnoreCase("n"))
				break;
		}
					
					raf.seek(address*92);
					rec.writeToFile(raf);
					
					displayRandomAccessFile(raf, rec,bst, temp);
			menu(raf,rec,bst, temp);
	
	} catch (InputMismatchException e) {
	
		System.out.println("Search Failure, Enter correct key");
		
		menu(raf,rec,bst, temp);
	}
}


/**
 * 7)Add a new record: It asks the user to enter data for the new record. This new record will be
appended to the end of the database (the random-access file). Next, the student ID (the key
value) and the position (ADDRESS) of the record just written to the end of the database must be
added to the index. This means a new node containing the pair (KEY, ADDRESS) must be added
to the binary search tree (the index).
 * @param raf
 * @param rec
 * @param bst
 * @throws IOException
 */
private static void addRecord(RandomAccessFile raf, Student rec, BinarySearchTree<Pair> bst,File temp) throws IOException {
	System.out.println("******Add a Record:******");
	
	// ask the user to input new record
	
	System.out.print("Enter your first name, last name, student ID, and GPA: ");

	Scanner keyIn = new Scanner(System.in);

	rec.readFromTextFile(keyIn);

	raf.seek(raf.length());
	
	rec.writeToFile(raf);
	
	bst.reset();// resets the binary search tree so that the new record can be added 

					//now add new record entered to the binary search tree
	try {
		for (int i = 0; i < raf.length()/92; i++) {
				raf.seek((i * 92) + 80); 
				Pair myPair =	new Pair(raf.readInt(),i);
				bst.add(myPair);
				
		}
	} catch (NullPointerException e) {

		System.out.println("Failed!");
	}
	
	
	bst.breadthFirstTraversal();

	//display raf after adding new record
	displayRandomAccessFile(raf, rec,bst, temp);

menu(raf, rec, bst, temp);
}


/**8)Delete a record: It asks the user to input the student ID (the key value) of the record that
needs to be deleted. It searches the index (the binary search tree) for the key value, and if the
search is successful it uses the ADDRESS associated with that key value to delete the
corresponding record from the database (using lazy deletion). It also deletes the node
containing the key value (i.e., (KEY, ADDRESS)) from the index (the binary search tree).
 * 
 * @throws IOException 
*/
@SuppressWarnings("resource")
private static void deleteRecord(RandomAccessFile raf, Student rec, BinarySearchTree<Pair> bst,File temp) throws IOException {
	System.out.println("******Delete a record:*****");
	try {
		
		// ask the user to input the record to be deleted
	
		System.out.println("Enter the Student Id (Key) to be deleted:");	
		Scanner scan = new Scanner(System.in);
		int studentId	= scan.nextInt();
		
		//(1) DELETE FROM RandomAccess File

		int address = find(studentId,bst);
		
		raf.seek((address)*92);
		rec.readFromFile(raf);
		
		System.out.println("Record to be deleted:"+rec);
		
		//Now Mark the record as "delete" followed by 14 whitespaces
		
		rec.setfName("Delete              ");
		rec.setlName("DELETE              ");
		rec.setID(0000);
		rec.setGPA(00);

		raf.seek(address*92);
		rec.writeToFile(raf);
		System.out.println(rec);
		
		//(2) Now DELETE the record from binary search tree
		
		//Now using the key from remove the Node from the binary search tree

		Pair pairToBeDeleted = new Pair(address, studentId);	
		
		bst.delete(pairToBeDeleted);

		//display raf after deletion
		
		displayRandomAccessFile(raf, rec,bst, temp);
		
		System.out.println(rec);
menu(raf,rec,bst, temp);
	} catch (InputMismatchException e) {

		System.out.println("Please enter correct Key!");
		menu(raf,rec,bst, temp);
	}	
}

private static void exit(File temp) throws IOException {
		System.exit(0);
		// deleting the file on exit
		
		temp.deleteOnExit();
	}

}
	
