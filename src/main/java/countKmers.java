import java.io.*;
import java.util.Hashtable;

public class countKmers 
{
	public static int k=18;									// scalar
	public static Hashtable<String, Integer> count = new Hashtable<String, Integer>();	// hash

	public static void countSeqKmers(String seq)
	{                               
		if(seq.length()>0)
                {
			int len=seq.length();
                        for(int i=0;i<=len-k;i++)
                        {                                 
				String kmer=seq.substring(i,i+k);
                                char[] revKmerArray = new char[kmer.length()];
                                for (int j=0 ;j<k;j++)
                                { 
					switch(kmer.charAt(j)) 
					{
						case 'A': 
						case 'a': revKmerArray[k-1-j] = 'T'; break;	
						case 'C': 
                                                case 'c': revKmerArray[k-1-j] = 'G'; break;
						case 'G': 
                                                case 'g': revKmerArray[k-1-j] = 'C'; break;
						case 'T': 
                                                case 't': revKmerArray[k-1-j] = 'A'; break;
						default:  break;
					}
				}

	                        String revKmer=new String(revKmerArray);
				if(kmer.compareTo(revKmer)>0) {  kmer=revKmer; }

				if(count.containsKey(kmer))
				{
				    int value=count.get(kmer);
				    value++;
				    count.put(kmer,value);
				}
				else
				{
				    count.put(kmer,1);
				}
			}
                }
	}

        public static void printKmers()
        {
	    for (String kmer : count.keySet()) {
		System.out.println(kmer + " " + count.get(kmer));
	    }
	}

	public static void main(String args[]) throws IOException
	{
		// initialize variables
		String seq="";				// scalar

		// parse command line arguments
		if (args.length > 1 && args[0].equals("-k"))
		{ 
			k=Integer.parseInt(args[1]);
		}

		// read STDIN
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		while ((line = br.readLine()) != null) 
		{
			if((line.substring(0,1)).equals(">")) 
			{
				//process previous sequence
				countSeqKmers(seq);

				//initialize new sequence
				seq="";
			}
			else
			{
				seq=seq+line;
			}
		}

		// process last sequence                                
		countSeqKmers(seq);

		// prink kmer hash
		printKmers();

		br.close();
	}
}

