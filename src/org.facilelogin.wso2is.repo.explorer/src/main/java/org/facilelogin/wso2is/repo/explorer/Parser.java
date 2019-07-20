package org.facilelogin.wso2is.repo.explorer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

public class Parser {

    private static final String OUTPUT_FILE = "updates";
    private static final String INPUT_FILE = "properties.updates";

    public static void main(String[] args) throws IOException {
        new Parser().parsePomProperties();
    }

    /**
     * patch_numbe|component_name|version|month_of_the_year|year
     * 
     * @param filePath
     * @throws IOException
     */
    private void parsePomProperties() throws IOException {

        BufferedReader reader = null;
        FileWriter writer = null;
        
        System.out.println(System.getProperty("java.class.path"));
        
        Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
        System.out.println(path.toString());


        try {
            writer = new FileWriter(OUTPUT_FILE);
            reader = new BufferedReader(new FileReader(INPUT_FILE));
            String line = reader.readLine();
            System.out.println(line);
            while (line != null) {
                if (line.endsWith(".properties")) {
                    line = line.replace("./", "");
                    String patchName = line.substring(0, line.indexOf("/"));
                    BufferedReader propReader = null;
                    try {
                        propReader = new BufferedReader(new FileReader(line));
                        Properties properties = new Properties();
                        properties.load(propReader);
                        String version = (String) properties.get("version");
                        String componentName = (String) properties.get("artifactId");
                        propReader.close();

                        propReader = new BufferedReader(new FileReader(line));
                        String propLine = propReader.readLine();
                        propLine = propReader.readLine();
                        if (propLine != null) {
                            propLine = propLine.substring(1);
                            String props[] = propLine.split(" ");
                            String month = props[1];
                            String year = props[5];
                            writer.write(patchName + "|" + componentName + "|" + version + "|" + month + "|" + year);
                        }

                    } finally {
                        if (propReader != null) {
                            propReader.close();
                        }
                    }
                }

                line = reader.readLine();

            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}
