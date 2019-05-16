package lazyTrees;

import java.util.*;

/**
 * An object of LazySearchTree represents a BST with lazy deletion to insert, remove, and find data.
 *
 * @param <E> bounded generic object
 * @author Foothill College, Alice Ding
 */
public class LazySearchTree<E extends Comparable<? super E>> implements Cloneable {

    //  reflect the number of undeleted nodes
    private int mSizeHard;// tracks the number of hard nodes in it, i.e., both deleted and undeleted
    protected int mSize;
    protected LazySTNode mRoot;


    // tracks deleted nodes
    private int numDeletedNodes;

    public LazySearchTree() {

        clear();
    }

    private class LazySTNode {

        LazySTNode lftChild, rtChild;
        E data;
        LazySTNode myRoot;  // needed to test for certain error
        boolean deleted;

        public LazySTNode(E d, LazySTNode lft, LazySTNode rt) {

            lftChild = lft;
            rtChild = rt;
            data = d;
            deleted = false;
        }

        public LazySTNode() {

            this(null, null, null);
            deleted = false;
        }
    }

    /**
     * Returns the max of tree
     *
     * @return the max of tree
     */
    public E findMax() {

        if (mRoot == null)
            throw new NoSuchElementException();
        return findMax(mRoot).data;
    }

    /**
     * Returns true if tree is empty
     *
     * @return true if tree is empty
     */
    public boolean empty() {

        return (mSize == 0);
    }

    /**
     * Returns the size of the tree
     *
     * @return the size of the tree
     */
    public int size() {

        return mSize;
    }

    /**
     * Returns the min of tree
     *
     * @return the min of tree
     */
    public E findMin() {

        if (mRoot == null)
            throw new NoSuchElementException();

        return findMin(mRoot).data;
    }

    /**
     * Returns the number of the deleted nodes and undeleted in the tree
     *
     * @return the number of the deleted nodes and undeleted in the tree
     */
    public int sizeHard() {

        return this.mSizeHard;
    }

    /**
     * Clears the tree
     */
    public void clear() {

        mSize = 0;
        mRoot = null;
        numDeletedNodes = 0;
        this.mSizeHard = 0;
    }

    /**
     * Returns the height of the tree
     *
     * @return the height of the tree
     */
    public int showHeight() {

        return findHeight(mRoot, -1);
    }

    /**
     * Returns true if tree contains x
     *
     * @param x the data to be checked
     * @return true if tree contains x
     */
    public boolean contains(E x) {

        return find(mRoot, x) != null;
    }

//----------------- basic functionalities------------------


    /**
     * Returns data x if a node in the tree contains x; if x is not found, throws NoSuchElementException
     *
     * @param x the data to be found
     * @return data x if a node in the tree contains x
     */
    public E find(E x) {

        LazySTNode resultNode;
        resultNode = find(mRoot, x);

        if (resultNode == null)
            throw new NoSuchElementException();

        return resultNode.data;
    }


    /**
     * Insert a node x to the tree
     *
     * @param x the data to be inserted
     * @return true if insertion succeed
     */
    public boolean insert(E x) {

        int oldSize = mSize;
        mRoot = insert(mRoot, x);
        return (mSize != oldSize);
    }

    /**
     * Remove the node x from the tree and mark it as deleted
     *
     * @param x the data to be removed
     * @return true if removal succeed
     */
    public boolean remove(E x) {

        int oldSize = mSize;
        remove(mRoot, x);
        return (mSize != oldSize);
    }

    /**
     * Traverse each deleted and undeleted nodes in the tree
     *
     * @param func
     * @param <F>
     */
    public <F extends Traverser<? super E>> void traverseHard(F func) {

        traverseHard(func, mRoot);
    }

    /**
     * Traverse all the undeleted nodes in the tree
     *
     * @param func
     * @param <F>
     */
    public <F extends Traverser<? super E>> void traverseSoft(F func) {

        traverseSoft(func, mRoot);
    }

