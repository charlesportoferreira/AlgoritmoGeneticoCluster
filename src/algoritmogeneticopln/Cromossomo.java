/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmogeneticopln;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class Cromossomo {

    private List<Gene> genes;
    private double fitness;
    Resultado resultado;
    public String baseDados;
    public String memoria;
    public List<String> fileNames = new ArrayList<>();
    public List<String> filePaths = new ArrayList<>();
    public int nrGenes;

    public Cromossomo(int nrBits) {
        nrGenes = nrBits;
        genes = new ArrayList<>(nrBits);
        criaGenes();
    }

    public Cromossomo() {
        genes = new ArrayList<>();
    }

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public void calculaFitness() {
        Resultado res;
        res = executaProgramas();
        this.resultado = res;
        double f = Double.parseDouble(res.getPorcentagemAcertos());
        if (Double.isNaN(f)) {
            f = 0.0;
        }
        System.out.println("   " + f + " : " + res.getNumeroAtributos());
        this.resultado.setFitness(f);
        this.setFitness(f);
    }

    public void calculaFitnessRandomico() {
        Random random = new Random();
        int fitnessRandomico = random.nextInt(100 - 0 + 1) + 0;
        this.setFitness(fitnessRandomico);
    }

    private Resultado executaProgramas() {
        executaWeka();
        Resultado res = leResultado();
        return res;
    }

    private void executaWeka() {
        // executaPrograma("cd discover", "saidaWeka.txt", "erroWeka.txt");
        String comandoWeka = "java -jar wekaExecutor.jar hi -m " + memoria + " -c 1";
        //System.out.println(comandoWeka);
        executaPrograma(comandoWeka, "saidaWeka.txt", "erroWeka.txt");
    }

    private Resultado leResultado() {
        String diretorio = System.getProperty("user.dir");

        fileTreePrinter(new File(diretorio), 0);
        //System.out.println("**********************************************");
        Resultado res = new Resultado();
        for (int i = 0; i < fileNames.size(); i++) {
            if (fileNames.get(i).contains(".arff.txt")) {
                res.setNomeClassificador(fileNames.get(i).split("_")[0]);
                res.setNomeTeste(fileNames.get(i).split("_")[1].replace(".arff.txt", ""));
                String pathArquivoLog = diretorio + "/logs/arquivo.arfferro.txt";
                res.setNumeroAtributos((getNumeroAtributos(pathArquivoLog)));
                res.setMinMaxFreq(getMinMaxFreq(pathArquivoLog));
                try {
                    res = getPredictions(filePaths.get(i), res);
                } catch (IOException ex) {
                    Logger.getLogger(Cromossomo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }

    private void criaGenes() {
        for (int i = 0; i < nrGenes; i++) {
            genes.add(new Gene());
        }
    }

    @Override
    public String toString() {
        return "Cromossomo{" + "genes=" + genes + '}';
    }

    public static void executaPrograma(String comando, String arquivoSaida, String arquivoErro) {
//                if (args.length < 1)
//        {
//            System.out.println("USAGE java GoodWinRedirect <outputfile>");
//            System.exit(1);
//        }

        try {
            //FileOutputStream fos = new FileOutputStream(args[0]);
            FileOutputStream fosError = new FileOutputStream(arquivoErro);
            FileOutputStream fosExit = new FileOutputStream(arquivoSaida);

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(comando);

            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "E--", fosError);

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "O--", fosExit);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            // any error???
            int exitVal = proc.waitFor();
            //System.out.println("ExitValue: " + exitVal);
            // fos.flush();
            // fos.close();        
        } catch (IOException | InterruptedException t) {
            t.printStackTrace();
        }
    }

    public List<String> fileTreePrinter(File initialPath, int initialDepth) {

        int depth = initialDepth++;
        if (initialPath.exists()) {
            File[] contents = initialPath.listFiles();
            for (File content : contents) {
                if (content.isDirectory()) {
                    fileTreePrinter(content, initialDepth + 1);
                } else {
                    char[] dpt = new char[initialDepth];
                    for (int j = 0; j < initialDepth; j++) {
                        dpt[j] = '+';
                    }

                    //System.out.println(new String(dpt) + content.getName() + " " + content.getPath());
                    // System.out.println(content.toString());
                    //System.out.println(content.getName());
                    fileNames.add(content.getName());
                    filePaths.add(content.toString());
                }
            }
        }
        return filePaths;
    }

    public static Resultado getPredictions(String filePath, Resultado resultado) throws FileNotFoundException, IOException {
        // StringBuilder linha = new StringBuilder();
        int anterior = 0;
        int atual = 0;

        List<Fold> folds = new ArrayList<>();
        String linha;
        try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linha = br.readLine();

                if (linha.contains("error prediction")) {
                    Fold fold = new Fold();
                    while (br.ready()) {
                        linha = br.readLine();
                        if (!linha.equals("")) {
                            String[] palavras = linha.split(" ");
                            for (String palavra : palavras) {
                                if (palavra.matches("[0-9]+")) {
                                    atual = Integer.valueOf(palavra);
                                    break;
                                }
                            }

                            if (atual < anterior) {
                                folds.add(fold);
                                fold = new Fold();
                                anterior = atual;
                            } else {
                                anterior = atual;
                            }
                            fold.addInstancia();
                            if (linha.contains("+")) {
                                fold.addErro();
                            }
                        } else {
                            folds.add(fold);
                            break;
                        }

                    }
                }

            }
            resultado.setFolds(folds);
            //System.out.println(resultado.getQtdeAcertos() + " " + resultado.getPorcentagemAcertos() + " " + resultado.getQtdeErros() + " " + resultado.getPorcentagemErros());
            br.close();
            fr.close();
        }
        return resultado;
    }

    public int getNumeroAtributos(String filePath) {
        //filePath = "C50.arfferro.txt";
        String linha;
        int numeroAtributos = 0;
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
                String str;
                while (in.ready()) {
                    str = in.readLine();
                    if (str.contains("Number of Stems")) {
                        str = str.replace("Number of Stems                                              ", "");
                        numeroAtributos += Integer.parseInt(str);
                        //return str;
                    }
                    //System.out.println("***Show****");
                    //System.exit(0);
                    //process(str);
                }
            }
        } catch (IOException e) {
            //System.out.println(e)
        }

        return numeroAtributos;
    }

    public String getMinMaxFreq(String filePath) {
        //filePath = "C50.arfferro.txt";
        String linha;
        String min = "";
        String max = "";
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
                String str;
                while (in.ready()) {
                    str = in.readLine();
                    if (str.contains("std_dev min")) {
                        str = str.replace("Number of Stems                                              ", "");
                        min = str;
                        // return str;
                    }
                    if (str.contains("max freq")) {
                        str = str.replace("Number of Stems                                              ", "");
                        max = str;
                        return min + "-" + max;
                    }
                    //System.out.println("***Show****");
                    //System.exit(0);
                    //process(str);
                }
            }
        } catch (IOException e) {
        }

        return "0";
    }


}
