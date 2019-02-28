package com.creatures.siang.sever;


public class Book {

    private Node head;
    private int  size;
    private SplayTree<Integer, Node>       splayId;
    private SplayTree<String , Node>    splayTitle;
    private SplayTree<String , Node>   splayAuthor;
    private SplayTree<Integer, Node> splayCategory;

    public class Node {
        Integer           id;
        String         title;
        String        author;
        Integer     category;
        int    totalQuantity;
        int borrowedQuantity;

        Node pre;
        Node next;

        Node() {        // ���ڹ���ͷ�ڵ�
            this.pre = this.next = null;
        }

        Node(Integer id, String title, String author, Integer category, int totalQuantiy) {
            this.id            =           id;
            this.title         =        title;
            this.author        =       author;
            this.category      =     category;
            this.totalQuantity = totalQuantiy;

            this.borrowedQuantity   = 0;
            this.pre = this.next = null;
        }

       boolean changeTotalQuantity(int increment) {
            int newQuantity = this.totalQuantity + increment;
            if (newQuantity >= 0 && newQuantity >= this.borrowedQuantity) {
                this.totalQuantity = newQuantity;
                return true;
            } else {
                return false;
            }
        }

        boolean changeBorrowedQuantity(int increment) {
            int newQuantity = this.borrowedQuantity + increment;
            if (newQuantity >= 0 && newQuantity <= this.totalQuantity) {
                this.borrowedQuantity = newQuantity;
                return true;
            } else {
                return false;
            }
        }
    }

    public Book() {
        this.head = new Node();
        this.size =          0;

        this.splayId       = new SplayTree<Integer, Node>();
        this.splayTitle    = new SplayTree<String , Node>();
        this.splayAuthor   = new SplayTree<String , Node>();
        this.splayCategory = new SplayTree<Integer, Node>();
    }

    public Node getHead() {
        return this.head;
    }



    public boolean insert(String title, String author, Integer category, int totalQuantiy) {
        if (this.splayTitle.find(title) != null) {
            return false;
        }
        this.size++;
        Node newBook = new Node(this.size, title, author, category, totalQuantiy);
        if (this.head.next == null) {
            this.head.next = newBook;
            newBook.pre = this.head;
        } else {
            Node preBook = this.splayTitle.findFuzzy(title).next.datum;

            if (newBook.title.compareTo(preBook.title) < 0) {
                newBook.pre = preBook.pre;
                if (preBook.pre != null) {
                    preBook.pre.next = newBook;
                }
                newBook.next = preBook;
                preBook.pre = newBook;

            } else {
                newBook.next = preBook.next;
                newBook.pre  =      preBook;
                preBook.next =      newBook;
                if (newBook.next != null) {
                    newBook.next.pre = newBook;
                }
            }
        }

        this.splayId.      insert(newBook.id, newBook);
        this.splayTitle .  insert(title     , newBook);
        this.splayAuthor.  insert(author    , newBook);
        this.splayCategory.insert(category  , newBook);
        return true;
    }



    public boolean changeTotalQuantity(String title, int increment) {
        Node node = this.splayTitle.find(title).next.datum;

        return (node != null && node.changeTotalQuantity(increment));
    }



    public boolean changeBorrowedQuantity(String title, int increment) {
        Node node = this.splayTitle.find(title).next.datum;

        return (node != null && node.changeBorrowedQuantity(increment));
    }



    public boolean erase(String title) {
        Node deleteNode = this.splayTitle.find(title).next.datum;

        if (deleteNode == null || deleteNode.borrowedQuantity != 0) {
            return false;
        } else {
            deleteNode.pre.next = deleteNode.next;
            if (deleteNode.next != null) {
                deleteNode.next.pre = deleteNode.pre;
            }

            this.splayId      .erase(deleteNode.id                  );
            this.splayTitle   .erase(deleteNode.title               );
            this.splayAuthor  .erase(deleteNode.author  , deleteNode);
            this.splayCategory.erase(deleteNode.category, deleteNode);

            return true;
        }
    }



    public SplayTree<Integer, Node>.Node.DataNode findById(Integer id) {
        return (this.splayId.find(id));
    }


    public SplayTree<String, Node>.Node.DataNode findByTitle(String title) {
        return (this.splayTitle.find(title));
    }

    public SplayTree<String, Node>.Node.DataNode findByTitleFuzzy(String title) {
        return (this.splayTitle.findFuzzy(title));
    }


    private int compareString(String a, String b) {
        int length = Math.min(a.length(), b.length());

        int i = 0;
        for (i = 0; i < length; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                break;
            }
        }

        return i;
    }



    public SplayTree<String, Node>.Node.DataNode findByAuthor(String author) {
        return (this.splayAuthor.find(author));
    }



    public SplayTree<Integer, Node>.Node.DataNode findByCategory(Integer category) {
        return (this.splayCategory.find(category));
    }
}
