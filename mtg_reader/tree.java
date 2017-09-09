package mtg_reader;
// The tree is different than the digraph in that each possible word is represented.
// Not every node contains a unique node like in the diagraph. Instead, the tree
// is intended to track specific word combinations and resolve their contents
// based on which words came in and in which order they came.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import static mtg_reader.tree.keyType.arrows;
import static mtg_reader.tree.keyType.braces;
import static mtg_reader.tree.keyType.brackets;
import static mtg_reader.tree.keyType.normal;



public class tree {
    // This enum is to be used to diffrenciate each node's String in the insertRoot method
    
    public enum keyType {
        normal, braces, brackets, arrows
    }
    
    // The Node is what contains each piece of data in the tree
    class Node {
        // Primary variables
        private String string;
        private Node parent;
        private ArrayList<Node> children = new ArrayList<>();
        private int nodeNumber;
        
        // Functions
        public Boolean isChildOf(Node inQuestion) {
            // Function that returns whether or not the called node is a parent node of the calling node
            return inQuestion == parent;
        }
        public Boolean isParentOf(Node inQuestion) {
            // Function that returns whether or not the called node is a child node of the calling node
            for (int i = 0; i < children.size(); i++) {
                if (inQuestion == children.get(i)) return true;
            }
            return false;
        }
        
        
        // Root constructor
        public Node(String object) {
            string = object;
            parent = null;
        }
        // Non-root constructor
        public Node(String object, Node calling) { 
            string = object;
            calling.addChild(this);
            setParent(calling);
        }
        public void addChild(Node node) {
            children.add(node);
        }
        public void setParent(Node node) {
            parent = node;
        }
        public void explainMe() {
            if (parent == null) {
                System.out.println("NO PARENT, IS ROOT");
            }
            else {
                System.out.println("PARENT: " + parent.get());
            }
            
            System.out.println("VALUE: " + string);
            
            if (children.isEmpty()) {
                System.out.println("NO CHILDREN");
            }
            else {
                System.out.println("-- CHILDREN --");
                for (int i = 0; i < children.size(); i++) {
                    System.out.println("CHILD " + i + " VALUE: " + children.get(i).get());
                }
            }

        }
        public Node getParent() {
            return parent;
        }
        public Node getChild(int index) {
            if (index >= 0 && index < children.size()) return children.get(index);
            else return null;
        }
        public Node findChild(String withThisData) {
            if (children.isEmpty()) return null;
            for (int child = 0 ; child < children.size(); child++) {
                if (children.get(child).get().equals(withThisData)) return children.get(child);
            }
            return null;
        }
        public int numChildren() {
            return children.size();
        }
        public void set(String object) { string = object; }
        public String get() { return string; }
        public void setNodeNumber(int value) { nodeNumber = value; }
        public int nodeNumber() { return nodeNumber; }
    }
    
    class Key {
        private String primaryKey;
        private ArrayList<String> secondaryKeys = new ArrayList<>();
        
        public Key(String primary, String ... secondary) {
            primaryKey = primary;
            secondaryKeys.addAll(Arrays.asList(secondary));
        }
        public String getPrimary() { return primaryKey; }
        public ArrayList<String> getSecondaries() { return secondaryKeys; }
    }
    
    public final String rootName = "*root*";
    private Node rootNode;
    private ArrayList<Key> keys = new ArrayList<>();
    
    
    public int numNodes = 0;
    
    public tree(){
        rootNode = new Node(rootName);
    }
    
    public keyType getKey(String string) {
        char first = string.charAt(0);
        char last = string.charAt(string.length() - 1);
        
        if (first == '{' && last == '}') return braces;
        else if (first == '[' && last == ']') return brackets;
        else if (first == '<' && last == '>') return arrows;
        return normal;
    }
    
    public void addKey(String prim, String ... seco) {
        if (getKey(prim) == braces) {
            keys.add(new Key(prim, seco));
        }
        else System.out.println("Error: '" + prim + "' does not have braces");
    }
    
    // breaks a string in the format <string/string/string/> into an arraylist of each string then returns the arraylist
    private ArrayList<String> arrowBreakString(String toBreak) {
        ArrayList<String> ret = new ArrayList<>();
        String push = "";
        for (int i = 0; i < toBreak.length(); i++) {
            char curr = toBreak.charAt(i);
            if (curr == '/' || curr == '>') {
                ret.add(push);
                push = "";
            }
            else if (curr != '<') push += curr;
        }
        return ret;
    }
    
    public ArrayList<String> bracketBreakString(String toBreak) {
        String newString = "";
        if (toBreak.charAt(0) == '[' && toBreak.charAt(toBreak.length()-1) == ']') {
            newString = toBreak.substring(1, toBreak.length() - 1);
        }
        return breakString(newString);
    }
    
    // The breakstring method converts a String to an ArrayList of Strings
    // Each space separates each word and character.
    // The if statement below includes each character (discluding space) which is seaparted as its own word in the array
    private ArrayList<String> breakString(String toBreak) {
        ArrayList<String> ret = new ArrayList<>();
        String push = "";
        for (int i = 0; i < toBreak.length(); i++) {
            char curr = toBreak.charAt(i);
            if (curr == ' ' || curr == ':' || curr == '.' || curr == ',' || curr == '(' || curr == ')') {
                if (!"".equals(push)) {
                    ret.add(push);
                }
                push = "";
                if (curr != ' ') {
                    push += curr;
                    if (!"".equals(push)) ret.add(push);
                    push = "";
                }
            }
            else push += curr;
        }
        if (!"".equals(push)) {
            ret.add(push);
        }
        return ret;
    }
    
