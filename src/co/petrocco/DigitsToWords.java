package co.petrocco;

import java.io.*;
import java.util.*;
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
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String input = in.readLine();
                    if (input == null || input.isEmpty() || input.matches("^\\s+$")) {
                        break;
                    }
                    numbersList.add(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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

    public static void main(String[] args) {
        DigitsToWords digitsToWords = new DigitsToWords(args);
        digitsToWords.parse();
    }

    public void parse() {
    }


}
