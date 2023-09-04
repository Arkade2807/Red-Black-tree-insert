// --== CS400 Spring 2023 File Header Information ==--
// Name: Aditya kamasani
// Email: kamasani@wisc.edu
// Team: CM
// TA: Ksrsn Grover
// Lecturer: Florian
// Notes to Grader: <optional extra notes>
import java.util.LinkedList;
import java.util.Stack;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Red-Black Tree implementation with a Node inner class for representing the nodes of the tree.
 * Currently, this implements a Binary Search Tree that we will turn into a red black tree by
 * modifying the insert functionality. In this activity, we will start with implementing rotations
 * for the binary search tree insert algorithm.
 */
public class RedBlackTree<T extends Comparable<T>> implements SortedCollectionInterface<T> {

  /**
   * This class represents a node holding a single value within a binary tree.
   */
  protected static class Node<T> {
    public T data;
    public int blackHeight;
    // The context array stores the context of the node in the tree:
    // - context[0] is the parent reference of the node,
    // - context[1] is the left child reference of the node,
    // - context[2] is the right child reference of the node.
    // The @SupressWarning("unchecked") annotation is used to supress an unchecked
    // cast warning. Java only allows us to instantiate arrays without generic
    // type parameters, so we use this cast here to avoid future casts of the
    // node type's data field.
    @SuppressWarnings("unchecked")
    public Node<T>[] context = (Node<T>[]) new Node[3];

    public Node(T data) {
      this.data = data;
      this.blackHeight = 0;
    }

    /**
     * @return true when this node has a parent and is the right child of that parent, otherwise
     *         return false
     */
    public boolean isRightChild() {
      return context[0] != null && context[0].context[2] == this;
    }

  }

  protected Node<T> root; // reference to root node of tree, null when empty
  protected int size = 0; // the number of values in the tree

  /**
   * Performs a naive insertion into a binary search tree: adding the input data value to a new node
   * in a leaf position within the tree. After this insertion, no attempt is made to restructure or
   * balance the tree. This tree will not hold null references, nor duplicate data values.
   * 
   * @param data to be added into this binary search tree
   * @return true if the value was inserted, false if not
   * @throws NullPointerException     when the provided data argument is null
   * @throws IllegalArgumentException when data is already contained in the tree
   */
  public boolean insert(T data) throws NullPointerException, IllegalArgumentException {
    // null references cannot be stored within this tree
    if (data == null)
      throw new NullPointerException("This RedBlackTree cannot store null references.");

    Node<T> newNode = new Node<>(data);
    if (this.root == null) {
      // add first node to an empty tree
      root = newNode;
      size++;
      enforceRBTreePropertiesAfterInsert(newNode);
      return true;
    } else {
      // insert into subtree
      Node<T> current = this.root;
      while (true) {
        int compare = newNode.data.compareTo(current.data);
        if (compare == 0) {
          throw new IllegalArgumentException(
              "This RedBlackTree already contains value " + data.toString());
        } else if (compare < 0) {
          // insert in left subtree
          if (current.context[1] == null) {
            // empty space to insert into
            current.context[1] = newNode;
            newNode.context[0] = current;
            this.size++;
            enforceRBTreePropertiesAfterInsert(newNode);
            return true;
          } else {
            // no empty space, keep moving down the tree
            current = current.context[1];
          }
        } else {
          // insert in right subtree
          if (current.context[2] == null) {
            // empty space to insert into
            current.context[2] = newNode;
            newNode.context[0] = current;
            this.size++;
            enforceRBTreePropertiesAfterInsert(newNode);
            return true;
          } else {
            // no empty space, keep moving down the tree
            current = current.context[2];

          }

        }
      }

    }
  }

