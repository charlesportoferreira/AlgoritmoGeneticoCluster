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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class Cromossomo implements Serializable {

    private List<Gene> genes;
    private double fitness;
    Resultado resultado;
    public String baseDados;
    public String memoria;
    public List<String> fileNames = new ArrayList<>();
    public List<String> filePaths = new ArrayList<>();

    public Cromossomo(int nrBits) {
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
        CromossomoFacade cf = new CromossomoFacade(bancoDados);
        List<Cromossomo> lc = cf.find(getConfigGenes(), "Cromossomo", "configGenes");
        //List<Cromossomo> lc = cf.find("1.3,1,0,1,1,0,1,1,0,0,1,1,0,1,1,0,1,1,0,0,0,1,0,0,0,0,0,1,0,0,1,1,0,1,0,1,0,1", "Cromossomo", "configGenes");
        if (lc.isEmpty()) {
            res = executaProgramas();

            this.resultado = res;
            resultado.setCromossomo(this.getStringConfiguracao());
            // double f = (res.getPesoClassificacao() * Double.parseDouble(res.getPorcentagemAcertos()))
            //       - (res.getPesoAtributos() * res.getPorcentagemAtributos(nrAtributos));
            double f = Double.parseDouble(res.getPorcentagemAcertos());
            if (Double.isNaN(f)) {
                f = 0.0;
            }
            System.out.println("   " + f + " : " + res.getNumeroAtributos());
            this.resultado.setFitness(f);
            this.setFitness(f);
            cf.edit(this);
            this.fitness = f;
        } else {
            //ResultadoFacade rf = new ResultadoFacade();
            res = lc.get(0).getResultado();
            System.out.println("   " + res.getPorcentagemAcertos() + " : " + res.getNumeroAtributos());
            this.fitness = lc.get(0).getFitness();
        }
    }

    private Resultado executaProgramas() {
        executaPretext();
        executaWeka();
        Resultado res = leResultado();
        return res;
    }

    private void executaPretext() {
        String comandoPretext = decodificaCromossomo();
        //System.out.println(comandoPretext);
        String diretorio = System.getProperty("user.dir");
        executaPrograma(comandoPretext, "saidaPretext.txt", "erroPretext.txt");
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

  
      private String decodificaCromossomo() {
        //String baseDados = "C50test25";
        String baseStoplist = "stoplist";
        String desvio = getDesvio(genes.get(0).getValor());
        String ngram = getNGram(genes.get(1).getValor());
        String stoplist = getStopList();
        return "java -jar PretextExecutor.jar -b " + baseDados + " -ma 0-0 -mf 0-0 -g " + ngram
                + " -sl " + baseStoplist + " -sf " + stoplist + " -d " + desvio + " -f tfidf";
    }
    
    private String getNGram(double valorNGram) {
        if (valorNGram >= 0 && valorNGram <= 10) {
            return "1";
        }
        if (valorNGram >= 11 && valorNGram <= 20) {
            return "2";
        }
        if (valorNGram >= 21 && valorNGram <= 30) {
            return "3";
        }
        if (valorNGram >= 31 && valorNGram <= 40) {
            return "4";
        }
        if (valorNGram >= 41 && valorNGram <= 50) {
            return "1-2";
        }
        if (valorNGram >= 51 && valorNGram <= 60) {
            return "1-3";
        }
        if (valorNGram >= 61 && valorNGram <= 70) {
            return "1-4";
        }
        if (valorNGram >= 71 && valorNGram <= 80) {
            return "2-3";
        }
        if (valorNGram >= 81 && valorNGram <= 90) {
            return "2-4";
        }
        if (valorNGram >= 91 && valorNGram <= 100) {
            return "3-4";
        }

        throw new AssertionError("Valor nao esperado de ngram: " + valorNGram);
    }
    
     private String getDesvio(double desvio) throws AssertionError {
        if (desvio >= 0 && desvio <= 10) {
            return "1.0";
        }
        if (desvio >= 11 && desvio <= 20) {
            return "1.1";
        }
        if (desvio >= 21 && desvio <= 30) {
            return "1.2";
        }
        if (desvio >= 31 && desvio <= 40) {
            return "1.3";
        }
        if (desvio >= 41 && desvio <= 50) {
            return "1.4";
        }
        if (desvio >= 51 && desvio <= 60) {
            return "1.5";
        }
        if (desvio >= 61 && desvio <= 70) {
            return "1.6";
        }
        if (desvio >= 71 && desvio <= 80) {
            return "1.7";
        }
        if (desvio >= 81 && desvio <= 90) {
            return "1.8";
        }
        if (desvio >= 91 && desvio <= 100) {
            return "1.9";
        }

        throw new AssertionError("Valor nao esperado de desvio: " + desvio);
    }
     
    private String getStopList() {
        StringBuilder stoplist = new StringBuilder();
        boolean isParametroSelecionado = false;
        //comeca da posicao 2 porque essa eh a posicao do array que comecam os bits de stoplist
        for (int i = 2; i < genes.size(); i++) {
            if (genes.get(i).getValor() == 1) {
                stoplist.append(genes.get(i).getNome()).append("-");
                isParametroSelecionado = true;
            }
        }

        //retira a ultima virgula a mais
        if (isParametroSelecionado) {
            stoplist.deleteCharAt(stoplist.length() - 1);
        } else {
            return "CD";
        }
        return stoplist.toString();
    }

    private void criaGenes() {
        genes.add(new Gene("desvio"));
        genes.add(new Gene("ngram"));
        String[] stopLists = {"ingl.xml", "CC.xml", "CD.xml", "DT.xml", "EX.xml", "FW.xml", "IN.xml",
            "JJ.xml", "JJR.xml", "JJS.xml", "LS.xml", "MD.xml", "NN.xml", "NNS.xml", "NNP.xml", "NNPS.xml", "PDT.xml", "POS.xml", "PRP.xml", "PRP$.xml", "RB.xml",
            "RBR.xml", "RBS.xml", "RP.xml", "SYM.xml", "TO.xml", "UH.xml", "VB.xml", "VBD.xml", "VBG.xml", "VBN.xml", "VBP.xml", "VBZ.xml", "WDT.xml", "WP.xml",
            "WP$.xml", "WRB.xml"};
        for (String stopList : stopLists) {
            genes.add(new Gene(stopList));
        }
        genes.stream().forEach((gene) -> {
            gene.setCromossomo(this);
        });

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


    private String getStringGeneStopList() {
        StringBuilder geneStoplist = new StringBuilder();
        for (int i = 2; i < this.getGenes().size(); i++) {
            geneStoplist.append(",").append(this.getGenes().get(i));
        }
        return geneStoplist.toString();
    }

}