    public void insertRoot(String full) {
        // The public method converts a full string to an arrayList of strings that can be
        // added as nodes. Then the official insertFromNode method is called
        insertFromNode(rootNode, breakString(full));
    }
    
    private void insertFromNode(Node parent, ArrayList<String> children) {
        // A recurvise method which checks whether the first string of the list is contained by a child of the parent Node
        // If it isn't, a new node is created
        // In either case, the first element of the list is removed and the remaining list is recalled with the node which contained the removed string
        // When arrows or keys are used, the method is recursively recalled for each new split
        
        if (children.size() > 0) {
            ArrayList<String> newChildren = children;
            String first = newChildren.get(0);
            newChildren.remove(0);
            switch (getKey(first)) {
                case arrows: {
                    ArrayList<String> arrowList = arrowBreakString(first);
                    ArrayList<String> arrowChildren;
                    
                    // In this loop, a new list of children for each string inside the arrows
                    // Each new list instead of beginning with the string in the format <string1/string2/string3/...> followed by the rest of the children
                    // it begins with string1 followed by each child, then string2 followed by each child, etc.
                    for (String arrowString : arrowList) {
                        arrowChildren = new ArrayList<>();
                        arrowChildren.add(arrowString);
                        for (String child : newChildren) {
                            arrowChildren.add(child);
                        }
                        insertFromNode(parent, arrowChildren);
                    }
                    break;
                }
                case braces: {
                    Key chosenKey = null;
                    for (Key key : keys) {
                        if (key.getPrimary().equals(first)) {
                            chosenKey = key;
                            break;
                        }
                    }
                    if (chosenKey != null) {
                        ArrayList<String> braceList = chosenKey.getSecondaries();
                        ArrayList<String> braceChildren;
                        
                        // This loop is almost exactly the same as the arrows loop.
                        // For each secondary key in the chosen key, the insertFromRoot method will be recalled, with the first
                        // string being replaced with the secondary key.
                        
                        for (String braceString : braceList) {
                            braceChildren = new ArrayList<>();
                            braceChildren.add(braceString);
                            for (String child : newChildren) {
                                braceChildren.add(child);
                            }
                            insertFromNode(parent, braceChildren);
                        }
                    }
                    else System.out.println("Error: Key not found");
                    break;
                }
                case brackets: {
                    ArrayList<String> bracketList = bracketBreakString(first);
                    for (String child : newChildren) {
                        bracketList.add(child);
                    }
                    insertFromNode(parent, bracketList);
                    break;
                }
                default: {
                    if (parent.findChild(first) == null) {
                        Node newNode = new Node(first, parent);
                        numNodes++;
                        newNode.setNodeNumber(numNodes);
                        insertFromNode(newNode, newChildren);
                    }
                    else insertFromNode(parent.findChild(first), newChildren);
                    break;
                }
            }
        }
    }

    private void explainFrom(Node start) {
        System.out.println("=== NODE " + start.nodeNumber() + " ===");
        start.explainMe();
        System.out.println();
        Node currentNode = start;
        for (int child = 0; child < start.numChildren(); child++) {
            currentNode = start.getChild(child);
            if (currentNode != null) explainFrom(currentNode);
            else System.out.println("Error from explainFrom");
        }
    }
    public void explainMe() {
        System.out.println("==== TREE ====");
        explainFrom(rootNode);
        System.out.println("Number of nodes: " + numNodes);
    }
    
    private ArrayList<Integer> visited = new ArrayList<>();
    private void listFromNode(Node current, String toPrint) {
        int numAvailableChildren = current.numChildren();
        int nodeNum = current.nodeNumber();
        
        // if the current node is not the root and it has no children, add a space and the surrent node's string to the sentence, 
            // print the sentence, add the nodenumber to the visited list and start back from the root with a blank string
        if (numAvailableChildren == 0 && nodeNum != 0) {
            toPrint += " ";
            toPrint += current.get();
            System.out.println(toPrint);
            visited.add(nodeNum);
            listFromNode(rootNode, "");
        }
        else {
            // Checking how many of the current node's children are available
            Node firstAvailable = null;
            for (int child = 0; child < current.numChildren(); child++) {
                Node currentChild = current.getChild(child);
                // checking whether the current child is contained in the visited list.
                Boolean wasVisited = false;
                for (int v = 0; v < visited.size(); v++) {
                    if (currentChild.nodeNumber() == visited.get(v)) {
                        wasVisited = true;
                        break;
                    }
                }
                if (!wasVisited) {
                    firstAvailable = currentChild;
                    break;
                }
            }
            
            // If there are no available children
            if (firstAvailable == null) {
                // if the current node is not the root, add this nodenumber to the visited map and recall this from the parent of the current node
                // if the current node is the root and it has no available children, you're done
                if (nodeNum != 0) {
                    visited.add(nodeNum);
                    listFromNode(current.getParent(), toPrint);
                }
                
            }
            else {
                // if there is an available child, add the current node's string to the sentence and recursivley call the child with the current string
                toPrint += " ";
                toPrint += current.get();
                listFromNode(firstAvailable, toPrint);
            }
        }
        
        
        
        
        
        
        
        
        
    }
    public void listSentences() {
        visited.clear();
        listFromNode(rootNode, "");
    }
}