  /**
   * This method is used to enforce the properties of the RBT tree after an insert is done
   * 
   * @param redNode The node that we are checking the validity of the tree with
   */
  protected void enforceRBTreePropertiesAfterInsert(Node<T> redNode) {
    this.root.blackHeight = 1;
    if (redNode.context[0] == null) { // if root
      this.root.blackHeight = 1;
      return;
    }


    if (redNode.context[0].blackHeight == 0) { // if has red parent use that red node
      enforceRBTreePropertiesAfterInsert(redNode.context[0]);
      return;
    }



    else if ((redNode.context[1] != null && redNode.context[1].blackHeight == 0)
        || (redNode.context[2] != null && redNode.context[2].blackHeight == 0)) { // if rednode has
                                                                                  // red child


      if (redNode.context[0].context[1] != null && redNode.context[0].context[2] != null
          && redNode.context[0].context[1].blackHeight == 0
          && redNode.context[0].context[2].blackHeight == 0) { // CASE 1 when parent is black and
                                                               // both children and one grand child
                                                               // is red


        redNode.context[0].context[1].blackHeight = 1; // both cildren turn black
        redNode.context[0].context[2].blackHeight = 1;
        redNode.context[0].blackHeight = 0; // parent turns red
        enforceRBTreePropertiesAfterInsert(redNode.context[0]); // call method on parent
        return;
      }
      // CASE 2 when left child is red and the other is either black or null
      else if (redNode.context[0].context[2] == null || redNode.context[0].context[1] != null
          && redNode.context[0].context[1].blackHeight == 0) {
        if (redNode.context[2] != null && redNode.context[2].blackHeight == 0) { // checks if
                                                                                 // rotation is
                                                                                 // needed
          rotate(redNode.context[2], redNode);
          redNode = redNode.context[0].context[1].context[1]; // makes the new node the redNode
                                                              // because of how rotate is coded
          if (redNode.context[0]!= null && redNode.context[0].blackHeight == 0) { // after rotation checks if the parent is red
            enforceRBTreePropertiesAfterInsert(redNode.context[0]);
            return;
          }
        }

        rotate(redNode, redNode.context[0]);
        redNode = redNode.context[1].context[0];
        redNode.blackHeight = 1;
        return;
      }
      // CASE 2 case 2 when right child is red and the other is either black or null
      else if (redNode.context[0].context[1] == null || redNode.context[0].context[2] != null
          && redNode.context[0].context[2].blackHeight == 0) {
        if (redNode.context[1] != null && redNode.context[1].blackHeight == 0) { 
          
          // checks if rotation is required to setup final rotation
          
          rotate(redNode.context[1], redNode);
          redNode = redNode.context[0].context[2].context[2];

          if (redNode.context[0]!= null &&redNode.context[0].blackHeight == 0) {
            enforceRBTreePropertiesAfterInsert(redNode.context[0]); // after rotation checks if the
                                                                    // parent is red
            return;
          }
        }

        rotate(redNode, redNode.context[0]); // rotation to finish case 2

        redNode = redNode.context[2].context[0];

        redNode.blackHeight = 1; // the rednode turns black

        return;

      }


    }



    this.root.blackHeight = 1; // making sure root is black
    return;
  }

