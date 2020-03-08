
import java.util.BitSet;
import java.util.Random;
import java.util.Iterator;

public class BloomFilter implements Cloneable {
    private BitSet hashes;
    private RandomRange randomRange;
    private int k; // Number of hash functions
    private static final double LN2 = 0.6931471805599453; // ln(2)

    public BloomFilter(int n, int m) {
        k = (int) Math.round(LN2 * m / n);
        if (k <= 0) k = 1;
        this.hashes = new BitSet(m);
        this.randomRange = new RandomRange(m, k);
    }

    public BloomFilter(int n) {
        this(n, 1024 * 1024 * 8);
    }

    public void add(Object o) {
        randomRange.init(o);
        for (RandomRange r : randomRange) hashes.set(r.value);
    }

    public boolean contains(Object o) {
        randomRange.init(o);
        for (RandomRange r : randomRange)
            if (!hashes.get(r.value))
                return false;
        return true;
    }


    public void clear() {
        hashes.clear();
    }


    public BloomFilter clone() throws CloneNotSupportedException {
        return (BloomFilter) super.clone();
    }

    public int hashCode() {
        return hashes.hashCode() ^ k;
    }

    public boolean equals(BloomFilter other) {
        return this.hashes.equals(other.hashes) && this.k == other.k;
    }

    private class RandomRange implements Iterable<RandomRange>, Iterator<RandomRange> {

        private Random random;
        private int max;
        private int count;
        private int i = 0;
        public int value;

        RandomRange(int maximum, int k) {
            max = maximum;
            count = k;
            random = new Random();
        }

        public void init(Object o) {
            random.setSeed(o.hashCode());
        }

        public Iterator<RandomRange> iterator() {
            i = 0;
            return this;
        }

        public RandomRange next() {
            i++;
            value = random.nextInt() % max;
            if (value < 0) value = -value;
            return this;
        }

        public boolean hasNext() {
            return i < count;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

