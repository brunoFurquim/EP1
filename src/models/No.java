package models;

import java.io.IOException;
import java.util.Arrays;

/**
 * Classe dos nos da arvore-B
 */
public class No {
    private static final int NULL = -1;
    private static final int TRUE = 1;
    private static final int FALSE = 0;

    public int endereco;

    public ArvoreB arvore;

    /**
     * Grau da arvore B, define o tamanho maximo de chaves e filhos do no
     */
    int grau;
    /**
     * Numero de chaves presentes no arranjo de chaves,
     * as chaves a serem consideradas sao apenas do indice 0 ate o indice (numeroDeChaves - 1)
     */
    public int numeroDeChaves;
    /**
     * Arranjo de chaves do no (do tipo int)
     * Numero maximo de chaves igual a: (grau * 2) - 1
     */
    public int[] chaves;
    /**
     * Arranjo de nos filhos do no atual
     * Numero de filhos = numero de chaves + 1 ou (grau * 2)
     */
    public int[] filhos;
    /**
     * Flag para informar se o no e uma folha da arvore ou não (do tipo boolean)
     */
    int folha;

    /**
     * Construtor padrao da classe No
     */
    public No() {}

    /**
     * Construtor da classe No
     *
     * @param folha: flag informando se o no e uma folha ou nao
     */
    public No(ArvoreB arvore, int folha) {
        this.arvore = arvore;
        // Inicializa as variaveis com os parametros passados para o construtor
        this.grau = arvore.grau;
        this.folha = folha;

        // Inicializa o arranjo de chaves com o tamanho maximo de grau * 2 - 1
        this.chaves = new int[arvore.NUM_MAX_CHAVES];

        // No momento de inicializacao, ha 0 chaves no no
        this.numeroDeChaves = 0;

        if (folha == FALSE) {
            filhos = new int[arvore.NUM_MAX_FILHOS];
            Arrays.fill(filhos, NULL);
        }
    }

    /**
     * O metodo dividir e um metodo auxiliar.
     * Divide o no "no", que e filho do no (this). Ocupa a posição 'indice' do arranjo this.filhos
     *
     * Esse metodo so e chamado quando o no filho "no" esta cheio, ou seja,
     * seu nemero de chaves e igual ao grau da arvore * 2 - 1.
     *
     *
     * @param indice: int indicando o indice do no filho do no que chama a funcao
     * @param no: No filho do no chamando a funcao
     */
    public void dividir(int indice, No no) throws IOException {
        /**
         * Cria e inicializa um novo no com o mesmo grau do no atual (this)
         * Se o no sendo dividido for folha, então esse no tambem será.
         * Senão, não será.
         */
        No novoNo = new No(arvore, no.folha);
        arvore.gravaNovoNo(novoNo);

        // Número de chaves do novo no e igual ao grau da arvore - 1
        novoNo.numeroDeChaves = this.grau - 1;

        /**
         * O laço "for" abaixo copia as chaves do no sendo dividido para o novo no
         * (da posição "this.grau" ate a posição "this.grau - 2" - última posição do arranjo de chaves)
         */
        for (int i = 0; i < this.grau - 1; i++) novoNo.chaves[i] = no.chaves[i + this.grau];

        // Se o no não for folha, significa que ele tem filhos, logo:
        if (no.folha == FALSE) {
            /**
             * O laço "for" abaixo copia os filhos do no sendo dividido para o novo no
             * (da posição "this.grau" ate a posição "this.grau - 1" - a última posição do arranjo de filhos)
             */
            for (int i = 0; i < this.grau; i++) novoNo.filhos[i] = no.filhos[i + this.grau];
        }

        // Reduz o número de chaves no no sendo dividido
        no.numeroDeChaves = this.grau - 1;

        /**
         * O laço "for" abaixo abre espaço no arranjo de filhos do no atual (this) para receber o novo no.
         * Faz isso deslocando os filhos para a esquerda (ate uma posição após o no sendo dividido, que e "indice")
         */
        for (int i = this.numeroDeChaves; i >= indice + 1; i--) this.filhos[i + 1] = this.filhos[i];

        // Insere o novo no na lista de filhos do no atual (this)
        this.filhos[indice + 1] = novoNo.endereco;

        /**
         * O laço "for" abaixo pega a localização da chave sendo inserida (que causou a divisão)
         * e desloca todas as chaves maiores um espaço para a esquerda
         */
        for (int i = this.numeroDeChaves - 1; i >= indice; i--) this.chaves[i + 1] = this.chaves[i];

        // Copia a chave media do no filho (passado como parâmetro, que e o no sendo dividido) para o no atual (this)
        this.chaves[indice] = no.chaves[this.grau - 1];
        // Aumenta o número de chaves do no atual (this)
        this.numeroDeChaves++;
        arvore.atualizaNo(this);
        arvore.atualizaNo(no);
        arvore.atualizaNo(novoNo);
    }

