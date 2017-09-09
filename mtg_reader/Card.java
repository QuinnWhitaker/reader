package mtg_reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static mtg_reader.Card.CardType.artifact;
import static mtg_reader.Card.CardType.enchantment;
import static mtg_reader.Card.CardType.instant;
import static mtg_reader.Card.ColorStatus.colorLess;
import static mtg_reader.Card.ColorStatus.monoColored;
import static mtg_reader.Card.ColorStatus.multiColored;
import static mtg_reader.Card.SuperType.NONE;
import mtg_reader.Cost.ManaType;
import static mtg_reader.Cost.ManaType.BLA;
import static mtg_reader.Cost.ManaType.BLU;
import static mtg_reader.Cost.ManaType.CLS;
import static mtg_reader.Cost.ManaType.GRE;
import static mtg_reader.Cost.ManaType.RED;
import static mtg_reader.Cost.ManaType.WHI;

public class Card {
    public enum CardType {
        artifact, creature, enchantment, instant, land, planeswalker, sorcery
    }
    public enum SuperType {
        NONE, basic, legendary, snow
    }
    public enum ColorStatus { // All three of these possible color status
        colorLess, monoColored, multiColored
    }
    
    // General Card info
    private String cardName;
    private Cost manaCost;
    private int CMC;
    private ArrayList<ManaType> listOfColors = new ArrayList<>();
    private ColorStatus colorStatus;
    private ArrayList<CardType> cardTypes = new ArrayList<>();
    private SuperType superType;
    private ArrayList<String> subTypes = new ArrayList<>();
    private ArrayList<String> cardText = new ArrayList<>();
    private int power;
    private int toughness;
    
    // Abilities on the card
    private ArrayList<Effect> effects = new ArrayList<>();
    
    // Additional variables
    private Boolean isArtifact = false;
    private Boolean isCreature = false;
    private Boolean isEnchantment = false;
    private Boolean isInstant = false;
    private Boolean isLand = false;
    private Boolean isPlaneswalker = false;
    private Boolean isSorcery = false;
    private Boolean isPermanent = false;
    
    private void initialize(String name, Cost cost, SuperType supT, String subT, CardType... CT) {
        
        cardName = name;
        manaCost = cost;
        CMC = manaCost.cmc();
        
        // Determining what colors the card is.
        // For each color, add it if it applies.
        if (manaCost.nW() > 0) listOfColors.add(WHI);
        if (manaCost.nU() > 0) listOfColors.add(BLU);
        if (manaCost.nB() > 0) listOfColors.add(BLA);
        if (manaCost.nR() > 0) listOfColors.add(RED);
        if (manaCost.nG() > 0) listOfColors.add(GRE);
        // Based on how many colors have been added so far, we can determine
        // whether the card is colorless, monocolored, or multicolored.
        switch (listOfColors.size()) {
            case (0): { colorStatus = colorLess; break; }
            case (1): { colorStatus = monoColored; break; }
            default: { colorStatus = multiColored; break; }
        }
        if (colorStatus == colorLess) listOfColors.add(CLS);
        
        // Adding each card type to the list of card types
        // This section also initializes the isCreature (and potentially other) booleans
        
        cardTypes.addAll(Arrays.asList(CT));
        for (int i = 0; i < cardTypes.size(); i++) {
            switch (cardTypes.get(i)) {
                case artifact: { isArtifact = true; break; }
                case creature: { isCreature = true; break; }
                case enchantment: { isEnchantment = true; break; }
                case instant: { isInstant = true; break; }
                case land: { isLand = true; break; }
                case planeswalker: { isPlaneswalker = true; break; }
                case sorcery: { isSorcery = true; break; }
            }
        }
        
        isPermanent = (isArtifact || isCreature || isEnchantment || isLand || isPlaneswalker);
        
        
        
        
        if (cardTypes.isEmpty()) cardTypes.add(artifact); // By default there will always be at least 1 card type
        
        superType = supT;
        
        // Determining what subtypes the card has
        // Goes through the string (IE "Zombie Wizard") and for every space,
        // pushes the string onto the list of strings.
        // subTypes[0] = "Zombie" subTypes[1] = "Wizard"
        subTypes.clear();
        if (subT.length() > 0) {
            String take = "";
            subT += ' '; // Adding this space at the end allows the for loop to catch the very last subtype listed.
            for (int i = 0; i < subT.length(); i++) {
                char curr = subT.charAt(i);
                if (curr != ' ') {
                    take += curr;
                }
                else {
                    subTypes.add(take);
                    take = "";
                }
            }
        }
    }
    
