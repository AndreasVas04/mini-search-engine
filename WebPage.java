package project;

public class WebPage {
    private String url;                         // page URL
    private int hits;                           // number of visits
    private int inDegree;                       // number of pages linking to this one
    private singlyLinkedList keys;              // keyword hashes
    private singlyLinkedList links;             // hashes of outgoing links
    private KeywordEntry keywordEntry;

    public WebPage(String url) {
        this.url = url;
        this.hits = 0;
        this.inDegree = 0;
        this.keys = new singlyLinkedList();
        this.links = new singlyLinkedList(); // stores int hashes of linked URLs
    }

    public String getUrl() {
        return url;
    }

    public int getHits() {
        return hits;
    }

    public void incrementHits() {
        hits++;
    }

    public int getInDegree() {
        return inDegree;
    }

    public void incrementInDegree() {
        inDegree++;
    }

    public void decrementInDegree() {
        if (inDegree > 0) {
            inDegree--;
        }
    }

    public singlyLinkedList getKeys() {
        return keys;
    }

    public singlyLinkedList getLinks() {
        return links;
    }

    public void addKey(String keyword) {
        int keyHash = RobinHoodHashing.fnv1aHash(keyword);
        keys.insertLast(keyHash);
    }

    public boolean addLink(String linkUrl) {
        int linkHash = RobinHoodHashing.fnv1aHash(linkUrl);
        if (links.findNode(linkHash)) {
            return false; // link already exists
        } else {
            links.insertLast(linkHash);
            return true;
        }
    }

    public void setKeywordEntry(KeywordEntry entry) {
        this.keywordEntry = entry;
    }

    public KeywordEntry getKeywordEntry() {
        return this.keywordEntry;
    }
}