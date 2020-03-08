



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KMerCounterTest {

    KMerCounter kMerCounter;
    int elements = 1_000_000;
    int bitsize = 10_000_000;

    BloomFilter filter;
    Random prng;
    ThreadMXBean bean;

    @BeforeEach
    public void KmerCounterTest() {
        bean = ManagementFactory.getThreadMXBean();
        prng = new Random();
        prng.setSeed(0);
        filter = new BloomFilter(elements, bitsize);
    }

    @Test
    public void testReadKmer() throws IOException {
        String string = ">SRR1748776.1 1 length=251\n" +
                "CGGTTCXGCAGGAXTGCCGAGATCGGAAGAGCGGTTCAGCAGGAATGCCGAGACCGGATAGCGATCTCGT\n" +
                "ATGCCGTCTTCTGCTTGAAAAAAAAAGACAAGGCTCCTGAATTCGCGTCTGCATATCGGGTGACCATCCC\n" +
                "CCAAGGCCTAATCCGCCAACCXGACCGACAGCGXTCCATTACCGCGAGGGAAAGGCGCTACTACCCCCTG\n" +
                "TGAGGTCAGCGAACCAGATCCTTACACCGGATCGGTATAGC";
        kMerCounter = new KMerCounter(25);
        Consumer<String> consumer = seq -> System.out.println(seq);
        BufferedReader br = new BufferedReader(new StringReader(string));
        kMerCounter.readKmers(br, consumer);
    }

    @Test
    public void shouldFindNonATCG() {
        String seq = "CGGTTCXGCAGGAXTGCCGAGATCGGAAGAGCGGTTCAGCAGGAATGCCGAGACCGGATAGCGATCTCGTATGCCGTCTTCTGCTTGAAAAAAAAAGACAAGGCTCCTGAATTCGCGTCTGCATATCGGGTGACCATCCCCCAAGGCCTAATCCGCCAACCXGACCGACAGCGXTCCATTACCGCGAGGGAAAGGCGCTACTACCCCCTGTGAGGTCAGCGAACCAGATCCTTACACCGGATCGGTATAGC";
        int index = seq.indexOf('X');
        while (index >= 0) {
            System.out.println(index);
            index = seq.indexOf('X', index + 1);
        }
    }
    @Test
    public void testCountKmer() {
        String seq = "CGGTTCXGCAGGAXTGCCGAGATCGGAAGAGCGGTTCAGCAGGAATGCCGAGACCGGATAGCGATCTCGTATGCCGTCTTCTGCTTGCAGCAGGAATGCCGAGACCGGATAGTCGCGTCTGCATATCGGGTGACCATCCCCCAAGGCCTAATCCGCCAACCXGACCGACAGCGXTCCATTACCGCGAGGGAAAGGCGCTACTACCCCCTGTGAGGTCAGCGAACCAGATCCTTACACCGGATCGGTATAGC";
        kMerCounter = new KMerCounter(25);
        kMerCounter.countKmers(seq);
        assertTrue(kMerCounter.frequencyMap.size() > 0);
        kMerCounter.printKmers();

        filter.clear();
        for(Map.Entry<String, Integer> entry : kMerCounter.frequencyMap.entrySet()) {
            assertTrue(entry.getKey().length() == 25);
            assertTrue(entry.getKey().matches("[ATCG]{25}"));
            filter.add(entry.getKey());
            assertTrue(filter.contains(entry.getKey())); // there should not be any false negative
        }
        assertTrue(kMerCounter.getTotal() > kMerCounter.getDistinctCount());
        assertTrue(kMerCounter.getHighestCount() > 1);
        assertTrue(kMerCounter.getHighestCountKmer().getValue() == kMerCounter.getHighestCount());
    }

}