  /**
   * Performs the rotation operation on the provided nodes within this tree. When the provided child
   * is a left child of the provided parent, this method will perform a right rotation. When the
   * provided child is a right child of the provided parent, this method will perform a left
   * rotation. When the provided nodes are not related in one of these ways, this method will throw
   * an IllegalArgumentException.
   * 
   * @param child  is the node being rotated from child to parent position (between these two node
   *               arguments)
   * @param parent is the node being rotated from parent to child position (between these two node
   *               arguments)
   * @throws IllegalArgumentException when the provided child and parent node references are not
   *                                  initially (pre-rotation) related that way
   */
  private void rotate(Node<T> child, Node<T> parent) throws IllegalArgumentException {
    // TODO: Implement this method.
    if (child.isRightChild()) {
      Node<T> newNode = new Node<>(parent.data);
      Node<T> newNode2 = new Node<>(child.data);
      newNode.context[1] = parent.context[1];
      if (parent.context[1] != null) {
        parent.context[1].context[0] = newNode;
      }
      newNode.context[2] = child.context[1]; // creating new nodes to change and swap
      if (child.context[1] != null) {
        child.context[1].context[0] = newNode;
      }
      newNode2.context[1] = newNode;
      newNode2.context[2] = child.context[2];
      if (child.context[2] != null) {
        child.context[2].context[0] = newNode2;
      }
      newNode.context[0] = newNode2;

      replaceNode(parent, newNode2); // swapping


    } else if (parent.context[1] != null && parent.context[1].equals(child)) { // if left child
      Node<T> newNode = new Node<>(parent.data);
      Node<T> newNode2 = new Node<>(child.data);
      newNode.context[2] = parent.context[2];
      if (parent.context[2] != null) {
        parent.context[2].context[0] = newNode;
      }
      newNode.context[1] = child.context[2];
      if (child.context[2] != null) {
        child.context[2].context[0] = newNode;
      }
      newNode2.context[2] = newNode;
      newNode2.context[1] = child.context[1];
      if (child.context[1] != null) {
        child.context[1].context[0] = newNode2;
      }
      newNode.context[0] = newNode2;
      replaceNode(parent, newNode2);

    } else {
      throw new IllegalArgumentException(); // if not child
    }
  }

  /**
   * Get the size of the tree (its number of nodes).
   * 
   * @return the number of nodes in the tree
   */
  public int size() {
    return size;
  }

  /**
   * Method to check if the tree is empty (does not contain any node).
   * 
   * @return true of this.size() return 0, false if this.size() > 0
   */
  public boolean isEmpty() {
    return this.size() == 0;
  }

  /**
   * Removes the value data from the tree if the tree contains the value. This method will not
   * attempt to rebalance the tree after the removal and should be updated once the tree uses
   * Red-Black Tree insertion.
   * 
   * @return true if the value was remove, false if it didn't exist
   * @throws NullPointerException     when the provided data argument is null
   * @throws IllegalArgumentException when data is not stored in the tree
   */
  public boolean remove(T data) throws NullPointerException, IllegalArgumentException {
    // null references will not be stored within this tree
    if (data == null) {
      throw new NullPointerException("This RedBlackTree cannot store null references.");
    } else {
      Node<T> nodeWithData = this.findNodeWithData(data);
      // throw exception if node with data does not exist
      if (nodeWithData == null) {
        throw new IllegalArgumentException(
            "The following value is not in the tree and cannot be deleted: " + data.toString());
      }
      boolean hasRightChild = (nodeWithData.context[2] != null);
      boolean hasLeftChild = (nodeWithData.context[1] != null);
      if (hasRightChild && hasLeftChild) {
        // has 2 children
        Node<T> successorNode = this.findMinOfRightSubtree(nodeWithData);
        // replace value of node with value of successor node
        nodeWithData.data = successorNode.data;
        // remove successor node
        if (successorNode.context[2] == null) {
          // successor has no children, replace with null
          this.replaceNode(successorNode, null);
        } else {
          // successor has a right child, replace successor with its child
          this.replaceNode(successorNode, successorNode.context[2]);
        }
      } else if (hasRightChild) {
        // only right child, replace with right child
        this.replaceNode(nodeWithData, nodeWithData.context[2]);
      } else if (hasLeftChild) {
        // only left child, replace with left child
        this.replaceNode(nodeWithData, nodeWithData.context[1]);
      } else {
        // no children, replace node with a null node
        this.replaceNode(nodeWithData, null);
      }
      this.size--;
      return true;
    }
  }

  /**
   * Checks whether the tree contains the value *data*.
   * 
   * @param data the data value to test for
   * @return true if *data* is in the tree, false if it is not in the tree
   */
  public boolean contains(T data) {
    // null references will not be stored within this tree
    if (data == null) {
      throw new NullPointerException("This RedBlackTree cannot store null references.");
    } else {
      Node<T> nodeWithData = this.findNodeWithData(data);
      // return false if the node is null, true otherwise
      return (nodeWithData != null);
    }
  }

