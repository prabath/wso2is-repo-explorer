package org.facilelogin.wso2is.repo.explorer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.facilelogin.wso2is.repo.explorer.bean.*;

/**
 * 
 * @author prabathsiriwardana
 *
 */
public class Crawler {

    private static final String WSO2_DIR = "/identity-repos/.repodata/wso2-components";
    private static final String WSO2_EXT_DIR = "/identity-repos/.repodata/wso2-extensions-components";
    private static final String PATCHES = "/identity-repos/.repodata/updates";

    private static final String IS580 = "/is580";
    private static final String IS570 = "/is570";
    private static final String IS560 = "/is560";
    private static final String IS550 = "/is550";
    private static final String IS541 = "/is541";
    private static final String IS540 = "/is540";
    private static final String IS530 = "/is530";
    private static final String IS520 = "/is520";
    private static final String IS510 = "/is510";
    private static final String IS500 = "/is500";
    private static final String IS460 = "/is460";

    protected Map<String, Set<String>> componentNamesByRepoMap = new HashMap<String, Set<String>>();
    protected Map<String, Component> componentsWithPatchesMap = new HashMap<String, Component>();
    protected Map<String, Set<Patch>> patchesByProductVersionMap = new HashMap<String, Set<Patch>>();
    protected Map<String, Long> totalPatchCountByRepo = new HashMap<String, Long>();
    protected Map<String, Long> totalPatchCountByComponent = new HashMap<String, Long>();
    protected int totalPatchCount = 0;

    private Map<String, Set<String>> productVersionsByJarMap = new HashMap<String, Set<String>>();
    private List<String> skipRepos = new ArrayList<>();

    Long highestPatchCountByRepo = 0L;
    Long highestPatchCountByComponent = 0L;
    Long highestPatchCountByProduct = 0L;

    String highestPatchCountByRepoName;
    String highestPatchCountByComponentName;
    String highestPatchCountByComponentRepoName;

    /**
     * @throws IOException
     * 
     */
    public void populateData() throws IOException {

        skipRepos.add("identity-test-integration");
        skipRepos.add("identity-endpoint-authentication");

        addRepo(WSO2_DIR, "https://github.com/wso2/");
        addRepo(WSO2_EXT_DIR, "https://github.com/wso2-extensions/");
        addProduct(IS460, "IS_4.6.0");
        addProduct(IS500, "IS_5.0.0");
        addProduct(IS510, "IS_5.1.0");
        addProduct(IS520, "IS_5.2.0");
        addProduct(IS530, "IS_5.3.0");
        addProduct(IS540, "IS_5.4.0");
        addProduct(IS541, "IS_5.4.1");
        addProduct(IS550, "IS_5.5.0");
        addProduct(IS560, "IS_5.6.0");
        addProduct(IS570, "IS_5.7.0");
        addProduct(IS580, "IS_5.8.0");
        addPatches(PATCHES);
    }

