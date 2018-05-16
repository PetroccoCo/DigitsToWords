package co.petrocco;

import javax.swing.text.MaskFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PowerCutter {
    // After some research I was going to generate a powerset here
    // Looked up some powerset generation code and didn't want to copy and paste so here is a unique-ish solution
    @Deprecated
    public static List<String> digitListGeneratorIterator(final String inputDigits) {
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
     * Output is undefined if input string contains anything other than digits
     * @param inputString A string of digits to separate using commas
     * @output A list of strings with commas injected in every possible position
     */
    public static List<String> powerCutString(final String inputString) {
        if (inputString.length() < 2) {
            return Arrays.asList(inputString);
        }

        final int outputSize = (int) Math.pow(2, inputString.length()-1);

        return IntStream.range(0, outputSize).parallel().mapToObj(i -> {
            String binaryString = Integer.toBinaryString(i);
            StringBuilder stringBuilder = new StringBuilder(inputString.length()+ inputString.length()-1);

            int lastSplit = inputString.length()-binaryString.length();
            stringBuilder.append(inputString.substring(0,lastSplit));
            for (int j = 0; j < (binaryString.length()); j++) {
                if(binaryString.charAt(j) == '1') {
                    stringBuilder.append(',');
                }
                stringBuilder.append(inputString.charAt(lastSplit++));
            }

            return stringBuilder.toString();
        }).collect(Collectors.toList());
    }

}
