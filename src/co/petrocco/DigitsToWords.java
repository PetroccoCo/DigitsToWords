package co.petrocco;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DigitsToWords {
    private static final String UNIX_DICTIONARY = "/usr/share/dict/words";
    private String dictionaryPath;
    private List<String> numbersList = new ArrayList<>();
    private Dictionary dictionary;

    public DigitsToWords(String[] args) {
        // Parse args, set dictionary and list of input strings
        readArgs(args);
    }

    public static void main(String[] args) {
        DigitsToWords digitsToWords = new DigitsToWords(args);
        digitsToWords.parse();
    }

    static boolean doubleDigitFilter(String sanitizedDigits) {
        return !(sanitizedDigits.contains("00")
                || sanitizedDigits.contains("01")
                || sanitizedDigits.contains("10")
                || sanitizedDigits.contains("11"));
    }

    static String sanitizeInputString(String inputString) {
        return inputString.replaceAll("\\D", "");
    }

    private void readArgs(String[] args) {
        // Since you could pass in multiple -d params, we will just iterate and take the last value
        String tempDictionaryPath = null;
        // Go for a set to toss duplicate paths
        Set<String> filesToParse = new HashSet<>();

        if (args.length == 0) {
            // TODO go straight to user input parsing
        } else {

            for (int i = 0; i < args.length; i++) {
                // Get the dictionary path by grabbing the next element after "-d"
                if (args[i].equalsIgnoreCase("-d")) {
                    if (i + 1 < args.length) {
                        tempDictionaryPath = args[++i];
                    }
                } else {
                    filesToParse.add(args[i]);
                }
            }
        }
        dictionaryPath = tempDictionaryPath;

        if (filesToParse.isEmpty()) {
            String line = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            try {
                while ((line = in.readLine()) != null) {
                    if (line.isEmpty() || line.matches("^\\s+$")) {
                        break;
                    }
                    numbersList.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }  else {
            numbersList.addAll(getStringsFromFiles(filesToParse));
        }

        if (dictionaryPath == null || dictionaryPath.isEmpty()) {
            dictionaryPath = UNIX_DICTIONARY;
        }
        dictionary = new Dictionary(dictionaryPath);

    }

    private List<String> getStringsFromFiles(Set<String> filesToParse) {
        return filesToParse.stream().map(File::new).filter(inputFile -> {
            // TODO in hypothetical verbose mode add logging of unreadable paths
            return inputFile.exists() && inputFile.canRead();
        }).flatMap(inputFile -> {
            List<String> outputStrings = new ArrayList<>();
            try {
                FileReader fileReader = new FileReader(inputFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    outputStrings.add(line);
                }
            } catch (FileNotFoundException ex) {
                System.err.println("Could not find file: '"+inputFile.getPath()+"'");
            } catch (IOException ex) {
                System.err.println("Could not read file: '"+inputFile.getPath()+"'");
            }
            return outputStrings.stream();
        }).collect(Collectors.toList());
    }

    public void parse() {
        numbersList.parallelStream()
                .map(DigitsToWords::sanitizeInputString)
                .filter(DigitsToWords::doubleDigitFilter)
                .flatMap( sanitizedDigits -> PowerCutter.powerCutString(sanitizedDigits).stream() )
                .filter(word -> !word.isEmpty())
                .forEach(word -> System.out.println(word));
    }


}
