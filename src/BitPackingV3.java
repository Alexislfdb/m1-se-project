import java.util.Arrays;
import java.util.BitSet;

public class BitPackingV3 extends BitPacking {
    int nBitsPerIntsInNormalArea;
    int nBitsPerIntsInOverflowArea;

    public BitPackingV3(int[] originalArray) {
        super(originalArray);
    }

    void compress() {
        System.out.println("Compressing with BitPackingV3...");
        long startTime = System.nanoTime();

        int intThresholdOverflow; // On calcule un seuil. Si un int demande plus de int que se seuil, il ira dans dans la zone d'overflow
                                  // Pour déterminer ce seuil, on va utiliser la médiane de l'array.
                                  // En fonction des cas, peut-être qu'utiliser la moyenne est mieux, et résultera en une meilleurs compression
        int[] tempResult = findMedianAndLengthsAndNBits(this.originalArray);
        int elementsInNormalArea = tempResult[0];
        int elementsInOverflowArea = tempResult[1];

        int nBitsNormalArea = tempResult[2] + 1;// Les int en dessus du seuil utiliseront ce nombre de bits
                                                 // +1 pour distinguer entre valeur et position
        this.nBitsPerIntsInNormalArea = nBitsNormalArea;
        int nBitsOverflowArea = tempResult[3]; // Les int égal ou au-dessus du seuil utiliseront ce nombre de bits

        this.nBitsPerIntsInOverflowArea = nBitsOverflowArea;

        intThresholdOverflow = (int) Math.pow(2, nBitsNormalArea - 1);
        int totalBitsNeededNormalArea = (elementsInNormalArea + elementsInOverflowArea) * nBitsNormalArea; // + elementsInOverflowArea pour ajouter les adresses des valeurs dans l'overflow area
        int totalBitsNeededOverflowArea = elementsInOverflowArea * nBitsOverflowArea;

        int lengthCompressedNormalArea = (int) Math.ceil(totalBitsNeededNormalArea / 32.0f);
        int lengthCompressedOverflowArea =  (int) Math.ceil(totalBitsNeededOverflowArea / 32.0f);

        int[] compressedArrayNormalArea = new int[lengthCompressedNormalArea];
        int[] compressedArrayOverflowArea = new int[lengthCompressedOverflowArea];
        int[] compressedArray = new int[lengthCompressedNormalArea + lengthCompressedOverflowArea];

        BitSet bitSetCompressedIntsOfNormalArea = new BitSet(32);
        BitSet bitSetCompressedIntsOfOverflowArea = new BitSet(32);

        int indexNormalArea = 0;
        int indexOverflowArea = 0;
        int positionInOverflowArea = 0;

        this.sizeBeforeCompression = this.originalArray.length * 32;
        for(int i = 0; i < originalArray.length; i++){
            if (originalArray[i] < intThresholdOverflow){
                for (int j = 0; j < nBitsNormalArea - 1; j++){
                    if (indexNormalArea % 32 == 0) {
                        if (indexNormalArea != 0) {
                            compressedArrayNormalArea[(indexNormalArea / 32) - 1] = bitSetToInt(bitSetCompressedIntsOfNormalArea);
                        }
                        bitSetCompressedIntsOfNormalArea.clear();
                    }

                    if ((this.originalArray[i] & (1 << j)) != 0) {
                        bitSetCompressedIntsOfNormalArea.set(indexNormalArea % 32);
                    }
                    indexNormalArea++;
                }
                if (indexNormalArea % 32 == 0) {
                    if (indexNormalArea != 0) {
                        compressedArrayNormalArea[(indexNormalArea / 32) - 1] = bitSetToInt(bitSetCompressedIntsOfNormalArea);
                    }
                    bitSetCompressedIntsOfNormalArea.clear();
                }
                indexNormalArea++; // On laisse un bit à 0 pour indiquer que ceci est une valeur et non une position.
            }
            else { // On commence par ajoter l'adresse du int volumineux dans notre array
                for (int j = 0; j < nBitsNormalArea - 1; j++){
                    if (indexNormalArea % 32 == 0) {
                        if (indexNormalArea != 0) {
                            compressedArrayNormalArea[(indexNormalArea / 32) - 1] = bitSetToInt(bitSetCompressedIntsOfNormalArea);
                        }
                        bitSetCompressedIntsOfNormalArea.clear();
                    }
                    if ((positionInOverflowArea & (1 << j)) != 0) {
                        bitSetCompressedIntsOfNormalArea.set(indexNormalArea % 32);
                    }
                    indexNormalArea++;
                }
                positionInOverflowArea++;
                if (indexNormalArea % 32 == 0) {
                    if (indexNormalArea != 0) {
                        compressedArrayNormalArea[(indexNormalArea / 32) - 1] = bitSetToInt(bitSetCompressedIntsOfNormalArea);
                    }
                    bitSetCompressedIntsOfNormalArea.clear();
                }
                bitSetCompressedIntsOfNormalArea.set(indexNormalArea % 32); // On met ce bit à 1 pour montrer que ceci est une adresse
                indexNormalArea++;

                for(int j = 0; j < nBitsOverflowArea; j++){ // Ensuite on ajoute l'int volumineux dans notre zone d'overflow
                    if (indexOverflowArea % 32 == 0) {
                        if (indexOverflowArea != 0) {
                            compressedArrayOverflowArea[(indexOverflowArea / 32) - 1] = bitSetToInt(bitSetCompressedIntsOfOverflowArea);
                        }
                        bitSetCompressedIntsOfOverflowArea.clear();
                    }

                    if ((this.originalArray[i] & (1 << j)) != 0) {
                        bitSetCompressedIntsOfOverflowArea.set(indexOverflowArea % 32);
                    }
                    indexOverflowArea++;

                }
            }
        }
        if (indexNormalArea % 32 == 0) compressedArrayNormalArea[(indexNormalArea / 32) - 1] = bitSetToInt(bitSetCompressedIntsOfNormalArea);
        else compressedArrayNormalArea[(indexNormalArea / 32)] = bitSetToInt(bitSetCompressedIntsOfNormalArea);

        if (indexOverflowArea % 32 == 0) {
            if (indexOverflowArea == 0) {
                indexOverflowArea = 32;
            }
            compressedArrayOverflowArea[(indexOverflowArea / 32) - 1] = bitSetToInt(bitSetCompressedIntsOfOverflowArea);
        }
        else compressedArrayOverflowArea[(indexOverflowArea / 32)] = bitSetToInt(bitSetCompressedIntsOfOverflowArea);
        System.arraycopy(compressedArrayNormalArea, 0, compressedArray, 0, compressedArrayNormalArea.length);
        System.arraycopy(compressedArrayOverflowArea, 0, compressedArray, compressedArrayNormalArea.length, compressedArrayOverflowArea.length);

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
        System.out.printf("Time to send with no compression : %.2f ms\n", timeNoCompression);
        double timeWithCompression = totalTime + ((double) this.sizeAfterCompression / bandwidth);
        System.out.printf("Time to send with compression : %.2f ms\n", timeWithCompression);

        double whenIsCompressionUseful = (totalTime * bandwidth) / (sizeBeforeCompression * (1 - compressionRatio));
        System.out.printf("Conclusion : Compression is useful if the latency is above %.2f, otherwise its faster to send with no compression.\n", whenIsCompressionUseful);
    }

