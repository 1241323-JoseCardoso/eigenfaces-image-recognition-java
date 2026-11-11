import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IdentificacaoEigenfaces {
    public static void IdentificarImagem(File image, File folder, int nEigenfaces) throws IOException {

        File[] imagens = new File(folder.getAbsolutePath()).listFiles();
        double[][] imagemNovaArray = CSVFile.CSVFileToArray(image.getAbsolutePath());

        int matrizLinhas = Covariancias.ImagemParaVetor(CSVFile.CSVFileToArray(imagens[0].getAbsolutePath())).length;
        int matrizColunas = imagens.length;

        double[][] matrizImagens = Covariancias.MatrizImagensDeUmFicheiro(matrizLinhas, matrizColunas, imagens);

        int n_imagens = matrizImagens[0].length;

        double[] vetorMedia = Covariancias.CalcularVetorMedia(matrizImagens, n_imagens);

        double[] imagemNovaVetor = Decomposicao.ConverterMatrizEmVetor(imagemNovaArray);

        double[] omegaNova = CalcularVetorOmega(n_imagens, imagemNovaVetor, vetorMedia, matrizImagens, nEigenfaces);

        System.out.println("Vetor omega");
        Decomposicao.EscreverVetor(omegaNova);
        System.out.println();

        double[][] matrizOmegas = new double[omegaNova.length][imagens.length];

        System.out.println("Número de pesos (eigenfaces): " + matrizOmegas.length);
        for(int i = 0; i < imagens.length; i++)
        {
            double[][] imagem = CSVFile.CSVFileToArray(imagens[i].getAbsolutePath());

            double[] imagemVetor = Decomposicao.ConverterMatrizEmVetor(imagem);

            double[] omegaImagem = CalcularVetorOmega(n_imagens, imagemVetor, vetorMedia, matrizImagens, nEigenfaces);
            Covariancias.AdicionarVetorNaMatriz(matrizOmegas, omegaImagem, i);
        }

        double[] distanciasEuclidianas = new double[matrizOmegas[0].length];

        for(int i = 0; i < matrizOmegas[0].length; i++)
        {
            double[] omegaImagem = new double[matrizOmegas.length];
            for(int j = 0; j < omegaImagem.length; j++)
            {
                omegaImagem[j] = matrizOmegas[j][i];
            }

            double distance = 0;
            for(int j = 0; j < omegaNova.length; j++)
            {
                distance += Math.pow(omegaImagem[j] - omegaNova[j], 2);
            }

            distance = Math.sqrt(distance);
            distanciasEuclidianas[i] = distance;

            System.out.println("Imagem" + (i+1) + ": " + imagens[i].getName());
            System.out.printf("A distância euclidiana é %.2f%n", distanciasEuclidianas[i]);
            System.out.println();

            System.out.println("Vetor omega da imagem " + (i+1));
            Decomposicao.EscreverVetor(omegaImagem);
            System.out.println();
        }

        double menorDistancia = distanciasEuclidianas[0];
        int imagemMenorDistancia = 0;

        for(int i = 0; i < distanciasEuclidianas.length; i++)
        {
            if(distanciasEuclidianas[i] < menorDistancia)
            {
                menorDistancia = distanciasEuclidianas[i];
                imagemMenorDistancia = i;
            }
        }

        System.out.println("A imagem mais próxima da imagem " + image.getName() + " é " + imagens[imagemMenorDistancia].getName());
        System.out.printf("A distância euclidiana é %.2f", menorDistancia);

        double[][] imagemMaisProxima = CSVFile.CSVFileToArray(imagens[imagemMenorDistancia].getAbsolutePath());

        int[][] imagemMaisProximaInt = Decomposicao.ConverterMatrizDoubleEmInt(imagemMaisProxima);

        String nomePasta = CSVFile.CriarPasta("Identificacao");

        String JPGFilePath = nomePasta + "/ImagemMaisPróxima.jpg";
        ImageWriter.WriteIntArrayAsImage(imagemMaisProximaInt, JPGFilePath);
    }

    public static double[] CalcularVetorOmega(int n_imagens, double[] imageVetor, double[] vetorMedia, double[][] matrizImagens, int nEigenfaces)
    {
        double[] vetorPhi = SubtrairVetores(imageVetor, vetorMedia);
        double[][] matrizVetorPhi = new double[vetorPhi.length][1];

        for(int i = 0; i < matrizVetorPhi.length; i++)
        {
            matrizVetorPhi[i][0] = vetorPhi[i];
        }

        double[][] matrizA = Covariancias.CalcularMatrizA(matrizImagens, vetorMedia, n_imagens);

        double[][] matrizAt = Decomposicao.CalcularTransposta(matrizA);

        double[][] matrizAtA = Decomposicao.multiplicarMatrizes(matrizAt, matrizA);

        RealMatrix realMatrixAtA = MatrixUtils.createRealMatrix(matrizAtA);
        EigenDecomposition eigenDecomposition = new EigenDecomposition(realMatrixAtA);

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

        double[] omega = new double[nEigenfaces];
        for(int i = 0; i < nEigenfaces; i++)
        {
            double[][] matrizVetorU = new double[vetoresPropriosNormalizados.length][1];
            for(int b = 0; b < matrizVetorU.length; b++){
                matrizVetorU[b][0] = vetoresPropriosNormalizados[b][i];
            }

            double[][] matrizVetorW = Covariancias.CalcularPesos(matrizVetorU, matrizVetorPhi);

            omega[i] = matrizVetorW[0][0];
        }

        return omega;
    }

    public static double[] SubtrairVetores(double[] array1, double[] array2)
    {
        if(array1.length == array2.length)
        {
            double[] subArray = new double[array1.length];

            for(int i = 0; i < array1.length; i++)
            {
                subArray[i] = array1[i] - array2[i];
            }

            return subArray;
        }

        return null;
    }
}