    /**
     * Clones a tree and returns the cloned copy
     *
     * @return the cloned copy
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {

        LazySearchTree<E> newObject = (LazySearchTree<E>) super.clone();
        newObject.clear();  // can't point to other's data
        newObject.mRoot = cloneSubtree(mRoot);
        newObject.mSize = mSize;
        return newObject;
    }

    //--------------------- private helpers-------------------------

    protected LazySTNode findMin(LazySTNode root) {

        if (root == null)
            return null;

        LazySTNode result = findMin(root.lftChild);

        if (result != null)
            return result;

        if (!root.deleted)
            return root;

        /**
         * If result is null, search right subtree by calling findMin
         */
        return findMin(root.rtChild);
    }

    protected LazySTNode findMax(LazySTNode root) {

        if (root == null)
            return null;

        LazySTNode result = findMax(root.rtChild);

        if (result != null)
            return result;

        if (!root.deleted)
            return root;

        return findMax(root.lftChild);
    }

    protected LazySTNode cloneSubtree(LazySTNode root) {

        LazySTNode newNode;
        if (root == null)
            return null;

        // does not set myRoot which must be done by caller
        newNode = new LazySTNode
                (
                        root.data,
                        cloneSubtree(root.lftChild),
                        cloneSubtree(root.rtChild)
                );
        return newNode;
    }

    protected <F extends Traverser<? super E>> void traverseHard(F func, LazySTNode treeNode) {

        if (treeNode == null)
            return;

        traverseHard(func, treeNode.lftChild);
        func.visit(treeNode.data);
        traverseHard(func, treeNode.rtChild);
    }


    // traverses the undeleted nodes
    protected <F extends Traverser<? super E>> void traverseSoft(F func, LazySTNode treeNode) {

        if (treeNode == null)
            return;

        traverseSoft(func, treeNode.lftChild);

        if (!treeNode.deleted) {

            func.visit(treeNode.data);
        }

        traverseSoft(func, treeNode.rtChild);
    }

    protected LazySTNode insert(LazySTNode root, E x) {

        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null) {

            mSize++;
            mSizeHard++;
            return new LazySTNode(x, null, null);
        }

        compareResult = x.compareTo(root.data);

        if (compareResult == 0 && root.deleted == true) {

            root.deleted = false;
            numDeletedNodes--;
            mSize++;
            mSize = mSize + numDeletedNodes;
        }


        if (compareResult < 0)
            root.lftChild = insert(root.lftChild, x);

        else if (compareResult > 0)
            root.rtChild = insert(root.rtChild, x);

        return root;
    }


    protected void remove(LazySTNode root, E x) {

        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
            throw new NoSuchElementException();

        compareResult = x.compareTo(root.data);

        // checks if the found node is deleted and marks it undeleted
        if (compareResult == 0 && !root.deleted) {

            root.deleted = true;
            mSize--;
            numDeletedNodes++;
            mSizeHard = mSize + numDeletedNodes;
        } else if (compareResult < 0)
            remove(root.lftChild, x);

        else if (compareResult > 0)
            remove(root.rtChild, x);
    }


    protected LazySTNode find(LazySTNode root, E x) {

        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);

        // Any nodes that are marked deleted, return null
        if (compareResult == 0 && root.deleted)
            return null;

        if (compareResult < 0)
            return find(root.lftChild, x);

        if (compareResult > 0)
            return find(root.rtChild, x);

        return root;   // found
    }


    protected int findHeight(LazySTNode treeNode, int height) {

        int leftHeight, rightHeight;
        if (treeNode == null)
            return height;
        height++;
        leftHeight = findHeight(treeNode.lftChild, height);
        rightHeight = findHeight(treeNode.rtChild, height);
        return (leftHeight > rightHeight) ? leftHeight : rightHeight;
    }


}
