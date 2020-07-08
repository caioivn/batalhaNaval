import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client{
    private Socket socket = null;
    private DataOutputStream out = null;
    private char embarcacoes[][];
    private InputStreamReader in = null;
    private BufferedReader br = null;
    private Scanner sc = null;
    private String strComunicacao;
    private int contaAcertos;
    private boolean acertou;
    private char letraPrimeiroTiro;
    private char letraTiroAtual;
    private int contPortaAvioes;
    private int contTanque1;
    private int contTanque2;
    private int contContraTorped1;
    private int contContraTorped2;
    private int contContraTorped3;
    private int contSubmarino1;
    private int contSubmarino2;
    private int contSubmarino3;
    private int contSubmarino4;
    private int portaAvioes[];
    private int tanque1[];
    private int tanque2[];
    private int contraTorped1[];
    private int contraTorped2[];
    private int contraTorped3[];
    private int submarino1[];
    private int submarino2[];
    private int submarino3[];
    private int submarino4[];
    private boolean portaAvioesDestruido;
    private boolean tanque1Destruido;
    private boolean tanque2Destruido;
    private boolean contraTorped1Destruido;
    private boolean contraTorped2Destruido;
    private boolean contraTorped3Destruido;
    private boolean submarino1Destruido;
    private boolean submarino2Destruido;
    private boolean submarino3Destruido;
    private boolean submarino4Destruido;

    public Client(String endereco, int porta){
        try{
            /* Inicialização de variáveis
            */
            socket = new Socket(endereco,porta);
            out = new DataOutputStream(socket.getOutputStream());
            in = new InputStreamReader(socket.getInputStream());
            br = new BufferedReader(in);
            ouvirServidor();
            embarcacoes = new char [10][10];
            strComunicacao = "";
            letraPrimeiroTiro = ' ';
            letraTiroAtual = ' ';
            acertou = false;
            contaAcertos = 0;
            contPortaAvioes = 0;
            contTanque1 = 0;
            contTanque2 = 0;
            contContraTorped1 = 0;
            contContraTorped2 = 0;
            contContraTorped3 = 0;
            contSubmarino1 = 0;
            contSubmarino2 = 0;
            contSubmarino3 = 0;
            contSubmarino4 = 0;
            portaAvioes = new int[5];
            tanque1 = new int[4];
            tanque2 = new int[4];
            contraTorped1 = new int[3];
            contraTorped2 = new int[3];
            contraTorped3 = new int[3];
            submarino1 = new int[2];
            submarino2 = new int[2];
            submarino3 = new int[2];
            submarino4 = new int[2];
            for(int i=0;i<5;i++){
                portaAvioes[i] = 0;
            }
            for(int i=0;i<4;i++){
                tanque1[i] = 0;
                tanque2[i] = 0;
            }
            for(int i=0;i<3;i++){
                contraTorped1[i] = 0;
                contraTorped2[i] = 0;
                contraTorped3[i] = 0;
            }
            for(int i=0;i<2;i++){
                submarino1[i] = 0;
                submarino2[i] = 0;
                submarino3[i] = 0;
                submarino4[i] = 0;
            }
            portaAvioesDestruido = false;
            tanque1Destruido = false;
            tanque2Destruido = false;
            contraTorped1Destruido = false;
            contraTorped2Destruido = false;
            contraTorped3Destruido = false;
            submarino1Destruido = false;
            submarino2Destruido = false;
            submarino3Destruido = false;
            submarino4Destruido = false;
            sc = new Scanner(System.in);
            menu();
        }
        catch(UnknownHostException uhe){
            System.out.println(uhe);
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
    }

    /* Método utilizado apenas para iniciar a conversa entre servidor e cliente, onde
       o servidor diz ao cliente que encontra-se online, e aguarda uma resposta do cliente.
       O cliente responde dizendo estar online apenas para iniciar a conversa.
    */
    void ouvirServidor(){
        try{
            strComunicacao = br.readLine();
            System.out.println(strComunicacao);
            out.writeUTF("Cliente online");
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
    }
    
    /* Menu de opções de forma interativa com o cliente. Este método é usado para
       executar o método de start apenas caso o cliente já tenha alocado as posições
       desejadas para suas embarcações.
       A alocação é feita no arquivo batalhaNaval.txt, onde o cliente substitui o espaço em branco
       na posição desejada por uma letra que corresponda a uma das embarcações.
       Caso o cliente queira sair neste momento, o programa informará ao servidor uma mensagem de Fim
       onde o servidor responderá com a mensagem: Fim de jogo.
    */
    public void menu(){
        int op=0;
        boolean arquivo=false;
        criaArquivo();
        while(op != 3 && op != 2){
            System.out.println("Digite uma opção:\n1- Ler arquivo de entrada\n2- Jogar\n3-Sair");
            op = sc.nextInt();
            switch(op){
                case 1:
                    /* A variável arquivo terá o valor true, caso o usuário execute o método a seguir.
                    */
                    arquivo=leArquivo();
                    break;
                case 2:
                    if(arquivo){
                        start();
                    }
                    break;
                case 3:
                    try{
                        out.writeUTF("Fim");
                        strComunicacao = br.readLine();
                        System.out.println(strComunicacao);
                        close();
                    }
                    catch(IOException ioe){
                        System.out.println(ioe);
                    }
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    /* Método utilizado para mostrar a matriz do cliente, quando for solicitado
    */
    public void printaMatrizCliente(){
        System.out.println("Matriz Cliente:\nPosições de suas embarcacoes:\nPorta-Avioes: P\nNavios-Tanque: T\nContratorpedeiros: C\nSubmarinos: S\nAcertos do servidor: O\nErros do servidor: X");
        System.out.println("  | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |");
        for(int i=0;i<10;i++){
            System.out.print(((char)('A'+i))+" |");
            for(int j=0;j<10;j++){
                System.out.print(" "+embarcacoes[i][j]+" |");
            }
            System.out.print("\n");
        }
    }

    /* Método utilizado para verificar se o cliente venceu a partida, ou seja,
       receberá a mensagem:
       Parabens!
       Você venceu
       Caso contrário, irá mostrar ao cliente a posição em que o servidor pretende atacá-lo.
    */
    public boolean printaRespostaServidor(){
        strComunicacao = "";
        int n=2;
        try{
            while(n>0){
                strComunicacao = br.readLine();
                if(strComunicacao.contains("venceu")){
                    System.out.println(strComunicacao);
                    return true;
                }
                else if(n==1){
                    System.out.println("Servidor ataca posicao: "+strComunicacao);
                }
                else{
                    System.out.println(strComunicacao);
                }
                n--;
            }
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
        return false;
    }

    /* Método utiliado para receber a matriz das embarcações do servidor e mostrá-la ao cliente
       quando o cliente solicitar
    */
    public void printaMatrizAdversaria(){
        strComunicacao = "";
        int n=13;
        try{
            while(n>0){
                strComunicacao = br.readLine();
                System.out.println(strComunicacao);
                n--;
            }
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
    }

    /* Método utilizado caso o cliente digite Fim, ou caso o servidor vença a partida.
       Caso o cliente queira encerrar o jogo antes do final, a mensagem que o servidor
       enviará ao cliente e que será exibida é:
       Fim de jogo
       Caso contrário, e o servidor vença a partida, a mensagem que o servidor enviará
       ao cliente e que será exibida é:
       Vitória do servidor
    */
    public void fim(){
        strComunicacao = "";
        try{
            strComunicacao = br.readLine();
            System.out.println(strComunicacao);
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
    }

    /* Método utilizado para verificar se uma determinada posição da matriz de embarcações
       corresponde a um determinado vetor de alguma das embarcações
    */
    public boolean Achou(int vetor[], int numero){
        for(int i=0;i<vetor.length;i++){
            if(vetor[i]==numero){
                return true;
            }
        }
        return false;
    }

    /* Método utilizado para verificar se uma determinada posição da matriz de embarcações
       corresponde a qual contratorpedeiro
    */
    public int AchouContraTorpedeiro(int numero){
        for(int i=0;i<contraTorped1.length;i++){
            if(contraTorped1[i]==numero){
                return 1;
            }
            else if(contraTorped2[i]==numero){
                return 2;
            }
            else if(contraTorped3[i]==numero){
                return 3;
            }
        }
        return 4;
    }

    /* Método utilizado para verificar se uma determinada posição da matriz de embarcações
       corresponde a qual navio-tanque
    */
    public int AchouTanque(int numero){
        for(int i=0;i<tanque1.length;i++){
            if(tanque1[i]==numero){
                return 1;
            }
            else if(tanque2[i]==numero){
                return 2;
            }
        }
        return 3;
    }
    
    /* Método utilizado para verificar se uma determinada posição da matriz de embarcações
       corresponde a qual submarino
    */
    public int AchouSubmarino(int numero){
        for(int i=0;i<submarino1.length;i++){
            if(submarino1[i]==numero){
                return 1;
            }
            else if(submarino2[i]==numero){
                return 2;
            }
            else if(submarino3[i]==numero){
                return 3;
            }
            else if(submarino4[i]==numero){
                return 4;
            }
        }
        return 5;
    }

    public void lerComandoServidor(){
        try{
            if(embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)]=='P' || embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)]=='T' || embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)]=='C' || embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)]=='S'){
                if(embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)]!='O'){
                    contaAcertos++;
                }
                if(letraPrimeiroTiro == ' '){
                    /* letraPrimeiroTiro é uma variável que auxilia no controle de qual resposta será entregue ao servidor,
                       ela armazena a letra correspondente à embarcação que o servidor acertou quando fez seu primeiro acerto.
                       Ou seja, caso o servidor acerte uma embarcação, e no que ele for acertar uma posição vizinha, ele
                       acerte outra embarcação, será enviada a seguinte mensagem: Tente novamente.
                       Pois, apesar do fato do servidor ter acertado uma parte de uma embarcação, ele errou a embarcação
                       que seria "o foco" dele naquele momento. Porém, quando o servidor acerta a outra embarcação
                       "sem querer", o acerto é contabilizado mesmo assim.
                    */
                    letraPrimeiroTiro = embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)];
                }
                letraTiroAtual = embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)];
                embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)]='O';
                /* A variável acertou é utilizada para ajudar no controle de qual mensagem será enviada ao
                   servidor. Ou seja, enquanto acertou for igual a true, ou a mensagem que será enviada será um: Acertou
                   ou será um: Tente novamente.
                   Ou seja, esta variável auxilia o servidor a selecionar uma posição vizinha àquela onde ele havia acertado
                   anteriormente. Mesmo quando o servidor erra, a variável acertou continua valendo true caso o servidor tenha
                   acertado uma posição anteriormente e ainda não destruiu a embarcação. Ao destruir a embarcação, a variável
                   acertou, retorna ao seu valor inicial, ou seja, volta a valer false.
                */
                acertou = true;
                /* Caso o servidor tenha acertado uma posição correspondente ao porta-aviões
                */
                if(letraTiroAtual == 'P'){
                    contPortaAvioes++;
                }
                /* Caso o servidor tenha acertado uma posição correspondente a um dos navios-tanque
                */
                else if(letraTiroAtual == 'T'){
                    /* Caso a posição de ataque seja correspondente ao primeiro navio-tanque (tanque1)
                       indicamos que o tanque1 teve uma de suas partes destruídas
                    */
                    if(contTanque1 < 4 && Achou(tanque1,((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48))))){
                        contTanque1++;
                    }
                    /* Caso a posição de ataque seja correspondente ao segundo navio-tanque (tanque2)
                       indicamos que o tanque2 teve uma de suas partes destruídas
                    */
                    else if(contTanque2 < 4 && Achou(tanque2,((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48))))){
                        contTanque2++;
                    }
                }
                /* Caso o servidor tenha acertado uma posição correspondente a um dos contratorpedeiros
                */
                else if(letraTiroAtual == 'C'){
                    /* Caso a posição de ataque seja correspondente ao primeiro contratorpedeiro (contraTorped1)
                       indicamos que o contraTorped1 teve uma de suas partes destruídas
                    */
                    if(contContraTorped1 < 3 && Achou(contraTorped1,((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48))))){
                        contContraTorped1++;
                    }
                    /* Caso a posição de ataque seja correspondente ao segundo contratorpedeiro (contraTorped2)
                       indicamos que o contraTorped2 teve uma de suas partes destruídas
                    */
                    else if(contContraTorped2 < 3 && Achou(contraTorped2,((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48))))){
                        contContraTorped2++;
                    }
                    /* Caso a posição de ataque seja correspondente ao terceiro contratorpedeiro (contraTorped3)
                       indicamos que o contraTorped3 teve uma de suas partes destruídas
                    */
                    else if(contContraTorped3 < 3 && Achou(contraTorped3,((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48))))){
                        contContraTorped3++;
                    }
                }
                /* Caso o servidor tenha acertado uma posição correspondente a um dos submarinos
                */
                else if(letraTiroAtual == 'S'){
                    /* Caso a posição de ataque seja correspondente ao primeiro submarino (submarino1)
                       indicamos que o submarino1 teve uma de suas partes destruídas
                    */
                    if(contSubmarino1 < 2 && Achou(submarino1,((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48))))){
                        contSubmarino1++;
                    }
                    /* Caso a posição de ataque seja correspondente ao segundo submarino (submarino2)
                       indicamos que o submarino2 teve uma de suas partes destruídas
                    */
                    else if(contSubmarino2 < 2 && Achou(submarino2,((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48))))){
                        contSubmarino2++;
                    }
                    /* Caso a posição de ataque seja correspondente ao terceiro submarino (submarino3)
                       indicamos que o submarino3 teve uma de suas partes destruídas
                    */
                    else if(contSubmarino3 < 2 && Achou(submarino3,((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48))))){
                        contSubmarino3++;
                    }
                    /* Caso a posição de ataque seja correspondente ao quarto submarino (submarino4)
                       indicamos que o submarino4 teve uma de suas partes destruídas
                    */
                    else if(contSubmarino4 < 2 && Achou(submarino4,((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48))))){
                        contSubmarino4++;
                    }
                }
            }
            /* Caso o servidor, ao gerar aleatoriamente uma posição de ataque,
               ele novamente gere uma mesma posição e acerte uma posção de uma parte
               de uma embarcação que já foi destruída. Desta forma, apenas mantemos o símbolo O,
               significando que o servidor acertou, porém a mensagem que o servidor receberá é que
               ele errou o tiro. Pois nesta rodada, ele não acertou uma parte de uma embarcação
               que ainda não foi destruída.
            */
            else if(embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)]=='O'){
                letraTiroAtual = embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)];
                embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)]='O';
            }
            /* Caso o servidor erre o tiro, é atribuído o símbolo X à matriz do cliente. Indicando o erro do servior.
            */
            else{
                letraTiroAtual = embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)];
                embarcacoes[(int)(strComunicacao.charAt(0)-65)][(int)(strComunicacao.charAt(1)-48)]='X';
            }
            /* Se o servidor não acertar o tiro e anteriormente não havia acertado
               uma parte de uma embarcação, ou caso o servidor acerte uma posição
               de uma parte de uma embarcação que já havia sido gerada aleatoriamente,
               o servidor receberá a mensagem: Errou
            */
            if(!acertou){
                strComunicacao = "Errou";
            }
            /* Caso contrário, o servidor acertou uma nova parte de uma embarcação que ainda
               não havia sido destruída (mensagem: Acertou), ou ele acertou uma embarcação
               mas errou ao acertar uma das posições vizinhas (mensagem: Tente novamente),
               ou caso o servidor acerte a última posição da última parte da última embarcação
               restante (mensagem: Voce venceu).
            */
            else{
                /* Caso a posição atual contenha um caracter diferente letra do acerto inicial,
                   a mensagem a ser enviada ao servidor será: Tente novamente
                */
                if(letraPrimeiroTiro != letraTiroAtual){
                    strComunicacao = "Tente novamente";
                }
                /* Caso contrário, ele acertou uma nova parte ainda não destruída de uma embarcação,
                   mas ainda devemos analisar se com este tiro, a embarcação será destruída ou não
                */
                else{
                    /* Caso o servidor tenha acertado o porta-aviões, mas ainda não o destuiu por completo,
                       a mensagem a ser enviada será: Acertou
                    */
                    if((letraTiroAtual == 'P') && (contPortaAvioes < 5)){
                        strComunicacao = "Acertou";
                    }
                    /* Caso o servidor tenha acertado o porta-aviões e tenha destruído-o por completo,
                       a mensagem a ser enviada será: Destruiu embarcacao
                    */
                    else if((letraTiroAtual == 'P') && (contPortaAvioes == 5) && (!portaAvioesDestruido)){
                        strComunicacao = "Destruiu embarcacao";
                        letraPrimeiroTiro = ' ';
                        acertou = false;
                        portaAvioesDestruido = true;
                    }
                    /* Caso o servidor tenha acertado o primeiro navio-tanque, mas ainda não o destuiu por completo,
                       a mensagem a ser enviada será: Acertou
                    */
                    else if((letraTiroAtual == 'T') && (contTanque1 < 4) && (AchouTanque(((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48)))) == 1)){
                        strComunicacao = "Acertou";
                    }
                    /* Caso o servidor tenha acertado o primeiro navio-tanque e tenha destruído-o por completo,
                       a mensagem a ser enviada será: Destruiu embarcacao
                    */
                    else if((letraTiroAtual == 'T') && (contTanque1 == 4) && (!tanque1Destruido)){
                        strComunicacao = "Destruiu embarcacao";
                        letraPrimeiroTiro = ' ';
                        acertou = false;
                        tanque1Destruido = true;
                    }
                    /* Caso o servidor tenha acertado o segundo navio-tanque, mas ainda não o destuiu por completo,
                       a mensagem a ser enviada será: Acertou
                    */
                    else if((letraTiroAtual == 'T') && (contTanque2 < 4) && (AchouTanque(((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48)))) == 2)){
                        strComunicacao = "Acertou";
                    }
                    /* Caso o servidor tenha acertado o segundo navio-tanque e tenha destruído-o por completo,
                       a mensagem a ser enviada será: Destruiu embarcacao
                    */
                    else if((letraTiroAtual == 'T') && (contTanque2 == 4) && (!tanque2Destruido)){
                        strComunicacao = "Destruiu embarcacao";
                        letraPrimeiroTiro = ' ';
                        acertou = false;
                        tanque2Destruido = true;
                    }
                    /* Caso o servidor tenha acertado o primeiro contratorpedeiro, mas ainda não o destuiu por completo,
                       a mensagem a ser enviada será: Acertou
                    */
                    else if((letraTiroAtual == 'C') && (contContraTorped1 < 3) && (AchouContraTorpedeiro(((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48)))) == 1)){
                        strComunicacao = "Acertou";
                    }
                    /* Caso o servidor tenha acertado o primeiro contratorpedeiro e tenha destruído-o por completo,
                       a mensagem a ser enviada será: Destruiu embarcacao
                    */
                    else if((letraTiroAtual == 'C') && (contContraTorped1 == 3) && (!contraTorped1Destruido)){
                        strComunicacao = "Destruiu embarcacao";
                        letraPrimeiroTiro = ' ';
                        acertou = false;
                        contraTorped1Destruido = true;
                    }
                    /* Caso o servidor tenha acertado o segundo contratorpedeiro, mas ainda não o destuiu por completo,
                       a mensagem a ser enviada será: Acertou
                    */
                    else if((letraTiroAtual == 'C') && (contContraTorped2 < 3) && (AchouContraTorpedeiro(((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48)))) == 2)){
                        strComunicacao = "Acertou";
                    }
                    /* Caso o servidor tenha acertado o segundo contratorpedeiro e tenha destruído-o por completo,
                       a mensagem a ser enviada será: Destruiu embarcacao
                    */
                    else if((letraTiroAtual == 'C') && (contContraTorped2 == 3) && (!contraTorped2Destruido)){
                        strComunicacao = "Destruiu embarcacao";
                        letraPrimeiroTiro = ' ';
                        acertou = false;
                        contraTorped2Destruido = true;
                    }
                    /* Caso o servidor tenha acertado o terceiro contratorpedeiro, mas ainda não o destuiu por completo,
                       a mensagem a ser enviada será: Acertou
                    */
                    else if((letraTiroAtual == 'C') && (contContraTorped3 < 3) && (AchouContraTorpedeiro(((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48)))) == 3)){
                        strComunicacao = "Acertou";
                    }
                    /* Caso o servidor tenha acertado o terceiro contratorpedeiro e tenha destruído-o por completo,
                       a mensagem a ser enviada será: Destruiu embarcacao
                    */
                    else if((letraTiroAtual == 'C') && (contContraTorped3 == 3) && (!contraTorped3Destruido)){
                        strComunicacao = "Destruiu embarcacao";
                        letraPrimeiroTiro = ' ';
                        acertou = false;
                        contraTorped3Destruido = true;
                    }
                    /* Caso o servidor tenha acertado o primeiro submarino, mas ainda não o destuiu por completo,
                       a mensagem a ser enviada será: Acertou
                    */
                    else if((letraTiroAtual == 'S') && (contSubmarino1 < 2) && (AchouSubmarino(((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48)))) == 1)){
                        strComunicacao = "Acertou";
                    }
                    /* Caso o servidor tenha acertado o primeiro submarino e tenha destruído-o por completo,
                       a mensagem a ser enviada será: Destruiu embarcacao
                    */
                    else if((letraTiroAtual == 'S') && (contSubmarino1 == 2) && (!submarino1Destruido)){
                        strComunicacao = "Destruiu embarcacao";
                        letraPrimeiroTiro = ' ';
                        acertou = false;
                        submarino1Destruido = true;
                    }
                    /* Caso o servidor tenha acertado o segundo submarino, mas ainda não o destuiu por completo,
                       a mensagem a ser enviada será: Acertou
                    */
                    else if((letraTiroAtual == 'S') && (contSubmarino2 < 2) && (AchouSubmarino(((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48)))) == 2)){
                        strComunicacao = "Acertou";
                    }
                    /* Caso o servidor tenha acertado o segundo submarino e tenha destruído-o por completo,
                       a mensagem a ser enviada será: Destruiu embarcacao
                    */
                    else if((letraTiroAtual == 'S') && (contSubmarino2 == 2) && (!submarino2Destruido)){
                        strComunicacao = "Destruiu embarcacao";
                        letraPrimeiroTiro = ' ';
                        acertou = false;
                        submarino2Destruido = true;
                    }
                    /* Caso o servidor tenha acertado o terceiro submarino, mas ainda não o destuiu por completo,
                       a mensagem a ser enviada será: Acertou
                    */
                    else if((letraTiroAtual == 'S') && (contSubmarino3 < 2) && (AchouSubmarino(((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48)))) == 3)){
                        strComunicacao = "Acertou";
                    }
                    /* Caso o servidor tenha acertado o terceiro submarino e tenha destruído-o por completo,
                       a mensagem a ser enviada será: Destruiu embarcacao
                    */
                    else if((letraTiroAtual == 'S') && (contSubmarino3 == 2) && (!submarino3Destruido)){
                        strComunicacao = "Destruiu embarcacao";
                        letraPrimeiroTiro = ' ';
                        acertou = false;
                        submarino3Destruido = true;
                    }
                    /* Caso o servidor tenha acertado o quarto submarino, mas ainda não o destuiu por completo,
                       a mensagem a ser enviada será: Acertou
                    */
                    else if((letraTiroAtual == 'S') && (contSubmarino4 < 2) && (AchouSubmarino(((10*((int)(strComunicacao.charAt(0)-65))) + ((int)(strComunicacao.charAt(1)-48)))) == 4)){
                        strComunicacao = "Acertou";
                    }
                    /* Caso o servidor tenha acertado o quarto submarino e tenha destruído-o por completo,
                       a mensagem a ser enviada será: Destruiu embarcacao
                    */
                    else if((letraTiroAtual == 'S') && (contSubmarino4 == 2) && (!submarino4Destruido)){
                        strComunicacao = "Destruiu embarcacao";
                        letraPrimeiroTiro = ' ';
                        acertou = false;
                        submarino4Destruido = true;
                    }
                    /* Caso o contador de acertos atinja o final. Ou seja, caso o servidor vença
                       a mensagem a ser nviada será: Voce venceu
                    */
                    if(contaAcertos == 30){
                        strComunicacao = "Voce venceu";
                    }
                }
            }
            out.writeUTF(strComunicacao);
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
        
    }

    public void start(){
        String line = "";
        boolean fim = false;
        sc.nextLine();
        while(!line.equals("Fim") && !fim && contaAcertos < 30){
            try{
                System.out.println("Escolha uma letra e um número:\nOu digite P para ver as matrizes cliente e servidor:");
                line = sc.nextLine();
                out.writeUTF(line);
                //Mostra a matriz do cliente e matriz do servidor.
                if(line.equals("P")){
                    printaMatrizCliente();
                    printaMatrizAdversaria();
                }
                else if(!line.equals("Fim")){
                    /* Caso o cliente acerte a última posição da última parte da última embarcação
                       que restava para que vencesse o jogo, a variável fim conterá o valor true,
                       desta forma, o servidor não irá atacar, logo, não precisamos executar o método
                       lerComandoServidor.
                       Caso contrário, iremos processar o comando de ataque do servidor.
                    */
                    fim = printaRespostaServidor();
                    if(!fim){
                        lerComandoServidor();
                    }
                }
            }
            catch(IOException ioe){
                System.out.println(ioe);
            }
        }
        /* Caso o contAcertos seja igual a 30 (servidor venceu a partida), ou caso o usuário
           deseje encerrar o programa antes do fim, executamos a funçao Fim para receer e mostrar
           a mensagem que o servidor enviou
        */
        if(contaAcertos == 30 || line.equals("Fim")){
            fim();
        }
        //Encerramos as atividades do cliente
        close();
    }

    /* Método utilizado para encerramento das atividades do cliente.
    */
    public void close(){
        try{
            sc.close();
            br.close();
            in.close();
            out.close();
            socket.close();
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }
    }

    /* Método utilizado para ciar um arquivo de entrada com a formatação padrão. 
    */
    public static void criaArquivo(){
		File file=new File("batalhaNaval.txt");
		try{
			if(file.exists()){
				file.delete();
			}
			FileWriter fw=new FileWriter(file);
			BufferedWriter bw=new BufferedWriter(fw);
            bw.write("  | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |\n");
            for(int i=0;i<10;i++){
                bw.write('A'+i);
                bw.write(" |   |   |   |   |   |   |   |   |   |   |\n");
			}
			bw.close();
		}catch(Exception erro){}
    }
    
    /* Método utilizado para controlar se o usuário já realizou a alocação de suas embarcações
       nas posições desejadas.
    */
    public boolean leArquivo(){
        int i = 0, posicao;
        File file=new File("batalhaNaval.txt");
		String linha=null;
		try{
            /* Caso o usuário tenha excluído o arquivo sem querer, o programa irá gerar outro arquivo de entrada
            */
			if(!file.exists()){
                criaArquivo();
				return false;
            }
			FileReader fr = new FileReader(file);
            BufferedReader buffered=new BufferedReader(fr);
            linha=buffered.readLine();
			while((linha=buffered.readLine())!=null){
                /* A posição onde cada letra correspondente a cada embarcação se encontra, começa no
                   caracter 4 da linha do arquivo e caminha de 4 em 4 caracteres.
                */
                posicao = 4;
                for(int j=0;j<10;j++){
                    /* Alocação do porta-aviões
                    */
                    if(linha.charAt(posicao)=='P'||linha.charAt(posicao)=='p'){
                        //Salva as posições de onde se encontra o porta-aviões na matriz do cliente
                        portaAvioes[contPortaAvioes] = (10*i)+j;
                        embarcacoes[i][j]='P';
                        contPortaAvioes++;
                    }
                    /* Alocação dos navios-tanque
                    */
                    else if(linha.charAt(posicao)=='T'||linha.charAt(posicao)=='t'){
                        embarcacoes[i][j]='T';
                        /* Se o contTanque1 estiver em 0, significa que é a primeira letra T que o programa
                           identificou do tanque1, logo ele irá atribuir essa posição para o tanque1.
                           Caso ele já tenha alocado uma posição para o tanque1, ele verifica se a posição à esquerda
                           da posição atual corresponde ao tanque1, ou seja, se o tanque1 está na horizntal. 
                        */
                        if((contTanque1 == 0) || ((contTanque1 < 4) && Achou(tanque1,((10*i)+j-1)))){
                            //Salva as posições de onde se encontra o primeiro navio-tanque na matriz do cliente
                            tanque1[contTanque1] = (10*i)+j;
                            contTanque1++;
                        }
                        /* Caso contrário, ele verifica se a posição acima da posição atual corresponde ao tanque1,
                           ou seja, se o tanque1 está na vertical.
                        */
                        else if(((contTanque1 < 4) && Achou(tanque1,((10*i)+j-10)))){
                            //Salva as posições de onde se encontra o primeiro navio-tanque na matriz do cliente
                            tanque1[contTanque1] = (10*i)+j;
                            contTanque1++;
                        }
                        /* Caso contrário, o programa verifica se o contTanque2 está em 0, significando que ele encontrou
                           a primeira letra T correspondente ao tanque2, logo ele irá atribuir essa posição para o tanque2.
                           Caso ele já tenha alocado uma posição para o tanque2, ele verifica se a posição à esquerda
                           da posição atual corresponde ao tanque2, ou seja, se o tanque2 está na horizntal. 
                        */
                        else if((contTanque2 == 0) || ((contTanque2 < 4) && Achou(tanque2,((10*i)+j-1)))){
                            //Salva as posições de onde se encontra o segundo navio-tanque na matriz do cliente
                            tanque2[contTanque2] = (10*i)+j;
                            contTanque2++;
                        }
                        /* Caso contrário, ele verifica se a posição acima da posição atual corresponde ao tanque2,
                           ou seja, se o tanque2 está na vertical.
                        */
                        else if(((contTanque2 < 4) && Achou(tanque2,((10*i)+j-10)))){
                            //Salva as posições de onde se encontra o segundo navio-tanque na matriz do cliente
                            tanque2[contTanque2] = (10*i)+j;
                            contTanque2++;
                        }
                    }
                    /* Alocação dos contratorpedeiros
                    */
                    else if(linha.charAt(posicao)=='C'||linha.charAt(posicao)=='c'){
                        embarcacoes[i][j]='C';
                        /* Se o contContraTorped1 estiver em 0, significa que é a primeira letra C que o programa
                           identificou do contraTorped1, logo ele irá atribuir essa posição para o contraTorped1.
                           Caso ele já tenha alocado uma posição para o contraTorped1, ele verifica se a posição à esquerda
                           da posição atual corresponde ao contraTorped1, ou seja, se o contraTorped1 está na horizntal. 
                        */
                        if((contContraTorped1 == 0) || ((contContraTorped1 < 3) && Achou(contraTorped1,((10*i)+j-1)))){
                            //Salva as posições de onde se encontra o primeiro contratorpedeiro na matriz do cliente
                            contraTorped1[contContraTorped1] = (10*i)+j;
                            contContraTorped1++;
                        }
                        /* Caso contrário, ele verifica se a posição acima da posição atual corresponde ao contraTorped1,
                           ou seja, se o contraTorped1 está na vertical.
                        */
                        else if(((contContraTorped1 < 3) && Achou(contraTorped1,((10*i)+j-10)))){
                            //Salva as posições de onde se encontra o primeiro contratorpedeiro na matriz do cliente
                            contraTorped1[contContraTorped1] = (10*i)+j;
                            contContraTorped1++;
                        }
                        /* Caso contrário, o programa verifica se o contContraTorped2 está em 0, significando que ele encontrou
                           a primeira letra C correspondente ao contraTorped2, logo ele irá atribuir essa posição para o contraTorped2.
                           Caso ele já tenha alocado uma posição para o contraTorped2, ele verifica se a posição à esquerda
                           da posição atual corresponde ao contraTorped2, ou seja, se o contraTorped2 está na horizntal. 
                        */
                        else if((contContraTorped2 == 0) || ((contContraTorped2 < 3) && Achou(contraTorped2,((10*i)+j-1)))){
                            //Salva as posições de onde se encontra o segundo contratorpedeiro na matriz do cliente
                            contraTorped2[contContraTorped2] = (10*i)+j;
                            contContraTorped2++;
                        }
                        /* Caso contrário, ele verifica se a posição acima da posição atual corresponde ao contraTorped2,
                           ou seja, se o contraTorped2 está na vertical.
                        */
                        else if(((contContraTorped2 < 3) && Achou(contraTorped2,((10*i)+j-10)))){
                            //Salva as posições de onde se encontra o segundo contratorpedeiro na matriz do cliente
                            contraTorped2[contContraTorped2] = (10*i)+j;
                            contContraTorped2++;
                        }
                        /* Caso contrário, o programa verifica se o contContraTorped3 está em 0, significando que ele encontrou
                           a primeira letra C correspondente ao contraTorped3, logo ele irá atribuir essa posição para o contraTorped3.
                           Caso ele já tenha alocado uma posição para o contraTorped3, ele verifica se a posição à esquerda
                           da posição atual corresponde ao contraTorped3, ou seja, se o contraTorped3 está na horizntal. 
                        */
                        else if((contContraTorped3 == 0) || ((contContraTorped3 < 3) && Achou(contraTorped3,((10*i)+j-1)))){
                            //Salva as posições de onde se encontra o terceiro contratorpedeiro na matriz do cliente
                            contraTorped3[contContraTorped3] = (10*i)+j;
                            contContraTorped3++;
                        }
                        /* Caso contrário, ele verifica se a posição acima da posição atual corresponde ao contraTorped3,
                           ou seja, se o contraTorped3 está na vertical.
                        */
                        else if(((contContraTorped3 < 3) && Achou(contraTorped3,((10*i)+j-10)))){
                            //Salva as posições de onde se encontra o terceiro contratorpedeiro na matriz do cliente
                            contraTorped3[contContraTorped3] = (10*i)+j;
                            contContraTorped3++;
                        }
                    }
                    /* Alocação dos submarinos
                    */
                    else if(linha.charAt(posicao)=='S'||linha.charAt(posicao)=='s'){
                        embarcacoes[i][j]='S';
                        /* Se o contSumarino1 estiver em 0, significa que é a primeira letra S que o programa
                           identificou do submarino1, logo ele irá atribuir essa posição para o submarino1.
                           Caso ele já tenha alocado uma posição para o submarino1, ele verifica se a posição à esquerda
                           da posição atual corresponde ao submarino1, ou seja, se o submarino1 está na horizntal. 
                        */
                        if((contSubmarino1 == 0) || (contSubmarino1 < 2 && Achou(submarino1,((10*i)+j-1)))){
                            submarino1[contSubmarino1] = (10*i)+j;
                            contSubmarino1++;
                        }
                        /* Caso contrário, ele verifica se a posição acima da posição atual corresponde ao submarino1,
                           ou seja, se o submarino1 está na vertical.
                        */
                        else if((contSubmarino1 < 2 && Achou(submarino1,((10*i)+j-10)))){
                            submarino1[contSubmarino1] = (10*i)+j;
                            contSubmarino1++;
                        }
                        /* Caso contrário, o programa verifica se o contSubmarino2 está em 0, significando que ele encontrou
                           a primeira letra S correspondente ao submarino2, logo ele irá atribuir essa posição para o submarino2.
                           Caso ele já tenha alocado uma posição para o submarino2, ele verifica se a posição à esquerda
                           da posição atual corresponde ao submarino2, ou seja, se o submarino2 está na horizntal. 
                        */
                        else if((contSubmarino2 == 0) || (contSubmarino2 < 2 && Achou(submarino2,((10*i)+j-1)))){
                            submarino2[contSubmarino2] = (10*i)+j;
                            contSubmarino2++;
                        }
                        /* Caso contrário, ele verifica se a posição acima da posição atual corresponde ao submarino2,
                           ou seja, se o submarino2 está na vertical.
                        */
                        else if((contSubmarino2 < 2 && Achou(submarino2,((10*i)+j-10)))){
                            submarino2[contSubmarino2] = (10*i)+j;
                            contSubmarino2++;
                        }
                        /* Caso contrário, o programa verifica se o contSubmarino3 está em 0, significando que ele encontrou
                           a primeira letra S correspondente ao submarino3, logo ele irá atribuir essa posição para o submarino3.
                           Caso ele já tenha alocado uma posição para o submarino3, ele verifica se a posição à esquerda
                           da posição atual corresponde ao submarino3, ou seja, se o submarino3 está na horizntal. 
                        */
                        else if((contSubmarino3 == 0) || (contSubmarino3 < 2 && Achou(submarino3,((10*i)+j-1)))){
                            submarino3[contSubmarino3] = (10*i)+j;
                            contSubmarino3++;
                        }
                        /* Caso contrário, ele verifica se a posição acima da posição atual corresponde ao submarino3,
                           ou seja, se o submarino3 está na vertical.
                        */
                        else if((contSubmarino3 < 2 && Achou(submarino3,((10*i)+j-10)))){
                            submarino3[contSubmarino3] = (10*i)+j;
                            contSubmarino3++;
                        }
                        /* Caso contrário, o programa verifica se o contSubmarino4 está em 0, significando que ele encontrou
                           a primeira letra S correspondente ao submarino4, logo ele irá atribuir essa posição para o submarino4.
                           Caso ele já tenha alocado uma posição para o submarino4, ele verifica se a posição à esquerda
                           da posição atual corresponde ao submarino4, ou seja, se o submarino4 está na horizntal. 
                        */
                        else if((contSubmarino4 == 0) || (contSubmarino4 < 2 && Achou(submarino4,((10*i)+j-1)))){
                            submarino4[contSubmarino4] = (10*i)+j;
                            contSubmarino4++;
                        }
                        /* Caso contrário, ele verifica se a posição acima da posição atual corresponde ao submarino4,
                           ou seja, se o submarino4 está na vertical.
                        */
                        else if((contSubmarino4 < 2 && Achou(submarino4,((10*i)+j-10)))){
                            submarino4[contSubmarino4] = (10*i)+j;
                            contSubmarino4++;
                        }
                    }
                    /* Caso nenhuma letra seja identificada, é atribuído espaço em branco na matriz de embarcações
                    */
                    else{
                        embarcacoes[i][j]=' ';
                    }
                    posicao = posicao + 4;
                }
                i++;
            }
            contPortaAvioes = 0;
            contTanque1 = 0;
            contTanque2 = 0;
            contContraTorped1 = 0;
            contContraTorped2 = 0;
            contContraTorped3 = 0;
            contSubmarino1 = 0;
            contSubmarino2 = 0;
            contSubmarino3 = 0;
            contSubmarino4 = 0;
            buffered.close();
		}catch(IOException e){
			e.printStackTrace();
        }
        return true;
    }

    public static void main(String args[]){
        new Client("localhost",5000);
    }
}