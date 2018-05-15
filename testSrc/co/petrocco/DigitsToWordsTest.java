package co.petrocco;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(outContent.toString()).isEmpty();
    }
}