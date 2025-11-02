import java.util.BitSet;

public class BitPackingV1 extends BitPacking {
    public BitPackingV1(int[] originalArray) {
        super(originalArray);
    }

    void compress() {
        System.out.println("Compressing with BitPackingV1...");
        long startTime = System.nanoTime();

        int totalBitsNeeded = (this.originalArray.length * this.nBitsPerInt);
        int lengthCompressedArray = (int) Math.ceil(totalBitsNeeded / 32.0f);
        int[] compressedArray = new int[lengthCompressedArray];
        this.sizeBeforeCompression = this.originalArray.length * 32;

        int index = 0;
        BitSet bitSetCompressedInts = new BitSet(32);
        while (index != totalBitsNeeded) { // On place 1 à 1 les bits dans bitSetCompressedInts
            for (int j = 0; j < this.nBitsPerInt; j++) {
                // Une fois qu'on a placé 32 bits dans bitSetCompressedInts, on le converti en Int avant de le placer dans compressedArray
                // Ensuite on réinitialise bitSetCompressedInts
                if (index % 32 == 0) {
                    if (index != 0){
                        compressedArray[(index / 32) - 1] = bitSetToInt(bitSetCompressedInts);
                    }
                    bitSetCompressedInts.clear();
                }

                if ((this.originalArray[index / this.nBitsPerInt] & (1 << j)) != 0) {
                    bitSetCompressedInts.set(index % 32);
                }
                index++;
            }
        }
        if (index % 32 == 0) compressedArray[(index / 32) - 1] = bitSetToInt(bitSetCompressedInts);
        else compressedArray[(index / 32)] = bitSetToInt(bitSetCompressedInts);
        this.compressedArray = compressedArray;
        this.sizeAfterCompression = this.compressedArray.length * 32;

        float compressionRatio = (float) this.sizeAfterCompression / this.sizeBeforeCompression;

        long endTime = System.nanoTime();
        double totalTime = (endTime - startTime) / 1_000_000.0;

        System.out.printf("Compression Time is approximately %.2f ms.\n", totalTime);
        System.out.printf("Compressed Array is approximately %.2f%% of its original size.\n", compressionRatio * 100);
        System.out.println("We'll assume that the bandwidth is 100 Megabits per second. (seems to be average in France)");
        int bandwidth = 100_000;

        double timeNoCompression = (double) this.sizeBeforeCompression / bandwidth;
        System.out.printf("Time to send with no compression : %.2f ms\n",  timeNoCompression);
        double timeWithCompression = totalTime + ((double) this.sizeAfterCompression / bandwidth);
        System.out.printf("Time to send with compression : %.2f ms\n",  timeWithCompression);;

        double whenIsCompressionUseful = (totalTime * bandwidth) / (sizeBeforeCompression * (1 - compressionRatio));
        System.out.printf("Conclusion : Compression is useful if the latency is above %.2f, otherwise its faster to send with no compression.\n", whenIsCompressionUseful);
    }

    @Override
    int get(int nthNumber){ // ATTENTION : la méthod get renvoie le int numéro i de la suite de int, PAS le int l'index i.
                                // Donc si nous voulons le premier int de la suite, celui à l'index 0, on fait get(1)
        int firstBit = (nthNumber - 1) * this.nBitsPerInt;
        int indexNumberToUnpack = firstBit / 32;
        BitSet bitSetDesiredNumber = new BitSet(32);
        int indexBitSet = 0;
        for (int i = 0; i < this.nBitsPerInt; i++) {
            if (i + (firstBit % 32) >= 32){
                if ((this.compressedArray[indexNumberToUnpack + 1] & (1 << indexBitSet)) != 0) {
                    bitSetDesiredNumber.set(i);
                }
                indexBitSet++;
            }
            else if ((this.compressedArray[indexNumberToUnpack] & (1 << i + (firstBit % 32))) != 0) {
                bitSetDesiredNumber.set(i);
            }
        }
        return bitSetToInt(bitSetDesiredNumber);
    }
}