  /**
   * Helper method that will replace a node with a replacement node. The replacement node may be
   * null to remove the node from the tree.
   * 
   * @param nodeToReplace   the node to replace
   * @param replacementNode the replacement for the node (may be null)
   */
  protected void replaceNode(Node<T> nodeToReplace, Node<T> replacementNode) {
    if (nodeToReplace == null) {
      throw new NullPointerException("Cannot replace null node.");
    }
    if (nodeToReplace.context[0] == null) {
      // we are replacing the root
      if (replacementNode != null)
        replacementNode.context[0] = null;
      this.root = replacementNode;
    } else {
      // set the parent of the replacement node
      if (replacementNode != null)
        replacementNode.context[0] = nodeToReplace.context[0];
      // do we have to attach a new left or right child to our parent?
      if (nodeToReplace.isRightChild()) {
        nodeToReplace.context[0].context[2] = replacementNode;
      } else {
        nodeToReplace.context[0].context[1] = replacementNode;
      }
    }
  }

  /**
   * Helper method that will return the inorder successor of a node with two children.
   * 
   * @param node the node to find the successor for
   * @return the node that is the inorder successor of node
   */
  protected Node<T> findMinOfRightSubtree(Node<T> node) {
    if (node.context[1] == null && node.context[2] == null) {
      throw new IllegalArgumentException("Node must have two children");
    }
    // take a steop to the right
    Node<T> current = node.context[2];
    while (true) {
      // then go left as often as possible to find the successor
      if (current.context[1] == null) {
        // we found the successor
        return current;
      } else {
        current = current.context[1];
      }
    }
  }

  /**
   * Helper method that will return the node in the tree that contains a specific value. Returns
   * null if there is no node that contains the value.
   * 
   * @return the node that contains the data, or null of no such node exists
   */
  protected Node<T> findNodeWithData(T data) {
    Node<T> current = this.root;
    while (current != null) {
      int compare = data.compareTo(current.data);
      if (compare == 0) {
        // we found our value
        return current;
      } else if (compare < 0) {
        // keep looking in the left subtree
        current = current.context[1];
      } else {
        // keep looking in the right subtree
        current = current.context[2];
      }
    }
    // we're at a null node and did not find data, so it's not in the tree
    return null;
  }

  /**
   * This method performs an inorder traversal of the tree. The string representations of each data
   * value within this tree are assembled into a comma separated string within brackets (similar to
   * many implementations of java.util.Collection, like java.util.ArrayList, LinkedList, etc).
   * 
   * @return string containing the ordered values of this tree (in-order traversal)
   */
  public String toInOrderString() {
    // generate a string of all values of the tree in (ordered) in-order
    // traversal sequence
    StringBuffer sb = new StringBuffer();
    sb.append("[ ");
    if (this.root != null) {
      Stack<Node<T>> nodeStack = new Stack<>();
      Node<T> current = this.root;
      while (!nodeStack.isEmpty() || current != null) {
        if (current == null) {
          Node<T> popped = nodeStack.pop();
          sb.append(popped.data.toString());
          if (!nodeStack.isEmpty() || popped.context[2] != null)
            sb.append(", ");
          current = popped.context[2];
        } else {
          nodeStack.add(current);
          current = current.context[1];
        }
      }
    }
    sb.append(" ]");
    return sb.toString();
  }

  /**
   * This method performs a level order traversal of the tree. The string representations of each
   * data value within this tree are assembled into a comma separated string within brackets
   * (similar to many implementations of java.util.Collection). This method will be helpful as a
   * helper for the debugging and testing of your rotation implementation.
   * 
   * @return string containing the values of this tree in level order
   */
  public String toLevelOrderString() {
    StringBuffer sb = new StringBuffer();
    sb.append("[ ");
    if (this.root != null) {
      LinkedList<Node<T>> q = new LinkedList<>();
      q.add(this.root);
      while (!q.isEmpty()) {
        Node<T> next = q.removeFirst();
        if (next.context[1] != null) {
          q.add(next.context[1]);

        }
        if (next.context[2] != null) {
          q.add(next.context[2]);
        }
        sb.append(next.data.toString());
        if (!q.isEmpty())
          sb.append(", ");
      }
    }
    sb.append(" ]");
    return sb.toString();
  }

