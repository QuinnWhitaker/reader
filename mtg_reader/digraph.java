package mtg_reader;

import java.util.ArrayList;
import java.util.Arrays;

public class digraph {
    // The string_key is a class that associates multi-valued strings with each of their definitions
    
    // It contains two essential objects: The main string and the list of its definitions
    // Examples:
    // main string: "permanent", list of strings: "artifact", "creature", "enchantment", "land", "planeswalker"
    // main string: "color", list of strings: "white", "blue", "black", "red", "green"
    
    // the string_key class is also used for words which have no single primary definition, but multiple
    // Examples:
    // list of strings: a, an
    // list of strings: creature, creatures
    // list of strings: deal, deals
    
    
    // The KeyType enum is used by the string_key class to differenciate which kind of string_key it is
    // The synonym KeyType is for string_keys the which one primary has the same meaning as all of its secondaries
    // for instance, [player] holds the same value as player, players, and player's
    // The expander KeyType is for strings which the primary is not a part of the digraph, but automatically attaches
    // each of tis secondaries to the tree
    // For instance {color} means instead white, blue, black, red, and green will all be attached in {color}'s place
    
    
    public enum KeyType {
        synonym, expander 
    }
    
    class string_key {
        
        private String primary;
        private ArrayList<String> secondary = new ArrayList<>();
        KeyType keyType;
        
        public string_key(String prim, KeyType key, String ... seco) {
            primary = prim;
            secondary.addAll(Arrays.asList(seco));
            keyType = key;
        }
        
        // Getters
        public String primary() { return primary; }
        public int secondarySize() { return secondary.size(); }
        public String getSecondary(int index) { return secondary.get(index); }
        public KeyType keyType() { return keyType; }
        
    }
    // The Node is what contains each piece of data in the digraph
    class Node {
        // Primary variables
        private String data;
        private ArrayList<Node> children = new ArrayList<>();
        private ArrayList<Node> parents = new ArrayList<>();
        
        // Functions
        public Boolean isChildOf(Node inQuestion) {
            // Function that returns whether or not the called node is a parent node of the calling node
            for (int i = 0; i < parents.size(); i++) {
                if (inQuestion == parents.get(i)) return true;
            }
            return false;
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
            data = object;
        }
        // Non-root constructor
        public Node(String object, Node calling) { 
            data = object;
            
            if (object instanceof String) {
                
            }
            calling.addChild(this);
            addParent(calling);
        }
        public void addChild(Node node) {
            children.add(node);
        }
        public void addParent(Node node) {
            parents.add(node);
        }
        public void explainMe() {
            if (parents.isEmpty()) {
                System.out.println("NO PARENTS, IS ROOT");
            }
            else {
                System.out.println("-- PARENTS --");
                for (int i = 0; i < parents.size(); i++) {
                    System.out.println("PARENT " + i + " VALUE: " + parents.get(i).get());
                }
            }
            
            System.out.println("VALUE: " + data);
            
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
        public void set(String object) { data = object; }
        public String get() { return data; }
    }
    
    
    private ArrayList<Node> nodes = new ArrayList<>();
    // Each node in the digraph
    private ArrayList<string_key> synKeys = new ArrayList<>();
    private ArrayList<string_key> expKeys = new ArrayList<>();
    
