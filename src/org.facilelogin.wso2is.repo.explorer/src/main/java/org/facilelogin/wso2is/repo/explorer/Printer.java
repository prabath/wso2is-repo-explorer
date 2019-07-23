package org.facilelogin.wso2is.repo.explorer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.facilelogin.wso2is.repo.explorer.bean.Component;
import org.facilelogin.wso2is.repo.explorer.bean.Patch;

public class Printer {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private Map<String, Set<String>> componentNamesByRepoMap;
    private Map<String, Component> componentsWithPatchesMap;
    private Map<String, Set<Patch>> patchesByProductVersionMap;
    private Map<String, Long> totalPatchCountByRepoMap;
    private Map<String, Long> totalPatchCountByComponentMap;
    private Map<String, Set<String>> productsWithPatchesByRepoMap;
    private Map<String, Map<String, Set<Patch>>> patchesByTimeMap;

    Long highestPatchCountByRepo = 0L;
    Long highestPatchCountByComponent = 0L;
    Long highestPatchCountByProduct = 0L;

    String highestPatchCountByRepoName;
    String highestPatchCountByComponentName;
    String highestPatchCountByComponentRepoName;
    String highestPatchCountByProductName;

    int totalPatchedJarCount = 0;
    int totalProductPatchCount = 0;

    boolean color = true;

    public Printer(Reader reader) {
        this.componentNamesByRepoMap = reader.componentNamesByRepoMap;
        this.componentsWithPatchesMap = reader.componentsWithPatchesMap;
        this.patchesByProductVersionMap = reader.patchesByProductVersionMap;
        this.totalPatchedJarCount = reader.totalPatchedJarCount;
        this.totalPatchCountByRepoMap = reader.totalPatchCountByRepoMap;
        this.totalPatchCountByComponentMap = reader.totalPatchCountByComponentMap;
        this.highestPatchCountByRepo = reader.highestPatchCountByRepo;
        this.highestPatchCountByRepoName = reader.highestPatchCountByRepoName;
        this.highestPatchCountByComponent = reader.highestPatchCountByComponent;
        this.highestPatchCountByComponentName = reader.highestPatchCountByComponentName;
        this.highestPatchCountByComponentRepoName = reader.highestPatchCountByComponentRepoName;
        this.productsWithPatchesByRepoMap = reader.productsWithPatchesByRepoMap;
        this.patchesByTimeMap = reader.patchesByTimeMap;

        if (System.getenv("REX_COLOR") != null && System.getenv("REX_COLOR").equalsIgnoreCase("false")) {
            color = false;
        }
    }

    /**
     * 
     * @param colorCode
     * @return
     */
    private String color(String colorCode) {
        if (color) {
            return colorCode;
        } else {
            return "";
        }
    }

    /**
     * 
     */
    public void printAllPatches(boolean printAnomaliesOnly) {

        for (Map.Entry<String, Set<String>> entry : componentNamesByRepoMap.entrySet()) {
            if (doPrintPatchesByRepo(entry.getKey(), null, printAnomaliesOnly)) {
                if (!printAnomaliesOnly) {
                    System.out.println();
                }
            }
        }

        if (!printAnomaliesOnly) {
            System.out.println(color(ANSI_YELLOW) + "Repository with the most number of updates (since IS 5.1.0): "
                    + highestPatchCountByRepoName + " (" + highestPatchCountByRepo + ")");
            System.out.print(
                    "Component with the most number of updates (since IS 5.1.0): " + highestPatchCountByComponentName
                            + " (" + highestPatchCountByComponent + ") [" + highestPatchCountByComponentRepoName + "]");
            System.out.println(color(ANSI_RESET));
            System.out.print(
                    color(ANSI_BLUE) + "Total unumber of product patches since IS 5.1.0: " + totalProductPatchCount);
            System.out.println(color(ANSI_RESET));
        }

    }

