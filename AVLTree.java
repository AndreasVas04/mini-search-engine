package project;

public class AVLTree {

    // Root of the AVL tree
    private Node root;

    // Inner node class
    private class Node {
        WebPage data;
        Node left;
        Node right;
        int height;

        Node(WebPage data) {
            this.data = data;
            this.height = 1;
        }
    }

    // Returns the height of a node
    private int getHeight(Node node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    // Updates the height of a node
    private void updateHeight(Node node) {
        int leftHeight = getHeight(node.left);
        int rightHeight = getHeight(node.right);
        node.height = 1 + Math.max(leftHeight, rightHeight);
    }

    // Returns the balance factor of a node
    private int getBalance(Node node) {
        if (node == null) {
            return 0;
        }
        int leftHeight = getHeight(node.left);
        int rightHeight = getHeight(node.right);
        return leftHeight - rightHeight;
    }

    // Right rotation
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // Left rotation
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // Public insert method
    public void insert(WebPage data) {
        root = insert(root, data);
    }

    // Recursive insert with balancing
    private Node insert(Node node, WebPage data) {
        if (node == null) {
            return new Node(data);
        }

        // If new inDegree is smaller, go left
        if (data.getInDegree() < node.data.getInDegree()) {
            node.left = insert(node.left, data);
        }
        // If larger, go right
        else if (data.getInDegree() > node.data.getInDegree()) {
            node.right = insert(node.right, data);
        }
        // Equal inDegree: use URL as tie-breaker so pages are never lost
        else {
            int cmp = data.getUrl().compareTo(node.data.getUrl());
            if (cmp < 0) {
                node.left = insert(node.left, data);
            } else if (cmp > 0) {
                node.right = insert(node.right, data);
            } else {
                return node; // Exact same page (same URL), skip
            }
        }

        // Update node height
        updateHeight(node);

        // Calculate balance factor
        int balance = getBalance(node);

        // 4 imbalance cases — compare by (inDegree, URL):

        // Case 1 - Left Left
        if (balance > 1 && compare(data, node.left.data) < 0) {
            return rotateRight(node);
        }

        // Case 2 - Right Right
        if (balance < -1 && compare(data, node.right.data) > 0) {
            return rotateLeft(node);
        }

        // Case 3 - Left Right
        if (balance > 1 && compare(data, node.left.data) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Case 4 - Right Left
        if (balance < -1 && compare(data, node.right.data) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Reverse inorder traversal — descending order
    public void printDescending() {
        printDescending(root);
    }

    // Print nodes in descending order by inDegree
    private void printDescending(Node node) {
        if (node == null) {
            return;
        }

        printDescending(node.right);  // largest first
        System.out.println("URL: " + node.data.getUrl() + ", inDegree: " + node.data.getInDegree());
        printDescending(node.left);   // then smaller
    }

    // Helper comparison: first by inDegree, then alphabetically by URL
    private int compare(WebPage a, WebPage b) {
        if (a.getInDegree() != b.getInDegree()) {
            return Integer.compare(a.getInDegree(), b.getInDegree());
        }
        return a.getUrl().compareTo(b.getUrl());
    }
}