    // Finds and returns the node containing the given data, if it exists. Otherwise returns null.
    private Node access(String data) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).get().equals(data)) return nodes.get(i);
        }
        return null;
    }
    public void addKey(String newPrimary, KeyType key, String ... newSecondary) {
        switch (key) {
            case synonym: { synKeys.add(new string_key(newPrimary, key, newSecondary)); break; }
            case expander: { expKeys.add(new string_key(newPrimary, key, newSecondary)); break; }
        }
        
    }
    
    // Checks to see whether the string starts with the first char and ends with the last char
    private Boolean hasChars(char first, char last, String inQuestion) {
        return ((inQuestion.charAt(0) == first) && (inQuestion.charAt(inQuestion.length() - 1) == last));
    }
    
    // Takes a string that's in brackets, braces, arrows, etc. and returns the string contained
    // Ex. [player] returns player
    private String inside(String outside) {
        return outside.substring(1, outside.length()-2);
    }
    
    // Takes a string surrounded by arrows and automatically returns a list of each string it contains separated by commas
    // ex. <red, blue, green> returns a list of [0] = "red" [1] = "blue" [2] = "green"
    private ArrayList<String> breakByComma(String toBreak) {
        toBreak = inside(toBreak);
        ArrayList<String> ret = new ArrayList<>();
        
        String toAdd = "";
        for (int charNum = 0; charNum < toBreak.length(); charNum++) {
            switch(toBreak.charAt(charNum)) {
                case '<': break;
                case '>': break;
                case ' ': break;
                case ',': { ret.add(toAdd); break; }
                default: { toAdd += toBreak.charAt(charNum); break; }
            }
        }
        return ret;
    }
    public void insert(String parentData, String childData) { 

        Boolean parentBraces = false;
        Boolean childBraces = false;
        Boolean parentArrows = false;
        Boolean childArrows = false;
        
        if (hasChars('{', '}', parentData)) parentBraces = true;
        else if (hasChars('{', '}', childData)) childBraces = true;
        else if (hasChars('<', '>', parentData)) parentArrows = true;
        else if (hasChars('<', '>', childData)) childArrows = true;
        
        
        
        // Used in multiple situations for when we're looking for a string_key
        // foundChild represents whether a string_key was found to match the child
        // foundParent is the same for the parent
        Boolean foundChild = false;
        Boolean foundParent = false;
        
        // If the above statements were true, where were they found
        int foundChildAt = -1;
        int foundParentAt = -1;

        // First check each secondary string within each synKey. If the word is contained among a synKey
        // then re-call the insert method with the string replaced by the synKey's primary
        for (int key = 0; key < synKeys.size(); key++) {
            for (int seco = 0; seco < synKeys.get(key).secondarySize(); seco++) {
                if (synKeys.get(key).getSecondary(seco).equals(parentData)) {
                    foundParent = true;
                    foundParentAt = key;
                }
                else if (synKeys.get(key).getSecondary(seco).equals(childData)) {
                    foundChild = true;
                    foundChildAt = key;
                }
                else if (foundParent && foundChild) break;
            }
            if (foundParent && foundChild) break;
        }
        if (foundParent || foundChild) {
            // The strings which will be recursively called.
            String parentToCall = parentData;
            String childToCall = childData;
            if (foundParent) parentToCall = synKeys.get(foundParentAt).primary();
            if (foundChild) childToCall = synKeys.get(foundChildAt).primary();
            insert(parentToCall, childToCall);
        }
        else if (parentBraces || childBraces) {
            // If the phrase has braces then we know this is an expander key
            // so therefore instead of using the called string, recall insert for each secondary
            // and instead insert those secondaries into the same location
            foundParent = false;
            foundChild = false;
            for (int key = 0; key < expKeys.size(); key++) {
                if (expKeys.get(key).primary().equals(parentData)) {
                    foundParent = true;
                    foundParentAt = key;
                }
                else if (expKeys.get(key).primary().equals(childData)) {
                    foundChild = true;
                    foundChildAt = key;
                }
                else if (foundParent && foundChild) break;
            }
            if (foundParent && foundChild) {
                for (int seco1 = 0; seco1 < expKeys.get(foundParentAt).secondarySize(); seco1++) {
                    for (int seco2 = 0; seco2 < expKeys.get(foundChildAt).secondarySize(); seco2++) {
                        insert(expKeys.get(foundParentAt).getSecondary(seco1), expKeys.get(foundChildAt).getSecondary(seco2));
                    }
                }
            }
            else if (foundParent) {
                for (int seco = 0; seco < expKeys.get(foundParentAt).secondarySize(); seco++) {
                    insert(expKeys.get(foundParentAt).getSecondary(seco), childData);
                }
            }
            else if (foundChild) {
                for (int seco = 0; seco < expKeys.get(foundChildAt).secondarySize(); seco++) {
                    insert(parentData, expKeys.get(foundChildAt).getSecondary(seco));
                }
            }
            else System.out.println("Error: Found braces in '" + parentData + "', '" + childData + "' but could not find match in expKeys list!");
        }
        else if (parentArrows || childArrows) {
            // If the phrase has arrows then separate each string contained by the commas and then insert each individual word
            ArrayList<String> parentWords = new ArrayList<>();
            ArrayList<String> childWords = new ArrayList<>();
            
            if (parentArrows) parentWords = breakByComma(parentData);
            if (childArrows) childWords = breakByComma(childData);
            
            if (parentArrows && childArrows) {
                for (int i = 0; i < parentWords.size(); i++) {
                    for (int i2 = 0; i2 < childWords.size(); i2++) {
                        insert(parentWords.get(i), childWords.get(i2));
                    }
                }
            }
            else if (parentArrows) {
                for (int i = 0; i < parentWords.size(); i++) {
                    insert(parentWords.get(i), childData);
                }
            }
            else if (childArrows) {
                for (int i = 0; i < childWords.size(); i++) {
                    insert(parentData, childWords.get(i));
                }
            }
            
        }
        else {
            // The parent must exist.
            // If the child already exists then see if the connection already exists.
            // If the connection does not exist then connect the parent and the child
            // If the child does not exist it will be created attached to the parent.
            if (access(parentData) != null) {
                if (access(childData) != null) {
                    if (access(childData).isChildOf(access(parentData)) && access(parentData).isParentOf(access(childData))) {
                        System.out.println("Attempted to duplicate connection of '" + parentData + "'(parent) to '" + childData + "'(child)!");
                    }
                    else {
                        access(childData).addParent(access(parentData));
                        access(parentData).addChild(access(childData));
                    }
                }
                else {
                    Node newNode = new Node(childData, access(parentData));
                    nodes.add(newNode);
                }
            }
            else System.out.println("Attempted to create connection with nonexistant parent '" + parentData + "'!");
        }
    }
    public void insertRoot(String childData) {
        insert(rootName, childData);
    }
    public void explainMe() {
        System.out.println("==== DIRECTIONAL GRAPH ====");
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println("=== NODE " + i + " ===");
            nodes.get(i).explainMe();
            System.out.println("");
        }
    }
    public Object get(int index) {
        return nodes.get(index).get();
    }
    
    public digraph() {
        nodes.add(new Node(rootName));
    }
}
