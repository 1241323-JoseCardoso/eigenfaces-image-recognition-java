import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageWriter {
    public static void WriteIntArrayAsImage(int[][] array, String outputFilePath) throws IOException {
        int height = array.length;
        int width = array[0].length;

        // Create a BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        int min = EncontrarMinimoInt(array);
        int max = EncontarMaximoInt(array);

        // Set the pixel intensities
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int intensity = (int)(255 * ((double)(array[y][x] - min) / (max - min)));

                if (intensity < 0 || intensity > 255) {
                    throw new IllegalArgumentException("Pixel intensity must be between 0 and 255.");
                }

                int rgb = (intensity << 16) | (intensity << 8) | intensity; // Set the same value for R, G, B

                image.setRGB(x, y, rgb);

            }
        }

        // Write the image to the file
        File outputFile = new File(outputFilePath);
        ImageIO.write(image, "jpg", outputFile);
    }

    public static void WriteDoubleArrayAsImage(double[][] array, String outputFilePath) throws IOException {
        int height = array.length;
        int width = array[0].length;

        // Create a BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        double min = EncontrarMinimoDouble(array);
        double max = EncontarMaximoDouble(array);

        // Set the pixel intensities
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int intensity = (int)(255 * ((double)(array[y][x] - min) / (max - min)));

                if (intensity < 0 || intensity > 255) {
                    throw new IllegalArgumentException("Pixel intensity must be between 0 and 255.");
                }

                int rgb = (intensity << 16) | (intensity << 8) | intensity; // Set the same value for R, G, B

                image.setRGB(x, y, rgb);

            }
        }

        // Write the image to the file
        File outputFile = new File(outputFilePath);
        ImageIO.write(image, "jpg", outputFile);
    }

    public static int EncontarMaximoInt(int[][] vetor){
        int max = vetor[0][0];
        for(int i = 0; i < vetor.length; i++){
            for(int j = 0; j < vetor[0].length; j++){
                if(vetor[i][j] > max){
                    max = vetor[i][j];
                }
            }
        }

        return max;
    }

    public static int EncontrarMinimoInt(int[][] vetor){
        int min = vetor[0][0];
        for(int i = 0; i < vetor.length; i++){
            for(int j = 0; j < vetor[0].length; j++){
                if(vetor[i][j] < min){
                    min = vetor[i][j];
                }
            }
        }

        return min;
    }

    public static double EncontarMaximoDouble(double[][] vetor){
        double max = vetor[0][0];
        for(int i = 0; i < vetor.length; i++){
            for(int j = 0; j < vetor[0].length; j++){
                if(vetor[i][j] > max){
                    max = vetor[i][j];
                }
            }
        }

        return max;
    }

    public static double EncontrarMinimoDouble(double[][] vetor){
        double min = vetor[0][0];
        for(int i = 0; i < vetor.length; i++){
            for(int j = 0; j < vetor[0].length; j++){
                if(vetor[i][j] < min){
                    min = vetor[i][j];
                }
            }
        }

        return min;
    }
}