  public String toString() {
    return "level order: " + this.toLevelOrderString() + "\nin order: " + this.toInOrderString();
  }


  @Test
  /**
   * checks if the tree ends up like expected
   * 
   * this tests every single insert case and checks if ther final result is  as expected
   * this has to cases where rotation and color switch is used
   *          
   *                 
   */
   void test1EnforceRBTreePropertiesAfterInsert() {
    RedBlackTree<Integer> tree = new RedBlackTree<Integer>();
    tree.insert(23);
    tree.insert(7);
    tree.insert(47);
    tree.insert(19);
    tree.insert(8);
    tree.insert(3);
    tree.insert(2);
    tree.insert(1);
    //Checks all the final tree black heights to see if they are what they are supposed to be
    assertEquals(tree.root.blackHeight, 1);

    assertEquals(tree.root.context[2].blackHeight, 0);
    assertEquals(tree.root.context[1].context[2].blackHeight, 1);
    assertEquals(tree.root.context[1].context[1].blackHeight, 1);
    assertEquals(tree.root.context[2].context[2].blackHeight, 1);
    assertEquals(tree.root.context[2].context[1].blackHeight, 1);
    assertEquals(tree.root.context[1].context[1].context[1].blackHeight, 0);
    assertEquals(tree.root.context[1].blackHeight, 0);
  }



  @Test
  /**
   * Tests the case of when there is   black node
   *                                   /     \
   *                                 red      red
   *                                /
   *                              red      
   */
  void test2EnforceRBTreePropertiesAfterInsert() {
    RedBlackTree<Integer> tree = new RedBlackTree<Integer>();
    tree.insert(23);
    tree.insert(7);
    tree.insert(47);
    tree.insert(19);
    //Checks all the final tree black heights to see if they are what they are supposed to be
    assertEquals(tree.root.blackHeight, 1);
    assertEquals(tree.root.context[1].blackHeight, 1);
    assertEquals(tree.root.context[2].blackHeight, 1);
    assertEquals(tree.root.context[1].context[2].blackHeight, 0);

  }

  @Test
  /**
   Tests the case of when there is   black node
   *                                   /     \
   *                                 red      null
   *                                   \
   *                                      red     
   */
  void test3EnforceRBTreePropertiesAfterInsert() {
    RedBlackTree<Integer> tree = new RedBlackTree<Integer>();
    tree.insert(23);
    tree.insert(7);
    tree.insert(47);
    tree.insert(19);
    tree.insert(8);
    //Checks all the final tree black heights to see if they are what they are supposed to be
    assertEquals(tree.root.blackHeight, 1);

    assertEquals(tree.root.context[2].blackHeight, 1);
    assertEquals(tree.root.context[1].context[2].blackHeight, 0);
    assertEquals(tree.root.context[1].context[1].blackHeight, 0);
    assertEquals(tree.root.context[1].blackHeight, 1);

  }



  // Implement at least 3 boolean test methods by using the method signatures below,
  // removing the comments around them and add in your testing code to them. You can
  // use your notes from lecture for ideas on concrete examples of rotation to test for.
  // Make sure to include rotations within and at the root of a tree in your test cases.
  // Give each of the methods a meaningful header comment that describes what is being
  // tested and make sure your test have inline comments to help developers read through them.
  // If you are adding additional tests, then name the method similar to the ones given below.
  // Eg: public static boolean test4() {}
  // Do not change the method name or return type of the existing tests.
  // You can run your tests by commenting in the calls to the test methods
  /**
   * tests if my rotate left method is working
   * 
   * @return true if my method is passing the tester else false
   */

  /**
   * Main method to run tests. Comment out the lines for each test to run them.
   * 
   * @param args
   */
  public static void main(String[] args) {

  }
}
