package models;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

public class ArvoreB {

    private static final int NULL = -1;
    private static final int TRUE = 1;
    private static final int FALSE = 0;
    private static final int TAMANHO_CABECALHO = 4;

    private String nomeArquivo;

    public int TAMANHO_NO_INTERNO;
    public int TAMANHO_NO_FOLHA;
    public int NUM_MAX_CHAVES;
    public int NUM_MAX_FILHOS;

    /**
     * Objeto do tipo No apontando para a raiz da arvore B
     */
    public No raiz;

    /**
     * Int contendo o grau da arvore B
     * (necessário para saber o tamanho máximo dos nos - número max. de chaves e número max de filhos
     */
    public int grau;

    public void inicializarConstantes(int grau) {
        this.grau = grau;
        NUM_MAX_CHAVES = 2 * grau - 1;
        NUM_MAX_FILHOS = NUM_MAX_CHAVES + 1;
        TAMANHO_NO_INTERNO = TAMANHO_CABECALHO * 2 + TAMANHO_CABECALHO * NUM_MAX_CHAVES + TAMANHO_CABECALHO * (2 * grau);
        TAMANHO_NO_FOLHA = TAMANHO_CABECALHO * 2 + TAMANHO_CABECALHO * NUM_MAX_CHAVES;
    }

    /**
     * Construtor padrão da classe ArvoreB
     */
    public ArvoreB() {}

    /**
     * Construtor da classe ArvoreB
     * @param grau: int contendo o valor do grau da arvore B
     */
    public ArvoreB(String nomeArquivo, int grau) throws IOException {
        /**
         * Inicializa as variáveis da Classe
         */
        this.nomeArquivo = nomeArquivo;

        inicializarConstantes(grau);

        if (!new File(nomeArquivo).exists()) {
            No no = new No(this, TRUE);
            no.endereco = TAMANHO_CABECALHO;
            try {
                trocaRaiz(no);
            } catch (Exception e) {
                System.out.println("LANÇANDO EXCEÇÃO");
            }
            atualizaNo(no);
        } else {
            carregaRaizNaRAM();
        }
    }

    public No leNo(int endereco) throws IOException {
        RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "r");
        if (arquivo.length() == 0 || endereco == NULL) {
            arquivo.close();
            return null;
        }

        arquivo.seek(endereco);
        int iFolha = arquivo.readInt();
        boolean folha = iFolha == TRUE;
        byte[] bytes = folha ? new byte[TAMANHO_NO_FOLHA - TAMANHO_CABECALHO] : new byte[TAMANHO_NO_INTERNO - TAMANHO_CABECALHO];
        arquivo.read(bytes);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        No no = new No(this, folha ? TRUE : FALSE);
        no.numeroDeChaves = leInt(inputStream);
        no.endereco = endereco;

        for (int i = 0; i < no.chaves.length; i++) no.chaves[i] = leInt(inputStream);

        if (!folha) {
            for (int i = 0; i < no.filhos.length; i++) no.filhos[i] = leInt(inputStream);
        }

        arquivo.close();
        return no;
    }

    private void trocaRaiz(No no) throws FileNotFoundException, IOException {
        RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "rw");
        this.raiz = no;
        arquivo.writeInt(this.raiz.endereco);
        arquivo.close();
    }

    private void carregaRaizNaRAM() throws FileNotFoundException, IOException {
        RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "r");
        this.raiz = leNo(arquivo.readInt());
        arquivo.close();
    }

    public void atualizaNo(No no) throws IOException {
        int numeroBytes = no.folha == TRUE ? TAMANHO_NO_FOLHA : TAMANHO_NO_INTERNO;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(numeroBytes);

        escreveInt(outputStream, no.folha);
        escreveInt(outputStream, no.numeroDeChaves);

        for (int i = 0; i < no.chaves.length; i++) {
            escreveInt(outputStream, no.chaves[i]);
        }

        if (no.folha == FALSE) {
            for (int i = 0; i < no.chaves.length + 1; i++) {
                escreveInt(outputStream, no.filhos[i]);
            }
        }
        RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "rw");
        arquivo.seek(no.endereco);
        arquivo.write(outputStream.toByteArray());
        arquivo.close();
    }

    private void escreveInt(ByteArrayOutputStream outputStream, int i) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(i);

        byte[] num = buffer.array();

        outputStream.write(num, 0, 4);
    }

    private int leInt(ByteArrayInputStream inputStream) {
        byte[] byteInt = new byte[TAMANHO_CABECALHO];
        int readByte = inputStream.read(byteInt, 0, TAMANHO_CABECALHO);
        int r = ByteBuffer.wrap(byteInt).asIntBuffer().get();
        return r;
    }

    public void gravaNovoNo(No no) throws IOException {
        no.endereco = (int) new File(nomeArquivo).length();
        atualizaNo(no);
    }

    /**
     * Metodo para inserção de chaves na arvore.
     * @param chave: int contendo a chave que deve ser inserida
     */
    public void inserir(int chave) throws Exception {
        // Cria e inicializa uma variável do tipo No, apontando para a raiz da arvore B
        No no = this.raiz;

        // Entra nesse "if" caso o número de chaves da raiz seja igual ao valor máximo (2 * grau da arvore - 1)
        if (no.numeroDeChaves == 2 * this.grau - 1) {
            // Cria e instancia um novo no que será a nova raiz da arvore
            No novoNo = new No(this, FALSE);
            gravaNovoNo(novoNo);
            // Aponta a raiz da arvore para o no criado
            trocaRaiz(novoNo);
            raiz = novoNo;

            // no criado recebe no como filho (antiga raiz da arvore B)
            novoNo.filhos[0] = no.endereco;

            // O no (antiga raiz da arvore B) e dividido em dois
            novoNo.dividir(0, no);

            // A chave (que não cabia na antiga raiz) e inserido no no recem criado
            novoNo.inserirNaoCheio(chave);
            atualizaNo(novoNo);
        } else {
            // Entra nesse "else" caso exista espaço disponível no arranjo de chaves da raiz
            no.inserirNaoCheio(chave);
            atualizaNo(no);
        }
    }

    /**
     * Metodo auxiliar para percorrer os nos da arvore B e imprimir os valores de chave presentes em cada no
     */
    public void percorrer() throws IOException {
        if (this.raiz != null) this.raiz.percorrer();
    }

    /**
     * Metodo de busca da arvore B: recebe uma chave e, caso a raiz não seja nula,
     * começa uma busca recursiva do no que deve conter a chave buscada.
     *
     * @param chave: int contendo a chave sendo buscada na arvore B
     * @return o no que contem a chave buscada caso exista, null caso contrário
     */
    public No buscar(int chave) throws IOException {
        if (this.raiz != null) return this.raiz.buscar(chave);
        return null;
    }
}
