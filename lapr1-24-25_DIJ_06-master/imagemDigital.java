import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class imagemDigital {
    static Scanner inputReader = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {
        // Caminho da base de dados de imagens em CSV
        System.out.print("Digite o caminho para a base de imagens (CSV): ");
        String csvDatabasePath = inputReader.nextLine();

        File databaseFile = new File(csvDatabasePath);
        if (!databaseFile.exists()) {
            System.out.println("O ficheiro indicado não existe! Finalizando o programa.");
            return;
        }

        // Carregar base de imagens
        double[][] database = CSVFile.CSVFileToArray(csvDatabasePath);

        // Lê a nova imagem
        System.out.print("Digite o caminho do ficheiro CSV contendo a imagem de entrada: ");
        String imagePath = inputReader.nextLine();
        double[][] inputImageMatrix = CSVFile.CSVFileToArray(imagePath);

        // Validar que a imagem tem dimensões compatíveis
        if (database[0].length != inputImageMatrix[0].length) {
            System.out.println("ERRO: A imagem de entrada não tem as mesmas dimensões que a base de dados.");
            return;
        }

        // Decomposição própria utilizando a classe fornecida
        System.out.println("Realizando decomposição própria na base de imagens...");
        Decomposicao.executarDecomposicaoPropria(database, database[0].length);

        // Encontrar a imagem mais próxima
        System.out.println("Projetando imagem de entrada no espaço das eigenfaces...");
        int closestImageIndex = encontrarImagemMaisProxima(inputImageMatrix[0], database);

        System.out.println("A imagem mais próxima está no índice: " + closestImageIndex);
    }

    /**
     * Calcula a imagem mais próxima no espaço projetado.
     */
    public static int encontrarImagemMaisProxima(double[] imagemEntrada, double[][] database) {
        int closestIndex = -1;
        double menorDistancia = Double.MAX_VALUE;

        for (int i = 0; i < database.length; i++) {
            double distancia = calcularDistancia(imagemEntrada, database[i]);

            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    /**
     * Calcula a distância euclidiana entre dois vetores.
     */
    public static double calcularDistancia(double[] vetor1, double[] vetor2) {
        double soma = 0.0;
        for (int i = 0; i < vetor1.length; i++) {
            soma += Math.pow(vetor1[i] - vetor2[i], 2);
        }
        return Math.sqrt(soma);
    }
}
