abstract class ArrayCompressor {
    public int[] getOriginalArray() {
        return originalArray;
    }

    public int getnBitsPerInt() {
        return nBitsPerInt;
    }

    public int[] getCompressedArray() {
        return compressedArray;
    }

    int[] originalArray;
    int[] compressedArray;
    int nBitsPerInt;

    ArrayCompressor(int[] originalArray) {
        this.originalArray = originalArray;
    }
}
