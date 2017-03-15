import java.io.*;
import java.lang.*;
import java.util.*;

public class Solution {

    public static int hash_djb2(String[] w) {

        HashSet<Integer> h = new HashSet<Integer>();
        int count = 0;

        for (String s : w) {
            int hash = 5381;
            for (int i = 0; i < s.length(); i++) {
                hash = ((hash << 5 ) + hash) + (int) s.charAt(i);
            }

            if (h.add(hash) == false) count++;
        }

        return count;

    }

    public static int hash_sdbm(String[] w) {

        HashSet<Integer> h = new HashSet<Integer>();
        int count = 0;

        for (String s : w) {
          int hash = 0;

          for (int i = 0; i < s.length(); i++) {
              hash = (int) s.charAt(i) + (hash << 6) + (hash << 16) - hash;
          }

          if (h.add(hash) == false) count++;
        }

        return count;
    }

    public static int hash_loselose(String[] w) {

        HashSet<Integer> h = new HashSet<Integer>();
        int count = 0;

        for (String s : w) {
          int hash = 0;

          for (int i = 0; i < s.length(); i++) {
              hash += (int) s.charAt(i);
          }

          if (h.add(hash) == false) count++;
        }

        return count;
    }

    public static int hash_murmurhash3(String[] w) {

        HashSet<Integer> h = new HashSet<Integer>();
        int count = 0;

        for (String s : w) {

          int hash = murmurhash3_x86_32(s, 0, s.length(), 0);

          if (h.add(hash) == false) count++;
        }

        return count;
    }

    public static int murmurhash3_x86_32(CharSequence data, int offset, int len, int seed) {

      //function developed from : https://github.com/yonik/java_util/blob/master/src/util/hash/MurmurHash3.java

      final int c1 = 0xcc9e2d51;
      final int c2 = 0x1b873593;

      int h1 = seed;

      int pos = offset;
      int end = offset + len;
      int k1 = 0;
      int k2 = 0;
      int shift = 0;
      int bits = 0;
      int nBytes = 0;   // length in UTF8 bytes


      while (pos < end) {
        int code = data.charAt(pos++);
        if (code < 0x80) {
          k2 = code;
          bits = 8;

          /***
          // optimized ascii implementation (currently slower!!! code size?)
          if (shift == 24) {
            k1 = k1 | (code << 24);
            k1 *= c1;
            k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
            k1 *= c2;
            h1 ^= k1;
            h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
            h1 = h1*5+0xe6546b64;
            shift = 0;
            nBytes += 4;
            k1 = 0;
          } else {
            k1 |= code << shift;
            shift += 8;
          }
          continue;
         ***/

        }
        else if (code < 0x800) {
          k2 = (0xC0 | (code >> 6))
                  | ((0x80 | (code & 0x3F)) << 8);
          bits = 16;
        }
        else if (code < 0xD800 || code > 0xDFFF || pos>=end) {
          // we check for pos>=end to encode an unpaired surrogate as 3 bytes.
          k2 = (0xE0 | (code >> 12))
                  | ((0x80 | ((code >> 6) & 0x3F)) << 8)
                  | ((0x80 | (code & 0x3F)) << 16);
          bits = 24;
        } else {
          // surrogate pair
          // int utf32 = pos < end ? (int) data.charAt(pos++) : 0;
          int utf32 = (int) data.charAt(pos++);
          utf32 = ((code - 0xD7C0) << 10) + (utf32 & 0x3FF);
          k2 = (0xff & (0xF0 | (utf32 >> 18)))
               | ((0x80 | ((utf32 >> 12) & 0x3F))) << 8
               | ((0x80 | ((utf32 >> 6) & 0x3F))) << 16
               |  (0x80 | (utf32 & 0x3F)) << 24;
          bits = 32;
        }


        k1 |= k2 << shift;

        // int used_bits = 32 - shift;  // how many bits of k2 were used in k1.
        // int unused_bits = bits - used_bits; //  (bits-(32-shift)) == bits+shift-32  == bits-newshift

        shift += bits;
        if (shift >= 32) {
          // mix after we have a complete word

          k1 *= c1;
          k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
          k1 *= c2;

          h1 ^= k1;
          h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
          h1 = h1*5+0xe6546b64;

          shift -= 32;
          // unfortunately, java won't let you shift 32 bits off, so we need to check for 0
          if (shift != 0) {
            k1 = k2 >>> (bits-shift);   // bits used == bits - newshift
          } else {
            k1 = 0;
          }
          nBytes += 4;
        }

      } // inner

      // handle tail
      if (shift > 0) {
        nBytes += shift >> 3;
        k1 *= c1;
        k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
        k1 *= c2;
        h1 ^= k1;
      }

      // finalization
      h1 ^= nBytes;

      // fmix(h1);
      h1 ^= h1 >>> 16;
      h1 *= 0x85ebca6b;
      h1 ^= h1 >>> 13;
      h1 *= 0xc2b2ae35;
      h1 ^= h1 >>> 16;

      return h1;
    }



    public static void main(String[] args) {

        String[] functionName = {"djb2", "sdbm", "lose lose", "murmur hash 3"};

        Scanner fileScanner;
        LinkedList<String> w = new LinkedList<String>();

    		try {
    			fileScanner = new Scanner (new File ("words.txt"));
          while (fileScanner.hasNext()) {
      			w.add(fileScanner.next().toLowerCase());
      		}
          fileScanner.close();
    		}
    		catch (IOException e) {
    			System.err.println("Unable to open dictionary file. "+e.getMessage());
    			System.err.println("Currently in directory "+ System.getProperty("user.dir"));
    			System.exit(0);
    		}

    		String [] words = w.toArray(new String[0]);

        int start,end;
        if (args.length == 0) {
          start = 0;
          end = 3;
        } else {
           start = Integer.parseInt(args[0]);
           end = start;
        }

        for (int choice = start; choice <= end; choice++) {

          int collisions;
          final long startTime = System.currentTimeMillis();

          switch (choice) {

              case 0 : collisions = hash_djb2(words);
                       break;
              case 1 : collisions = hash_sdbm(words);
                       break;
              case 2 : collisions = hash_loselose(words);
                       break;
              default : collisions = hash_murmurhash3(words);
                        break;
          }

          final long endTime = System.currentTimeMillis();
          System.out.println("Hash Function:\t\t" + functionName[choice] + "\nTotal execution time:\t" + (endTime - startTime) + "ms\nNumber of Collisions:\t" + collisions + "\n\n");

        }

    }

}
