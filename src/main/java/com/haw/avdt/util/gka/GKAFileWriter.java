package com.haw.avdt.util.gka;

import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Utility Klasse zum schreiben von .gka Dateien
 */
public class GKAFileWriter {

    /**
     * Privater Konstruktor da es eine Utility Klasse ist
     */
    private GKAFileWriter() {
    }


    public static void write(String filename, GKAGraphDescripton graphDesc) throws IOException {

        if (graphDesc.getComponents().size() == 0) {
            throw new IOException("Will not write empty file!");
        }

        System.out.println("GKAFileWriter::write() | filename=" + filename);
        File f = new File(filename);
        BufferedWriter bw = Files.newWriter(f, Charset.forName("UTF8"));

        graphDesc.getComponents().stream().forEach(comp -> {
            try {
                bw.write(comp.toString());
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bw.flush();
        bw.close();


    }


}
