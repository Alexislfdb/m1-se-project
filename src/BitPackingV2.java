import java.util.BitSet;

public class BitPackingV2 extends BitPacking {
    public BitPackingV2(int[] originalArray) {
        super(originalArray);
    }

    void compress() {
        System.out.println("Compressing with BitPackingV2...");
        long startTime = System.nanoTime();

        this.sizeBeforeCompression = this.originalArray.length * 32;
        // TODO: Ne rien faire si nBitsPerInt > 16, et mettre un message d'erreur expliquant pourquoi.
        int nNumbersPer32Bits = 32 / this.nBitsPerInt;
        int lengthCompressedArray = (int) Math.ceil((double) this.numberOfElements / nNumbersPer32Bits);
        int[] compressedArray = new int[lengthCompressedArray];
        BitSet bitSetCompressedInts = new BitSet(32);
        int nNumbersCompressed = 0;
        int indexCompressedArray = 0;
        for (int i = 0; i < this.numberOfElements; i++) {
            for (int k = 0; k < this.nBitsPerInt; k++) {
                if ((this.originalArray[i] & (1 << k)) != 0) {
                    bitSetCompressedInts.set(k + (this.nBitsPerInt * nNumbersCompressed));
                }
            }
            nNumbersCompressed++;
            if (nNumbersCompressed == nNumbersPer32Bits) {
                compressedArray[indexCompressedArray] = bitSetToInt(bitSetCompressedInts);
                bitSetCompressedInts.clear();
                nNumbersCompressed = 0;
                indexCompressedArray++;
            }
        }
        if (nNumbersCompressed != 0) compressedArray[indexCompressedArray] = bitSetToInt(bitSetCompressedInts);
        this.compressedArray = compressedArray;
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
    int get(int nthNumber){ // ATTENTION : la méthode get renvoie le int numéro 1 de la suite de int, PAS le int l'index i.
        // Donc si nous voulons le premier int de la suite, celui à l'index 0, on fait get(1)
        int nNumbersPer32Bits = 32 / this.nBitsPerInt;
        int indexOfNumberInCompressedArray = (nthNumber - 1) / nNumbersPer32Bits;
        BitSet bitSetDesiredNumber = new BitSet(32);
        int firstBit = ((nthNumber - 1) % nNumbersPer32Bits) * this.nBitsPerInt;
        for (int i = 0; i < this.nBitsPerInt; i++) {
            if ((this.compressedArray[indexOfNumberInCompressedArray] & (1 << i + firstBit)) != 0) {
                bitSetDesiredNumber.set(i);
            }
        }

        return bitSetToInt(bitSetDesiredNumber);
    }

}
