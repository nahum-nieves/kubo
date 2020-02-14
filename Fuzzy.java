
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author arturo
 */
public class Fuzzy implements Serializable {

    private final String REGEXP_JSON_NAME = "\\{\\\"name\\\"\\:\\\"([A-ZÀ-Ú][A-zÀ-ú]* ?)*\\\"\\}";
    private final String PREFIX_JSON_NAME = "{\"name\":\"";
    private final String SUFFIX_JSON_NAME = "\"}";
    private final String REGEXP_JSON_SEARCH = "\\{\\\"search\\\"\\:\\\"([A-zÀ-ú][A-zÀ-ú]* ?)*\\\"\\}";
    private final String PREFIX_JSON_SEARCH =  "{\"search\":\"";
    private final String SUFFIX_JSON_SEARCH = "\"}";
    private final String EMPTY_STRING = "";
    private final String FILE_NAME = "fuzzy-search.txt";
    private final Character ANY_CHARACTER = '*';
    private static final String BREAK_LINE = "\n";
    private Map<Character, Integer> tablaD1;

    public static void main(String[] args) {
        if (args.length < 0) {
            System.out.println("Por favor indica la operación que deseas realizar");
            return;
        }
        switch (args[0]) {
            case "add":
                if (args.length != 2) {
                    System.out.println("El comando add sólo acepta un parámetro." + BREAK_LINE);
                    return;
                }
                new Fuzzy().add(args[1]);
            break;
            case "list":
                if (args.length != 1) {
                    System.out.println("El comando list no acepta parámetros." + BREAK_LINE);
                    return;
                }
                new Fuzzy().list();
            break;
            case "fuzzy-search":
                if (args.length != 2) {
                    System.out.println("El comando search sólo acepta un parámetro." + BREAK_LINE);
                    return;
                }
                new Fuzzy().fuzzySearch(args[1]);
            break;

        }
    }

    public void add(String sujeto) {
        if (!sujeto.matches(REGEXP_JSON_NAME)) {
            System.out.println("Por favor ingresa registro en formato JSON. Ingresa nombres y apellidos iniciando con mayúsculas.");
            return;
        }
        try (BufferedWriter br = new BufferedWriter(new FileWriter(new File(FILE_NAME), true))) {
            br.write(sujeto.replace(PREFIX_JSON_NAME, EMPTY_STRING).replace(SUFFIX_JSON_NAME, EMPTY_STRING) + BREAK_LINE);
        } catch (Exception ex) {
            System.out.println("Error al escribir el usuario. " + BREAK_LINE);
            ex.printStackTrace();
        }
        System.out.println("Usuario agregado." + BREAK_LINE);
    }

    
    private List<String> leerNombresDeArchivo() {
        List<String> nombres = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));) {
            String line = reader.readLine();
            while (line != null) {
                nombres.add(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            throw new InternalError("No se puede leer el archivo." + e.getMessage());
        }
        return nombres;
    }
    
    private void list() {
        List<String> nombres = leerNombresDeArchivo();
        Collections.sort(nombres, (l1, l2) -> l1.toLowerCase().compareTo(l2.toLowerCase()));
        System.out.println("[");
        for (int i = 0; i < nombres.size(); i++) {
            System.out.print(formatJson(nombres.get(i)));
            if (i != nombres.size() - 1) {
                System.out.println(",");
            }
        }
        System.out.println("\n]");
    }
    
      
    private String formatJson(String string){
        return PREFIX_JSON_NAME + string + SUFFIX_JSON_NAME;
    }
    
    public void fuzzySearch(String pattern) {
        if (!pattern.matches(REGEXP_JSON_SEARCH)) {
            System.out.println("Por favor ingresa la búsqueda en formato JSON." + BREAK_LINE);
            return;
        }
        pattern = pattern.replace(SUFFIX_JSON_SEARCH, "").replace(PREFIX_JSON_SEARCH, "").toLowerCase();
        tablaD1 = crearTablaD1(pattern);
        int indiceMejorScore = -1;
        int mejorScore= 0;
        pattern = limpiarString(pattern);
        List<String> nombres = leerNombresDeArchivo();
        if (nombres.size() > 0) {
            mejorScore = distanciaNombreCompleto(nombres.get(0), pattern);
            indiceMejorScore = 0;
            if (nombres.size() > 1) {
                for (int i = 1; i < nombres.size(); i++) {
                    int distanciaNombreCompleto = distanciaNombreCompleto(nombres.get(i), pattern);
                    if (distanciaNombreCompleto < mejorScore) {
                        indiceMejorScore = i;
                        mejorScore = distanciaNombreCompleto;
                    }
                }
            }
        }
        System.out.println((indiceMejorScore != -1 && mejorScore < 4) ? formatJson(nombres.get(indiceMejorScore)) : "Sin coincidencias");
    }

  
    private int distanciaNombreCompleto(String nombreCompleto, String patron) {
        String[] split = nombreCompleto.split(" ");
        int mejorScoreNombreCompleto = distanciaLevenshtein(limpiarString(split[0]), patron);
        if(split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                int distancia = distanciaLevenshtein(limpiarString(split[1]), patron);
                if(distancia<mejorScoreNombreCompleto){
                    mejorScoreNombreCompleto = distancia;
                }
            }
        }
        return mejorScoreNombreCompleto;
    }

    public int distanciaLevenshtein(String a, String b) {
        int[] costos = new int[b.length() + 1];
        for (int j = 0; j < costos.length; j++) {
            costos[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            costos[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costos[j], costos[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costos[j];
                costos[j] = cj;
            }
        }
        return costos[b.length()];
    }
 
    private String limpiarString(String string){
        String proc = java.text.Normalizer.normalize(string, Normalizer.Form.NFD);
        StringBuilder stringLimpio = new StringBuilder();
        for (char c : proc.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BASIC_LATIN) {
                stringLimpio.append(c);
            }
        }
        return stringLimpio.toString();
    }
    
    /**
     * Tabla D1 definida en algoritmo Boyer-Moore
    */
    private Map<Character, Integer> crearTablaD1(String pattern) {
        String proc = java.text.Normalizer.normalize(pattern, Normalizer.Form.NFD);
        pattern = limpiarString(proc);

        Map<Character, Integer> d1 = new HashMap<>();
        d1.put(ANY_CHARACTER, pattern.length());
        for (int i = pattern.length() - 1; i >= 0; i--) {
            if (d1.get(pattern.charAt(i)) == null) {
                d1.put(pattern.charAt(i), pattern.length()-1 - i);
            }
        }
        return d1;
    }
  
    /*
     * Idealmente las cadenas que llegue son de la misma dimensión
     * Sin embargo se valida cuál es la más corta para evitar Excepciones.
     *
     */
    private int calcularDistanciaHamming(String string1, String string2) {
        int limite = string1.length() > string2.length() ? string2.length() : string1.length();
        int distanciaHamming = 0;
        for (int i = 0; i < limite; i++) {
            if(string1.charAt(i) == string2.charAt(0)){
                distanciaHamming++;
            }
        }
        return distanciaHamming;
    }

    
}
       