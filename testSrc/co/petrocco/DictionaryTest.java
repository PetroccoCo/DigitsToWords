package co.petrocco;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

class DictionaryTest {

    @Test
    void dictFromFile() throws IOException {
        File inputTemp = File.createTempFile("tempdict", ".tmp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(inputTemp));
        bw.write("BEER\nBEES\n");
        bw.close();

        Dictionary dictionary = new Dictionary(inputTemp.getPath());
        assertThat(dictionary.getStringsForNumber("2337"))
                .containsExactlyInAnyOrder(new String[]{"BEER", "BEES"});
    }

    @Test
    void dictFromStringArray() {
        Dictionary dictionary = new Dictionary(new String[]{"CAT", "DOG"});
        assertThat(dictionary.getStringsForNumber("364")).containsExactly("DOG");
    }

    @Test
    void singleDigitTest() {
        Dictionary dictionary = new Dictionary(new String[]{"CAT", "DOG"});
        assertThat(dictionary.getStringsForNumber("1")).containsExactly("1");
    }

    @Test
    void dictionaryShouldNormalizeCharacters() {
        qt().forAll(strings().allPossible().ofLengthBetween(0,10)).checkAssert( inputString ->
                {
                    Dictionary dictionary = new Dictionary(new String[]{inputString});
                    for (Map.Entry<String, List<String>> entry : dictionary.digitsMap.entrySet()) {
                        assertThat(entry.getKey()).containsOnlyDigits();
                        for (String string : entry.getValue()) {
                            // For any input string we should get capital letters
                            assertThat(string).containsPattern("[A-Z]+");
                        }
                    }
                }
        );
    }
}