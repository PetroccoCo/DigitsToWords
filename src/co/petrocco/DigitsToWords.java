package co.petrocco;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
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
        numbersList.stream()
                // Take the input and remove all non-digits
                .map(DigitsToWords::sanitizeInputString)
                // Output a separator between inputs
                .peek( sanitizedInputString -> System.out.println("Options for '"+sanitizedInputString+"': "))
                // Remove any double digit strings 01, 00, etc
                .filter(DigitsToWords::doubleDigitFilter)
                // Convert the input into stream of comma separated strings 123 -> 1,23; 12,3; 1,2,3; 123
                .flatMap( (String sanitizedDigits) -> PowerCutter.powerCutString(sanitizedDigits).stream() )
                // Make a list from the comma separated splits
                .map((String csd) -> Arrays.asList(csd.split(",")))
                // Covert the list of digits to a list of words per digit
                .map((List<String> digitList) -> digitList.stream().map(s -> dictionary.getStringsForNumber(s)).collect(Collectors.toList()))
                // Make every possible combination of words out of the result
                .map( (List<List<String>> wordsList) -> WordCombiner.cartesianProduct(wordsList))
                // Change out stream of Lists into a stream of strings
                .flatMap( (List<String> allCombos) -> allCombos.stream())
                // Filter output making sure we don't have double digit output
                .filter(DigitsToWords::stringFilter)
                // Output each string to the console
                .forEach((String word) -> System.out.println(word));
    }

    static boolean stringFilter(String potentialOutputString) {
        // Prevent one or more digits from printing
        if (Pattern.matches("\\d+", potentialOutputString))
            return false;

        // if two digits are next to each other skip it
        if (Pattern.matches(".*\\d+\\-\\d+.*", potentialOutputString))
            return false;

        // make sure there is something to output
        if (potentialOutputString.isEmpty() || potentialOutputString.trim().isEmpty())
            return false;

        // make sure we don't have any empty combos
        if (potentialOutputString.trim().equals("-") || potentialOutputString.contains("--"))
            return false;

        // leading and trailing hyphens are bad
        if (potentialOutputString.startsWith("-") || potentialOutputString.endsWith("-"))
            return false;

        return true;
    }

}
