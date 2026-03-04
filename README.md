# Mini Search Engine

A Java project that simulates a mini search engine backend. Web pages are modeled as a directed graph, and the system supports inserting pages, following links, searching by keyword, ranking by popularity, and finding shortest paths between pages. All core data structures are implemented from scratch, only Java's standard I/O and Scanner are used.

Tested on: Ubuntu 24 (WSL), OpenJDK 21. Java 11+ recommended.

## How to run

From the repo root (where `pages.txt` is):

```bash
javac -encoding UTF-8 -d . *.java
java -cp . project.Main
```

On Windows:
```
javac -encoding UTF-8 -d . *.java
java -cp . project.Main
```

## Input format

The program reads web pages from `pages.txt`. Each entry looks like this:

```
www.example.com 2 1
science, research
www.example.com/lab
```

The first line is the URL, number of keywords, and number of outgoing links. The next lines (if any) are the keywords and links, comma-separated.

## What it can do

Once loaded, a menu lets you interact with the graph:

1. Insert a new web page with keywords and links
2. Add a link between two existing pages
3. Remove a link from a page
4. Visit a page (increments its hit counter)
5. Search pages by keyword, results sorted by in-degree, highest first
6. Find the top-K most visited pages
7. See all pages reachable from a given URL via links (BFS)
8. Find shortest paths between every pair of pages, saved to `shortestPaths.txt`
9. Get suggestions for new links based on shared keywords
10. Exit

## Example

```
Choice: 5
Enter keyword to search: artificial
Pages containing keyword: artificial
URL: www.cs.ucy.ac.cy/research, inDegree: 1
URL: www.cs.ucy.ac.cy/ai-lab, inDegree: 0
```

## Complexity

- **Lookup / Insert** (hash table): O(1) average — Robin Hood Hashing with FNV-1a, rehashes at 90% load factor
- **Keyword search** (AVL tree): O(log n) insert, O(n) sorted traversal
- **Top-K pages** (min-heap): O(n log k)
- **BFS** (connected pages / shortest paths): O(V + E)

## Data structures

Everything is implemented from scratch. The main hash table uses Robin Hood Hashing for fast lookup and insertion, rehashing automatically when the load factor exceeds 90%. Each keyword maps to an AVL tree of pages sorted by in-degree, so search results always come back in the right order. A min-heap handles top-K popularity queries. Links and keyword hashes are stored in singly linked lists. Graph traversal uses BFS with an array-based queue.

The AVL tree uses (inDegree, URL) as a composite key so pages with equal in-degree are never dropped.
