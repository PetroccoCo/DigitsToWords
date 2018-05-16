package co.petrocco;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

class PowerCutterTest {

    /**
     * Generate a list of all of our possible combinations of the digits
     * Eg the input 123 can become [[123], [1, 23], [12, 3], [1, 2, 3]]
     */
    @Test
    void combinationGenerator() {
        qt().forAll(strings().betweenCodePoints(0x0030, 0x0039).ofLength(20)).checkAssert(
                inputString -> {
                    List<String> listOfCombinations = PowerCutter.digitListGeneratorIterator(inputString);
                    if (inputString.length() == 0) {
                        assertThat(listOfCombinations.size()).isEqualTo(1);
                    } else {
                        assertThat((double) listOfCombinations.size()).isEqualTo(Math.pow(2, inputString.length() - 1));
                    }
                    //assertThat(listOfCombinations).contains(inputString.split(""));
                    assertThat(listOfCombinations).contains(inputString);
                }
        );
    }

    @Test
    void combinationStreamGenerator() {
        qt().forAll(strings().betweenCodePoints(0x0030, 0x0039).ofLengthBetween(0, 10)).checkAssert(
                inputString -> {
                    List<String> listOfCombinations = PowerCutter.powerCutString(inputString);
                    if (inputString.length() == 0) {
                        assertThat(listOfCombinations.size()).isEqualTo(1);
                    } else {
                        assertThat((double) listOfCombinations.size()).isEqualTo(Math.pow(2, inputString.length() - 1));
                    }
                    assertThat(listOfCombinations).contains(inputString);
                }
        );
    }


    /**
     * My laptop handles 24 digits great, 30 digits should be the theoretical max
     */
    @Test
    void bigComboGenerator() {
        String inputString = "233723372337233723372337";
        List<String> listOfCombinations = PowerCutter.powerCutString(inputString);
        assertThat((double) listOfCombinations.size()).isEqualTo(Math.pow(2, inputString.length() - 1));
        assertThat(listOfCombinations).contains(inputString);
    }

}