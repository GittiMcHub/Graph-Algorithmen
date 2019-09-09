package com.haw.avdt.util.gka;

import com.haw.avdt.util.gka.ex.GKAFormatException;
import com.haw.avdt.util.gka.ex.GKAGraphIncompabilityException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Diese Utility-Klasse ermoeglicht das einlesen von .gka Dateien,
 * die nach folgendem Format aufgebaut sind
 * gerichtet
 * <name node1>[ -> <name node2> [(<edge name>)] [: <edgeweight>]];
 * ungerichtet
 * <name node1>[ -- <name node2> [(<edge name>)] [: <edgeweight>]];
 */
public class GKAFileReader {


    // Gruppenindex {Source = 1}, {Un/Directed = 3}, {Target = 4}, {EdgeName = 6}, {EdgeWeight = 8}
    private static final String GKA_FORMAT_REGEX = "^([a-zA-ZÄÖÜäöü0-9]+)(\\s(--|->)\\s([a-zA-ZÄÖÜäöü0-9]+)(\\s\\(([a-zA-Z0-9ÄÖÜäöü0-9]+)\\)\\s{0,1}){0,1}(\\s[:]\\s([0-9]+)){0,1}){0,1};";
    private static final int GKA_FORMAT_REGEX_GROUP_VERTEX_SOURCE = 1;
    private static final int GKA_FORMAT_REGEX_GROUP_VERTEX_TARGET = 4;
    private static final int GKA_FORMAT_REGEX_GROUP_EDGE_NAME = 6;
    private static final int GKA_FORMAT_REGEX_GROUP_EDGE_WEIGHT = 8;
    private static final int GKA_FORMAT_REGEX_GROUP_EDGE_DIRECTED = 3;

    private static Pattern gkaPattern = Pattern.compile(GKA_FORMAT_REGEX);


    /**
     * Diese Utility Klasse braucht keine Instanz um zu funktionieren,
     * daher ist Konstruktor private
     */
    private GKAFileReader() {
    }

    /**
     * Versucht eine Datei im GKA Format zu lesen und daraus eine Graphbeschreibung zu erzeugen
     *
     * @param filename
     * @return GraphDescription
     * @throws IOException
     */
    public static GKAGraphDescripton read(String filename) throws IOException {
        System.out.println("GKAFileReader::read() | filename=" + filename);
        // Datei einlesen
        Path path = Paths.get(filename);
        List<String> fileLines = Files.readAllLines(path).stream().filter(line -> !line.isEmpty() && line.length() > 0).collect(Collectors.toList());
        if (fileLines.isEmpty()) {
            throw new IOException("GKA File is empty!");
        }

        // Die erste Edge suchen und damit die Graphdescription initialisieren
        GKAGraphComponentDescripton firstValid = null;
        for (String s : fileLines) {
            // Nur durch eine Edge koennen wir feststellen, ob der Graph gerichtet oder ungerichtet ist
            if (firstValid != null && firstValid.hasTarget()) {
                break;
            }
            try {
                firstValid = GKAFileReader.gkaText2GraphComponent(s);
            } catch (GKAFormatException e) {
                System.err.println("Line <" + s + "> does not match GKA File pattern");
            }
        }
        // Wenn kein Valider eintrag vorhanden, dann ist das keine gültige GKA Datei
        if (firstValid == null) {
            throw new IOException("File does not Match GKA Fileformat");
        }

        // Initialisieren mit dem ersten validen Beispiel
        GKAGraphDescripton graphDescripton = new GKAGraphDescripton(firstValid);

        // Zeilenweise einlesen und der Graphdescirption hinzufügen
        fileLines.stream().forEach(s -> {
            GKAGraphComponentDescripton gcd = null;
            try {
                gcd = GKAFileReader.gkaText2GraphComponent(s);
                try {
                    graphDescripton.addComponent(gcd);
                } catch (GKAGraphIncompabilityException e) {
                    System.err.println("Component <" + gcd.toString() + "> is not compatible with Graph <" + graphDescripton.toStringWithHeaderOnly() + ">!");
                }
            } catch (GKAFormatException e) {
                System.err.println("Line <" + s + "> does not match GKA File pattern");
            }

        });
        return graphDescripton;
    }

    /**
     * Nimmt ein String und erzeugt eine Graphkomponente
     *
     * @param text
     * @return GKAGraphComponentDescripton
     * @throws GKAFormatException
     */
    private static GKAGraphComponentDescripton gkaText2GraphComponent(String text) throws GKAFormatException {

        Matcher matcher = gkaPattern.matcher(text);
        if (!matcher.matches()) {
            throw new GKAFormatException();
        }

        String source = matcher.group(GKA_FORMAT_REGEX_GROUP_VERTEX_SOURCE);
        Boolean isDirected = matcher.group(GKA_FORMAT_REGEX_GROUP_EDGE_DIRECTED) != null && matcher.group(GKA_FORMAT_REGEX_GROUP_EDGE_DIRECTED).equals("->");
        String target = matcher.group(GKA_FORMAT_REGEX_GROUP_VERTEX_TARGET);
        String edgeName = matcher.group(GKA_FORMAT_REGEX_GROUP_EDGE_NAME);
        Integer edgeWeight = null;
        // Verhindere NullPointer Exception beim Aufruf von createJUNGGraph
        if (matcher.group(GKA_FORMAT_REGEX_GROUP_EDGE_WEIGHT) != null) {
            edgeWeight = Integer.valueOf(matcher.group(GKA_FORMAT_REGEX_GROUP_EDGE_WEIGHT));
        }

        return new GKAGraphComponentDescripton(source, target, edgeName, isDirected, edgeWeight);
    }

}
