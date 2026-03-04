package project;

public class KeywordEntry {
    private String keyword;
    private AVLTree tree;

    public KeywordEntry(String keyword) {
        this.keyword = keyword;
        this.tree = new AVLTree();
    }

    public String getKeyword() {
        return keyword;
    }

    public AVLTree getTree() {
        return tree;
    }

    public void insertWebPage(WebPage page) {
        tree.insert(page);
    }
}
