import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try {

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choisissez le fichier contenant votre suite d'integer"); // Sélection du fichier contenant l'array d'int à compresser.
            int result = chooser.showOpenDialog(null);
            String filePath = chooser.getSelectedFile().getAbsolutePath();
            File fileToOpen = new File(filePath);

            Scanner scannerFile = new Scanner(fileToOpen);

            List<Integer> tempList = new ArrayList<>(); // On récupère l'array en la mettant d'abord dans une ArrayList...
            while(scannerFile.hasNextInt()){
                tempList.add(scannerFile.nextInt());
            }

            scannerFile.close();

            int[] originalArray = new int[tempList.size()]; // ... avant de la mettre dans une array simple
            for (int i = 0; i < tempList.size(); i++) {
                originalArray[i] = tempList.get(i);
            }
            // ************************************ RENTREZ VOTRE CODE EN-DESSOUS ************************************ //

            /*BitPackingV1 bitPackingV1 = new BitPackingV1(originalArray);
            bitPackingV1.compress();
            System.out.println(bitPackingV1.get(4));
            int[] decompressedArray1 = new int[originalArray.length];
            bitPackingV1.decompress(decompressedArray1);
            System.out.println(Arrays.toString(decompressedArray1));*/

            /*BitPackingV2 bitPackingV2 = new BitPackingV2(originalArray);
            bitPackingV2.compress();
            System.out.println(bitPackingV2.get(4));
            int[] decompressedArray2 = new int[originalArray.length];
            bitPackingV2.decompress(decompressedArray2);
            System.out.println(Arrays.toString(decompressedArray2));*/

            /*BitPackingV3 bitPackingV3 = new BitPackingV3(originalArray);
            bitPackingV3.compress();
            System.out.println(bitPackingV3.get(4));
            int[] decompressedArray3 = new int[originalArray.length];
            bitPackingV3.decompress(decompressedArray3);
            System.out.println(Arrays.toString(decompressedArray3));*/


            // ************************************ RENTREZ VOTRE CODE AU-DESSUS ************************************ //

        } catch (FileNotFoundException e){
            System.out.println("Error with reading the file : " + e.getMessage());
        }
    }
}