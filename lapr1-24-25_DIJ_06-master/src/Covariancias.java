import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class Covariancias {
    static Scanner inputReader = new Scanner(System.in);
    public static void MatrizCovariancias(String folderPath, int n_eigenfaces) throws IOException {

        File[] file = new File(folderPath).listFiles();

        int matrizLinhas = ImagemParaVetor(CSVFile.CSVFileToArray(file[0].getAbsolutePath())).length;
        int matrizColunas = file.length;

        double[][] matrizImagens = MatrizImagensDeUmFicheiro(matrizLinhas, matrizColunas, file);

        int n_imagens = matrizImagens[0].length;

        double[] vetorMedia = CalcularVetorMedia(matrizImagens, n_imagens);
        // Mostrar vetor média
        System.out.println("-Vetor Média-");
        Decomposicao.EscreverVetor(vetorMedia);
        System.out.println();


        double[][] matrizA = CalcularMatrizA(matrizImagens, vetorMedia, n_imagens);

        double[][] matrizAt = Decomposicao.CalcularTransposta(matrizA);

        double[][] matrizAtA = Decomposicao.multiplicarMatrizes(matrizAt, matrizA);
        // Mostrar matriz de covariâncias
        System.out.println("-Matriz de covariâncias (adaptada para mostrar a matriz AtA)-");
        Decomposicao.EscreverMatriz(matrizAtA, 12);
        System.out.println();

        RealMatrix realMatrixAtA = MatrixUtils.createRealMatrix(matrizAtA);
        EigenDecomposition eigenDecomposition = new EigenDecomposition(realMatrixAtA);

        //double[] valoresPropriosAtA = eigenDecomposition.getRealEigenvalues();
        double[][] vetoresPropriosAtA = new double[matrizAtA.length][matrizAtA[0].length];

        for(int i = 0; i < vetoresPropriosAtA[0].length; i++){
            double[] vetorProprio = eigenDecomposition.getEigenvector(i).toArray();
            for(int j = 0; j < vetorProprio.length; j++){
                vetoresPropriosAtA[j][i] = vetorProprio[j];
            }
        }

        double[][] tempMatrizVetorProprio;
        double[][] vetoresPropriosAAt = new double[matrizA.length][vetoresPropriosAtA[0].length];

        for(int i = 0; i < vetoresPropriosAtA[0].length; i++){
            tempMatrizVetorProprio = new double[vetoresPropriosAtA.length][1];
            for(int j = 0; j < vetoresPropriosAtA.length; j++){
                tempMatrizVetorProprio[j][0] = vetoresPropriosAtA[j][i];
            }

            double[][] tempMatrizVetorAvi = Decomposicao.multiplicarMatrizes(matrizA, tempMatrizVetorProprio);

            for(int k = 0; k < tempMatrizVetorAvi.length; k++){
                vetoresPropriosAAt[k][i] = tempMatrizVetorAvi[k][0];
            }
        }

        int numUNormal = 0;

        for(int i = 0; i < vetoresPropriosAAt[0].length; i++)
        {
            double[] arrayU = new double[vetoresPropriosAAt.length];
            for(int j = 0; j < arrayU.length; j++)
            {
                arrayU[j] = vetoresPropriosAAt[j][i];
            }

            double[] arrayUNormal = NormalizarVetor(arrayU);

            if(arrayUNormal != null && Math.round(Norma(arrayUNormal)) == 1)
            {
                numUNormal++;
            }
        }

        double[][] vetoresPropriosNormalizados = VetoresPropriosNormalizados(vetoresPropriosAAt, numUNormal);

        int nEigenFaces;
        if(n_eigenfaces == -1){
            nEigenFaces = numUNormal;
        } else {
            nEigenFaces = Math.min(n_eigenfaces, numUNormal);
        }

        // Mostrar o número de eigenfaces utilizadas
        System.out.println("Número de eigenfaces utilizadas: \n" + nEigenFaces);

        //double[][] matrizReconstruida = new double[matrizLinhas][matrizColunas];

        for(int i = 0; i < matrizColunas; i++){
            double[][] matrizImagemReconstruida = CalcularReconstrucaoEigenface(matrizA, vetoresPropriosNormalizados, vetorMedia, i, nEigenFaces);

            String nomePasta = CSVFile.CriarPasta("ImagensReconstruidas");

            String csvFilePath = nomePasta + "/imagemReconstruida" + (i + 1) + ".csv";

            CSVFile.ImagearrayToCSVFile(matrizImagemReconstruida, csvFilePath);

            double[][] arrayFormatado = Decomposicao.ConverterMatrizVetorEmMatriz(matrizImagemReconstruida);
            int[][] intArrayFormatado = Decomposicao.ConverterMatrizDoubleEmInt(arrayFormatado);

            String JPGFilePath = nomePasta + "/imagemReconstruida" + (i + 1) + ".jpg";

            ImageWriter.WriteIntArrayAsImage(intArrayFormatado, JPGFilePath);

            double[][] matrizImagemOriginal = new double[matrizImagens.length][1];
            for(int j = 0; j < matrizImagens.length; j++){
                matrizImagemOriginal[j][0] = matrizImagens[j][i];
            }
            double EAM = Decomposicao.ErroAbsolutoMedio(matrizImagemOriginal, matrizImagemReconstruida);

            System.out.println("\n\n-Erro Absoluto Médio " + (i+1) + "-");
            System.out.printf("%.2f%n%n", EAM);
        }

        for(int i = 0; i < vetoresPropriosNormalizados[0].length; i++){
            double[][] eigenVetor = new double[vetoresPropriosNormalizados.length][1];
            for(int j = 0; j < vetoresPropriosNormalizados.length; j++){
                eigenVetor[j][0] = vetoresPropriosNormalizados[j][i];
            }
            double[][] eigenVetorMatriz = Decomposicao.ConverterMatrizVetorEmMatriz(eigenVetor);

            String nomePasta = CSVFile.CriarPasta("Eigenfaces");

            String csvFilePath = nomePasta + "/eigenFace" + (i + 1) + ".csv";
            CSVFile.ArrayToCSVFile(eigenVetorMatriz, csvFilePath);

            String JPGFilePath = nomePasta + "/eigenFace" + (i + 1) + ".jpg";
            ImageWriter.WriteDoubleArrayAsImage(eigenVetorMatriz, JPGFilePath);
        }
    }

    public static double[][] MatrizImagensDeUmFicheiro(int nLinhas, int nColunas, File[] folder) throws FileNotFoundException {
        double[][] matrizImagens = new double[nLinhas][nColunas];

        for(int i = 0; i < folder.length; i++)
        {
            double[] imagemVetor = ImagemParaVetor(CSVFile.CSVFileToArray(folder[i].getAbsolutePath()));

            AdicionarVetorNaMatriz(matrizImagens, imagemVetor, i);
        }

        return matrizImagens;
    }

    public static double[][] VetoresPropriosNormalizados(double[][] array, int Unormal)
    {
        double[][] vetoresPropriosNormalizados = new double[array.length][Unormal];

        for(int i = 0; i < array[0].length; i++)
        {
            double[] arrayU = new double[array.length];
            for(int j = 0; j < arrayU.length; j++)
            {
                arrayU[j] = array[j][i];
            }

            double[] arrayUNormal = Covariancias.NormalizarVetor(arrayU);

            if(arrayUNormal != null && Math.round(Covariancias.Norma(arrayUNormal)) == 1)
            {
                Covariancias.AdicionarVetorNaMatriz(vetoresPropriosNormalizados, arrayUNormal, i);
            }
        }

        return vetoresPropriosNormalizados;
    }

    public static void AdicionarVetorNaMatriz(double[][] matriz, double[] vetor, int coluna)
    {
        for(int i = 0; i < matriz.length; i++)
        {
            matriz[i][coluna] = vetor[i];
        }
    }

    public static void AdicionarMatrizVetorNaMatriz(double[][] matriz, double[][] vetorMatriz, int coluna)
    {
        for(int i = 0; i < matriz.length; i++)
        {
            matriz[i][coluna] = vetorMatriz[i][0];
        }
    }

    public static double[] ImagemParaVetor(double[][] imagem)
    {
        double[] imagemVetor = new double[imagem.length * imagem[0].length];

        int cont = 0;

        for(int i = 0; i < imagem.length; i++)
        {
            for(int j = 0; j < imagem[0].length; j++)
            {
                imagemVetor[cont] = imagem[i][j];
                cont++;
            }
        }

        return imagemVetor;
    }

    public static double[] CalcularVetorMedia(double[][] matrizImages, int n_imagens){
        double[] vetorMediaImagens = new double[matrizImages.length];

        for(int i = 0; i < vetorMediaImagens.length; i++){
            vetorMediaImagens[i] = 0;
            for(int j = 0; j < n_imagens; j++){
                vetorMediaImagens[i] += matrizImages[i][j];
            }
            vetorMediaImagens[i] = vetorMediaImagens[i]  / n_imagens;
        }

        return vetorMediaImagens;
    }

    public static double[][] CalcularMatrizA(double[][] matrizImagens, double[] vetorMedia, int n_imagens){

        int linhas = matrizImagens.length;
        int colunas = matrizImagens[0].length;

        double[][] matrizA = new double[linhas][colunas];

        for(int i = 0; i < colunas; i++){
            for(int j = 0; j < linhas; j++){
                matrizA[j][i] = matrizImagens[j][i] - vetorMedia[j];
            }
        }

        return matrizA;
    }

    public static double[][] CalcularMatrizCovariancias(double[][] matrizA, double[][] matrizAt, int n_imagens){

        double[][] matrizAAt = Decomposicao.multiplicarMatrizes(matrizA, matrizAt);

        int linhasC = matrizAAt.length;
        int colunasC = matrizAAt[0].length;

        double[][] matrizCovariancias = new double[linhasC][colunasC];

        double constante = (double)1 / n_imagens;

        for(int i = 0; i < linhasC; i++){
            for(int j = 0; j < colunasC; j++){
                matrizCovariancias[i][j] = constante * matrizAAt[i][j];
            }
        }

        return matrizCovariancias;
    }

    public static double[] NormalizarVetor(double[] array)
    {
        double norma = Norma(array);

        if(norma != 0)
        {
            double[] arrayNormalizado = new double[array.length];

            for(int i = 0; i < array.length; i++)
            {
                arrayNormalizado[i] = array[i] / norma;
            }

            return arrayNormalizado;
        }else
        {
            return null;
        }
    }

    public static double Norma(double[] array)
    {
        double soma = 0;

        for(int i = 0; i < array.length; i++)
        {
            soma += Math.pow(array[i], 2);
        }

        return Math.sqrt(soma);
    }

    public static double[][] CalcularPesos(double[][] matrizVetorUj, double[][] matrizVetorO)
    {
        double[][] matrizVetorUjT = Decomposicao.CalcularTransposta(matrizVetorUj);
        double[][] matrizVetorWj = Decomposicao.multiplicarMatrizes(matrizVetorUjT, matrizVetorO);

        return matrizVetorWj;
    }

    public static double[][] CalcularReconstrucaoEigenface(double[][] matrizA, double[][] matrizEigenfaces, double[] vetorMedia, int index, int k){

        double[][] matrizVetorO = new double[matrizA.length][1];
        for(int a = 0; a < matrizA.length; a++){
            matrizVetorO[a][0] = matrizA[a][index];
        }

        double[][] matrizSomatorio = new double[vetorMedia.length][1];

        // Pesos W utilizados na reconstrução da imagem
        System.out.println("\n-Pesos utilizados na reconstrução da imagem " + (index+1) + "-\n");
        System.out.print("|");

        for(int j = 0; j < k; j++){
            double[][] matrizVetorUj = new double[matrizEigenfaces.length][1];
            for(int b = 0; b < matrizVetorUj.length; b++){
                matrizVetorUj[b][0] = matrizEigenfaces[b][j];
            }

            double[][] matrizVetorWj = CalcularPesos(matrizVetorUj, matrizVetorO);

            System.out.printf(" %.2f |", matrizVetorWj[0][0]);

            double[][] matrizVetorUjWj = Decomposicao.multiplicarMatrizes(matrizVetorUj, matrizVetorWj);

            for(int c = 0; c < matrizVetorUjWj.length; c++){
                matrizSomatorio[c][0] = matrizSomatorio[c][0] + matrizVetorUjWj[c][0];
            }
        }

        double[][] matrizSoma = new double[vetorMedia.length][1];
        for(int d = 0; d < vetorMedia.length; d++){
            matrizSoma[d][0] = vetorMedia[d] + matrizSomatorio[d][0];
        }

        return matrizSoma;
    }
}
