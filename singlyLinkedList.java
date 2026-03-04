package project;


public class singlyLinkedList {
    private Node head;
    private int size;

    // Inner class for the list node
    private class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    // Constructor
    public singlyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    // Makes the list empty
    public void makeEmpty() {
        head = null;
        size = 0;
    }

    // Checks if the list is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Returns the size of the list
    public int size() {
        return size;
    }

    // Inserts at the end of the list
    public void insertLast(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;
        }
        size++;
    }

    // Inserts in sorted ascending order
    public void insertSorted(int data) {
        Node newNode = new Node(data);
        if (head == null || head.data >= data) { // Insert at the beginning
            newNode.next = head;
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != null && temp.next.data < data) {
                temp = temp.next;
            }
            newNode.next = temp.next;
            temp.next = newNode;
        }
        size++;
    }

    // Deletes ALL nodes with the given value
    public void deleteNode(int data) {
        while (head != null && head.data == data) { // If the head contains the value, move head forward
            head = head.next;
            size--;
        }

        Node temp = head;
        while (temp != null && temp.next != null) {
            if (temp.next.data == data) {
                temp.next = temp.next.next; // Skip the node with the given value
                size--;
            } else {
                temp = temp.next; // Move to the next node
            }
        }
    }


    // Searches for a node with a specific value
    public boolean findNode(int data) {
        Node temp = head;
        while (temp != null) {
            if (temp.data == data) return true;
            temp = temp.next;
        }
        return false;
    }

    // Returns the string representation of the list
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node temp = head;
        while (temp != null) {
            sb.append(temp.data).append(" -> ");
            temp = temp.next;
        }
        sb.append("null");
        return sb.toString();
    }


    public int findMax() {
        if (head == null) {
            throw new IllegalStateException("List is empty");
        }
        return findMaxRecursive(head);
    }

    // Returns the maximum element in the list

    private int findMaxRecursive(Node node) {
        // Base case: last node
        if (node.next == null) {
            return node.data;
        }

        int maxFromRest = findMaxRecursive(node.next);
        
        return Math.max(node.data, maxFromRest);
    }
    
 // Returns element at given index (0-based). Throws if index is out of bounds.
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        Node temp = head;
        int count = 0;

        while (count < index) {
            temp = temp.next;
            count++;
        }

        return temp.data;
    }
}