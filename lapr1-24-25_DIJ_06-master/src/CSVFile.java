import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class CSVFile {
    public static double[][] CSVFileToArray(String file) throws FileNotFoundException {
        int linhas = 0;
        int colunas = 0;

        Scanner sc = new Scanner(new File(file));
        sc.useDelimiter(",");

        while (sc.hasNextLine())
        {
            String nextLine = sc.nextLine();

            if(!nextLine.trim().isEmpty())
            {
                linhas++;
                String[] line = nextLine.split(",");

                if(linhas == 1)
                    colunas = line.length;
            }
        }

        double[][] matriz = new double[linhas][colunas];

        sc = new Scanner(new File(file));
        sc.useDelimiter(",");

        int i = 0;
        while (sc.hasNextLine())
        {
            String nextLine = sc.nextLine();

            if(!nextLine.trim().isEmpty()) {
                String[] line = nextLine.split(",");
                for(int j = 0; j < colunas; j++)
                {
                    matriz[i][j] = Integer.parseInt(line[j]);
                }
                i++;
            }
        }

        return matriz;
    }

    public static void ArrayToCSVFile(double[][] array, String csvFilePath) throws FileNotFoundException {

        String[] csvArray = new String[array.length];

        for(int i = 0; i < array.length; i++){
            String csvLine = String.valueOf(array[i][0]);
            for(int j = 1; j < array[0].length; j++){
                csvLine = csvLine + "," + array[i][j];
            }
            csvArray[i] = csvLine;
        }

        File csvFile = new File(csvFilePath);

        PrintWriter printWriter = new PrintWriter(csvFile);

        printWriter.print(csvArray[0]);
        for(int i = 1; i < csvArray.length; i++){
            printWriter.println();
            printWriter.print(csvArray[i]);
        }

        printWriter.close();
    }

    public static String CriarPasta(String nomePasta) throws IOException {
        String nomePastaTotal = "lapr1-DIJ-06/" + nomePasta;

        Files.createDirectories(Paths.get(nomePastaTotal));

        return nomePastaTotal;
    }

    public static void ImagearrayToCSVFile(double[][] array, String csvFilePath) throws FileNotFoundException {

        double[][] arrayFormatado = Decomposicao.ConverterMatrizVetorEmMatriz(array);

        String[] csvArray = new String[arrayFormatado.length];

        for(int i = 0; i < arrayFormatado.length; i++){
            String csvLine = "";
            for(int j = 0; j < arrayFormatado[i].length; j++){
                csvLine = csvLine + String.valueOf(Math.round(array[(i * arrayFormatado[i].length) + j][0])) + ",";
            }
            csvArray[i] = csvLine;
        }

        File csvFile = new File(csvFilePath);

        PrintWriter printWriter = new PrintWriter(csvFile);

        printWriter.print(csvArray[0]);
        for(int i = 1; i < csvArray.length; i++){
            printWriter.println();
            printWriter.print(csvArray[i]);
        }

        printWriter.close();
    }
}
