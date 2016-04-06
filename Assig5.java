// Solomon Astley, Id #3938540
// CS 0445 Assignment 5 Ramirez, Rec. Tue. 10:00
// April 5th, 2016
// This program is the main program for assignment 5

import java.util.*;
import java.lang.*;
import java.io.*;

public class Assig5
{
	// instance variables for ease of access in other methods
	private BinaryNode<Character> root; // BinaryNode to keep track of tree
	private ArrayList<TableEntry> table = new ArrayList<TableEntry>(); // Huffman Table

	public static void main(String [] args) throws IOException
	{
		String filename = args[0]; // Get filename from command line
		new Assig5(filename); // Instantiate Assig5
	}

	public Assig5(String filename) throws IOException
	{
		// Create file scanner
		File inFile = new File(filename);
		Scanner inScan = new Scanner(inFile);

		// This method call creates the Huffman Tree from the input file
		root = huffmanTree(inScan);

		// This method call creates the encoding table from the Huffman tree
		StringBuilder b = new StringBuilder();
		huffmanTable(root, b);

		// Table is initially unsorted, so this sorts it alphabetically
		Collections.sort(table);

		System.out.println("The Huffman Tree has been restored\n");

		// Create prompt for the user with StringBuilder
		StringBuilder prompt = new StringBuilder("Please choose from the following:\n");
		prompt.append("1) Encode a text string\n");
		prompt.append("2) Decode a Huffman string\n");
		prompt.append("3) Quit");

		Scanner userScan = new Scanner(System.in); // Scanner for user input
		boolean goAgain = true; // boolean for multiple user input iterations
		while (goAgain)
		{
			System.out.println(prompt.toString()); // prompt the user for what to do
			int userChoice = Integer.parseInt(userScan.nextLine());
			switch (userChoice)
			{
				// case 1 allows user to encode a string of their choosing
				case 1: System.out.println("Enter a string from the following characters:");
						printOptions(); // prints characters from Huffman table
						String userWord = userScan.nextLine(); // Gets user input

						String response = encodeString(userWord); // This method attempts
																// to encode the user input
						// If the user gave a valid input:
						if (response != null)
						{
							System.out.println("Huffman string:");
							System.out.println(response); // print the output
						}
						else // Error in user input
							System.out.println("There was an error in your text string\n");
						break;
				// case 2 allows user to decode a Huffman string of their choosing
				case 2: System.out.println("Here is the encoding table:");
						printTable(); // prints Huffman table
						System.out.println("Please enter a Huffman string (one line, no spaces):");
						String userHuff = userScan.nextLine(); // Gets user input

						String hResponse = decodeString(userHuff); // This method attempts to
																// decode the user input
						// If the user gave a valid input:
						if (hResponse != null)
						{
							System.out.println("Text string:");
							System.out.println(hResponse); // print the output
							System.out.println();
						}
						else // Error in user input
							System.out.println("There was an error in your Huffman string\n");
						break;
				// case 3 allows the user to quit
				case 3:	System.out.println("Goodbye");
						goAgain = false;
						break;
			}
		}
	}

	/** Creates a Huffman tree from a given file input
		@param inScan  The file scanner
		@return  The root of the node for the Huffman Tree **/
	private BinaryNode<Character> huffmanTree(Scanner inScan)
	{
		BinaryNode<Character> newNode;
		String [] fileChars = inScan.nextLine().split(" "); // Get next line and split
		
		// If next char represents a leaf node, then make a new node and return
		if (fileChars[0].equals("L"))
		{
			char newChar = fileChars[1].charAt(0);
			newNode = new BinaryNode<Character>(newChar);
			return newNode;
		}
		// Else next char represents an inner node which is guaranteed to have two
		// children, so make a new node with dummy data and set its children
		// recursively, then return.
		else
		{
			newNode = new BinaryNode<Character>('O');
			newNode.setLeftChild(huffmanTree(inScan));
			newNode.setRightChild(huffmanTree(inScan));
			return newNode;
		}
	}

