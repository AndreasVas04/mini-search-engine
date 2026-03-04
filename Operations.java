package project;

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Operations {
	
    // Print all web pages with full details
    public void printAllWebPagesDetails(RobinHoodHashing table) {
        Element[] elements = table.getTable();

        System.out.println("\nLoaded WebPages:");
        for (int i = 0; i < elements.length; i++) {
            Element e = elements[i];
            if (e != null) {
                WebPage wp = e.getWebPage();

                // Basic info
                System.out.println("URL: " + wp.getUrl() + ", Hits: " + wp.getHits());

                // Keyword hashes
                System.out.print("  Keywords Hashes: ");
                singlyLinkedList keys = wp.getKeys();
                for (int j = 0; j < keys.size(); j++) {
                    System.out.print(keys.get(j));
                    if (j < keys.size() - 1) System.out.print(" -> ");
                }
                System.out.println(" -> null");

                // Link hashes
                System.out.print("  Links Hashes: ");
                singlyLinkedList links = wp.getLinks();
                for (int j = 0; j < links.size(); j++) {
                    System.out.print(links.get(j));
                    if (j < links.size() - 1) System.out.print(" -> ");
                }
                System.out.println(" -> null");

                // InDegree
                System.out.println("  InDegree: " + wp.getInDegree());
                System.out.println();
            }
        }
    }
 // Operation 1: Insert a new web page (with keywords and links)
    public void insertNewWebPage(RobinHoodHashing hashTable, RobinHoodHashing keywordTable, Scanner scanner) {

        String[] parts;
        String url = "";
        int numKeys = 0;
        int numLinks = 0;

        // Read input line: url numKeys numLinks
        while (true) {
            System.out.println("Enter the web page, number of keys, number of links (format: url numKeys numLinks):");
            String line1 = scanner.nextLine();
            parts = line1.trim().split(" ");
            if (parts.length == 3) {
                try {
                    url = parts[0];
                    numKeys = Integer.parseInt(parts[1]);
                    numLinks = Integer.parseInt(parts[2]);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format. Please enter integers.");
                }
            } else {
                System.out.println("Invalid input. Expected: url numKeys numLinks");
            }
        }

        // Page already exists
        if (hashTable.search(url)) {
            System.out.println("Web page already exists.");
            return;
        }

        // Create new page
        WebPage page = new WebPage(url);

        // Read keywords if any
        if (numKeys > 0) {
            while (true) {
                System.out.println("Enter " + numKeys + " keyword(s) separated by commas:");
                String[] keys = scanner.nextLine().split(",\\s*");
                if (keys.length == numKeys) {
                    for (int i = 0; i < keys.length; i++) {
                        String keyword = keys[i].trim();
                        page.addKey(keyword); // add to page
                        insertKeywordToTable(keywordTable, keyword, page); // update keyword index
                    }
                    break;
                } else {
                    System.out.println("Wrong number of keywords.");
                }
            }
        }

        // Read links if any
        if (numLinks > 0) {
            while (true) {
                System.out.println("Enter " + numLinks + " link(s) separated by commas:");
                String[] links = scanner.nextLine().split(",\\s*");
                if (links.length == numLinks) {
                    for (int i = 0; i < links.length; i++) {
                        String linkUrl = links[i].trim();
                        page.addLink(linkUrl);

                        Element target = hashTable.searchElement(linkUrl);
                        if (target != null) {
                            target.getWebPage().incrementInDegree();
                        } else {
                            WebPage temp = new WebPage(linkUrl);
                            temp.incrementInDegree();
                            Element tempElement = new Element(temp);
                            hashTable.insert(tempElement);
                        }
                    }
                    break;
                } else {
                    System.out.println("Wrong number of links.");
                }
            }
        }

        // Insert page into main hash table
        Element newElement = new Element(page);
        hashTable.insert(newElement);

        System.out.println("Web page added successfully.");
    }

    
    // Operation 2: Add a link between two pages
    public void addNewLink(RobinHoodHashing table, Scanner scanner) {
        System.out.print("Enter the source URL: ");
        String sourceUrl = scanner.nextLine().trim();

        System.out.print("Enter the target URL: ");
        String targetUrl = scanner.nextLine().trim();

        Element sourceElement = table.searchElement(sourceUrl);
        Element targetElement = table.searchElement(targetUrl);

        if (sourceElement == null || targetElement == null) {
            System.out.println("One or both pages not found. Link was not added.");
            return;
        }

        WebPage sourcePage = sourceElement.getWebPage();

        // Check if link already exists
        if (!sourcePage.addLink(targetUrl)) {
            System.out.println("Link already exists. Not added again.");
            return;
        }

        // Link added successfully — increment target's inDegree
        targetElement.getWebPage().incrementInDegree();

        System.out.println("Link added successfully.");
    }
    
    // Operation 3: Remove a link from a page
    public void removeLink(RobinHoodHashing table, Scanner scanner) {
        // Read source URL
        System.out.print("Enter the URL of the page to remove a link from: ");
        String sourceUrl = scanner.nextLine().trim();

        // Check if page exists
        Element sourceElement = table.searchElement(sourceUrl);
        if (sourceElement == null) {
            System.out.println("Web page not found.");
            return;
        }

        WebPage sourcePage = sourceElement.getWebPage();
        singlyLinkedList links = sourcePage.getLinks();

        // Page has no outgoing links
        if (links.isEmpty()) {
            System.out.println("This page has no outgoing links.");
            return;
        }

        // Display all links numbered
        System.out.println("Links on this page:");
        for (int i = 0; i < links.size(); i++) {
            int hash = links.get(i);
            String url = null;
            for (Element elem : table.getTable()) {
                if (elem != null && RobinHoodHashing.fnv1aHash(elem.getWebPage().getUrl()) == hash) {
                    url = elem.getWebPage().getUrl();
                    break;
                }
            }
            if (url != null) {
                System.out.println((i + 1) + ". " + url);
            } else {
                System.out.println((i + 1) + ". Unknown link (hash: " + hash + ")");
            }
        }

        // Ask user which link to remove
        int choice = -1;
        while (true) {
        	System.out.print("Enter the number of the link to remove (or 0 to cancel): ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) {
                    System.out.println("Removal cancelled.");
                    return;
                }

                if (choice >= 1 && choice <= links.size()) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        // Remove the link and decrement target's inDegree
        int linkHashToRemove = links.get(choice - 1);
        links.deleteNode(linkHashToRemove);

        for (Element elem : table.getTable()) {
            if (elem != null && RobinHoodHashing.fnv1aHash(elem.getWebPage().getUrl()) == linkHashToRemove) {
                elem.getWebPage().decrementInDegree();
                break;
            }
        }

        System.out.println("Link removed successfully.");
    }
    
    
    // Operation 4: Visit a web page (increment hits)
    public void visitWebPage(RobinHoodHashing table, Scanner scanner) {
        System.out.print("Enter the URL to visit: ");
        String url = scanner.nextLine().trim();

        // Check if page exists
        Element element = table.searchElement(url);
        if (element == null) {
            System.out.println("Web page not found.");
            return;
        }

        // Increment hit counter
        WebPage page = element.getWebPage();
        page.incrementHits();

        System.out.println("Successfully visited " + url);
    }

 // Operation 5: Show all pages containing a keyword, sorted by inDegree descending
    public void printPagesWithKeyword(RobinHoodHashing keywordTable, Scanner scanner) {
        // Ask user for keyword
        System.out.print("Enter keyword to search: ");
        String keyword = scanner.nextLine().trim();

        // Look up keyword in the hash table
        Element element = keywordTable.searchElement(keyword);
        if (element == null) {
            System.out.println("No pages found for this keyword.");
            return;
        }

        // Get the KeywordEntry which holds the AVL tree for this keyword
        KeywordEntry entry = element.getWebPage().getKeywordEntry();
        if (entry == null || entry.getTree() == null) {
            System.out.println("No pages stored for this keyword.");
            return;
        }

        // Print all pages in the AVL tree in descending inDegree order
        System.out.println("Pages containing keyword: " + keyword);
        entry.getTree().printDescending();
    }



    
 // Insert keyword into the keyword table, linking it to an AVL tree
    public void insertKeywordToTable(RobinHoodHashing keywordTable, String keyword, WebPage page) {
        Element element = keywordTable.searchElement(keyword);

        if (element == null) {
            // Create new KeywordEntry with AVL tree
            KeywordEntry entry = new KeywordEntry(keyword);
            entry.insertWebPage(page);

            // Use a dummy WebPage as a wrapper to store the KeywordEntry
            WebPage keywordWrapper = new WebPage(keyword);
            keywordWrapper.setKeywordEntry(entry);

            Element newElement = new Element(keywordWrapper);
            keywordTable.insert(newElement);
        } else {
            // Keyword already exists — get its AVL tree and insert the page
            KeywordEntry entry = element.getWebPage().getKeywordEntry();
            entry.insertWebPage(page);
        }
    }
    
    // Operation 6: Print top-K pages by hit count
    public void printTopKPages(RobinHoodHashing hashTable, Scanner scanner) {
        System.out.print("Enter the number of top pages (k): ");
        int k = Integer.parseInt(scanner.nextLine());

        // Validate k
        if (k <= 0) {
            System.out.println("Invalid value for k.");
            return;
        }

        MinHeapWebPage heap = new MinHeapWebPage(k);
        Element[] table = hashTable.getTable();

        for (int i = 0; i < table.length; i++) {
            Element el = table[i];
            if (el != null) {
                WebPage page = el.getWebPage();

                // Heap not full yet — just insert
                if (heap.getSize() < k) {
                    heap.insert(page);
                } else {
                    // Heap full — replace min only if new page has more hits
                    if (page.getHits() > heap.getMin().getHits()) {
                        heap.removeMin(); // remove least popular
                        heap.insert(page); // insert new page
                    }
                }
            }
        }

        // Heap now contains the top-K pages
        System.out.println("Top " + k + " pages by hit count:");

        // Extract into array for descending-order print
        WebPage[] topPages = new WebPage[heap.getSize()];
        for (int i = heap.getSize() - 1; i >= 0; i--) {
            topPages[i] = heap.removeMin();
        }

        for (int i = 0; i < topPages.length; i++) {
            System.out.println("URL: " + topPages[i].getUrl() + ", Hits: " + topPages[i].getHits());
        }
    }
    
 // Operation 7: BFS to find all pages reachable from a given URL
    public void findConnectedWebPages(RobinHoodHashing hashTable, Scanner scanner) {
        System.out.print("Enter the starting URL: ");
        String startUrl = scanner.nextLine().trim();

        // Check page exists
        if (!hashTable.search(startUrl)) {
            System.out.println("Web page not found.");
            return;
        }

        int tableSize = hashTable.getTable().length;
        String[] queue = new String[tableSize];
        int front = 0, rear = 0;

        singlyLinkedList visited = new singlyLinkedList();

        // Enqueue start page
        queue[rear++] = startUrl;
        visited.insertLast(RobinHoodHashing.fnv1aHash(startUrl));

        System.out.println("Pages reachable from " + startUrl + " via links:");

        while (front < rear) {
            String currentUrl = queue[front++];
            Element currentElement = hashTable.searchElement(currentUrl);

            // Skip if element not found
            if (currentElement == null) {
                continue;
            }

            WebPage currentPage = currentElement.getWebPage();
            singlyLinkedList links = currentPage.getLinks();

            for (int i = 0; i < links.size(); i++) {
                int linkHash = links.get(i);
                String linkedUrl = null;

                // Resolve link hash to URL
                Element[] table = hashTable.getTable();
                for (int j = 0; j < table.length; j++) {
                    Element elem = table[j];
                    if (elem != null) {
                        WebPage wp = elem.getWebPage();
                        if (RobinHoodHashing.fnv1aHash(wp.getUrl()) == linkHash) {
                            linkedUrl = wp.getUrl();
                            break;
                        }
                    }
                }

                // URL not found in table — skip
                if (linkedUrl == null) {
                    continue;
                }

                // Not yet visited — enqueue
                int linkedHash = RobinHoodHashing.fnv1aHash(linkedUrl);
                if (!visited.findNode(linkedHash)) {
                    visited.insertLast(linkedHash);
                    queue[rear++] = linkedUrl;
                    System.out.println(linkedUrl);
                }
            }
        }
    }


    
    // Operation 8: BFS shortest paths between all pairs, saved to shortestPaths.txt
    public void findShortestPaths(RobinHoodHashing hashTable, Scanner scanner) {
        Element[] elements = hashTable.getTable();
        int N = elements.length;

        try {
            PrintWriter writer = new PrintWriter(new FileWriter("shortestPaths.txt"));

            // For each page as BFS source
            for (int i = 0; i < N; i++) {
                if (elements[i] != null) {
                    WebPage startPage = elements[i].getWebPage();
                    String startUrl = startPage.getUrl();

                    // BFS setup
                    String[] queue = new String[N];
                    int front = 0, rear = 0;
                    singlyLinkedList visited = new singlyLinkedList();
                    String[] parent = new String[N];

                    queue[rear++] = startUrl;
                    visited.insertLast(RobinHoodHashing.fnv1aHash(startUrl));

                    while (front < rear) {
                        String currentUrl = queue[front++];
                        Element currentElement = hashTable.searchElement(currentUrl);

                        if (currentElement != null) {
                            WebPage currentPage = currentElement.getWebPage();
                            singlyLinkedList links = currentPage.getLinks();

                            // For each outgoing link
                            for (int j = 0; j < links.size(); j++) {
                                int linkHash = links.get(j);
                                Element neighborElement = findElementByHash(hashTable, linkHash);

                                if (neighborElement != null) {
                                    String neighborUrl = neighborElement.getWebPage().getUrl();
                                    int neighborHash = RobinHoodHashing.fnv1aHash(neighborUrl);

                                    // Not yet visited
                                    if (!visited.findNode(neighborHash)) {
                                        visited.insertLast(neighborHash);
                                        queue[rear++] = neighborUrl;
                                        setParent(parent, elements, neighborUrl, currentUrl);
                                    }
                                }
                            }
                        }
                    }

                    // For each page as BFS destination
                    for (int k = 0; k < N; k++) {
                        if (elements[k] != null) {
                            WebPage endPage = elements[k].getWebPage();
                            String endUrl = endPage.getUrl();

                            if (!startUrl.equals(endUrl)) {
                                if (isVisited(visited, endUrl)) {
                                    writer.print("Path from " + startUrl + " to " + endUrl + ": ");
                                    printPath(writer, parent, elements, startUrl, endUrl);
                                    writer.println();
                                } else {
                                    writer.println("No path found from " + startUrl + " to " + endUrl);
                                }
                            }
                        }
                    }
                }
            }

            writer.close();
            System.out.println("All shortest paths saved to shortestPaths.txt");

        } catch (IOException e) {
            System.out.println("Error writing to file.");
        }
    }

    // Find an element in the table by its URL hash
    private Element findElementByHash(RobinHoodHashing table, int hash) {
        Element[] elements = table.getTable();
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] != null) {
                int urlHash = RobinHoodHashing.fnv1aHash(elements[i].getWebPage().getUrl());
                if (urlHash == hash) {
                    return elements[i];
                }
            }
        }
        return null;
    }

    // Set the BFS parent of a URL
    private void setParent(String[] parent, Element[] elements, String childUrl, String fromUrl) {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] != null && elements[i].getWebPage().getUrl().equals(childUrl)) {
                parent[i] = fromUrl;
                return;
            }
        }
    }

    // Check if a URL has been visited in BFS
    private boolean isVisited(singlyLinkedList visited, String url) {
        int hash = RobinHoodHashing.fnv1aHash(url);
        return visited.findNode(hash);
    }

    // Reconstruct and print path from start to end using parent array
    private void printPath(PrintWriter writer, String[] parent, Element[] elements, String startUrl, String endUrl) {
        String[] path = new String[elements.length];
        int count = 0;
        String currentUrl = endUrl;

        while (currentUrl != null && !currentUrl.equals(startUrl)) {
            path[count++] = currentUrl;
            currentUrl = getParent(parent, elements, currentUrl);
        }

        if (currentUrl == null) {
            writer.print("(broken path)");
            return;
        }

        writer.print(startUrl);
        for (int i = count - 1; i >= 0; i--) {
            writer.print(" -> " + path[i]);
        }
    }

    // Get the BFS parent of a URL
    private String getParent(String[] parent, Element[] elements, String url) {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] != null && elements[i].getWebPage().getUrl().equals(url)) {
                return parent[i];
            }
        }
        return null;
    }
    
    
 // Operation 9: Suggest new links between pages with >50% keyword overlap
    public void suggestNewLinks(RobinHoodHashing hashTable) {
        Element[] elements = hashTable.getTable();
        int N = elements.length;

        System.out.println("Suggested new links:");

        // For each pair of pages
        for (int i = 0; i < N; i++) {
            if (elements[i] != null) {
                WebPage pageA = elements[i].getWebPage();
                singlyLinkedList keysA = pageA.getKeys();
                singlyLinkedList linksA = pageA.getLinks();

                for (int j = 0; j < N; j++) {
                    if (elements[j] != null && i != j) {
                        WebPage pageB = elements[j].getWebPage();
                        singlyLinkedList keysB = pageB.getKeys();

                        // Count shared keywords
                        int common = 0;
                        for (int k = 0; k < keysA.size(); k++) {
                            int hashA = keysA.get(k);
                            for (int l = 0; l < keysB.size(); l++) {
                                int hashB = keysB.get(l);
                                if (hashA == hashB) {
                                    common++;
                                    break;
                                }
                            }
                        }

                        // Calculate Jaccard-style similarity percentage
                        int total = keysA.size() + keysB.size() - common;

                        if (total > 0) {
                            int percentage = (common * 100) / total;

                            // Suggest link if similarity > 50% and link doesn't already exist
                            if (percentage > 50) {
                                int hashB = RobinHoodHashing.fnv1aHash(pageB.getUrl());
                                boolean alreadyLinked = false;

                                for (int k = 0; k < linksA.size(); k++) {
                                    if (linksA.get(k) == hashB) {
                                        alreadyLinked = true;
                                        break;
                                    }
                                }

                                if (!alreadyLinked) {
                                    // Include similarity percentage in output
                                    System.out.println("Suggested link from " + pageA.getUrl() + " to " + pageB.getUrl() +
                                                       " (" + percentage + "% shared keywords)");
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}