    /**
     * 
     * @param filePath
     * @param prefix
     * @throws IOException
     */
    private void addRepo(String filePath, String prefix) throws IOException {

        BufferedReader reader = null;
        try {
            // read all the git repo from the provided file.
            // this includes a line per each file in the git repos.
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null && line.length() > 2) {
                    // this is the first character in the file.
                    line = line.replace("./", "");
                    if (line.indexOf("/") > 0) {
                        // now the line starts with the repo name.
                        String repoName = line.substring(0, line.indexOf("/"));
                        // we only worry about the components that starts with org.wso2.carbon
                        if (line.indexOf("org.wso2.carbon") > 0) {
                            String componentName = line.substring(line.indexOf("org.wso2.carbon"), line.length());
                            if (componentName.indexOf("/") > 0) {
                                // component name may not be the end of the line.
                                componentName = componentName.substring(0, componentName.indexOf("/"));
                            }
                            // we do not need to add all repos.
                            if (!skipRepos.contains(repoName)) {
                                // this is how we construct the repo url.
                                if (prefix != null) {
                                    repoName = prefix + repoName;
                                }

                                if (componentNamesByRepoMap.containsKey(repoName)) {
                                    if (!componentNamesByRepoMap.get(repoName).contains(componentName)) {
                                        componentNamesByRepoMap.get(repoName).add(componentName);
                                        // this map will be later populated with the patches.
                                        componentsWithPatchesMap.put(componentName,
                                                new Component(repoName, componentName));
                                    }
                                } else {
                                    Set<String> componentSet;
                                    componentSet = new HashSet<String>();
                                    componentSet.add(componentName);
                                    componentNamesByRepoMap.put(repoName, componentSet);
                                    // this map will be later populated with the patches.
                                    componentsWithPatchesMap.put(componentName, new Component(repoName, componentName));
                                }
                            }
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

    /**
     * 
     * @param filePath
     * @throws IOException
     */
    private void addProduct(String filePath, String version) throws IOException {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null && line.length() > 2) {
                    if (line.startsWith("org.wso2.carbon") && line.endsWith(".jar")) {
                        line.replaceAll("-", "_");
                        if (productVersionsByJarMap.containsKey(line)) {
                            productVersionsByJarMap.get(line).add(version);
                        } else {
                            Set<String> versions = new HashSet<String>();
                            versions.add(version);
                            productVersionsByJarMap.put(line, versions);
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

    /**
     * 
     * @param filePath
     * @throws IOException
     */
    private void addPatches(String filePath) throws IOException {

        BufferedReader reader = null;
        try {
            // reads the patch list. this includes all the patches issued across all the products.
            // the same patch number can have multiple lines - one line per jar.
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null && line.length() > 2) {
                    line = line.replace("./", "");
                    if (line.indexOf("/") > 0) {
                        String patchName = line.substring(0, line.indexOf("/"));
                        if (line.indexOf("org.wso2.carbon") > 0 && line.endsWith(".jar")) {
                            String jarName = line.substring(line.indexOf("org.wso2.carbon"), line.length());
                            String jarVersion = null;
                            jarName.replaceAll("-", "_");
                            Set<String> productVersions = productVersionsByJarMap.get(jarName);

                            if (productVersions != null && productVersions.size() > 0) {
                                // we only worry about patched jars in any of the IS releases from 5.0.0 to the latest.
                                totalPatchCount++;
                            }

                            String compName = null;

                            // find the component name and the version from the jar file name.
                            if (jarName.indexOf("_") > 0) {
                                String[] parts = jarName.split("_");
                                compName = parts[0];
                                jarVersion = parts[1];
                                jarVersion = jarVersion.substring(0, jarVersion.indexOf(".jar"));
                            }

                            // component name here comes from the git repo.
                            // we assume there are no two repos with the same component name.
                            Component comp = componentsWithPatchesMap.get(compName);
                            if (comp != null) {
                                Patch patch = new Patch(patchName, jarVersion, productVersions);
                                comp.addPatch(patch);
                                String repoName = comp.getRepoName();

                                if ("org.wso2.carbon.identity.application.authentication.framework".equals(compName)
                                        && !"https://github.com/wso2/carbon-identity-framework".equals(repoName)) {
                                    System.out.println("TEST");
                                }

                                if (totalPatchCountByComponent.containsKey(compName)) {
                                    totalPatchCountByComponent.put(compName,
                                            totalPatchCountByComponent.get(compName) + 1);
                                } else {
                                    totalPatchCountByComponent.put(compName, 1L);
                                }

                                if (totalPatchCountByRepo.containsKey(repoName)) {
                                    totalPatchCountByRepo.put(repoName, totalPatchCountByRepo.get(repoName) + 1);
                                } else {
                                    totalPatchCountByRepo.put(repoName, 1L);
                                }

                                if (totalPatchCountByRepo.get(repoName) > highestPatchCountByRepo) {
                                    highestPatchCountByRepo = totalPatchCountByRepo.get(repoName);
                                    highestPatchCountByRepoName = repoName;
                                }

                                if (totalPatchCountByComponent.get(compName) > highestPatchCountByComponent) {
                                    highestPatchCountByComponent = totalPatchCountByComponent.get(compName);
                                    highestPatchCountByComponentName = compName;
                                    highestPatchCountByComponentRepoName = repoName;
                                }

                                if (productVersions != null && productVersions.size() > 0) {
                                    for (Iterator<String> iterator = productVersions.iterator(); iterator.hasNext();) {
                                        String prodVersion = (String) iterator.next();
                                        if (patchesByProductVersionMap.containsKey(prodVersion)) {
                                            patchesByProductVersionMap.get(prodVersion).add(patch);
                                        } else {
                                            Set<Patch> patchSet = new HashSet<Patch>();
                                            patchSet.add(patch);
                                            patchesByProductVersionMap.put(prodVersion, patchSet);
                                        }
                                    }
                                }

                            }
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
