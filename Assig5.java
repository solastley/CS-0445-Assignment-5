// Solomon Astley, Id #3938540
// CS 0445 Assignment 5 Ramirez, Rec. Tue. 10:00
// April 5th, 2016
// This program is the main program for assignment 5

import java.util.*;
import java.lang.*;
import java.io.*;

public class Assig5
{
	private BinaryNode<Character> root;
	private ArrayList<TableEntry> table = new ArrayList<TableEntry>();

	public static void main(String [] args) throws IOException
	{
		String filename = args[0];
		new Assig5(filename);
	}

	public Assig5(String filename) throws IOException
	{
		File inFile = new File(filename);
		Scanner inScan = new Scanner(inFile);

		root = huffmanTree(inScan);

		StringBuilder b = new StringBuilder();
		huffmanTable(root, b);

		sortTable();

		System.out.println("The Huffman Tree has been restored\n");
		StringBuilder prompt = new StringBuilder("Please choose from the following:\n");
		prompt.append("1) Encode a text string\n");
		prompt.append("2) Decode a Huffman string\n");
		prompt.append("3) Quit");
		Scanner userScan = new Scanner(System.in);
		boolean goAgain = true;
		while (goAgain)
		{
			System.out.println(prompt.toString());
			int userChoice = Integer.parseInt(userScan.nextLine());
			switch (userChoice)
			{
				case 1: System.out.println("Enter a string from the following characters:");
						printOptions();
						String userWord = userScan.nextLine();
						String response = encodeString(userWord);
						if (response != null)
						{
							System.out.println("Huffman string:");
							System.out.println(response);
						}
						else
							System.out.println("There was an error in your text string\n");
						break;
				case 2: System.out.println("Here is the encoding table:");
						printTable();
						System.out.println("Please enter a Huffman string (one line, no spaces):");
						String userHuff = userScan.nextLine();
						String hResponse = decodeString(userHuff);
						if (hResponse != null)
						{
							System.out.println("Text string:");
							System.out.println(hResponse);
							System.out.println();
						}
						else
							System.out.println("There was an error in your Huffman string\n");
						break;
				case 3:	System.out.println("Goodbye");
						goAgain = false;
						break;
			}
		}
	}

	private BinaryNode<Character> huffmanTree(Scanner inScan)
	{
		BinaryNode<Character> newNode;
		String [] fileChars = inScan.nextLine().split(" ");
		if (fileChars[0].equals("L"))
		{
			char newChar = fileChars[1].charAt(0);
			newNode = new BinaryNode<Character>(newChar);
			return newNode;
		}
		else
		{
			newNode = new BinaryNode<Character>('O');
			newNode.setLeftChild(huffmanTree(inScan));
			newNode.setRightChild(huffmanTree(inScan));
			return newNode;
		}
	}

	private void huffmanTable(BinaryNode<Character> node, StringBuilder b)
	{
		if (node.getData() != 'O')
		{
			table.add(new TableEntry(node.getData(), b.toString()));
			return;
		}
		else
		{
			b.append("0");
			huffmanTable(node.getLeftChild(), b);
			b.deleteCharAt(b.length() - 1);
			b.append("1");
			huffmanTable(node.getRightChild(), b);
			b.deleteCharAt(b.length() - 1);
			return;
		}
	}

	private void printTable()
	{
		for (int i = 0; i < table.size(); i++)
		{
			System.out.println(table.get(i).getKey() + ": " + table.get(i).getValue());
		}
		return;
	}

	private void printOptions()
	{
		for (int i = 0; i < table.size(); i++)
		{
			System.out.print(table.get(i).getKey());
		}
		System.out.println();
	}

	private void sortTable()
	{
		TableEntry [] tArray = new TableEntry [table.size()];
		for (int i = 0; i < table.size(); i++)
		{
			tArray[i] = table.get(i);
		}

		for (int i = 1; i < tArray.length; i++)
		{
			TableEntry temp = tArray[i];
			int j;
			for (j = i - 1; j >= 0 && temp.compareTo(tArray[j]) == -1; j--)
			{
				tArray[j + 1] = tArray[j];
			}
			tArray[j + 1] = temp;
		}
		table = new ArrayList<TableEntry>(Arrays.asList(tArray));
	}

	private String encodeString(String str)
	{
		StringBuilder encoded = new StringBuilder("");
		for (int i = 0; i < str.length(); i++)
		{
			Character c = new Character(str.charAt(i));
			int index = Character.getNumericValue(c) - 10;
			if (index >= table.size())
				return null;
			encoded.append(table.get(index).getValue() + "\n");
		}
		return encoded.toString();
	}

	private String decodeString(String str)
	{
		StringBuilder decoded = new StringBuilder("");
		BinaryNode<Character> node = root;
		for (int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			if (c == '0')
			{
				if (!node.hasLeftChild())
					return null;
				node = node.getLeftChild();
				if (node.getData() != 'O')
				{
					decoded.append(node.getData());
					node = root;
				}
				else if (i == str.length() - 1)
					return null;
			}
			else
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
		}
		return decoded.toString();
	}

	private class TableEntry
	{
		private char key;
		private String value;

		public TableEntry(char newKey, String newValue)
		{
			key = newKey;
			value = newValue;
		}

		public char getKey()
		{
			return key;
		}

		public String getValue()
		{
			return value;
		}

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