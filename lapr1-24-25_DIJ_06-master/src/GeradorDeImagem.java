import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GeradorDeImagem {

    public static void GerarImagem(File folder, int nEigenfaces) throws IOException {
        File[] imagens = new File(folder.getAbsolutePath()).listFiles();

        int matrizLinhas = Covariancias.ImagemParaVetor(CSVFile.CSVFileToArray(imagens[0].getAbsolutePath())).length;
        int matrizColunas = imagens.length;

        double[][] matrizImagens = Covariancias.MatrizImagensDeUmFicheiro(matrizLinhas, matrizColunas, imagens);

        int n_imagens = matrizImagens[0].length;

        double[] vetorMedia = Covariancias.CalcularVetorMedia(matrizImagens, n_imagens);

        double[][] matrizA = Covariancias.CalcularMatrizA(matrizImagens, vetorMedia, n_imagens);

        double[][] matrizAt = Decomposicao.CalcularTransposta(matrizA);

        double[][] matrizAtA = Decomposicao.multiplicarMatrizes(matrizAt, matrizA);

        RealMatrix realMatrixAtA = MatrixUtils.createRealMatrix(matrizAtA);
        EigenDecomposition eigenDecomposition = new EigenDecomposition(realMatrixAtA);

        double[] valoresPropriosAtA = eigenDecomposition.getRealEigenvalues();
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

            double[] arrayUNormal = Covariancias.NormalizarVetor(arrayU);

            if(arrayUNormal != null && Math.round(Covariancias.Norma(arrayUNormal)) == 1)
            {
                numUNormal++;
            }
        }

        double[][] vetoresPropriosNormalizados = Covariancias.VetoresPropriosNormalizados(vetoresPropriosAAt, numUNormal);

        if(nEigenfaces == -1)
        {
            nEigenfaces = vetoresPropriosNormalizados[0].length;
        }else
        {
            nEigenfaces = Math.min(nEigenfaces, vetoresPropriosNormalizados[0].length);
        }

        GerarImagemAleatoria(vetorMedia, valoresPropriosAtA, vetoresPropriosAAt, nEigenfaces);
    }

    public static void GerarImagemAleatoria(double[] vetorMedia, double[] valoresProprios, double[][] vetoresProprios, int nEigenfaces) throws IOException {
        double[] vetorSomatorio = SomatorioImagemAleatoria(valoresProprios, vetoresProprios, nEigenfaces);

        double[] imagemAleatoriaVetor = SomarVetores(vetorMedia, vetorSomatorio);

        double[][] imagemAleatoriaMatriz = new double[imagemAleatoriaVetor.length][1];

        for(int i = 0; i < imagemAleatoriaMatriz.length; i++)
        {
            imagemAleatoriaMatriz[i][0] = imagemAleatoriaVetor[i];
        }

        double[][] imagemAleatoriaDouble = Decomposicao.ConverterMatrizVetorEmMatriz(imagemAleatoriaMatriz);
        int[][] imagemAleatoriaInt = Decomposicao.ConverterMatrizDoubleEmInt(imagemAleatoriaDouble);

        String nomePasta = CSVFile.CriarPasta("Output");

        String JPGFilePath = nomePasta + "/ImagemAleatória.jpg";
        ImageWriter.WriteIntArrayAsImage(imagemAleatoriaInt, JPGFilePath);

        System.out.println("Uma imagem aleatória foi gerada!");
    }

    public static double[] SomatorioImagemAleatoria(double[] valoresProprios, double[][] vetoresProprios, int k)
    {
        double[] vetorSoma = new double[vetoresProprios.length];

        for(int i = 0; i < k; i++)
        {
            double[] vetorProprio = new double[vetoresProprios.length];

            for(int j = 0; j < vetorProprio.length; j++) {
                vetorProprio[j] = vetoresProprios[j][i];
            }

            double max = Math.sqrt(Math.abs(valoresProprios[i]));
            double min = -Math.sqrt(Math.abs(valoresProprios[i]));

            double pesoAleatorio = (Math.random() * (max - min)) + min;

            double[] vetorMultiplicado = MultiplicarVetorPorConstante(vetorProprio, pesoAleatorio);
            double[] vetorAtualSoma = vetorSoma;

            vetorSoma = SomarVetores(vetorAtualSoma, vetorMultiplicado);
        }

        return vetorSoma;
    }

    public static double[] MultiplicarVetorPorConstante(double[] array, double constante)
    {
        double[] arrayMultiplicacao = new double[array.length];

        for(int i = 0; i < arrayMultiplicacao.length; i++)
        {
            arrayMultiplicacao[i] = array[i] * constante;
        }

        return arrayMultiplicacao;
    }

    public static double[] SomarVetores(double[] array1, double[] array2)
    {
        if(array1.length == array2.length)
        {
            double[] arraySoma = new double[array1.length];
            for(int i = 0; i < array1.length; i++)
            {
                arraySoma[i] = array1[i] + array2[i];
            }

            return arraySoma;
        }else
            return array1;
    }
}
