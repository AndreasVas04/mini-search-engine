package project;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader("pages.txt"));
            RobinHoodHashing hashTable = new RobinHoodHashing();
            RobinHoodHashing keywordTable = new RobinHoodHashing(); // stores keyword -> AVL tree mappings
            // Create Operations handler
            Operations ops = new Operations();


            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String url = parts[0];
                int numKeys = Integer.parseInt(parts[1]);
                int numLinks = Integer.parseInt(parts[2]);

                // Check if this page was already created as a link target
                Element existingElement = hashTable.searchElement(url);
                WebPage page;

                if (existingElement != null) {
                    page = existingElement.getWebPage();
                } else {
                    page = new WebPage(url);
                    Element element = new Element(page);
                    hashTable.insert(element);
                }

                // Read and store keywords
                if (numKeys > 0) {
                    String[] keys = reader.readLine().split(",\\s*");
                    for (int i = 0; i < keys.length; i++) {
                        page.addKey(keys[i]);
                        ops.insertKeywordToTable(keywordTable, keys[i], page);

                    }
                }

                // Read and store outgoing links
                if (numLinks > 0) {
                    String[] links = reader.readLine().split(",\\s*");
                    for (int i = 0; i < links.length; i++) {
                        String linkUrl = links[i];
                        page.addLink(linkUrl);

                        Element targetElement = hashTable.searchElement(linkUrl);
                        if (targetElement != null) {
                            targetElement.getWebPage().incrementInDegree();
                        } else {
                            WebPage newPage = new WebPage(linkUrl);
                            newPage.incrementInDegree();
                            Element newElement = new Element(newPage);
                            hashTable.insert(newElement);
                        }
                    }
                }
            }

            reader.close();

            hashTable.printTable();

            
            // Main menu loop
            Scanner scanner = new Scanner(System.in);
            
            while (true) {
            	System.out.println("\n===== MENU =====");
            	System.out.println("1. Insert new web page");
            	System.out.println("2. Add a link between two web pages");
            	System.out.println("3. Remove a link from a web page");
            	System.out.println("4. Visit a web page");
            	System.out.println("5. Search pages by keyword (sorted by in-degree descending)");
            	System.out.println("6. Top-K most popular pages (by hit count)");
            	System.out.println("7. Find reachable pages from a given URL (BFS)");
            	System.out.println("8. Find shortest paths between all page pairs (saved to shortestPaths.txt)");
            	System.out.println("9. Suggest new links based on shared keywords");
            	System.out.println("10. Exit");
            	System.out.print("Choice: ");


                String input = scanner.nextLine();
                switch (input) {
                  case "1":
                	  ops.insertNewWebPage(hashTable, keywordTable, scanner);
                      ops.printAllWebPagesDetails(hashTable);
                        break;
                  case "2":
                	  ops.addNewLink(hashTable, scanner);
                	  ops.printAllWebPagesDetails(hashTable);
                      break;
                  case "3":
                      ops.removeLink(hashTable, scanner);
                      ops.printAllWebPagesDetails(hashTable);
                      break;
                  case "4":
                	  ops.visitWebPage(hashTable, scanner);
                      ops.printAllWebPagesDetails(hashTable);
                      break;
                  case "5":
                	    ops.printPagesWithKeyword(keywordTable, scanner);
                	    break;
                  case "6":
                	    ops.printAllWebPagesDetails(hashTable); 
                	    ops.printTopKPages(hashTable, scanner); 
                	    break;
                  case "7":
                	    ops.findConnectedWebPages(hashTable, scanner);
                	    break;
                  case "8":
                        ops.findShortestPaths(hashTable, scanner);
                        break;
                  case "9":
                	    ops.suggestNewLinks(hashTable);
                	    break;
                  case "10":
                        System.out.println("Exiting...");
                        scanner.close();
                        System.exit(0);
                        break;
                  default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }


        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}