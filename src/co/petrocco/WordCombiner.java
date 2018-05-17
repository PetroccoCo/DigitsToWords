package co.petrocco;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class WordCombiner {
    public static final String WORD_SEPARATOR = "-";

    final Dictionary dictionary;

    public WordCombiner(Dictionary dictionary) {
        this.dictionary = dictionary;
    }
    /*
     Take a Lists a,b; o,n; y,z
     produce output a-o-y, a-o-z, a-n-y, a-n-z, b-o-y, b-o-z, b-n-y, b-n-z
      */
    static List<String> cartesianProduct(List<List<String>> listsToMerge) {
        if (listsToMerge.isEmpty()) {
            return Collections.emptyList();
        }
        if (listsToMerge.size() == 1) {
            return listsToMerge.get(0);
        }

        List<String> retList = listsToMerge.get(0);
        for (int i = 1; i < listsToMerge.size(); i++) {
            retList = listProduct(retList, listsToMerge.get(i));
        }
        return retList;
    }

    static List<String> listProduct(List<String> leftList, List<String> rightList) {
        return Optional.of(leftList.stream().flatMap(leftString -> {
            return Optional.of(
                    rightList.stream()
                            .map(rightString -> {
                                return leftString+WORD_SEPARATOR+rightString;
                            })
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList()).stream();
        }).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    public List<?> product(List<?>... a) {
        if (a.length >= 2) {
            List<?> product = a[0];
            for (int i = 1; i < a.length; i++) {
                product = product(product, a[i]);
            }
            return product;
        }

        return emptyList();
    }

}
