import models.ArvoreB;
import models.No;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.concurrent.ThreadLocalRandom;

public class EP1 {
    private static ArvoreB arvore;
    private static final String nomeArquivo = "arvore.txt";

    public static void main(String[] args) throws Exception {
        deletarArquivo();
        arvore = new ArvoreB(nomeArquivo, 50);
        testeInsercao(100000, true, 1, 500000);
    }

    public static void deletarArquivo() {
        File file = new File(nomeArquivo);
        file.delete();
    }

    public static void testeInsercao(int quantidade, boolean random, int min, int max) throws Exception {
        int i = 0;
        while (i < quantidade) {
            int ins = i;
            if (random) ins = ThreadLocalRandom.current().nextInt(min, max);
            inserir(ins);
            i++;
        }
    }

    public static void percorrer() throws IOException {
        arvore.percorrer();
    }

    public static void inserir(int chave) throws Exception {
        arvore.inserir(chave);
    }

    public static void buscar(int chave) throws IOException {
        No no = arvore.buscar(chave);
        if (no != null) {
            no.percorrer();
        } else {
            System.out.println("NÃO ENCONTRADO!");
        }
        System.out.println();
    }
}
