package org.facilelogin.wso2is.repo.explorer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    // private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private Map<String, Set<String>> componentNamesByRepoMap;
    private Map<String, Component> componentsWithPatchesMap;
    private Map<String, Set<Patch>> patchesByProductVersionMap;
    private Map<String, Long> totalPatchCountByRepoMap;
    private Map<String, Long> totalPatchCountByComponentMap;
    private Map<String, Set<String>> productsWithPatchesByRepoMap;

    Long highestPatchCountByRepo = 0L;
    Long highestPatchCountByComponent = 0L;
    Long highestPatchCountByProduct = 0L;

    String highestPatchCountByRepoName;
    String highestPatchCountByComponentName;
    String highestPatchCountByComponentRepoName;
    String highestPatchCountByProductName;

    int totalPatchCount = 0;

    public Printer(Reader crawler) {
        this.componentNamesByRepoMap = crawler.componentNamesByRepoMap;
        this.componentsWithPatchesMap = crawler.componentsWithPatchesMap;
        this.patchesByProductVersionMap = crawler.patchesByProductVersionMap;
        this.totalPatchCount = crawler.totalPatchCount;
        this.totalPatchCountByRepoMap = crawler.totalPatchCountByRepoMap;
        this.totalPatchCountByComponentMap = crawler.totalPatchCountByComponentMap;
        this.highestPatchCountByRepo = crawler.highestPatchCountByRepo;
        this.highestPatchCountByRepoName = crawler.highestPatchCountByRepoName;
        this.highestPatchCountByComponent = crawler.highestPatchCountByComponent;
        this.highestPatchCountByComponentName = crawler.highestPatchCountByComponentName;
        this.highestPatchCountByComponentRepoName = crawler.highestPatchCountByComponentRepoName;
        this.productsWithPatchesByRepoMap = crawler.productsWithPatchesByRepoMap;
    }

    /**
     * 
     */
    public void printAllPatches() {

        for (Map.Entry<String, Set<String>> entry : componentNamesByRepoMap.entrySet()) {
            if (doPrintPatchesByRepo(entry.getKey(), null)) {
                System.out.println();
            }
        }

        System.out.println(ANSI_YELLOW + "Repository with the most number of updates (since IS 5.2.0): "
                + highestPatchCountByRepoName + " (" + highestPatchCountByRepo + ")");
        System.out
                .print("Component with the most number of updates (since IS 5.2.0): " + highestPatchCountByComponentName
                        + " (" + highestPatchCountByComponent + ") [" + highestPatchCountByComponentRepoName + "]");
        System.out.println(ANSI_RESET);

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
                    if (doPrintPatchesByRepo(entry.getKey(), version)) {
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

            System.out.print("|--" + ANSI_CYAN + comp.getRepoName() + "(" + count + "/" + totalPatchCount + ")");

            System.out.println(ANSI_RESET);

            Map<String, Set<Patch>> productPatches = doprintPatchesByComponentName(comp, version);

            System.out.println();
            System.out.print(ANSI_YELLOW + "Summary: ");
            printProductPatchCount(productPatches);
            System.out.println(ANSI_RESET);
        }
    }

    private void printProductPatchCount(Map<String, Set<Patch>> productPatches) {
        System.out.println(ANSI_YELLOW);
        System.out.print(RepoExplorer.IS_510 + ": " + (productPatches.containsKey(RepoExplorer.IS_510)
                ? productPatches.get(RepoExplorer.IS_510).size() : "0"));
        System.out.print(" | " + RepoExplorer.IS_520 + ": " + (productPatches.containsKey(RepoExplorer.IS_520)
                ? productPatches.get(RepoExplorer.IS_520).size() : "0"));
        System.out.print(" | " + RepoExplorer.IS_530 + ": " + (productPatches.containsKey(RepoExplorer.IS_530)
                ? productPatches.get(RepoExplorer.IS_530).size() : "0"));
        System.out.print(" | " + RepoExplorer.IS_540 + ": " + (productPatches.containsKey(RepoExplorer.IS_540)
                ? productPatches.get(RepoExplorer.IS_540).size() : "0"));
        System.out.print(" | " + RepoExplorer.IS_541 + ": " + (productPatches.containsKey(RepoExplorer.IS_541)
                ? productPatches.get(RepoExplorer.IS_541).size() : "0"));
        System.out.print(" | " + RepoExplorer.IS_550 + ": " + (productPatches.containsKey(RepoExplorer.IS_550)
                ? productPatches.get(RepoExplorer.IS_550).size() : "0"));
        System.out.print(" | " + RepoExplorer.IS_560 + ": " + (productPatches.containsKey(RepoExplorer.IS_560)
                ? productPatches.get(RepoExplorer.IS_560).size() : "0"));
        System.out.print(" | " + RepoExplorer.IS_570 + ": " + (productPatches.containsKey(RepoExplorer.IS_570)
                ? productPatches.get(RepoExplorer.IS_570).size() : "0"));
        System.out.println(" | " + RepoExplorer.IS_580 + ": " + (productPatches.containsKey(RepoExplorer.IS_580)
                ? productPatches.get(RepoExplorer.IS_580).size() : "0"));
        System.out.println(ANSI_RESET);
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
                    System.out.print("|  |--" + ANSI_GREEN + comp.getComponentName() + " (" + totalPatchCountByComponent
                            + "/" + totalPatchCountByRepo + ")");
                    System.out.println(ANSI_RESET);
                }
            } else {
                System.out.print("|  |--" + ANSI_GREEN + comp.getComponentName() + " (" + totalPatchCountByComponent
                        + "/" + totalPatchCountByRepo + ")");
                System.out.println(ANSI_RESET);
            }
        }

        if (productPatches != null && !productPatches.isEmpty()) {
            if (version != null) {
                for (Map.Entry<String, Set<Patch>> entry : productPatches.entrySet()) {
                    String ver = entry.getKey();
                    if (ver.equalsIgnoreCase(version)) {
                        Set<Patch> pches = entry.getValue();
                        if (pches.size() > 0) {
                            System.out.print("|  |  |--" + ANSI_PURPLE + ver + " (" + pches.size() + "/"
                                    + totalPatchCountByComponentByProducts + ")");
                            System.out.println(ANSI_RESET);
                            for (Iterator<Patch> iterator = pches.iterator(); iterator.hasNext();) {
                                Patch patch = iterator.next();
                                System.out
                                        .println("|  |  |  |--" + patch.getName() + " (" + patch.getJarVersion() + ")");
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
                        System.out.print("|  |  |--" + ANSI_PURPLE + ver + " (" + pches.size() + "/"
                                + totalPatchCountByComponentByProducts + ")");
                        System.out.println(ANSI_RESET);
                        for (Iterator<Patch> iterator = pches.iterator(); iterator.hasNext();) {
                            Patch patch = iterator.next();
                            System.out.println("|  |  |  |--" + patch.getName() + " (" + patch.getJarVersion() + ")");
                        }
                    }
                }

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

        doPrintPatchesByRepo(repoName, version);
    }

    /**
     * 
     * @param repoName
     * @param version
     */
    private boolean doPrintPatchesByRepo(String repoName, String version) {

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
                System.out.print("|--" + ANSI_CYAN + repoName + "(" + count + "/" + totalPatchCount + ")");
                System.out.println(ANSI_RESET);

                // iterate through all the components in the repo to find patches under each component.
                for (Iterator<String> compIterator = compNames.iterator(); compIterator.hasNext();) {
                    Component component = componentsWithPatchesMap.get(compIterator.next());
                    if (component != null && !component.getPatches().isEmpty()) {
                        doprintPatchesByComponentName(component, version);
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
