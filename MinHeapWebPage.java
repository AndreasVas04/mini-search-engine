package project;

// Min-Heap of WebPages ordered by hit count
public class MinHeapWebPage {
 private WebPage[] heap; // heap array
 private int size;       // current number of elements
 private int capacity;   // max capacity (k)

 public MinHeapWebPage(int k) {
     this.capacity = k;
     this.heap = new WebPage[k + 1]; // 1-indexed
     this.size = 0;
 }

 // Returns true if the heap is full
 public boolean isFull() {
     return size == capacity;
 }

 // Returns true if the heap is empty
 public boolean isEmpty() {
     return size == 0;
 }
 
// Returns the number of elements in the heap
public int getSize() {
  return size;
}


 // Returns the root (WebPage with fewest hits)
 public WebPage getMin() {
     if (isEmpty()) return null;
     return heap[1];
 }

 // Insert a new page into the heap
 public void insert(WebPage page) {
     if (isFull()) {
         // If the new page has more hits than the current min, replace it
         if (page.getHits() > heap[1].getHits()) {
             heap[1] = page;
             percolateDown(1);
         }
     } else {
         // Append at the end and bubble up
         heap[++size] = page;
         percolateUp(size);
     }
 }

 // Bubble up (used after insert)
 private void percolateUp(int i) {
     WebPage temp = heap[i];
     while (i > 1 && temp.getHits() < heap[i / 2].getHits()) {
         heap[i] = heap[i / 2];
         i = i / 2;
     }
     heap[i] = temp;
 }

 // Percolate down (used after replacing min)
 private void percolateDown(int i) {
     WebPage temp = heap[i];
     int child;
     while (2 * i <= size) {
         child = 2 * i;
         if (child != size && heap[child + 1].getHits() < heap[child].getHits()) {
             child++;
         }
         if (heap[child].getHits() < temp.getHits()) {
             heap[i] = heap[child];
             i = child;
         } else {
             break;
         }
     }
     heap[i] = temp;
 }

 // Print all pages in the heap
 public void printHeap() {
     for (int i = 1; i <= size; i++) {
         System.out.println("URL: " + heap[i].getUrl() + ", Hits: " + heap[i].getHits());
     }
 }
 
// Remove and return the root (min element)
public WebPage removeMin() {
  if (isEmpty()) return null;
  WebPage min = heap[1];
  heap[1] = heap[size--]; // move last element to root and shrink size
  percolateDown(1);       // restore heap order
  return min;
}

}