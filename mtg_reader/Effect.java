package mtg_reader;

import java.util.ArrayList;
import static mtg_reader.Effect.AbilityType.activated_ability;
import static mtg_reader.Effect.AbilityType.instant_effect;
import static mtg_reader.Effect.AbilityType.static_ability;
import static mtg_reader.Effect.AbilityType.triggered_ability;

public class Effect {
    public enum AbilityType {
        non, activated_ability, triggered_ability, static_ability, instant_effect
    }
    
    String unbrokenText;
    ArrayList<String> actualText = new ArrayList<>(); // The actual text of the card IE "Flying (This creature can't be blocked except by creatures with flying or reach)"
    ArrayList<String> funcText = new ArrayList<>(); // The functional text of the card IE "This creature can't be blocked except by creatures with flying or reach"
    AbilityType aType;
    ArrayList<String> cost = new ArrayList<>();
    ArrayList<String> trigger = new ArrayList<>();
    ArrayList<String> effect = new ArrayList<>();
    
    private void breakString(){
        String push = "";
        for (int i = 0; i < unbrokenText.length(); i++) {
            char curr = unbrokenText.charAt(i);
            if (curr == ' ' || curr == ':' || curr == '.' || curr == ',' || curr == '(' || curr == ')') {
                if (!"".equals(push)) actualText.add(push);
                push = "";
                if (curr != ' ') {
                    push += curr;
                    if (!"".equals(push)) actualText.add(push);
                    push = "";
                }
                
            }
            else push += curr;
        }
    }
    
    private AbilityType whatType(Card card, ArrayList<String> sText) {
        funcText = sText;
        AbilityType rType;
        // First check if the text is a keyword, that is if it contains a left and right parenthesis
        Boolean containsLeft = false;
        Boolean containsRight = false;
        int rightIndex = 0;
        ArrayList<String> inParenthesis = new ArrayList<>();
        
        // WARNING: THIS METHOD OF CHECKING FOR PARENTHESIS DOES NOT ALWAYS WORK
        // For cards which have full legal lines inside their parenthessi (ie keywords)
        // this is a functional method. However, for cards with random parenthesis inserted into
        // the card, such as the explanation of devotion, this will only evalutate the
        // text inside the parenthesis, which makes for an incomplete ability. 
        // In order to deal with this, you need to create an isLegalText
        // method which determines whether the entirety of a string can be made into a legal line
        // and use this to diffrenciate which lines with parenthesis need to be utilized the current way.

        // Starting from the end, look for the (rightmost) right parenthesis
        for (int i = sText.size() - 1; i >= 0; i--) {
            if (sText.get(i).equals(")")) {
                containsRight = true;
                rightIndex = i;
                break;
            }
        }

        // If the rightmost was found, we then start from the left, searching until we find a left parenthesis.
        // Once we find the left, we save each string inside until we hit the rightmost parenthesis we found in the above for loop.
        if (containsRight) {
            for (int i = 0; i < rightIndex; i++) {
                if (containsLeft) inParenthesis.add(sText.get(i));
                if (sText.get(i).equals("(")) containsLeft = true;
            }
            
            // If we've found both a left and a right parenthesis, we pass what was contained inside recursively.
        }

        if (containsRight && containsLeft) return whatType(card, inParenthesis);
        else {
            if (card.isPermanent()) {
                rType = static_ability; // By default
                // It is a triggered ability if:
                // - The first word is "At", "When", or "Whenever"
                // - It contains a comma
                // It is an activated ability if:
                // - It contains a colon
                
                Boolean containsComma = false;
                Boolean containsColon = false;
                for (int i = 0; i < sText.size(); i++) {
                    if (sText.get(i).equals(",")) containsComma = true;
                    else if (sText.get(i).equals(":")) {
                        containsColon = true;
                        break;
                        // We know if there is a colon then it must be an activated ability
                    }
                }
                
                // Whether or not the first word is "At", "When", or "Whenever"
                Boolean hasTrigger = (sText.get(0).equals("At") || sText.get(0).equals("When") || sText.get(0).equals("Whenever"));
                
                // Determining if this is actually a triggered ability or an activated ability rather than a static ability.
                if (hasTrigger && containsComma) return triggered_ability;
                else if (containsColon) return activated_ability;
            }
            else rType = instant_effect; // If it's not a permanent, it must be an instant or sorcery

            return rType;
        }
    }
    
    // This method takes the current ability and diffreciates what part is which
    // - For triggered abilities, it places the text before the comma as the trigger
    // and the text after as the effect
    // For activated abilities, it places the text before the colon as the cost
    // and the text after as the effect
    // For static abiities and instant effects it takes the entire text as the effect
    
    private void breakSections() {
        switch (aType) {
            case triggered_ability: {
                Boolean foundComma = false;
                int commaLocation = 0;
                // This loop finds the very last comma in the array and sets it as the character between the trigger and the effect.
                for (int i = funcText.size() - 1 ; i >= 0; i--) {
                    if (funcText.get(i).equals(",")) {
                        foundComma = true;
                        commaLocation = i;
                        break;
                    }
                }
                if (foundComma) {
                    for (int i = 0; i < funcText.size(); i++) {
                        // If we're before the comma, this character is part of the trigger.
                        // If we're after it, this character is part of the effect.
                        if (i < commaLocation) trigger.add(funcText.get(i));
                        else if (i > commaLocation) effect.add(funcText.get(i));
                    }
                }
                
                break;
            }
            case activated_ability: {
                Boolean foundColon = false;
                for (int i = 0; i < funcText.size(); i++) {
                    if (funcText.get(i).equals(":")) foundColon = true;
                    else if (!foundColon) cost.add(funcText.get(i));
                    else effect.add(funcText.get(i));
                }
                break;
            }
            default: {
                effect = funcText;
                break;
            }
        }
    }
    private void init(Card card, int index) {
        if (card.getCardText().size() > 0 && index < card.getCardText().size()) {
            unbrokenText = card.getCardText().get(index);
            unbrokenText += ' ';
            
            breakString();
            
            aType = whatType(card, actualText);
            breakSections();
            
        }
        else if (card.getCardText().size() == 0){}
        else throw new java.lang.Error("Out of bounds exception.");
    }
    public Effect(Card card, int index) {
        init(card, index);
    }
    public Effect(Card card) {
        init(card, 0);
    }
    
    public void explainMe() {
        for (int i = 0; i < funcText.size(); i++) {
            System.out.println("funcText[" + i + "]: " + funcText.get(i));
        }
        System.out.println("Ability Type: " + aType.name());
        switch (aType) {
            case activated_ability: {
                System.out.println("Cost: " + cost);
                System.out.println("Effect: " + effect);
                break;
            }
            case triggered_ability: {
                System.out.println("Trigger: " + trigger);
                System.out.println("Effect: " + effect);
                break;
            }
            default: {
                System.out.println("Effect: " + effect);
                break;
            }
        }
    }
}
