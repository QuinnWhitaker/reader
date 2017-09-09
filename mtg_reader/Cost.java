package mtg_reader;

import java.lang.reflect.Array;
import static mtg_reader.Cost.ManaType.ANY;
import static mtg_reader.Cost.ManaType.BLA;
import static mtg_reader.Cost.ManaType.BLU;
import static mtg_reader.Cost.ManaType.CLS;
import static mtg_reader.Cost.ManaType.GRE;
import static mtg_reader.Cost.ManaType.RED;
import static mtg_reader.Cost.ManaType.WHI;

public class Cost {
    public enum ManaType {
        ANY, CLS, WHI, BLU, BLA, RED, GRE
    } // Any color, colorless, white, blue, black, red, green
    
    // The number of each type of mana in the cost.
    private int numANY;
    private int numCLS;
    private int numWHI;
    private int numBLU;
    private int numBLA;
    private int numRED;
    private int numGRE;
    
    private int castingCost;
    
    private void calculate(ManaType mana) {
        switch (mana) {
            case CLS: { numCLS++; break; }
            case WHI: { numWHI++; break; }
            case BLU: { numBLU++; break; }
            case BLA: { numBLA++; break; }
            case RED: { numRED++; break; }
            case GRE: { numGRE++; break; }
            default: break;
        }
        castingCost++;
    }
    
    public String manaToString(ManaType mana) {
        switch (mana) {
            case CLS: return "C";
            case WHI: return "W";
            case BLU: return "U";
            case BLA: return "B";
            case RED: return "R";
            case GRE: return "G";
            default: return "x";
        }
    }
    
    private String perColor(ManaType mana) {
        int numMana;
        switch (mana) {
            case ANY: if (numANY > 0 && castingCost > 0) return Integer.toString(numANY);
            case CLS: numMana = numCLS;
            case WHI: numMana = numWHI;
            case BLU: numMana = numBLU;
            case BLA: numMana = numBLA;
            case RED: numMana = numRED;
            case GRE: numMana = numGRE;
            default: numMana = 0;
        }
        if (numMana == 0) return "";
        String ret = "";
        for (int i = 0; i < numMana + 1; i++) {
            ret += manaToString(mana);
        }
        return ret;
    }
    
    @Override
    public String toString() {
        String ret = "";
        if (numANY > 0 || castingCost == 0) ret += Integer.toString(numANY);
        for (int i = 0; i < numCLS; i++) {
            ret += manaToString(CLS);
        }
        for (int i = 0; i < numWHI; i++) {
            ret += manaToString(WHI);
        }
        for (int i = 0; i < numBLU; i++) {
            ret += manaToString(BLU);
        }
        for (int i = 0; i < numBLA; i++) {
            ret += manaToString(BLA);
        }
        for (int i = 0; i < numRED; i++) {
            ret += manaToString(RED);
        }
        for (int i = 0; i < numGRE; i++) {
            ret += manaToString(GRE);
        }
        return ret;
    }
    // A mana cost can include any number of any type mana as well as any number of type-specific mana
    // IE a RR spell would be (0, RED, RED) or a 4UBG spell would (4, BLU, BLA, GRE) or a 5 spell would be (5)
    public Cost(int numAny, ManaType... manas) {
        numANY = numAny;
        castingCost = numAny;
        
        for (int i = 0; i < Array.getLength(manas); i++) {
            calculate(manas[i]);
        }
    }
    
    // Getters
    public int cmc() {
        return castingCost;
    }
    public int nA() { return numANY; }
    public int nC() { return numCLS; }
    public int nW() { return numWHI; }
    public int nU() { return numBLU; }
    public int nB() { return numBLA; }
    public int nR() { return numRED; }
    public int nG() { return numGRE; }
} 
