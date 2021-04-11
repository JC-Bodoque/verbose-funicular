package mx.edu.tecnm.itcm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * This class implements the alphabet. Internally, for automata, grammars
 * or transducers, the alphabet is always a set of consecutive integers
 * beginning with <code>0</code>.
 *
 * @author Henry McElwaine
 */
public class Alphabet {
    static final short ENGLISH = 26;
    static final short SPANISH = 27;
    public char minLetter = 'a';
    public char maxLetter;
    public int size;
    public short[] charToShort = new short[256];
    public char[] shortToChar;

    /**
     * The Spanish alphabet.
     */
    public static Alphabet spanish() {
        char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        return new Alphabet(chars);
    }

    /**
     * The english alphabet.
     */
    public static Alphabet english() {
        char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        return new Alphabet(chars);
    }

    /**
     * Creates an alphabet of size <code>n</code> beginning
     * at character <code>letter</code>
     */
    public Alphabet(char letter, int n) {
        minLetter = letter;
        maxLetter = (char) (letter + n - 1);
        size = n;
        shortToChar = new char[size];
        for (short i = 0; i < size; i++) {
            char c = (char) (minLetter + i);
            charToShort[c] = i;
            shortToChar[i] = c;
        }
    }

    /**
     * Creates an alphabet from a boolean array <code>isLetter</code>
     */
    public Alphabet(boolean[] isLetter) {
        int count = 0;
        for (int i = 0; i < isLetter.length; i++)
            if (isLetter[i])
                count++;
        char[] chars = new char[count];
        int current = 0;
        for (char i = 0; i < isLetter.length; i++)
            if (isLetter[i])
                chars[current++] = i;
        size = chars.length;
        shortToChar = new char[size];
        System.arraycopy(chars, 0, shortToChar, 0, size);
        Arrays.sort(shortToChar);
        for (short i = 0; i < shortToChar.length; i++)
            charToShort[shortToChar[i]] = i;
    }

    /**
     * Creates an alphabet of size <code>n</code> beginning at character
     * <code>a</code>.
     */
    public Alphabet(int n) {
        this('a', n);
    }

    /**
     * Creates an alphabet consisting in a set  of characters.
     */
    public Alphabet(Set charSet) {
        size = charSet.size();
        shortToChar = new char[size];
        short n = 0;
        for (Iterator i = charSet.iterator(); i.hasNext(); n++) {
            char c = ((Character) i.next()).charValue();
            charToShort[c] = n;
            shortToChar[n] = c;
        }
    }

    /**
     * Creates an alphabet consisting in a set given in an array of characters.
     */
    public Alphabet(char[] chars) {
        size = chars.length;
        shortToChar = new char[size];
        System.arraycopy(chars, 0, shortToChar, 0, size);
        Arrays.sort(shortToChar);
        for (short i = 0; i < shortToChar.length; i++)
            charToShort[shortToChar[i]] = i;
    }

    /**
     * Returns the alphabet composed of all the characters appearing
     * in the file <code>name</code>.
     */
    public static Alphabet fromFile(String name) throws Exception {
        FileReader fileIn = new FileReader(name);
        BufferedReader r = new BufferedReader(fileIn);
        String line;
        boolean[] isLetter = new boolean[256];
        while ((line = r.readLine()) != null) {
            addToCharSet(line, isLetter);
        }
        fileIn.close();
        return new Alphabet(isLetter);
    }

    /**
     * Returns the alphabet composed of the letters appearing in the expression
     * <code>exp</code> (used to create an alphabet from a regular expression).
     */
    public static Alphabet fromExpression(String exp) {
        boolean[] isLetter = new boolean[256];
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (Character.isLetter(c))
                isLetter[c] = true;
        }
        return new Alphabet(isLetter);
    }

    public static void addToCharSet(String line, boolean[] isLetter) {
        for (int i = 0; i < line.length(); i++)
            isLetter[line.charAt(i)] = true;
    }

    public int size() {
        return size;
    }

    /**
     * Translates the character <code>c</code> to a short integer
     * using the array <code>charToShort</code>.
     */
    public short toShort(char c) {
        return charToShort[c];
    }

    /**
     * Translates the String <code>w</code> to Short using the method
     * toShort().
     */
    public short[] toShort(String w) {
        int n = w.length();
        short[] s = new short[n];
        for (int i = 0; i < n; i++)
            s[i] = toShort(w.charAt(i));
        return s;
    }

    /**
     * Converts the integer <code>i</code> to a character
     * using the array <code>shortToChar</code>.
     */
    public char toChar(int i) {
        return shortToChar[i];
    }

    /**
     * Converts to characters the elements of the set of <code>Short</code>
     * <code>s</code>.
     */
    public Set toChar(Set s) {
        Set ss = new HashSet();
        for (Iterator i = s.iterator(); i.hasNext(); ) {
            short c = ((Short) i.next()).shortValue();
            ss.add(new Character(toChar(c)));
        }
        return ss;
    }

    /**
     * Returns true if the character <code>c</code> is in the alphabet.
     */
    public boolean isIn(char c) {
        return toChar(toShort(c)) == c;
    }

    /**
     * Converts the short integer <code>i</code>
     * to its value in the alphabet <code>a</code>.
     */
    public short convert(Alphabet a, short i) {
        return a.toShort(toChar(i));
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < size; i++) s += toChar(i);
        return s;
    }

    public static void main(String[] args) {
        Set C = new TreeSet();
        char[] data = new char[]{'a', '(', ')', '+', '-', '/', '*', ','};
        for (int i = 0; i < data.length; i++)
            C.add(new Character(data[i]));
        Alphabet a = new Alphabet(C);
        System.out.println(C.toString());
        System.out.println(a.toString());
        Alphabet b = new Alphabet(data);
        System.out.println(b.toString());
        Alphabet c = new Alphabet(4);
        System.out.println(c.toString());
        Alphabet d = new Alphabet('0', 7);
        System.out.println(d.toString());
        Alphabet e = Alphabet.english();
        System.out.println(e.toString());
        Alphabet f = Alphabet.spanish();
        System.out.println(f.toString());
    }
}

