package project;

// Robin Hood Hashing for storing WebPages.
// Provides efficient O(1) average insert and lookup even with many pages.

public class RobinHoodHashing {

    private Element[] table; // array holding Elements (which wrap WebPages)
    private int capacity; // current table size (starts at 7, doubles on rehash)
    private int size; // number of elements inserted so far
    private int maxProbeLength; // max probe length seen so far (used to bound search)

    public RobinHoodHashing() {
        this.table = new Element[7];
        this.capacity = 7;
        this.size = 0;
        this.maxProbeLength = 0;
    }
    

    // Insert an element into the table
    public void insert(Element element) {
        // If load factor exceeds 90%, rehash (double capacity)
        if ((float)(size + 1)/capacity > 0.9)
            this.rehash();

        int key = fnv1aHash(element.getWebPage().getUrl());
        int initial = key % this.capacity;

        // Cell is empty — insert directly
        if (isWantedCellEmpty(initial)) {
            this.table[initial] = element;
            this.size++;
        } else {
            // Otherwise — probe using Robin Hood logic
            while (!this.isWantedCellEmpty(initial)) {
                // Existing element has longer or equal probe — new element moves forward
                if (getElementInCell(initial).getProbeLength() >= element.getProbeLength()) {
                    initial = (initial + 1) % this.capacity;
                    element.incrementProbeLength();
                    if (element.getProbeLength() > this.maxProbeLength)
                        this.maxProbeLength = element.getProbeLength();
                } else {
                    // New element has longer probe — displace the existing one (Robin Hood)
                    Element temp = getElementInCell(initial);
                    this.table[initial] = element;
                    element = temp;
                    element.incrementProbeLength();
                }
            }
            // Found empty cell — place the element here
            this.table[initial] = element;
            this.size++;
        }
    }

    // Returns true if the given URL exists in the table
    public boolean search(String url) {
        int key = fnv1aHash(url);
        int initial = key % this.capacity;
        int probe = 0;

        while (probe <= this.maxProbeLength) {
            if (this.table[initial] == null)
                return false;
            if (url.equals(this.table[initial].getWebPage().getUrl()))
                return true;
            initial = (initial + 1) % this.capacity;
            probe++;
        }
        return false;
    }

    // Returns the Element for the given URL, or null if not found
    public Element searchElement(String url) {
        int key = fnv1aHash(url);
        int initial = key % this.capacity;
        int probe = 0;

        while (probe <= this.maxProbeLength) {
            if (this.table[initial] == null)
                return null;
            if (url.equals(this.table[initial].getWebPage().getUrl()))
                return this.table[initial];
            initial = (initial + 1) % this.capacity;
            probe++;
        }
        return null;
    }

    // Rehash: move all elements into a new table of double the capacity
    public void rehash() {
        this.capacity *= 2;
        this.size = 0;
        this.maxProbeLength = 0;

        Element []oldTable = this.table;
        this.table = new Element[this.capacity];

        for (int i = 0; i < oldTable.length; i++)
            if (oldTable[i] != null) {
                oldTable[i].resetProbeLength();
                this.insert(oldTable[i]);
            }
    }

    // Returns true if the given cell is empty
    public boolean isWantedCellEmpty(int num) {
        return this.table[num] == null;
    }

    // Returns the Element at position num
    public Element getElementInCell(int num) {
        return this.table[num];
    }

    // FNV-1a hash function
    public static int fnv1aHash(String input) {
        final int FNV_OFFSET = 0x811c9dc5;
        final int FNV_PRIME = 0x01000193;
        int hash = FNV_OFFSET;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            hash ^= c;
            hash *= FNV_PRIME;
        }
        return Math.abs(hash);
    }

    // Prints all URLs and their hit counts
    public void printTable() {
        System.out.println("Loaded WebPages:");
        for (int i = 0; i < this.table.length; i++)
            if (this.table[i] != null)
                System.out.println("URL: " + this.table[i].getWebPage().getUrl() + ", Hits: " + this.table[i].getWebPage().getHits());
    }

    // Returns the internal table array (used in Main and Operations)
    public Element[] getTable() {
        return this.table;
    }
}