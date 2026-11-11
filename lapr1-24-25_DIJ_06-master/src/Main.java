import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.File;

public class Main {
    static Scanner inputReader = new Scanner(System.in);
    public static void main(String[] args) throws IOException {

        boolean modoInterativo = args.length == 0;

        int option;

        Files.createDirectories(Paths.get("lapr1-DIJ-06"));

        if(modoInterativo)
        {
            do{
                option = -1;

                System.out.println("\n-BEM VINDO-");

                MostrarMenu();

                while (option < 1 || option > 5){
                    option = LerOpcao();
                }

                EscolherOpcao(option, args);
            }while (option >= 1 && option <= 4);
        }else
        {
            option = Integer.parseInt(args[1]);

            EscolherOpcao(option, args);
        }
    }

    public static void DecomposicaoPropria(String[] args) throws IOException {

        final int tamanhoMaximo = 256;

        double[][] matriz = {};
        String csvMatrixPath;
        File file;

        int n_eigenfaces;

        boolean erro;

        if(args.length == 0)
        {
            do {
                System.out.print("\nDigite a localização do ficheiro CSV que contém a matriz de entrada: ");
                csvMatrixPath = inputReader.nextLine();

                file = new File(csvMatrixPath);

                if (!file.exists()) {
                    System.out.println("\nO ficheiro indicado não existe, tente novamente!\n");
                    erro = true;
                }else
                {
                    matriz = CSVFile.CSVFileToArray(csvMatrixPath);

                    erro = VertificarErroNaMatriz(matriz, tamanhoMaximo);
                }
            }while(erro);

            int maxN_values = matriz.length;

            System.out.println("Digite o número de valores próprios que deseja utilizar na decomposição própria da matriz");
            System.out.println("O número deve estar contido no intervalo de 1 a " + maxN_values + ". Caso seja introduzido -1, será utilizado o valor máximo.");

            do{
                System.out.print("Digite o valor: ");
                n_eigenfaces = inputReader.nextInt();
                inputReader.nextLine();

                if(!(n_eigenfaces == -1 || (n_eigenfaces >= 1 && n_eigenfaces <= maxN_values))){
                    System.out.println("\nERRO - A valor introduzido tem de estar dentro do intervalo 1 a " + maxN_values + ", ou -1!\n");
                }

            }while(!(n_eigenfaces == -1 || (n_eigenfaces >= 1 && n_eigenfaces <= maxN_values)));

            if(n_eigenfaces == -1){
                n_eigenfaces = maxN_values;
            }
        }else
        {
            n_eigenfaces = Integer.parseInt(args[3]);
            csvMatrixPath = args[5];

            file = new File(csvMatrixPath);

            if (!file.exists()) {
                System.out.println("\nO ficheiro indicado não existe!\n");
                erro = true;
            }else
            {
                matriz = CSVFile.CSVFileToArray(csvMatrixPath);

                erro = VertificarErroNaMatriz(matriz, tamanhoMaximo);
            }
        }

        if(n_eigenfaces < -1)
            n_eigenfaces = matriz.length;

        if(!erro)
            Decomposicao.executarDecomposicaoPropria(matriz, n_eigenfaces);
    }

    public static boolean VertificarErroNaMatriz(double[][] matriz, int tamanhoMaximo)
    {
        int linhas = matriz.length;
        int colunas = matriz[0].length;

        if(linhas > tamanhoMaximo || colunas > tamanhoMaximo){
            System.out.println("\nERRO - A imagem introduzida tem de ter um tamanho máximo de 256x256!\n");
            return true;
        }

        for(int i = 0; i < matriz.length; i++){
            for(int j = 0; j < matriz[0].length; j++){
                if((matriz[i][j] < 0) || (matriz[i][j] > 255)){
                    System.out.println("\nERRO - O valor " + (int)matriz[i][j] + " não está dentro do intervalo [0,255]!\n");
                    return true;
                }
            }
        }

        return false;
    }

    public static void FuncionalidadeMatrizCovariancias(String[] args) throws IOException {

        String imageBaseFolder;
        File folder;
        int n_eigenfaces;
        boolean erro;

        if(args.length == 0)
        {
            do{
                System.out.print("\nDigite a localização da pasta que contém as imagens de entrada: ");
                imageBaseFolder = inputReader.nextLine();

                erro = !VerificarPasta(imageBaseFolder);
            }while(erro);

            n_eigenfaces = InserirEigenfaces("Digite o número de eigenfaces que deseja utilizar na reconstrução de cada uma das imagens");
        }else
        {
            n_eigenfaces = Integer.parseInt(args[3]);

            imageBaseFolder = args[5];

            erro = !VerificarPasta(imageBaseFolder);
        }

        if(n_eigenfaces < -1)
            n_eigenfaces = -1;

        if(!erro)
            Covariancias.MatrizCovariancias(imageBaseFolder, n_eigenfaces);
    }

