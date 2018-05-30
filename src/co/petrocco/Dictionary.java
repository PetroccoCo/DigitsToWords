package co.petrocco;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Dictionary {
    final HashMap<String, List<String>> digitsMap = new HashMap<>();

    public Dictionary(String fileName) {
        generateDictionary(fileName);
    }

    Dictionary(String[] words) {
        for (String word : words) {
            addStringToDict(word);
        }
    }

    public List<String> getStringsForNumber(String number) {
        return digitsMap.getOrDefault(number, number.length() == 1 ? Arrays.asList(number) : Collections.emptyList());
    }

    private void generateDictionary(String fileName) {
        String line = null;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                addStringToDict(line);
                //System.out.println(line + "|" + keyStringBuilder.toString());
            }

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find file: '"+fileName+"'");
        } catch (IOException ex) {
            System.out.println("IOException reading file: '"+fileName+"'");
        }
    }

    private void addStringToDict(String line) {
        final String parsedLine = line.toUpperCase().replaceAll("[^A-Z]", "");
        if (parsedLine.isEmpty())
            return;
        StringBuilder keyStringBuilder = new StringBuilder(parsedLine.length());
        for (int i = 0; i < parsedLine.length(); i++) {
            keyStringBuilder.append(digitToCharacter(parsedLine.charAt(i)));
        }
        final String keyString = keyStringBuilder.toString();
        List<String> stringsForNumber = digitsMap.getOrDefault(keyString, new ArrayList<String>());
        stringsForNumber.add(parsedLine);
        digitsMap.put(keyString, stringsForNumber);
    }

    private String digitToCharacter(final char input) {
        final char upperCaseInput = Character.toUpperCase(input);
        switch (upperCaseInput) {
            case 'A':
            case 'B':
            case 'C':
                return "2";
            case 'D':
            case 'E':
            case 'F':
                return "3";
            case 'G':
            case 'H':
            case 'I':
                return "4";
            case 'J':
            case 'K':
            case 'L':
                return "5";
            case 'M':
            case 'N':
            case 'O':
                return "6";
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
                return "7";
            case 'T':
            case 'U':
            case 'V':
                return "8";
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                return "9";
            default:
                // Undefined chars we skip
                return "";
        }
    }
}
