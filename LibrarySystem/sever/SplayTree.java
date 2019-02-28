package com.creatures.siang.sever;


public class SplayTree<KeyType extends Comparable<KeyType>, DataType> {

    private Node root;



    public class Node {
        KeyType     key;
        DataNode   data;
        Node  leftChild;
        Node rightChild;

        public class DataNode {
            DataType datum;
            DataNode  next;

            DataNode() {
                this.datum = null;
                this. next = null;
            }

            DataNode(DataType datum) {
                this.datum = datum;
                this.next  =  null;
            }

            void insert(DataType datum) {
                DataNode node  = new DataNode(datum);
                node.next      =           this.next;
                this.next      =                node;
            }
        }

        Node() {
            this.data       = null;
            this.leftChild  = null;
            this.rightChild = null;
        }

        Node(KeyType key, DataType datum) {
            this.key        =  key;
            this.leftChild  = null;
            this.rightChild = null;
            
            this.data = new DataNode();
            this.insertData(datum);
        }

        public void insertData(DataType datum) {
            DataNode node  = new DataNode(datum);
            node.next      =      this.data.next;
            this.data.next =                node;
        }
    }


    public SplayTree() {
        root = null;
    }



    private Node.DataNode find(Node tree, KeyType key) {
        if (tree == null) {
            return null;
        }

        Node  currentNode = tree;
        int compareResult =    0;

        while (currentNode != null) {
            compareResult = key.compareTo(currentNode.key);

            if (compareResult < 0) {
                currentNode = currentNode.leftChild;
            } else if (compareResult > 0) {
                currentNode = currentNode.rightChild;
            } else {
                return currentNode.data;
            }
        }
        
        return null;
    }

    public Node.DataNode find(KeyType key) {
        return find(this.root, key);
    }



    private Node.DataNode findFuzzy(Node tree, KeyType key) {
        if (tree == null) {
            return null;
        }
        
        Node      preNode = tree;
        Node  currentNode = tree;
        int compareResult =    0;

        while (currentNode != null) {
            preNode = currentNode;
            compareResult = key.compareTo(currentNode.key);

            if (compareResult < 0) {
                currentNode = currentNode.leftChild;
            } else if (compareResult > 0) {
                currentNode = currentNode.rightChild;
            } else {
                return currentNode.data;
            }
        }
        
        return preNode.data;
    }

    public Node.DataNode findFuzzy(KeyType key) {
        return findFuzzy(this.root, key);
    }


    private Node max(Node tree) {
        if (tree == null)
            return null;

        while(tree.rightChild != null)
            tree = tree.rightChild;
        return tree;
    }

    public KeyType max() {
        Node p = max(root);
        if (p != null) {
            return p.key;
        }

        return null;
    }

    private Node splay(Node tree, KeyType key) {
        if (tree == null) {
            return null;
        }

        Node treeRoot  = new Node();
        Node leftTree  =   treeRoot;
        Node rightTree =   treeRoot;
        Node tempNode  =       null;
        int compareResult =       0;

        while (true) {
            compareResult = key.compareTo(tree.key);

            if (compareResult == 0) {
                break;
            } else if (compareResult < 0) {
                if (tree.leftChild == null) {
                    break;
                } else {
                    if (key.compareTo(tree.leftChild.key) < 0) {
                        tempNode            =      tree.leftChild;
                        tree.leftChild      = tempNode.rightChild;
                        tempNode.rightChild =                tree;
                        
                        tree = tempNode;

                        if (tree.leftChild == null) {
                            break;
                        }
                    }
                }

                rightTree.leftChild = tree;
                rightTree           = tree;

                tree = tree.leftChild;
            } else {
                if (tree.rightChild == null) {
                    break;
                } else {
                    if (key.compareTo(tree.rightChild.key) > 0) {
                        tempNode           =    tree.rightChild;
                        tree.rightChild    = tempNode.leftChild;
                        tempNode.leftChild =               tree;
                        
                        tree = tempNode;

                        if (tree.rightChild == null) {
                            break;
                        }
                    }
                }

                leftTree.rightChild = tree;
                leftTree            = tree;

                tree = tree.rightChild;
            }
        }

        leftTree .rightChild =     tree. leftChild;
        rightTree. leftChild =     tree.rightChild;
        tree     . leftChild = treeRoot.rightChild;
        tree     .rightChild = treeRoot. leftChild;

        return tree;
    }

    public void splay(KeyType key) {
        this.root = splay(this.root, key);
    }


    private Node insert(Node tree, KeyType key, DataType datum) {
        if (tree == null) {
            Node newNode = new Node(key, datum);
            return newNode;
        }

        Node  currentNode =        tree;
        Node   parentNode = currentNode;
        int compareResult =           0;

        while (currentNode != null) {
            parentNode = currentNode;
            compareResult = key.compareTo(currentNode.key);

            if (compareResult < 0) {
                currentNode = currentNode.leftChild;
            } else if (compareResult > 0) {
                currentNode = currentNode.rightChild;
            } else {
                currentNode.insertData(datum);
                return tree;
            }
        }


        Node newNode = new Node(key, datum);
        compareResult = newNode.key.compareTo(parentNode.key);
        if (compareResult < 0) {
            parentNode.leftChild  = newNode;
        } else {
            parentNode.rightChild = newNode;
        }
        return tree;
    }

    public void insert(KeyType key, DataType data) {
        this.root = this.insert(this.root, key, data);
        this.root = this.splay (this.root, key      );
    }


    private Node erase(Node tree, KeyType key) {
        if (tree == null) {
            return null;
        }
        if (this.find(tree, key) == null) {
            return tree;
        }

        tree = splay(tree, key);
        
        Node newRoot;
        if (tree.leftChild == null) {
            newRoot = tree.rightChild;
        } else if (tree.rightChild == null) {
            newRoot = tree.leftChild;
        } else {
            newRoot = splay(tree.leftChild, max(tree.leftChild).key);
            newRoot.rightChild = tree.rightChild;
        }

        return newRoot;
    }

    public void erase(KeyType key) {
        this.root = this.erase(this.root, key);
    }

    private Node erase(Node tree, KeyType key, DataType datum) {
        if (tree == null) {
            return null;
        }

        Node.DataNode deleteNode = find(tree, key);
        if (deleteNode != null) {
            Node.DataNode preNode = deleteNode;
            deleteNode = preNode.next;

            while (deleteNode != null) {
                if (deleteNode.datum == datum) {
                    preNode.next = deleteNode.next;
                    break;
                }

                preNode = deleteNode;
                deleteNode = deleteNode.next;
            }
        }

        return tree;
    }

    public void erase(KeyType key, DataType datum) {
        this.root = this.erase(this.root, key, datum);
        Node.DataNode deleteNode = find(this.root, key);

        if (deleteNode.next == null) {
            this.root = this.erase(this.root, key);
        }
    }
}