    public static void IdentificarEigenfaces(String[] args) throws IOException {
        String imagePath;
        File file, folder;
        int n_eigenfaces;
        boolean erro = false;

        if(args.length == 0)
        {
            do {
                System.out.print("\nDigite a localização do ficheiro CSV que contém a matriz de entrada: ");
                imagePath = inputReader.nextLine();

                file = new File(imagePath);

                if (!file.exists()) {
                    System.out.println("\nO ficheiro indicado não existe, tente novamente!\n");
                    erro = true;
                }
            }while(erro);

            String imageFolderPath;

            do {
                System.out.print("\nDigite a localização da pasta que contém as imagens de entrada: ");
                imageFolderPath  = inputReader.nextLine();

                erro = !VerificarPasta(imageFolderPath);

                folder = new File(imageFolderPath);
            }while (erro);

            n_eigenfaces = InserirEigenfaces("Digite o número de eigenfaces que quer utilizar na identificação de imagem");
        }else
        {
            n_eigenfaces = Integer.parseInt(args[3]);

            file = new File(args[5]);

            folder = new File(args[7]);

            erro = !VerificarPasta(args[7]) || !file.exists();
        }

        if(n_eigenfaces < -1)
            n_eigenfaces = -1;

        if(!erro)
            IdentificacaoEigenfaces.IdentificarImagem(file, folder, n_eigenfaces);
    }

    public static void GerarNovaImagem(String[] args) throws IOException {
        String imageBaseFolder;
        boolean erro;
        int n_eigenfaces;

        if(args.length == 0)
        {
            do{
                System.out.print("\nDigite a localização da pasta que contém as imagens de entrada: ");
                imageBaseFolder = inputReader.nextLine();

                erro = !VerificarPasta(imageBaseFolder);
            }while(erro);

            n_eigenfaces = InserirEigenfaces("Digite o número de eigenfaces que deseja utilizar na criação da imagem aleatória");
        }else
        {
            n_eigenfaces = Integer.parseInt(args[3]);

            imageBaseFolder = args[5];

            erro = !VerificarPasta(imageBaseFolder);
        }

        if(n_eigenfaces < -1)
            n_eigenfaces = -1;

        if(!erro)
            GeradorDeImagem.GerarImagem(new File(imageBaseFolder), n_eigenfaces);
    }

    public static boolean VerificarPasta(String path)
    {
        File folder = new File(path);

        if (folder.exists() && folder.isDirectory())
            System.out.println("\nA pasta indicada foi encontrada com sucesso!\n");
        else
            System.out.println("\nERRO - A pasta indicada não foi encontrada!\n");

        return folder.exists() && folder.isDirectory();
    }

    public static int LerOpcao(){
        System.out.print("Digite um opção: ");
        int option = inputReader.nextInt();
        inputReader.nextLine();
        return option;
    }

    public static int InserirEigenfaces(String mensagem)
    {
        System.out.println(mensagem);

        int n_eigenfaces;

        do{
            System.out.print("Digite o valor: ");
            n_eigenfaces = inputReader.nextInt();
            inputReader.nextLine();

            if(!(n_eigenfaces == -1 || n_eigenfaces > 0)){
                System.out.println("\nERRO - O número de eigenfaces tem de ser positivo ou -1!\n");
            }
        }while(!(n_eigenfaces == -1 || n_eigenfaces > 0));

        return n_eigenfaces;
    }

    public static void EscolherOpcao(int opcao, String[] args) throws IOException {
        switch (opcao) {
            case 1:
                DecomposicaoPropria(args);
                break;
            case 2:
                FuncionalidadeMatrizCovariancias(args);
                break;
            case 3:
                IdentificarEigenfaces(args);
                break;
            case 4:
                GerarNovaImagem(args);
        }
    }

    public static void MostrarMenu(){
        System.out.println("---Menu de opções---");
        System.out.println("1 - Decomposição própria de uma matriz simétrica");
        System.out.println("2 - Cálculo da matriz de covariâncias");
        System.out.println("3 - Identificação de imagens");
        System.out.println("4 - Gerar novas imagens");
        System.out.println("5 - Sair\n");
    }
}
