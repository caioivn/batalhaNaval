import java.net.*;
import java.io.*;
import java.util.Random;

public class Server{
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private PrintWriter print = null;
    private String strComunicacao;
    private String line;
    private String strResposta;
    private int numero;
    private char letra2;
    private int passo;
    private int tentativaNumero;
    private char tentativaLetra;
    private int contaAcertos;
    private Random aleatorio;
    private char matriz[][], embarcacoes[][];
    private int posicoesJaUsadas[];

    public Server(int porta){
        try{
            server = new ServerSocket(porta);
            socket = server.accept();
            print=new PrintWriter(socket.getOutputStream());
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            iniciaConversa();
            strComunicacao = "";
            line = "";
            strResposta = " ";
            contaAcertos = 0;
            aleatorio = new Random();
            /* A matriz 11x11 a seguir, é usada para enviar ao cliente onde foram feitas suas jogadas.
            */
            matriz = new char[11][11];
            posicoesJaUsadas = new int [100];
            /* Pelo fato das posições das embarcações do servidor serem alocadas de forma aleatória,
               este vetor é utilizado para controlar as posições que já foram alocadas para uma embarcação.
               Para que caso uma embarcação já tenha ocupado um determinado espaço, este espaço pertença
               somente àquela embarcação.
             */
            for(int i=0;i<100;i++){
                posicoesJaUsadas[i] = 0;
            }
            /* A matriz embarcacoes 10x10 a seguir, é usada para alocar as embarcações do servidor
            */
            embarcacoes = new char[10][10];
            /* A matriz abaixo é utilizada para ser exibida ao usuário que está jogando contra o
               servidor, neste caso, o cliente. A matriz abaixo contém 1 linha e 1 coluna a mais
               que a matriz embarcações, pois sua primeira linha é composta por números (de 0 a 9)
               e por letras (de A a J). Suas demais posições contém um espaço em branco que são
               substituídos por outros símbolos na medida em que o cliente realiza uma jogada.
            */
            for(int i=0;i<11;i++){
                for(int j=0;j<11;j++){
                    if(i==0&&j>=1){
                        matriz[i][j]=(char)(j+47);
                        matriz[j][i]=(char)(j+64);
                    }
                    else if((i==0&&j==0)||j!=0){
                        matriz[i][j]=' ';
                    }
                }
            }
            /* Abaixo é realizada a alocação das embarcações do servidor.
               Esta forma de alocação consiste em:
               São gerados 3 números aleatórios, onde os números contidos nas variáveis:
               posicaoX e posicaoY, correspondem à posição inicial em que o servidor tentará
               alocar a embarcação em questão, começando da maior para a menor, neste caso,
               começando do porta-aviões até o submarino.
               O terceiro valor aletório gerado, corresponde ao direcionamento da respectiva embarcação.
               Ou seja, se a mesma será alocada na horizontal ou na vertical.
               Como o programa seleciona as posições das embarcações de forma aleatória, após gerar uma posição
               aleatória, ele verifica com antecedência se a partir dali, é possível ou não realizar a alocação
               daquela deterinada embarcação. Caso seja possível, a aocação é realizada. Caso não sej possível,
               o programa irá gerar outro número aleatório até encontrar um local onde possa alocar aquela embarcação.
               Porém a orientação (horizntal ou vertical) é mantida.
               No caso do porta-aviões, esta veriicação não é necessária, pois, como o porta-aviões é a primeira embarcação
               a ser alocada, quando sua alocação ocorre, nenhuma posição ainda foi utilizada.
               O programa utiliza a posição inicial como referência para alocar todas as demais posições
               daquela embarcação em questão. Ou seja, o programa caminha para a direita (alocação horizontal)
               ou para baixo (alocação vertical), caso o programa atinja uma posição que se encontre nos limites
               da matriz de embarcações, por exemplo, ele tentar alocar um porta-aviões de tamanho 5, começando
               de uma posição próxima ao final da matriz, na coluna 7 por exemplo. O programa irá alocar até o final
               e depois, as posições restantes serão alocadas à esquerda da posição inicial.
               O mesmo ocorre no cenário da alocação vertical. Em que caso não seja possível continuar caminhando
               para baixo, o programa alocará as posições restantes acima da poição inicial.
               O vetor posicoesJaUsadas é utilizado para administrar onde pode ser aocada uma embarcação
               e onde não pode. Toda vez que uma embarcação é alocada num determinado espaço, as posições referentes
               àquele espaço são representadas com valor 1 no vetor posicoesJaUsadas. Além disso, na matriz embarcações (10x10),
               substituímos o símbolo do espaço em branco pela letra correspondente à inicial do nome do tipo da embarcação
               em questão, indicando que naquela posição há uma parte de uma embarcação.
            */
            int posicaoX = aleatorio.nextInt(10);
            int posicaoY = aleatorio.nextInt(10);
            int direcao = aleatorio.nextInt(2);
            for(int i=0;i<10;i++){
                int tamanhoNavio;
                int n;
                if(i==0){
                    //direcao = 0 (orientação horizontal)
                    if(direcao==0){
                        n=posicaoX;
                        tamanhoNavio = 5;
                        while(tamanhoNavio > 0){
                            while(n <= 9 && tamanhoNavio > 0){
                                embarcacoes[posicaoY][n] = 'P';
                                posicoesJaUsadas[(10*posicaoY) + n] = 1;
                                tamanhoNavio--;
                                n++;
                            }
                            n=posicaoX-1;
                            while(n >= 0 && tamanhoNavio > 0){
                                embarcacoes[posicaoY][n] = 'P';
                                posicoesJaUsadas[(10*posicaoY) + n] = 1;
                                tamanhoNavio--;
                                n--;
                            }
                        }
                    }
                    //direcao = 1 (orientação vertical)
                    else{
                        n=posicaoY;
                        tamanhoNavio = 5;
                        while(tamanhoNavio > 0){
                            while(n <= 9 && tamanhoNavio > 0){
                                embarcacoes[n][posicaoX] = 'P';
                                posicoesJaUsadas[(10*n) + posicaoX] = 1;
                                tamanhoNavio--;
                                n++;
                            }
                            n=posicaoY-1;
                            while(n >= 0 && tamanhoNavio > 0){
                                embarcacoes[n][posicaoX] = 'P';
                                posicoesJaUsadas[(10*n) + posicaoX] = 1;
                                tamanhoNavio--;
                                n--;
                            }
                        }
                    }
                }
                else if(i == 1 || i == 2){
                    posicaoX = aleatorio.nextInt(10);
                    posicaoY = aleatorio.nextInt(10);
                    direcao = aleatorio.nextInt(2);
                    //direcao = 0 (orientação horizontal)
                    if(direcao==0){
                        int k=0;
                        //Verificação se é possível alocar esta embarcação a partir da posição gerada.
                        while(k < 4){
                            if((k+posicaoX > 9)|| (posicoesJaUsadas[((10*posicaoY)+posicaoX)+k]==1)){
                                posicaoX = aleatorio.nextInt(10);
                                posicaoY = aleatorio.nextInt(10);
                                k=0;
                            }
                            else{
                                k++;
                            }
                        }
                        n=posicaoX;
                        tamanhoNavio = 4;
                        while(tamanhoNavio > 0){
                            while(n <= 9 && tamanhoNavio > 0){
                                embarcacoes[posicaoY][n] = 'T';
                                posicoesJaUsadas[(10*posicaoY) + n] = 1;
                                tamanhoNavio--;
                                n++;
                            }
                            n=posicaoX-1;
                            while(n >= 0 && tamanhoNavio > 0){
                                embarcacoes[posicaoY][n] = 'T';
                                posicoesJaUsadas[(10*posicaoY) + n] = 1;
                                tamanhoNavio--;
                                n--;
                            }
                        }
                    }
                    //direcao = 1, orientação vertical
                    else{
                        int k=0;
                        //Verificação se é possível alocar esta embarcação a partir da posição gerada.
                        while(k < 4){
                            if((k+posicaoY) > 9 || (posicoesJaUsadas[((10*(posicaoY+k))+posicaoX)]==1)){
                                posicaoX = aleatorio.nextInt(10);
                                posicaoY = aleatorio.nextInt(10);
                                k=0;
                            }
                            else{
                                k++;
                            }
                        }
                        n=posicaoY;
                        tamanhoNavio = 4;
                        while(tamanhoNavio > 0){
                            while(n <= 9 && tamanhoNavio > 0){
                                embarcacoes[n][posicaoX] = 'T';
                                posicoesJaUsadas[(10*n) + posicaoX] = 1;
                                tamanhoNavio--;
                                n++;
                            }
                            n=posicaoY-1;
                            while(n >= 0 && tamanhoNavio > 0){
                                embarcacoes[n][posicaoX] = 'T';
                                posicoesJaUsadas[(10*n) + posicaoX] = 1;
                                tamanhoNavio--;
                                n--;
                            }
                        }
                    }
                }
                else if(i == 3 || i == 4 || i ==5){
                    posicaoX = aleatorio.nextInt(10);
                    posicaoY = aleatorio.nextInt(10);
                    direcao = aleatorio.nextInt(2);
                    //direcao = 0 (orientação horizontal)
                    if(direcao==0){
                        int k=0;
                        //Verificação se é possível alocar esta embarcação a partir da posição gerada.
                        while(k < 3){
                            if((k+posicaoX > 9)|| (posicoesJaUsadas[((10*posicaoY)+posicaoX)+k]==1)){
                                posicaoX = aleatorio.nextInt(10);
                                posicaoY = aleatorio.nextInt(10);
                                k=0;
                            }
                            else{
                                k++;
                            }
                        }
                        n=posicaoX;
                        tamanhoNavio = 3;
                        while(tamanhoNavio > 0){
                            while(n <= 9 && tamanhoNavio > 0){
                                embarcacoes[posicaoY][n] = 'C';
                                posicoesJaUsadas[(10*posicaoY) + n] = 1;
                                tamanhoNavio--;
                                n++;
                            }
                            n=posicaoX-1;
                            while(n >= 0 && tamanhoNavio > 0){
                                embarcacoes[posicaoY][n] = 'C';
                                posicoesJaUsadas[(10*posicaoY) + n] = 1;
                                tamanhoNavio--;
                                n--;
                            }
                        }
                    }
                    //direcao = 1 (orientação vertical)
                    else{
                        int k=0;
                        //Verificação se é possível alocar esta embarcação a partir da posição gerada.
                        while(k < 3){
                            if((k+posicaoY) > 9 || (posicoesJaUsadas[((10*(posicaoY+k))+posicaoX)]==1)){
                                posicaoX = aleatorio.nextInt(10);
                                posicaoY = aleatorio.nextInt(10);
                                k=0;
                            }
                            else{
                                k++;
                            }
                        }
                        n=posicaoY;
                        tamanhoNavio = 3;
                        while(tamanhoNavio > 0){
                            while(n <= 9 && tamanhoNavio > 0){
                                embarcacoes[n][posicaoX] = 'C';
                                posicoesJaUsadas[(10*n) + posicaoX] = 1;
                                tamanhoNavio--;
                                n++;
                            }
                            n=posicaoY-1;
                            while(n >= 0 && tamanhoNavio > 0){
                                embarcacoes[n][posicaoX] = 'C';
                                posicoesJaUsadas[(10*n) + posicaoX] = 1;
                                tamanhoNavio--;
                                n--;
                            }
                        }
                    }
                }
                else if(i == 6 || i == 7 || i == 8 || i == 9){
                    posicaoX = aleatorio.nextInt(10);
                    posicaoY = aleatorio.nextInt(10);
                    direcao = aleatorio.nextInt(2);
                    //direcao = 0 (orientação horizontal)
                    if(direcao==0){
                        int k=0;
                        //Verificação se é possível alocar esta embarcação a partir da posição gerada.
                        while(k < 2){
                            if((k+posicaoX > 9)|| (posicoesJaUsadas[((10*posicaoY)+posicaoX)+k]==1)){
                                posicaoX = aleatorio.nextInt(10);
                                posicaoY = aleatorio.nextInt(10);
                                k=0;
                            }
                            else{
                                k++;
                            }
                        }
                        n=posicaoX;
                        tamanhoNavio = 2;
                        while(tamanhoNavio > 0){
                            while(n <= 9 && tamanhoNavio > 0){
                                embarcacoes[posicaoY][n] = 'S';
                                posicoesJaUsadas[(10*posicaoY) + n] = 1;
                                tamanhoNavio--;
                                n++;
                            }
                            n=posicaoX-1;
                            while(n >= 0 && tamanhoNavio > 0){
                                embarcacoes[posicaoY][n] = 'S';
                                posicoesJaUsadas[(10*posicaoY) + n] = 1;
                                tamanhoNavio--;
                                n--;
                            }
                        }
                    }
                    //direcao = 1 (orientação vertical)
                    else{
                        int k=0;
                        //Verificação se é possível alocar esta embarcação a partir da posição gerada.
                        while(k < 2){
                            if((k+posicaoY) > 9 || (posicoesJaUsadas[((10*(posicaoY+k))+posicaoX)]==1)){
                                posicaoX = aleatorio.nextInt(10);
                                posicaoY = aleatorio.nextInt(10);
                                k=0;
                            }
                            else{
                                k++;
                            }
                        }
                        n=posicaoY;
                        tamanhoNavio = 2;
                        while(tamanhoNavio > 0){
                            while(n <= 9 && tamanhoNavio > 0){
                                embarcacoes[n][posicaoX] = 'S';
                                posicoesJaUsadas[(10*n) + posicaoX] = 1;
                                tamanhoNavio--;
                                n++;
                            }
                            n=posicaoY-1;
                            while(n >= 0 && tamanhoNavio > 0){
                                embarcacoes[n][posicaoX] = 'S';
                                posicoesJaUsadas[(10*n) + posicaoX] = 1;
                                tamanhoNavio--;
                                n--;
                            }
                        }
                    }
                }
            }
            start();
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
    }

    /* Este método é utilizado para enviar a matriz do servidor para o cliente.
       Detalhe: o cliente não recebe a matriz embarcações (10x10), ele recebe a matriz 11x11
       em que não são mostradas onde as embarcações do servidor estão, apenas as referências de cada
       posição, ou seja, primeira linha composta por números e primeira coluna composta por letras.
       Além disso, também são mostradas as posições onde o cliente realizou suas tentativas.
       Podendo ser um X em caso de erros ou um # caso o cliente tenha acertado uma posição
       de uma parte de uma das embarcações. 
    */
    public void enviaMatriz(){
        strComunicacao = "Acertos: letra correspondente à inicial do nome da embarcação\n";
        strComunicacao = strComunicacao + "Erros: X\n";
        for(int i=0;i<11;i++){
            for(int j=0;j<11;j++){
                strComunicacao = strComunicacao + matriz[i][j]+" | ";
            }
            strComunicacao = strComunicacao + "\n";
        }
        print.print(strComunicacao);
        print.flush();
        strComunicacao="";
    }

    /* Método que lê a jogada do cliente. Caso o cliente informe uma posição em que se encontra parte de uma embarcação,
       a matriz das embarcações do servidor (11x11) que é enviada para o cliente, caso o mesmo solicite em vê-la,
       nela será atribuído o símbolo correspondente à inicial do nome do tipo de embarcação, indicando que o cliente acertou
       uma parte da respectiva embarcação em sua jogada. E o símbolo X é atuibuído à matriz de embarcações (10x10), indicando
       que o cliente já acertou aquela posição. E caso ele tente acertar aquela posição novamente, não serão contabilizados acertos.
       Ao contrário, será contado como se o cliente simplesmente errou a jogada.
       Caso o cliente erre, será atribuído o símbolo X para aquela posição da matriz.
       Caso o cliente informe uma jogada inválida, ou seja, algum comando diferente do esperado,
       nada será atribuído à matriz, e será retornado o valor 2, onde posteriormente será enviado ao cliente que sua
       jogada é inválida.
    */
    public int lerComandoCliente(){
        boolean maiusculo = false;
        if(line.length()==2 && (line.charAt(0)>='A' && line.charAt(0)<='J')||(line.charAt(0)>='a' && line.charAt(0)<='j')){
            if((line.charAt(0)>='A' && line.charAt(0)<='J')){
                maiusculo=true;
            }
            if(line.charAt(1)>='0' && line.charAt(1)<='9'){
                if(maiusculo){
                    if(embarcacoes[(int)(line.charAt(0)-65)][(int)(line.charAt(1)-48)]=='P'){
                        embarcacoes[(int)(line.charAt(0)-65)][(int)(line.charAt(1)-48)]='X';
                        matriz[(int)(line.charAt(0)-65)+1][(int)(line.charAt(1)-48)+1]='P';
                        contaAcertos++;
                        return 1;
                    }
                    else if(embarcacoes[(int)(line.charAt(0)-65)][(int)(line.charAt(1)-48)]=='T'){
                        embarcacoes[(int)(line.charAt(0)-65)][(int)(line.charAt(1)-48)]='X';
                        matriz[(int)(line.charAt(0)-65)+1][(int)(line.charAt(1)-48)+1]='T';
                        contaAcertos++;
                        return 1;
                    }
                    else if(embarcacoes[(int)(line.charAt(0)-65)][(int)(line.charAt(1)-48)]=='C'){
                        embarcacoes[(int)(line.charAt(0)-65)][(int)(line.charAt(1)-48)]='X';
                        matriz[(int)(line.charAt(0)-65)+1][(int)(line.charAt(1)-48)+1]='C';
                        contaAcertos++;
                        return 1;
                    }
                    else if(embarcacoes[(int)(line.charAt(0)-65)][(int)(line.charAt(1)-48)]=='S'){
                        embarcacoes[(int)(line.charAt(0)-65)][(int)(line.charAt(1)-48)]='X';
                        matriz[(int)(line.charAt(0)-65)+1][(int)(line.charAt(1)-48)+1]='S';
                        contaAcertos++;
                        return 1;
                    }
                    else if(embarcacoes[(int)(line.charAt(0)-65)][(int)(line.charAt(1)-48)]=='X'){
                        return 0;
                    }
                    else{
                        matriz[(int)(line.charAt(0)-65)+1][(int)(line.charAt(1)-48)+1]='X';
                        return 0;
                    }
                }
                else{
                    if(embarcacoes[(int)(line.charAt(0)-97)][(int)(line.charAt(1)-48)]=='P'){
                        embarcacoes[(int)(line.charAt(0)-97)][(int)(line.charAt(1)-48)]='X';
                        matriz[(int)(line.charAt(0)-97)+1][(int)(line.charAt(1)-48)+1]='P';
                        contaAcertos++;
                        return 1;
                    }
                    else if(embarcacoes[(int)(line.charAt(0)-97)][(int)(line.charAt(1)-48)]=='T'){
                        embarcacoes[(int)(line.charAt(0)-97)][(int)(line.charAt(1)-48)]='X';
                        matriz[(int)(line.charAt(0)-97)+1][(int)(line.charAt(1)-48)+1]='T';
                        contaAcertos++;
                        return 1;
                    }
                    else if(embarcacoes[(int)(line.charAt(0)-97)][(int)(line.charAt(1)-48)]=='C'){
                        embarcacoes[(int)(line.charAt(0)-97)][(int)(line.charAt(1)-48)]='X';
                        matriz[(int)(line.charAt(0)-97)+1][(int)(line.charAt(1)-48)+1]='C';
                        contaAcertos++;
                        return 1;
                    }
                    else if(embarcacoes[(int)(line.charAt(0)-97)][(int)(line.charAt(1)-48)]=='S'){
                        embarcacoes[(int)(line.charAt(0)-97)][(int)(line.charAt(1)-48)]='X';
                        matriz[(int)(line.charAt(0)-97)+1][(int)(line.charAt(1)-48)+1]='S';
                        contaAcertos++;
                        return 1;
                    }
                    else if(embarcacoes[(int)(line.charAt(0)-97)][(int)(line.charAt(1)-48)]=='X'){
                        return 0;
                    }
                    else{
                        matriz[(int)(line.charAt(0)-97)+1][(int)(line.charAt(1)-48)+1]='X';
                        return 0;
                    }
                }
            }
        }
        return 2;
    }

    /* Método utilizado para enviar a resposta da jogada do cliente, Você errou (retornou 0 da
       função lerComandoCliente, indicando que o cliente errou), Você acertou (retornou 1 da função
       lerComandoCliente, indicando que o cliente acertou), Opção inválida (retornou 2 da função
       lerComandoCliete, indicando que o cliente informou um comando que não condiz com o esperado).
       Além disso, aqui é feita a decisão da jogada do servidor.
    */
    public void enviaComandoCliente(int resposta){
        if(resposta == 0){
            strComunicacao = "Você errou!\n";
        }
        else if(resposta == 1){
            strComunicacao = "Você acertou!\n";
        }
        else if(resposta == 2){
            strComunicacao = "Opção inválida!\n";
        }
        /* No estado inicial da partida, onde strResposta ainda contém espaço em branco,
           ou toda que o servidor erra uma jogada, ou destrói uma embarcação do cliente,
           ele deve gerar uma jogada aleatória. De fora que valor e letra correspondem à jogada
           propriamente dita. As variáveis tentativaLetra e tentativaNumero sõ usadas apenas para
           salvar a "posição inicial" da jogada do servidor. Onde essa "posição inicial" se comporta de forma que
           caso o servidor acerte uma parte de uma embarcação do cliente, esta posição é armazenada em tentativaNumero e
           em tentativaLetra.
        */
        if(strResposta.equals(" ") || strResposta.equals("Errou") || strResposta.contains("Destruiu")){
            int valor = aleatorio.nextInt(10);
            int valor2 = aleatorio.nextInt(10);
            char letra = ((char)('A'+valor));
            tentativaLetra = letra;
            tentativaNumero = valor2;
            numero = tentativaNumero;
            letra2 = tentativaLetra;
            strComunicacao = strComunicacao + (Character.toString(letra)) + (Integer.toString(valor2)) + "\n";
            passo = 0;
        }
        /* Caso o servidor acerte uma posição de uma parte de uma embarcação do cliente, ele receberá a mensagem: Acertou.
           Desta forma, na próxima jogada, ele tentará acertar a posição à direita de onde ele acertou.
           Caso ele erre uma jogada, mas ele havia acertado anteriormente, mas não destruiu a embarcação
           ainda, a mensagem que ele receberá será: Tente novamente. Caso o servidor receba essa mensagem, ele
           tentará acertar a posição à esquerda da posição de início.
           Caso ele erre, novamente, receberá: Tente novamente. Porém, agora ele tentará acertar a posição abaixo da posição inicial
           (onde havia acertado), caso ele erre novamente, receberá: Tente novamente. Mas agora, ele tentará acertar a
           posição acima de onde ele havia acertado inicialmente nesta jogada.
           Ao destruir a embarcação, ele receberá: Destruiu embarcacao. E em sua próxima jogada, irá gerar outra
           posição aleatória.
        */
        else if(strResposta.equals("Tente novamente") || strResposta.equals("Acertou")){
            if(strResposta.equals("Tente novamente")){
                letra2 = tentativaLetra;
                numero = tentativaNumero;
            }
            /* Caso o servidor erre após ter acertado anteriormente, ou enquanto estiver caminhando para a direita
                e estiver acertando, o servidor executará os passos a seguir:
            */
            if((strResposta.equals("Acertou") && passo ==1) || passo == 0){
                /* Caso não seja posível andar para a direita (encontra-se numa posição da útima coluna
                   da matriz), ele ão pode avançar para a direita, desta forma,
                   ele irá atacar a posição à esquerda da posição de início.
                */
                if((numero + 1) > 9){
                    numero--;
                    strComunicacao = strComunicacao + (Character.toString(letra2)) + (Integer.toString(numero--)) + "\n";
                    passo =2;
                }
                /* Caso contrário, ele caminha para a direita enquanto estiver acertando
                */
                else{
                    numero++;
                    strComunicacao = strComunicacao + (Character.toString(letra2)) + (Integer.toString(numero)) + "\n";
                    passo =1;
                }
            }
            /* Caso o servidor erre após ter acertado anteriormente, ou enquanto estiver caminhando para a esquerda
                e estiver acertando, o servidor executará os passos a seguir:
            */
            else if((strResposta.equals("Acertou")&& passo == 2) || passo == 1){
                /* Caso não seja posível andar para a esquerda (encontra-se numa posição da primeira coluna
                   da matriz), ele verificará se está na última linha da matriz (pois não poderá andar para baixo).
                   Se não estiver na última linha, ele caminha para baixo, caso contrário, caminha para cima.
                */
                if((numero - 1) < 0){
                    if((letra2 + 1) > 74){
                        letra2--;
                        strComunicacao = strComunicacao + (Character.toString(letra2)) + (Integer.toString(numero)) + "\n";
                        passo = 4;
                    }
                    else{
                        letra2++;
                        strComunicacao = strComunicacao + (Character.toString(letra2)) + (Integer.toString(numero)) + "\n";
                        passo = 3;
                    }
                }
                /* Caso contrário, ele caminha para a esquerda enquanto estiver acertando
                */
                else{
                    numero--;
                    strComunicacao = strComunicacao + (Character.toString(letra2)) + (Integer.toString(numero)) + "\n";
                    passo =2;
                }
            }
            /* Caso o servidor erre após ter acertado anteriormente, ou enquanto estiver caminhando para baixo
                e estiver acertando, o servidor executará os passos a seguir:
            */
            else if((strResposta.equals("Acertou") && passo == 3) || passo == 2){
                /* Caso não seja posível andar para a baixo (encontra-se numa posição da útima linha
                   da matriz), ele ão pode avançar para a baixo, desta forma,
                   ele irá atacar a posição acima da posição de início.
                */
                if((letra2 + 1) > 74){
                    letra2--;
                    strComunicacao = strComunicacao + (Character.toString(letra2)) + (Integer.toString(numero)) + "\n";
                    passo = 4;
                }
                /* Caso contrário, ele caminha para baixo enquanto estiver acertando
                */
                else{
                    letra2++;
                    strComunicacao = strComunicacao + (Character.toString(letra2)) + (Integer.toString(numero)) + "\n";
                    passo = 3;
                }
            }
            /* Caso o servidor erre após ter acertado anteriormente, ou enquanto estiver caminhando para cima
                e estiver acertando, o servidor executará os passos a seguir:
            */
            else if((strResposta.equals("Acertou") && passo == 4) || passo == 3){
                /* Aqui não é necessário fazer nenhuma verificação de possibilidade, pois todas já
                   foram tratadas acima. Neste caso, ele caminha para cima enquanto estiver acertando
                */
                letra2--;
                strComunicacao = strComunicacao + (Character.toString(letra2)) + (Integer.toString(numero)) + "\n";
                passo = 4;
            }
        }
        print.print(strComunicacao);
        print.flush();
        strComunicacao="";
        try{
            strResposta = in.readUTF();
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
    }

    /* Método utilizado para enviar uma mensagem ao cliente, parabenizando o mesmo pela sua vitória
       caso vença a partida.
    */
    public void parabens(){
        strComunicacao = "Parabéns!\n";
        strComunicacao = strComunicacao + "Você venceu!\n";
        print.print(strComunicacao);
        print.flush();
        strComunicacao="";
    }

    /* Método utilizado apenas para iniciar a conversa entre servidor e cliente, onde
       o servidor diz ao cliente que encontra-se online, e aguarda uma resposta do cliente.
       No programa do cliente, ele responde dizendo estar online apenas para iniciar a conversa.
    */
    public void iniciaConversa(){
        try{
            strComunicacao = "Servidor online\n";
            print.print(strComunicacao);
            print.flush();
            strComunicacao = "";
            strComunicacao = in.readUTF();
            System.out.println(strComunicacao);
            strComunicacao = "";
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
        
    }

    /* Método utilizado para encerramento das atividades do servidor.
    */
    void close(){
        try{
            print.close();
            socket.close();
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
    }

    /* Método utilizado para enviar uma mensage ao cliente informando-o sobre a vitória do servidor.
    */
    public void vitoria(){
        strComunicacao = "Vitoria do servidor\n";
        print.print(strComunicacao);
        print.flush();
    }

    public void start(){
        try{
            /* Caso o cliente digite Fim (deseja terminar o jogo antes do final), ou caso o contador de acertos
               cliente chega a 30 (destruiu todas as embarcações do servidor), ou caso o servidor receba a mensagem que
               venceu a partida, a execução é interrompida e as atividades são encerradas.
            */
            while((contaAcertos < 30) && (!strResposta.contains("venceu")) && !((line = in.readUTF()).equals("Fim"))){
                strComunicacao="";
                /* Caso o cliente solicite em ver a matriz do servidor, o servidor
                   informará ao cliente o andamento do jogo sobre sua matriz.
                */
                if(line.equals("P")){
                    enviaMatriz();
                }
                else{
                    /* Caso contrário, o servidorirá processar a jogada do cliente.
                    */
                    int resposta = lerComandoCliente();
                    /* Caso o cliente realize o último acerto e vença a partida nesta jogada,
                       o servidor não irá mandar um comano de ataque, e irá parabenizar o cliente
                       por sua vitória.
                    */
                    if(contaAcertos<30){
                        enviaComandoCliente(resposta);
                    }
                    else{
                        parabens();
                    }
                }
            }
            /* Caso o servidor vença, o servidor informará ao cliente que o servidor venceu.
            */
            if(strResposta.contains("venceu")){
                vitoria();
            }
            /* Caso o cliente queira interromper o jogo, ou seja, parar de jogar, o servidor
               informará ao cliente que o jogo chegou ao fim. 
            */
            else if(line.equals("Fim")){
                strComunicacao = "Fim de jogo\n";
                print.print(strComunicacao);
                print.flush();
                strComunicacao="";
            }
            close();
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
    }

    public static void main(String args[]){
        new Server(5000);
    }
}
