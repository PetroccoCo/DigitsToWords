package co.petrocco;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.generators.SourceDSL.strings;

class DigitsToWordsTest {

    private PrintStream sysOut;
    private InputStream sysIn;
    private OutputStream outContent;

    @BeforeEach
    public void setUpStreams() {
        sysOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        sysIn = System.in;
    }

    @AfterEach
    public void revertStreams() {
        System.setOut(sysOut);
        System.setIn(sysIn);
    }


    @Test
    void baseCaseTest() {
        String testInput = "2337\n732";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(testInput.getBytes());
        System.setIn(byteArrayInputStream);

        DigitsToWords digitsToWords = new DigitsToWords(new String[]{});
        digitsToWords.parse();

        assertThat(outContent.toString()).contains("BEER").contains("SEA");
    }

    @Test
    void callMeTest() {
        String testInput = "2255.63\n";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(testInput.getBytes());
        System.setIn(byteArrayInputStream);

        DigitsToWords digitsToWords = new DigitsToWords(new String[]{});
        digitsToWords.parse();

        assertThat(outContent.toString()).contains("CALL-ME");
    }

    @Test
    void dictionaryArgsTest() throws IOException {
        // Setup dictionary with word not in regular dict
        File tempdict1 = File.createTempFile("tempdict1", ".tmp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempdict1));
        bw.write("PETROCCOCO");
        bw.close();

        String testInput = "7387622626\n";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(testInput.getBytes());
        System.setIn(byteArrayInputStream);

        DigitsToWords digitsToWords1 = new DigitsToWords(new String[]{"-d", tempdict1.getAbsolutePath()});
        digitsToWords1.parse();

        assertThat(outContent.toString()).contains("PETROCCOCO").doesNotContain("SET");
    }

    @Test
    void noOutputTests() throws IOException {
        File inputTemp = File.createTempFile("inputTemp", ".tmp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(inputTemp));
        bw.write("112337\n102337\n012337\n002337\n231137\n11233700\nabcde\n");
        bw.close();

        DigitsToWords digitsToWords1 = new DigitsToWords(new String[]{inputTemp.getAbsolutePath()});
        digitsToWords1.parse();

        assertThat(outContent.toString()).isEqualTo("Options for '112337': \n" +
                "Options for '102337': \n" +
                "Options for '012337': \n" +
                "Options for '002337': \n" +
                "Options for '231137': \n" +
                "Options for '11233700': \n" +
                "Options for '': \n");
    }
    /**
     * This tests to make sure user input is valid
     * Output is expected to be only digits, or an empty string
     */
    @Test
    void sanitizeInputs() {
        qt().forAll(strings().allPossible().ofLengthBetween(0,100)).check(
                inputString -> {
                    String outputStr = DigitsToWords.sanitizeInputString(inputString);
                    return outputStr.isEmpty() || outputStr.matches("\\d+");
                }
        );
    }

    @Test
    void doubleDigitFilter() {
        assertThat(DigitsToWords.doubleDigitFilter("01234")).isFalse();
        assertThat(DigitsToWords.doubleDigitFilter("10234")).isFalse();
        assertThat(DigitsToWords.doubleDigitFilter("23114")).isFalse();
        assertThat(DigitsToWords.doubleDigitFilter("23411")).isFalse();

        assertThat(DigitsToWords.doubleDigitFilter("12034")).isTrue();
        assertThat(DigitsToWords.doubleDigitFilter("120314")).isTrue();
    }

    @Test
    void finalFilterTest() {

        assertThat(DigitsToWords.stringFilter("")).isFalse();
        assertThat(DigitsToWords.stringFilter("  ")).isFalse();

        qt().forAll(
                integers().allPositive(),
                strings().betweenCodePoints(Character.codePointAt("A", 0), Character.codePointAt("Z", 0)).ofLengthBetween(1,3))
                .checkAssert((number, word) -> {
                    assertThat(DigitsToWords.stringFilter(number.toString())).isFalse();

                    assertThat(DigitsToWords.stringFilter(number + WordCombiner.WORD_SEPARATOR + number + WordCombiner.WORD_SEPARATOR + number)).isFalse();
                    assertThat(DigitsToWords.stringFilter(number + WordCombiner.WORD_SEPARATOR + number + WordCombiner.WORD_SEPARATOR + word)).isFalse();

                    assertThat(DigitsToWords.stringFilter(word + WordCombiner.WORD_SEPARATOR + word + WordCombiner.WORD_SEPARATOR + word)).isTrue();
                    assertThat(DigitsToWords.stringFilter(number + WordCombiner.WORD_SEPARATOR + word + WordCombiner.WORD_SEPARATOR + word)).isTrue();

                    assertThat(DigitsToWords.stringFilter(word + WordCombiner.WORD_SEPARATOR + number + WordCombiner.WORD_SEPARATOR)).isFalse();
                    assertThat(DigitsToWords.stringFilter(WordCombiner.WORD_SEPARATOR + number + WordCombiner.WORD_SEPARATOR)).isFalse();
                    assertThat(DigitsToWords.stringFilter(number + WordCombiner.WORD_SEPARATOR + number)).isFalse();
                });
    }

    @Test
    void extraDigitTests() {
        String testInput = "1225563\n12255163\n122551631\n22551163\n";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(testInput.getBytes());
        System.setIn(byteArrayInputStream);

        DigitsToWords digitsToWords = new DigitsToWords(new String[]{});
        digitsToWords.parse();

        assertThat(outContent.toString())
                .contains("1-CALL-ME")
                .contains("1-CALL-1-ME")
                .contains("1-CALL-1-ME-1")
                .doesNotContain("CALL-1-1-ME");
    }
}