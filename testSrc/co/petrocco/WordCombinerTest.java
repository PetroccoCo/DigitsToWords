package co.petrocco;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.integers;

class WordCombinerTest {

    List<String> animals = Arrays.asList("CAT", "DOG", "FISH");
    List<String> colors = Arrays.asList("RED", "BLUE", "GREEN");
    List<String> sizes = Arrays.asList("BIG", "MEDIUM", "SMALL");

    @Test
    void listProduct() {
        qt().forAll(integers().between(0,2)).checkAssert( index -> {
            assertThat(WordCombiner.listProduct(animals, colors)).contains(animals.get(index)+WordCombiner.WORD_SEPARATOR+colors.get(index));
        });
    }

    @Test
    void emptyProduct() {
        assertThat(WordCombiner.listProduct(animals, Collections.emptyList())).isEmpty();
        assertThat(WordCombiner.listProduct(Collections.emptyList(), animals)).isEmpty();
        assertThat(WordCombiner.listProduct(Collections.emptyList(), Collections.emptyList())).isEmpty();
    }

    @Test
    void cartesianProduct() {
        qt().forAll(integers().between(0,2)).checkAssert( index -> {
            assertThat(WordCombiner.cartesianProduct(Arrays.asList(animals, sizes, colors))
                    .contains(animals.get(index) + WordCombiner.WORD_SEPARATOR + sizes.get(index) + WordCombiner.WORD_SEPARATOR + colors.get(index)));
        });
    }

    @Test
    void emptyCartesianProduct() {
        assertThat(WordCombiner.cartesianProduct(Arrays.asList(animals, sizes, Collections.emptyList()))).isEmpty();
        assertThat(WordCombiner.cartesianProduct(Arrays.asList(animals, Collections.emptyList(), sizes))).isEmpty();
        assertThat(WordCombiner.cartesianProduct(Arrays.asList(Collections.emptyList(), animals, sizes))).isEmpty();
        assertThat(WordCombiner.cartesianProduct(Arrays.asList(Collections.emptyList()))).isEmpty();
    }
}