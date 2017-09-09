package mtg_reader;

import static mtg_reader.Card.CardType.artifact;
import static mtg_reader.Card.CardType.creature;
import static mtg_reader.Card.CardType.instant;
import static mtg_reader.Cost.ManaType.BLA;
import static mtg_reader.Cost.ManaType.BLU;
import static mtg_reader.Cost.ManaType.GRE;
import static mtg_reader.Cost.ManaType.RED;
import static mtg_reader.Cost.ManaType.WHI;
import static mtg_reader.digraph.KeyType.synonym;
import static mtg_reader.digraph.KeyType.expander;

public class Mtg_reader {

    public static void main(String[] args) {
        /*
        Card card1 = new Card("Acolyte of the Inferno", new Cost(2, RED), "Human Monk", 3, 1, creature);
        card1.addDesc("Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)");
        card1.addDesc("Whenever Acolyte of the Inferno becomes blocked by a creature, it deals 2 damage to that creature.");
        card1.explainMe();
        System.out.println();*/
        
        
        tree ie = new tree();
        
        /*
        // numbers and counters
        ie.addKey("{any_positive_numeric_integer}", "<1/2/3/4/5/6/7/8/9/10>");
        ie.addKey("{any_positive_english_integer}", "<one/two/three/four/five/six/seven/eight/nine/ten>");
        ie.addKey("{counter-s}", "<counter/counters>");
        ie.addKey("{counters}", "[+1/+1 {counter-s}]", "[-1/-1 {counter-s}]");
        
        // player definitions
        ie.addKey("{player}", "player", "opponent");
        ie.addKey("{players}", "players", "opponents");


        // creature adjective subdefinitions
        ie.addKey("{color}", "<white/blue/black/red/green>");
        ie.addKey("{color_with_non}", "{color}", "<nonwhite/nonblue/nonblack/nonred/nongreen>");
        ie.addKey("{color_adjective}", "{color_with_non}", "<colorless/colored/multicolored/monocolored>");
        
        ie.addKey("{creature_permanent_adjective}", "artifact", "enchantment", "land", "planeswalker", "nonartifact", "nonenchantment", "nonland", "nonplaneswalker");
        ie.addKey("{combat_adjective}", "attacking", "blocking", "blocked", "unblocked");

        // creature quality subdefinitions
        ie.addKey("{creature_keyword}", "flying", "hexproof", "trample", "lifelink", "first_strike", "double_strike", "vigilance", "deathtouch", "haste", "menace", "prowess");
        ie.addKey("{pt_equals}", "[<power/toughness> {any_positive_numeric_integer} or <greater/less>]");
            
        // creature verb past subdefinitions
        ie.addKey("{creature_entered}", "[entered the battlefield this turn]", "[entered the battlefield since your last turn ended]");
        ie.addKey("{creature_was_in_combat}", "[attacked this turn]", "[attacked you this turn]", "[blocked this turn]", "[<was/were> blocked this turn]", "[blocked or <was/were> blocked this turn]");
        ie.addKey("{creature_had_damage}", "[dealt damage this turn]", "[dealt damage to you this turn]", "[<was/were> dealt damage this turn]");
        
        // creature adjective, quality, and verb defintions
        ie.addKey("{creature_adjective}", "{color_adjective}", "{creature_permanent_adjective}", "{combat_adjective}");
        ie.addKey("{creature_quality}", "flying", "{pt_equals}");
        ie.addKey("{creature_verb_past}", "{creature_entered}", "{creature_was_in_combat}", "{creature_had_damage}");
        
        // creature definitions
        ie.addKey("{creature}", 
                "creature", 
                "[creature an opponent controls]",
                "[creature you control]",
                "[{creature_adjective} creature]", 
                "[creature with {creature_quality}]", 
                "[creature without {creature_quality}]", 
                "[creature that {creature_verb_past}]");
        ie.addKey("{creatures}", 
                "creatures", 
                "[{creature_adjective} creatures}", 
                "[creatures with {creature_quality}]", 
                "[creatures without {creature_quality}]", 
                "[creatures that {creature_verb_past}]");
        ie.addKey("{c_or_p}", "[creature or player]");
        
        
        
        
        
        
        ie.insertRoot("Destroy target {creature}.");
        ie.insertRoot("Destroy up to <X/{any_positive_integer}> target {creatures}.");
        ie.insertRoot("*Card_name* deals <X/{any_positive_numeric_integer}> damage to <target/each> <{creature}/{player}/{c_or_p}>.");
        */
        ie.insertRoot("The cat <jumped/climbed>.");
        ie.listSentences();

    }
    
}
