import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Decomposicao {
    public static void executarDecomposicaoPropria(double[][] matriz, int nValores) throws IOException {
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(matriz); //matriz A convertida em um objeto RealMatrix

        if (!realMatrix.equals(realMatrix.transpose())) { //verifica se A é simetrica
            System.out.println("ERRO - A matriz fornecida não é simétrica!");
            return;
        }

        EigenDecomposition eigenDecomposition = new EigenDecomposition(realMatrix);

        double[] valoresProprios = eigenDecomposition.getRealEigenvalues(); //D - valores próprios
        int k = Math.min(nValores, valoresProprios.length);

        // Mostrar número de valores próprios
        System.out.println("\nNúmero de valores próprios utilizados: " + k);

        // Mostrar valores próprios
        System.out.println("\nValores próprios utilizados: ");
        for(int i = 0; i < k; i++)
        {
            System.out.printf(" %5.2f\n", valoresProprios[i]);
        }

        // Calcular matriz diagonal com base em k
        double[][] Dk = new double[k][k];

        for(int i = 0; i < Dk.length; i++)
        {
            for(int j = 0; j < Dk[i].length; j++)
            {
                if(i == j)
                    Dk[i][j] = valoresProprios[i];
                else
                    Dk[i][j] = 0;
            }
        }

        // Calcular matriz de vetores com base em k
        double[][] Pk = new double[matriz.length][k];

        for(int i = 0; i < Pk[0].length; i++)
        {
            double[] vetorProprio = eigenDecomposition.getEigenvector(i).toArray();
            for(int j = 0; j < Pk.length; j++)
            {
                Pk[j][i] = vetorProprio[j];
            }
        }

        // Mostrar vetores próprios
        System.out.println("\nVetores próprios utilizados:");
        for(int i = 0; i < Pk[0].length; i++)
        {
            System.out.print("[");
            for(int j = 0; j < Pk.length; j++){
                System.out.printf(" %5.2f", Pk[j][i]);
            }
            System.out.println(" ]");
        }

        // Transposta da matriz de vetores
        double[][] PkTransporta = CalcularTransposta(Pk);

        // Matriz reconstruida
        double[][] Ak = multiplicarMatrizes(multiplicarMatrizes(Pk, Dk), PkTransporta);

        System.out.println("\nMatriz reconstruída");
        EscreverMatriz(Ak, 8);

        // Calcular Erro Absoluto Médio
        double EAM = ErroAbsolutoMedio(matriz, Ak);

        System.out.printf("%nO erro absoluto médio é %.2f", EAM);

        String nomePasta = CSVFile.CriarPasta("Output");

        // Escrever matriz reconstruida num ficheiro CSV
        CSVFile.ArrayToCSVFile(Ak, nomePasta + "/matrizReconstruida.csv");
    }

    public static double[][] multiplicarMatrizes(double[][] a, double[][] b)
    {
        double[][] matrizProduto = new double[0][0];

        if(a[0].length == b.length)
        {
            matrizProduto = new double[a.length][b[0].length];

            for(int i = 0; i < matrizProduto.length; i++)
            {
                for(int j = 0; j < matrizProduto[i].length; j++)
                {
                    for(int k = 0; k < b.length; k++)
                    {
                        matrizProduto[i][j] += a[i][k] * b[k][j];
                    }
                }
            }
        }else
        {
            System.out.println("Não é possível calcular produto");
        }

        return matrizProduto;
    }

    public static double[][] CalcularTransposta(double[][] array)
    {
        double[][] arrayTransposta = new double[array[0].length][array.length];

        for(int i = 0; i < arrayTransposta.length; i++)
        {
            for(int j = 0; j < arrayTransposta[i].length; j++)
            {
                arrayTransposta[i][j] = array[j][i];
            }
        }

        return arrayTransposta;
    }

    public static double ErroAbsolutoMedio(double[][] A, double[][] Ak)
    {
        if(A.length == Ak.length && A[0].length == Ak[0].length)
        {
            int M = A.length;
            int N = A[0].length;

            double soma = 0;

            for(int i = 0; i < M; i++)
            {
                for(int j = 0; j < N; j++)
                {
                    soma += Math.abs(A[i][j] - Ak[i][j]);
                }
            }

            return soma / (M * N);
        }

        return -1;
    }

    public static void EscreverMatriz(double[][] array, int espaco)
    {
        for(int i = 0; i < array.length; i++)
        {
            for(int j = 0; j < array[i].length; j++)
            {
                System.out.printf("%" + espaco + ".2f", array[i][j]);
            }
            System.out.println();
        }

        System.out.println();
    }

    public static void EscreverVetor(double[] array)
    {
        for(int i = 0; i < array.length; i++)
        {
            System.out.printf("%8.2f ", array[i]);
        }

        System.out.println();
    }

    public static int[][] ConverterMatrizDoubleEmInt(double[][] doubleArray)
    {
        int[][] intArray = new int[doubleArray.length][doubleArray[0].length];

        for(int i = 0; i < doubleArray.length; i++)
        {
            for(int j = 0; j < doubleArray[0].length; j++)
            {
                intArray[i][j] = (int)doubleArray[i][j];
            }
        }

        return intArray;
    }

    public static double[][] ConverterMatrizVetorEmMatriz(double[][] array)
    {
        int linhas = (int)Math.sqrt(array.length);
        int colunas = linhas;

        double[][] matrizNova = new double[linhas][colunas];
        int cont = 0;

        for(int i = 0; i < matrizNova.length; i++)
        {
            for(int j = 0; j < matrizNova[i].length; j++)
            {
                matrizNova[i][j] = array[cont][0];
                cont++;
            }
        }

        return matrizNova;
    }

    public static double[] ConverterMatrizEmVetor(double[][] array)
    {
        double[] vetor = new double[array.length * array[0].length];
        int cont = 0;

        for(int i = 0; i < array.length; i++)
        {
            for(int j = 0; j < array[i].length; j++)
            {
                vetor[cont] = array[i][j];
                cont++;
            }
        }

        return vetor;
    }
}

