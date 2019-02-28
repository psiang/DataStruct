package com.creatures.siang.sever;

public class TrieTree {

    private Node root;


    public class Node {
        public char        key;
        public User.Node  data;
        public Node[] children;
        public boolean   isEnd;
        public int     counter;

        public Node(char key, User.Node data) {
            this.key     =   key;
            this.data    =  data;
            this.isEnd   = false;
            this.counter =     0;

            this.children = new Node[36];
            for (int i = 0; i < 36; i++) {
                children[i] = null;
            }
        }



        public Node subNode(char theKey) {
            for (int i = 0; i < 36; i++) {
                if (children[i] != null && children[i].key == theKey) {
                    return children[i];
                }
            }

            return null;
        }



        public void insertChild(int index, Node theNode) {
            if (this.children[index] == null) {
                this.children[index] = theNode;
            }
        }


        public void eraseChild(int index) {
            if (this.children[index] != null) {
                this.children[index] = null;
            }
        }
    }


    public TrieTree() {
        this.root = new Node('#', null);
    }


    public User.Node find(String word) {
        Node currentNode = this.root;

        for (int i = 0; i < word.length(); i++) {
            currentNode = currentNode.subNode(word.charAt(i));
            if (currentNode == null) {
                return null;
            }    
        }

        if (currentNode.isEnd) {
            return currentNode.data;
        } else {
            return null;
        }
    }


    public boolean insert(String word, User.Node data) {
        if (this.find(word) != null) {
            return false;
        }

        Node currentNode = this.root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            Node nextNode = currentNode.subNode(c);
            if (nextNode != null) {
                currentNode = nextNode;
            } else {
                Node newNode = new Node(c, null);
                currentNode.insertChild(encodeCharacter(c), newNode);

                currentNode = currentNode.subNode(c);
            }
            currentNode.counter++;
        }
        currentNode.data  = data;
        currentNode.isEnd = true;
        return true;
    }


    public void erase(String word) {
        if (this.find(word) == null) {
            return;
        }

        Node currentNode = this.root;
        for (int i = 0; i < word.length(); i++) {
            Node nextNode = currentNode.subNode(word.charAt(i));
            if (nextNode.counter == 1) {
                currentNode.eraseChild(encodeCharacter(word.charAt(i)));
                return;
            } else {
                nextNode.counter--;
                currentNode = nextNode;
            }
        }
        currentNode.data  =  null;
        currentNode.isEnd = false;
    }


    private int encodeCharacter(char c) {
        if (c >= 'a' && c <= 'z') {
            return (int)(c - 'a');
        } else if (c >= '0' && c <= '9') {
            return (int)(c - '0') + 26;
        } else {
            return -1;
        }
    }
}
