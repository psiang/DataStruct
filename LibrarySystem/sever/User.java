package com.creatures.siang.sever;

import java.util.ArrayList;

public class User {
    
    private Node     head;
    private TrieTree trie;

    protected class Node {
        protected String     name;
        protected String password;
        protected java.util.List<Record> records;

        protected Node pre ;
        protected Node next;

        protected class Record {
            String       title;
            java.util.Date date;

            Record(String title, java.util.Date date) {
                this.title = title;
                this.date  =  date;
            }
        }

        protected Node() {
            this.records = new ArrayList<Record>();
            this.pre = this.next = null;
        }

        protected Node(String name, String password) {
            this.name     =     name;
            this.password = password;

            this.records = new ArrayList<Record>();
            this.pre = this.next = null;
        }

        protected boolean borrowBook(String title) {
            java.util.Date date = new java.util.Date();
            for (int i = 0; i < this.records.size(); i++) {
                if (this.records.get(i).title.equals(title)) {
                    return false;
                }
            }

            Record newRecord = new Record(title, date);
            this.records.add(newRecord);
            return true;
        }

        protected boolean borrowBook(String title, java.util.Date date) {
            for (int i = 0; i < this.records.size(); i++) {
                if (this.records.get(i).title.equals(title)) {
                    return false;
                }
            }

            Record newRecord = new Record(title, date);
            this.records.add(newRecord);
            return true;
        }

        protected boolean returnBook(String title) {
            for (int i = 0; i < this.records.size(); i++) {
                if (this.records.get(i).title.equals(title)) {
                    this.records.remove(i);
                    return true;
                }
            }

            return false;
        }

        protected boolean findBook(String title) {
            if (this.records.size() == 0) {
                return false;
            } else {
                for (int i = 0; i < this.records.size(); i++) {
                    if (this.records.get(i).title.equals(title)) {
                        return true;
                    }
                }

                return false;
            }
        }

        protected java.util.List<Record> getAllRecords() {
            return this.records;
        }
    }


    public User() {
        this.head = new     Node();
        this.trie = new TrieTree();
    }

    public Node getHead() {
        return this.head;
    }


    public boolean insert(String name, String password) {
        if (this.trie.find(name) != null) {
            return false;
        }

        Node newUser = new Node(name, password);

        newUser.pre    =      this.head;
        newUser.next   = this.head.next;
        this.head.next =        newUser;

        if (newUser.next != null) {
            newUser.next.pre = newUser;
        }

        this.trie.insert(name, newUser);

        return true;
    }


    public boolean erase(String name) {
        Node deleteNode = this.trie.find(name);

        if (deleteNode == null || deleteNode.records.size() != 0) {
            return false;
        } else {
            deleteNode.pre.next = deleteNode.next;
            if (deleteNode.next != null) {
                deleteNode.next.pre =  deleteNode.pre;
            }

            this.trie.erase(name);
            
            return true;
        }
    }


    public boolean changePassword(String name, String newPassword) {
        Node theUser = this.find(name);

        if (theUser != null) {
            theUser.password = newPassword;
            return true;
        }
        return false;
    }


    public boolean borrowBook(String name, String title) {
        Node theUser = this.find(name);

        return (theUser != null && theUser.borrowBook(title));
    }

    public boolean borrowBook(String name, String title, java.util.Date date) {
        Node theUser = this.find(name);

        return (theUser != null && theUser.borrowBook(title, date));
    }



    public boolean returnBook(String name, String title) {
        Node theUser = this.find(name);

        return (theUser != null && theUser.returnBook(title));
    }


    public java.util.List<Node.Record> getAllRecords(String name) {
        Node theUser = this.find(name);

        if (theUser != null) {
            return theUser.getAllRecords();
        } else {
            return null;
        }
    }



    public Node find(String name) {
        return this.trie.find(name);
    }
    


    public boolean match(String name, String password) {
        Node n = this.find(name);
        
        return (n != null && n.password.equals(password));
    }
}