    @Override
    int get(int nthNumber) { // ATTENTION : la méthod get renvoie le int numéro i de la suite de int, PAS le int l'index i.
        // Donc si nous voulons le premier int de la suite, celui à l'index 0, on fait get(1)
        int firstBit = (nthNumber - 1) * this.nBitsPerIntsInNormalArea;
        int indexNumberToUnpack = firstBit / 32;
        BitSet bitSetDesiredNumber = new BitSet(32);
        int indexBitSet = 0;
        for (int i = 0; i < this.nBitsPerIntsInNormalArea; i++) {
            if (i + (firstBit % 32) >= 32) {
                if ((this.compressedArray[indexNumberToUnpack + 1] & (1 << indexBitSet)) != 0) {
                    bitSetDesiredNumber.set(i);
                }
                indexBitSet++;
            } else if ((this.compressedArray[indexNumberToUnpack] & (1 << i + (firstBit % 32))) != 0) {
                bitSetDesiredNumber.set(i);
            }
        }
        int possibleResult = bitSetToInt(bitSetDesiredNumber);
        if ((possibleResult & (1 << nBitsPerIntsInNormalArea - 1)) == 0) {
            return possibleResult;
        }
        else {
            bitSetDesiredNumber.clear(nBitsPerIntsInNormalArea - 1);
            possibleResult = bitSetToInt(bitSetDesiredNumber);
            int indexFirstCellInOverflowArea = (int) Math.ceil((this.nBitsPerIntsInNormalArea * this.numberOfElements) / 32.0f);
            bitSetDesiredNumber.clear();
            firstBit = (indexFirstCellInOverflowArea * 32) + possibleResult * this.nBitsPerIntsInOverflowArea;
            indexNumberToUnpack = firstBit / 32;
            indexBitSet = 0;
            for (int i = 0; i < this.nBitsPerIntsInOverflowArea; i++) {
                if (i + (firstBit % 32) >= 32) {
                    if ((this.compressedArray[indexNumberToUnpack + 1] & (1 << indexBitSet)) != 0) {
                        bitSetDesiredNumber.set(i);
                    }
                    indexBitSet++;
                } else if ((this.compressedArray[indexNumberToUnpack] & (1 << i + (firstBit % 32))) != 0) {
                    bitSetDesiredNumber.set(i);
                }
            }
        }
        return bitSetToInt(bitSetDesiredNumber);
    }

    public static int[] findMedianAndLengthsAndNBits(int[] array) {
        int[] result = new int[4];

        int[] tempArray = array.clone();
        Arrays.sort(tempArray);

        int median;

        if (tempArray.length % 2 == 0) median = (tempArray[tempArray.length / 2 - 1] + tempArray[tempArray.length / 2]) / 2;
        else median = tempArray[tempArray.length / 2];

        result[2] = 32 - Integer.numberOfLeadingZeros(median);

        int indexSeparation = 0;
        while (indexSeparation < tempArray.length && tempArray[indexSeparation] < median) {
            indexSeparation++;
        }

        result[0] = indexSeparation;
        result[1] = tempArray.length - indexSeparation;

        result[3] = 32 - Integer.numberOfLeadingZeros(tempArray[tempArray.length - 1]);
        return result;
    }
}