    // Print method
    public void explainMe() {
        System.out.println("Hello! My name is " + cardName + "!");
        System.out.println("My cost is " + manaCost.toString() + " and my CMC is " + manaCost.cmc() +".");
        switch (colorStatus) {
            case colorLess: { System.out.println("I am colorless!"); break; }
            case monoColored: {
                System.out.println("I am mono-" + manaCost.manaToString(listOfColors.get(0)) + "!");
                break;
            }
            case multiColored: {
                System.out.print("I am multicolored! (");
                for (int i = 0; i < listOfColors.size(); i++) {
                    System.out.print(manaCost.manaToString(listOfColors.get(i)));
                }
                System.out.println(")");
                break;
            }
        }
        System.out.print("I am a");
        if (superType != NONE) System.out.print(superType.name() + " ");
        else if (cardTypes.get(0) == artifact || cardTypes.get(0) == enchantment || cardTypes.get(0) == instant) System.out.print("n");
        for (int i = 0; i < cardTypes.size(); i++) {
            System.out.print(" " + cardTypes.get(i).name());
        }
        if (subTypes.size() > 0) {
            System.out.print(" -- ");
            for (int i = 0; i < subTypes.size(); i++) {
                System.out.print(subTypes.get(i) + " ");
            }
        }
        System.out.println("!");
        if (isCreature) System.out.println("My P/T is " + power + "/" + toughness + "!");
        System.out.println("My card text reads: ");
        for (int i = 0; i < cardText.size(); i++) {
            System.out.println("'" + cardText.get(i) + "'");
        }
        System.out.println("Thes are my abilities: ");
        for (int i = 0; i < effects.size(); i++) {
            effects.get(i).explainMe();
        }
        
    }
    
    public void addDesc(String text) {
        cardText.add(text);
        effects.add(new Effect(this, cardText.size()-1));
    }
    
    // Constructor with supertype and subtype
    public Card(String name, Cost cost, SuperType supT, String subT, CardType... CT) {
        initialize(name, cost, supT, subT, CT);
    }
    // Creature constructor with supertype and subtype
    public Card(String name, Cost cost, SuperType supT, String subT, int pow, int tou, CardType... CT) {
        power = pow;
        toughness = tou;
        initialize(name, cost, supT, subT, CT);
        
    }
    
    // Creature constructor without supertype and with subtype
    public Card(String name, Cost cost, String subT, int pow, int tou, CardType... CT) {
        power = pow;
        toughness = tou;
        initialize(name, cost, NONE, subT, CT);
        
    }
    
    // Constructor without supertype or subtype
    public Card(String name, Cost cost, CardType... CT) {
        initialize(name, cost, NONE, "", CT);
    }
    
    // Constructor without supertype and with subtype
    public Card(String name, Cost cost, String subT, CardType... CT) {
        initialize(name, cost, NONE, subT, CT);
    }
    
    // Constructor with supertype and without subtype
    public Card(String name, Cost cost, SuperType supT, CardType... CT) {
        initialize(name, cost, supT, "", CT);
    }
    
    // Getters
    public String getCardName() { return cardName; }
    public Cost getManaCost() { return manaCost; }
    public int getCMC() { return CMC; }
    public ArrayList<ManaType> getColors() { return listOfColors; }
    public ColorStatus getColorStatus() { return colorStatus; }
    public ArrayList<CardType> getCardTypes() { return cardTypes; }
    public SuperType getSuperType() { return superType; }
    public ArrayList<String> getSubTypes() { return subTypes; }
    public ArrayList<String> getCardText() { return cardText; }
    public int getPower() { return power; }
    public int getToughness() { return toughness; }
    
    public Boolean isArtifact() { return isArtifact; }
    public Boolean isCreature() { return isCreature; }
    public Boolean isEnchantment() { return isEnchantment; }
    public Boolean isInstant() { return isInstant; }
    public Boolean isLand() { return isLand; }
    public Boolean isPlaneswalker() { return isPlaneswalker; }
    public Boolean isSorcery() { return isSorcery; }
    public Boolean isPermanent() { return isPermanent; }
}
