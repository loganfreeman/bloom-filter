import java.io.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

public class KMerCounter {
    public int k = 25;
    int elements = 1_000_000;
    int bitsize = 10_000_000;
    BloomFilter filter;
    private Integer total = 0;
    private Integer distinctCount = 0;


    public Hashtable<String, Integer> frequencyMap = new Hashtable<String, Integer>();

    public KMerCounter(int k) {
        this.k = k;
        filter = new BloomFilter(elements, bitsize);
    }

    public void countKmers(String seq) {
        int len = seq.length();
        for (int i = 0; i <= len - k; i++) {
            String kmer = seq.substring(i, i + k);
            char[] revKmerArray = new char[kmer.length()];
            boolean isValidLetter = true;
            int idx = 0;
            for (int j = 0; j < k; j++) {
                switch (kmer.charAt(j)) {
                    case 'A':
                    case 'a':
                        revKmerArray[k - 1 - j] = 'T';
                        break;
                    case 'C':
                    case 'c':
                        revKmerArray[k - 1 - j] = 'G';
                        break;
                    case 'G':
                    case 'g':
                        revKmerArray[k - 1 - j] = 'C';
                        break;
                    case 'T':
                    case 't':
                        revKmerArray[k - 1 - j] = 'A';
                        break;
                    default:
                        isValidLetter = false;
                        idx = j;
                        break;
                }
            }

            if (!isValidLetter) {
                i = i + idx + 1;
                continue;
            }

            String revKmer = new String(revKmerArray);
            if (kmer.compareTo(revKmer) > 0) {
                kmer = revKmer;
            }

            this.total++;

            if (filter.contains(kmer)) {
                if (frequencyMap.containsKey(kmer)) {
                    int value = frequencyMap.get(kmer);
                    value++;
                    frequencyMap.put(kmer, value);
                } else {
                    frequencyMap.put(kmer, 2);
                }
            } else {
                filter.add(kmer);
                this.distinctCount++;
            }


        }
    }

    public void printKmers() {
        for (String kmer : frequencyMap.keySet()) {
            System.out.println(kmer + " " + frequencyMap.get(kmer));
        }
    }

    public void readKmers(BufferedReader br, Consumer<String> consumer) throws IOException {
        String seq = "";
        String line = null;
        while ((line = br.readLine()) != null) {
            if ((line.substring(0, 1)).equals(">")) {
                //process previous sequence
                if (seq.length() > 0) {
                    consumer.accept(seq);
                }

                //initialize new sequence
                seq = "";
            } else {
                seq = seq + line;
            }
        }

        if (seq.length() > 0) {
            consumer.accept(seq);
        }
    }

    public static void main(String args[]) throws IOException {
        // initialize variables
        String seq = "";
        String filePath = "/Users/scheng/code/bloomfilter/SRR1748776.fa";
        int k = 25;

        // parse command line arguments
        if (args.length > 1 && args[0].equals("-k")) {
            k = Integer.parseInt(args[1]);
        }

        if (args.length > 3 && args[2].equals("-f")) {
            filePath = args[3];
        }

        File file = new File(filePath);

        KMerCounter kMerCounter = new KMerCounter(k);

        // read from File
        BufferedReader br = new BufferedReader(new FileReader(file));
        kMerCounter.readKmers(br, kMerCounter::countKmers);

        System.out.println("Total kmers: " + kMerCounter.getTotal());
        System.out.println("Total distinct kmers: " + kMerCounter.getDistinctCount());
        System.out.println("Highest count kmer: " + kMerCounter.getHighestCountKmer().getKey() + " " + kMerCounter.getHighestCountKmer().getValue());

        br.close();
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getHighestCount() {
        Integer highestCount = 0;
        for(Integer count : frequencyMap.values()) {
            highestCount = Math.max(highestCount, count);
        }
        return highestCount;
    }

    public Map.Entry<String, Integer> getHighestCountKmer() {
        Integer highestCount = 0;
        Map.Entry<String, Integer> highesetCountEntry = null;
        for(Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            if(entry.getValue() > highestCount) {
                highestCount = entry.getValue();
                highesetCountEntry = entry;
            }
        }
        return highesetCountEntry;
    }



    public Integer getDistinctCount() {
        return distinctCount;
    }

    public void setDistinctCount(Integer distinctCount) {
        this.distinctCount = distinctCount;
    }
}

