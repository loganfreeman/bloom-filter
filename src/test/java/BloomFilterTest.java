

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.lang.*;
import java.lang.management.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BloomFilterTest {
    int elements = 1_000_000;
    int bitsize = 10_000_000;
    BloomFilter filter;
    Random random;

    @BeforeEach
    public void BloomFilterTest() {
        random = new Random();
        random.setSeed(0);
        filter = new BloomFilter(elements, bitsize);
    }

    @Test
    public void testCorrectness() {

        filter.clear();
        Set<Integer> integerHashSet = new HashSet<>((int) (elements / 0.75));
        while (integerHashSet.size() < elements) {
            int v = random.nextInt();
            integerHashSet.add(v);
            filter.add(v);
            assertTrue(filter.contains(v), "There should be no false negative");
        }

        // testing
        int found = 0, total = 0;
        double rate = 0;
        while (total < elements) {
            int v = random.nextInt();
            if (integerHashSet.contains(v)) continue;
            total++;
            found += filter.contains(v) ? 1 : 0;

            rate = (float) found / total;
        }

        double ln2 = Math.log(2);
        double expectedRate = Math.exp(-ln2 * ln2 * bitsize / elements);
        assertTrue(rate <= expectedRate * 1.10, "error rate p = e^(-ln2^2*m/n)");
    }



}