	/** Creates an encoding table from a Huffman Tree
		@param node  The root of our Huffman Tree
		@param b  A StringBuilder to create binary strings for the table **/
	private void huffmanTable(BinaryNode<Character> node, StringBuilder b)
	{
		// If at a leaf node, create a new table entry and return
		if (node.getData() != 'O')
		{
			table.add(new TableEntry(node.getData(), b.toString()));
			return;
		}
		// If at an interior node, do the following:
		else
		{
			b.append("0"); // Append a 0 to the string
			huffmanTable(node.getLeftChild(), b); // Recurse left
			b.deleteCharAt(b.length() - 1); // Remove 0 from the string
			b.append("1"); // Append a 1 to the string
			huffmanTable(node.getRightChild(), b); // Recurse right
			b.deleteCharAt(b.length() - 1); // Remove 1 from the string
			return;
		}
	}

	/** Prints the entire Huffman Table **/
	private void printTable()
	{
		for (int i = 0; i < table.size(); i++)
		{
			System.out.println(table.get(i).getKey() + ": " + table.get(i).getValue());
		}
		return;
	}

	/** Prints the letters that are encoded by the Huffman Table **/
	private void printOptions()
	{
		for (int i = 0; i < table.size(); i++)
		{
			System.out.print(table.get(i).getKey());
		}
		System.out.println();
	}

	/** Encodes user input text into a Huffman string
		@param str  User input string
		@return  Encoded Huffman string **/
	private String encodeString(String str)
	{
		StringBuilder encoded = new StringBuilder("");
		for (int i = 0; i < str.length(); i++)
		{
			Character c = new Character(str.charAt(i)); // Get next char in str
			int index = Character.getNumericValue(c) - 10; // Convert to numeric value

			// If value is not in table or invalid, return null for error
			if (index >= table.size() || index < 0)
				return null;

			encoded.append(table.get(index).getValue() + "\n"); // Call getValue() method
																// to get associated char
		}
		return encoded.toString();
	}

	/** Decodes Huffman string into text string
		@param str  User inputted Huffman string
		@return  Decoded text string **/
	private String decodeString(String str)
	{
		StringBuilder decoded = new StringBuilder("");
		BinaryNode<Character> node = root;
		for (int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i); // Get next char in Huffman string
			if (c == '0') // If it's a zero
			{
				if (!node.hasLeftChild()) // If not valid, return null
					return null;
				node = node.getLeftChild(); // Set node to left child
				if (node.getData() != 'O') // If it's now a leaf node
				{
					decoded.append(node.getData()); // Append data
					node = root; // Set node to root of tree again
				}

				// Else if the end of the input string has been reached,
				// but the current node is not a leaf node, it must be
				// an invalid input. Return null.
				else if (i == str.length() - 1)
					return null;
			}

			// Repeat above process if char is a 1, but go right
			else if (c == '1')
			{
				if (!node.hasRightChild())
					return null;
				node = node.getRightChild();
				if (node.getData() != 'O')
				{
					decoded.append(node.getData());
					node = root;
				}
				else if (i == str.length() - 1)
					return null;
			}
			else // If char is not a 1 or a 0, invalid input. Return null.
				return null;
		}
		return decoded.toString();
	}

	/** Inner class to represent an entry in the Huffman Table **/
	private class TableEntry implements Comparable<TableEntry>
	{
		private char key; // The char that represents the table entry
		private String value; // The bit string associated with the entry

		/** Constructor method for TableEntry
			@param newKey  Char that represents table entry
			@param newValue  Bit string associated with the entry **/
		public TableEntry(char newKey, String newValue)
		{
			key = newKey;
			value = newValue;
		}

		// Getter method for key
		public char getKey()
		{
			return key;
		}

		// Getter method for value
		public String getValue()
		{
			return value;
		}

		/** Method to implement the Comparable interface for sorting the table
			@param t  TableEntry that this is being compared to
			@return  Integer value based on the comparison **/
		public int compareTo(TableEntry t)
		{
			if (key < t.getKey())
				return -1;
			else if (key == t.getKey())
				return 0;
			else
				return 1;
		}
	}
}