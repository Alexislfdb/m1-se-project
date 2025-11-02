import java.util.Arrays;
import java.util.BitSet;

abstract class BitPacking {
    public int[] getOriginalArray() {
        return originalArray;
    }

    public int getnBitsPerInt() {
        return nBitsPerInt;
    }

    public int[] getCompressedArray() {
        return compressedArray;
    }

    public void findHighestBitsNeeded() {
        int max = Arrays.stream(getOriginalArray()).max().getAsInt();
        if (max == 0) this.nBitsPerInt = 1;
        else this.nBitsPerInt = 32 - Integer.numberOfLeadingZeros(max);
    }

    int[] originalArray;
    int[] compressedArray;
    int nBitsPerInt;
    int numberOfElements; // J'aurais aimé trouver une façon plus élégante, mais cette variable facilite vraiment la vie
    int sizeBeforeCompression; // Pour calculer quand est-ce que la compression devient bénéfique
    int sizeAfterCompression;

    public static int bitSetToInt(BitSet bitSetCompressedInts) {
        int value = 0;
        for (int i = 0; i < 32; i++) {
            if (bitSetCompressedInts.get(i)) value |= (1 << i);
        }
        return value;
    }

    BitPacking(int[] originalArray) {
        this.originalArray = originalArray;
        this.numberOfElements = originalArray.length;
        findHighestBitsNeeded();
    }

    int get(int i){
        return 0;
    }

    void decompress(int[] givenArray) {
        for (int i = 1; i < this.numberOfElements + 1; i++) {
            givenArray[i - 1] = this.get(i);
        }
    }

}
