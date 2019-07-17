package org.facilelogin.wso2is.repo.explorer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

    /**
     * jar_name_with_version|patch_number|week_of_the_year|month_of_the_year|year
     * @param filePath
     * @throws IOException
     */
    private void parsePomProperties(String filePath) throws IOException {

        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null && line.endsWith(".properties")) {
                    BufferedReader propReader = null;
                    try {
                        propReader = new BufferedReader(new FileReader(line));
                        String propLine = propReader.readLine();
                        while (propLine != null) {
                            propLine = propReader.readLine();

                        }
                    } finally {
                        if (propReader != null) {
                            propReader.close();
                        }
                    }
                }
            }
            reader.close();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
