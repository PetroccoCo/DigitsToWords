package co.petrocco;

import javax.swing.text.MaskFormatter;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public static void main(String[] args) {
        DigitsToWords digitsToWords = new DigitsToWords(args);
        digitsToWords.parse();
    }

    public static String sanitizeInputString(String inputString) {
        return inputString.replaceAll("\\D", "");
    }

    public static List<String> digitListGeneratorIterator(final String inputDigits) {
        // After some research I was going to generate a powerset here
        // Looked up some powerset generation code and didn't want to copy and paste so here is a unique-ish solution
        if (inputDigits.length() < 2) {
            return Arrays.asList(inputDigits);
        }

        int outputSize = (int) Math.pow(2, inputDigits.length()-1);
        List<String> outputList = new LinkedList<>();
        MaskFormatter maskFormatter = new MaskFormatter();
        maskFormatter.setValueContainsLiteralCharacters(false);
        String binaryString;
        StringBuilder stringBuilder = new StringBuilder(inputDigits.length()+inputDigits.length()-1);

        for (int i = 0; i < outputSize; i++) {
            // Replaces every character with null
            // Trying to prevent repeated memory alloc and de-allocs
            stringBuilder.setLength(0);

            binaryString = Integer.toBinaryString(i);
            int lastSplit = inputDigits.length()-binaryString.length();
            stringBuilder.append(inputDigits.substring(0,lastSplit));
            for (int j = 0; j < (binaryString.length()); j++) {
                if(binaryString.charAt(j) == '1') {
                    stringBuilder.append(',');
                }
                stringBuilder.append(inputDigits.charAt(lastSplit++));
            }
            outputList.add(stringBuilder.toString());
        }

        return outputList;
    }

    /**
     * Iterative solution doesn't handle very large digit strings very well
     * We should be able to handle 30 digits without needing to start using custom data stores
     *
     * Haven't been able to play with Java 8 streams too much so thought this would be a fun time to poke at them
     */
    public static List<String> digitListGeneratorStreaming(final String inputDigits) {
        if (inputDigits.length() < 2) {
            return Arrays.asList(inputDigits);
        }

        final int outputSize = (int) Math.pow(2, inputDigits.length()-1);

        return IntStream.range(0, outputSize).mapToObj(i -> {
            String binaryString = Integer.toBinaryString(i);
            StringBuilder stringBuilder = new StringBuilder(inputDigits.length()+inputDigits.length()-1);

            int lastSplit = inputDigits.length()-binaryString.length();
            stringBuilder.append(inputDigits.substring(0,lastSplit));
            for (int j = 0; j < (binaryString.length()); j++) {
                if(binaryString.charAt(j) == '1') {
                    stringBuilder.append(',');
                }
                stringBuilder.append(inputDigits.charAt(lastSplit++));
            }

            return stringBuilder.toString();
        }).collect(Collectors.toList());

    }

    public void parse() {
        numbersList.parallelStream()
                .map( inputDigitString -> sanitizeInputString(inputDigitString) )
                .filter( sanitizedDigits -> !(sanitizedDigits.contains("00")
                        || sanitizedDigits.contains("01")
                        || sanitizedDigits.contains("10")
                        || sanitizedDigits.contains("11"))
                )
                .flatMap( sanitizedDigits -> digitListGeneratorStreaming(sanitizedDigits).stream() )
                .filter(word -> !word.isEmpty())
                .forEach(word -> System.out.println(word));
    }


}