    /**
     * 
     */
    public void printPatchesCountByRepoTop10() {

        // LinkedHashMap preserve the ordering of elements in which they are inserted
        LinkedHashMap<String, Long> reverseSortedMap = new LinkedHashMap<>();

        totalPatchCountByRepoMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        for (Map.Entry<String, Long> entry : reverseSortedMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

    }

    /**
     * 
     * @param componentName
     */
    public void printPatchesByProduct(String version) {

        if (version == null) {
            printProductPatchCount(patchesByProductVersionMap);
        } else {
            Set<Patch> patchSet = patchesByProductVersionMap.get(version);
            if (patchSet != null && !patchSet.isEmpty()) {
                for (Map.Entry<String, Set<String>> entry : componentNamesByRepoMap.entrySet()) {
                    if (doPrintPatchesByRepo(entry.getKey(), version, false)) {
                        System.out.println();
                    }
                }
            } else {
                System.out.println("No updates found for the given product version!");
            }
        }
    }

    public void printPatchesByComponentName(String componentName, String version) {
        Component comp = componentsWithPatchesMap.get(componentName);
        if (comp != null && !comp.getPatches().isEmpty()) {
            // each component has one ore more patches.

            // to keep track of all the patches provided under this repo for the provided product version.
            Long totalPatchCountByRepo = this.totalPatchCountByRepoMap.get(comp.getRepoName());
            long count = totalPatchCountByRepo == null ? 0 : totalPatchCountByRepo;

            System.out.print(
                    "|--" + color(ANSI_CYAN) + comp.getRepoName() + " (" + count + "/" + totalPatchedJarCount + ")");

            System.out.println(color(ANSI_RESET));

            Map<String, Set<Patch>> productPatches = doprintPatchesByComponentName(comp, version);

            System.out.println();
            System.out.print(color(ANSI_YELLOW) + "Summary: ");
            printProductPatchCount(productPatches);
            System.out.println(color(ANSI_RESET));
        }
    }

    private void printProductPatchCount(Map<String, Set<Patch>> productPatches) {
        System.out.println(color(ANSI_YELLOW));
        System.out.print(Rex.IS_510 + ": "
                + (productPatches.containsKey(Rex.IS_510) ? productPatches.get(Rex.IS_510).size() : "0"));
        System.out.print(" | " + Rex.IS_520 + ": "
                + (productPatches.containsKey(Rex.IS_520) ? productPatches.get(Rex.IS_520).size() : "0"));
        System.out.print(" | " + Rex.IS_530 + ": "
                + (productPatches.containsKey(Rex.IS_530) ? productPatches.get(Rex.IS_530).size() : "0"));
        System.out.print(" | " + Rex.IS_540 + ": "
                + (productPatches.containsKey(Rex.IS_540) ? productPatches.get(Rex.IS_540).size() : "0"));
        System.out.print(" | " + Rex.IS_541 + ": "
                + (productPatches.containsKey(Rex.IS_541) ? productPatches.get(Rex.IS_541).size() : "0"));
        System.out.print(" | " + Rex.IS_550 + ": "
                + (productPatches.containsKey(Rex.IS_550) ? productPatches.get(Rex.IS_550).size() : "0"));
        System.out.print(" | " + Rex.IS_560 + ": "
                + (productPatches.containsKey(Rex.IS_560) ? productPatches.get(Rex.IS_560).size() : "0"));
        System.out.print(" | " + Rex.IS_570 + ": "
                + (productPatches.containsKey(Rex.IS_570) ? productPatches.get(Rex.IS_570).size() : "0"));
        System.out.println(" | " + Rex.IS_580 + ": "
                + (productPatches.containsKey(Rex.IS_580) ? productPatches.get(Rex.IS_580).size() : "0"));
        System.out.println(color(ANSI_RESET));
    }

    /**
     * 
     * @param componentName
     */
    private Map<String, Set<Patch>> doprintPatchesByComponentName(Component comp, String version) {

        List<Patch> patches = comp.getPatches();
        Map<String, Set<Patch>> productPatches = new HashMap<String, Set<Patch>>();
        int totalPatchCountByComponentByProducts = 0;

        long totalPatchCountByComponent = this.totalPatchCountByComponentMap.get(comp.getComponentName());
        long totalPatchCountByRepo = this.totalPatchCountByRepoMap.get(comp.getRepoName());

        for (Iterator<Patch> patchIterator = patches.iterator(); patchIterator.hasNext();) {
            Patch patch = patchIterator.next();
            Set<String> products = patch.getProductVersion();
            totalPatchCountByComponentByProducts++;
            if (products != null && !products.isEmpty()) {
                // we know the product(s), where this patch is applicable.
                for (Iterator<String> prodIterator = products.iterator(); prodIterator.hasNext();) {
                    String prodVersion = prodIterator.next();
                    if (productPatches.containsKey(prodVersion)) {
                        productPatches.get(prodVersion).add(patch);
                    } else {
                        Set<Patch> patchSet = new HashSet<Patch>();
                        patchSet.add(patch);
                        productPatches.put(prodVersion, patchSet);
                    }
                }
            }
        }

        if (totalPatchCountByComponent > 0) {
            if (version != null) {
                if (productPatches.get(version) != null && !productPatches.get(version).isEmpty()) {
                    System.out.print("|  |--" + color(ANSI_GREEN) + comp.getComponentName() + " ("
                            + totalPatchCountByComponent + "/" + totalPatchCountByRepo + ")");
                    System.out.println(color(ANSI_RESET));
                }
            } else {
                System.out.print("|  |--" + color(ANSI_GREEN) + comp.getComponentName() + " ("
                        + totalPatchCountByComponent + "/" + totalPatchCountByRepo + ")");
                System.out.println(color(ANSI_RESET));
            }
        }

        if (productPatches != null && !productPatches.isEmpty()) {
            if (version != null) {
                for (Map.Entry<String, Set<Patch>> entry : productPatches.entrySet()) {
                    String ver = entry.getKey();
                    if (ver.equalsIgnoreCase(version)) {
                        Set<Patch> pches = entry.getValue();
                        if (pches.size() > 0) {
                            System.out.print("|  |  |--" + color(ANSI_PURPLE) + ver + " (" + pches.size() + "/"
                                    + totalPatchCountByComponentByProducts + ")");
                            System.out.println(color(ANSI_RESET));
                            for (Iterator<Patch> iterator = pches.iterator(); iterator.hasNext();) {
                                Patch patch = iterator.next();
                                System.out.println("|  |  |  |--" + patch.getName() + " (" + patch.getJarVersion() + "/"
                                        + patch.getMonth() + "," + patch.getYear() + ")");
                            }
                        }
                    }
                }
            } else {
                for (Map.Entry<String, Set<Patch>> entry : productPatches.entrySet()) {
                    String ver = entry.getKey();
                    Set<Patch> pches = entry.getValue();
                    if (pches.size() > 0) {
                        // no need to print if there are no patches.
                        System.out.print("|  |  |--" + color(ANSI_PURPLE) + ver + " (" + pches.size() + "/"
                                + totalPatchCountByComponentByProducts + ")");
                        System.out.println(color(ANSI_RESET));
                        for (Iterator<Patch> iterator = pches.iterator(); iterator.hasNext();) {
                            totalProductPatchCount++;
                            Patch patch = iterator.next();
                            System.out.println("|  |  |  |--" + patch.getName() + " (" + patch.getJarVersion() + "/"
                                    + patch.getMonth() + "," + patch.getYear() + ")");
                        }
                    }
                }

            }
        }

        return productPatches;
    }

    /**
     * 
     * @param comp
     * @param version
     * @return
     */
    private Map<String, Set<Patch>> doPrintAnomalies(Component comp, String version) {

        List<Patch> patches = comp.getPatches();
        Map<String, Set<Patch>> productPatches = new HashMap<String, Set<Patch>>();

        long totalPatchCountByComponent = this.totalPatchCountByComponentMap.get(comp.getComponentName());

        for (Iterator<Patch> patchIterator = patches.iterator(); patchIterator.hasNext();) {
            Patch patch = patchIterator.next();
            Set<String> products = patch.getProductVersion();
            if (products != null && !products.isEmpty()) {
                // we know the product(s), where this patch is applicable.
                for (Iterator<String> prodIterator = products.iterator(); prodIterator.hasNext();) {
                    String prodVersion = prodIterator.next();
                    if (productPatches.containsKey(prodVersion)) {
                        productPatches.get(prodVersion).add(patch);
                    } else {
                        Set<Patch> patchSet = new HashSet<Patch>();
                        patchSet.add(patch);
                        productPatches.put(prodVersion, patchSet);
                    }
                }
            }
        }

        if (totalPatchCountByComponent > 0) {
            if (productPatches == null || productPatches.isEmpty()) {
                System.out.println(comp.getComponentName());
            }
        }

        return productPatches;
    }

    /**
     * 
     * @param componentName
     */
    public void printPatchesByComponentName(String componentName) {
        printPatchesByComponentName(componentName, null);
    }

    /**
     * 
     */
    public void printPatchCountByTime() {

        for (Map.Entry<String, Map<String, Set<Patch>>> yr : patchesByTimeMap.entrySet()) {
            String year = yr.getKey();
            Map<String, Set<Patch>> monthlyPatches = yr.getValue();
            if (monthlyPatches.size() > 0) {
                System.out.print("|--" + color(ANSI_CYAN) + year);
                System.out.println(color(ANSI_RESET));

                for (Map.Entry<String, Set<Patch>> mt : monthlyPatches.entrySet()) {
                    String month = mt.getKey();
                    int patchedJarCount = mt.getValue().size();
                    if (patchedJarCount > 0) {
                        Set<Patch> patches = mt.getValue();
                        List<String> uniquePatches = new ArrayList<String>();
                        for (Iterator<Patch> iterator = patches.iterator(); iterator.hasNext();) {
                            Patch patch = iterator.next();
                            if (!uniquePatches.contains(patch.getName())) {
                                uniquePatches.add(patch.getName());
                                if (patch.getMonth().equals("Jul") && patch.getYear() == 2019) {
                                    System.out.println(patch.getName());
                                }
                            }

                        }

                        System.out.print("|  |--" + color(ANSI_GREEN) + month + " (" + uniquePatches.size() + ")");
                        System.out.println(color(ANSI_RESET));
                    }
                }

            }
        }
    }

    /**
     * 
     * @param repo
     * @param productVesions
     */
    public void printPatchesByRepo(String repo, String version) {

        // find the all the components in the given repository, assuming its from wso2-extensions git org.
        String repoName = "https://github.com/wso2-extensions/" + repo;
        Set<String> compNames = componentNamesByRepoMap.get(repoName);

        if (compNames == null || compNames.size() == 0) {
            // not found under wso2-extensions.
            // find the all the components in the given repository, assuming its from wso2 git org.
            repoName = "https://github.com/wso2/" + repo;
            compNames = componentNamesByRepoMap.get(repoName);
        }

        doPrintPatchesByRepo(repoName, version, false);
    }

    /**
     * 
     * @param repoName
     * @param version
     */
    private boolean doPrintPatchesByRepo(String repoName, String version, boolean printAnomaliesOnly) {

        // find the all the components in the given repository, assuming its from wso2-extensions git org.
        Set<String> compNames = componentNamesByRepoMap.get(repoName);

        if (compNames != null && compNames.size() > 0) {
            // now we have a valid git repo - and we know all the components under that.

            // to keep track of all the patches provided under this repo for the provided product version.
            Long totalPatchCountByRepo = this.totalPatchCountByRepoMap.get(repoName);
            long count = totalPatchCountByRepo == null ? 0 : totalPatchCountByRepo;

            if (version != null) {
                Set<String> productsWithPatches = productsWithPatchesByRepoMap.get(repoName);
                if (productsWithPatches == null || !productsWithPatches.contains(version)) {
                    return false;
                }
            }

            if (count > 0) {

                if (!printAnomaliesOnly) {
                    System.out.print(
                            "|--" + color(ANSI_CYAN) + repoName + "(" + count + "/" + totalPatchedJarCount + ")");
                    System.out.println(color(ANSI_RESET));
                }

                // iterate through all the components in the repo to find patches under each component.
                for (Iterator<String> compIterator = compNames.iterator(); compIterator.hasNext();) {
                    Component component = componentsWithPatchesMap.get(compIterator.next());
                    if (component != null && !component.getPatches().isEmpty()) {
                        if (printAnomaliesOnly) {
                            doPrintAnomalies(component, version);
                        } else {
                            doprintPatchesByComponentName(component, version);
                        }
                    }
                }
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * @param string
     */
    public void printPatchesByRepo(String repo) {
        printPatchesByRepo(repo, null);
    }
}