    /**
     * O metodo inserirNaoCheio e um metodo auxiliar.
     * Insere a chave passada como parâmetro no arranjo de chaves do no atual (this) contanto que esse não esteja cheio e seja uma folha
     *
     * Caso não seja uma folha, insere no no filho que deve receber a chave.
     *
     * @param chave: int indicando a chave que será inserida
     */
    public void inserirNaoCheio(int chave) throws Exception {
        // Caso seja folha, insere no arranjo de chaves
        if (this.folha == TRUE) {
            /**
             * O laço "while" abaixo percorre o arranjo de chaves
             * do no atual (this) e acha a posição em que a chave deve ser inserida (ordenada de forma crescente)
             */
            int i = this.numeroDeChaves - 1;
            while (i >= 0 && chave < this.chaves[i]) {
                this.chaves[i + 1] = this.chaves[i];
                i--;
            }

            // A chave e inserida à direita da posição encontrada
            this.chaves[i + 1] = chave;

            // O número de chaves do no atual (this) e incrementado em um.
            this.numeroDeChaves++;
            arvore.atualizaNo(this);
        } else {
            // Caso não seja folha:

            /**
             * O laço "while" abaixo acha o filho que receberá a nova chave
             */
            int i = this.numeroDeChaves - 1;
            while (i >= 0 && chave < this.chaves[i]) i--;

            /**
             *  A posição do filho que receberá a nova chave e uma após a posição encontrada,
             *  portanto, a posição e incrementada em um.
              */
            i++;

            /**
             * Cria e inicializa um no apontando para o filho do no atual (this)
             * que receberá a chave (posiçao i, encontrada acima)
             */
            No no = arvore.leNo(this.filhos[i]);

            /**
             * Caso o filho que receberá a chave esteja cheio (número de nos igual ao grau da arvore * 2 - 1)
             * o no filho será dividido e o no atual (this) receberá a chave mediana pós-inserção
             */
            if (no.numeroDeChaves == 2 * this.grau - 1) {
                // Divide o no que deve receber a chave
                this.dividir(i, no);

                /**
                 * O bloco condicional "if" abaixo checa se a chave a ser inserida
                 * e maior que a chave na posição do filho (i).
                 * Se for maior, o filho que receberá a chave e o que compoe a segunda metade do antigo no
                 * Se for menor, o filho que receberá a chave e o que compoe a primeira metade do antigo no
                 */
                if (chave > this.chaves[i]) i++;
            }
            /**
             * Insere a chave no filho recem criado após a divisão (esquerdo ou direito)
             */
            No filhoI = arvore.leNo(this.filhos[i]);
            filhoI.inserirNaoCheio(chave);
            arvore.atualizaNo(this);
        }
    }

    /**
     * Percorre o no, imprimindo todas as chaves presentes (começando pelas folhas).
     * e feito de forma recursiva.
     */
    public void percorrer() throws IOException {
        this.imprimir();
        int i = 0;
        while (i < this.numeroDeChaves) {
            if (this.folha == FALSE) arvore.leNo(this.filhos[i]).percorrer();
            i++;
        }
        if (this.folha == FALSE) arvore.leNo(this.filhos[i]).percorrer();
    }

    public void imprimir() {
        if (this == arvore.raiz) {
            System.out.print("RAIZ [");
        } else if (this.folha == FALSE) {
            System.out.print("NO INTERNO [");
        } else {
            System.out.print("FOLHA [");
        }
        int i = 0;
        while (i < this.numeroDeChaves - 1) System.out.print(this.chaves[i++] + ", ");
        System.out.println(this.chaves[i] + "]");
    }

    /**
     * Metodo de busca de chaves no no
     * @param chave: int representando a chave que está sendo buscada no no
     * @return No: retorna null se não encontrar, retorna o objeto do tipo No em que a chave está armazenada caso encontre.
     */
    public No buscar(int chave) throws IOException {
        /**
         * O laço "while" abaixo acha a posição em que a chave estaria no arranjo de chaves do no sendo buscado
         */
        int i = 0;
        while (i < this.numeroDeChaves && chave > this.chaves[i]) i++;

        // Caso a chave na posição achada seja a chave buscada, retorna o no atual
        if (i < this.grau * 2 - 1 && this.chaves[i] == chave) return this;
        // Caso seja uma folha e a chave não exista no arranjo de chaves, retorna null pois não estará em nenhum outro lugar
        if (this.folha == TRUE) return null;

        // Caso não seja folha, continua procurando de forma recursiva no filho do no atual (this) em que a chave deve estar
        return arvore.leNo(this.filhos[i]).buscar(chave);
    }
}
