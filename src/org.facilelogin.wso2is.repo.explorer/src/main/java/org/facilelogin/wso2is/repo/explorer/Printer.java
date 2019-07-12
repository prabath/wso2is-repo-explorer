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

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_PURPLE = "\u001B[35m";

	private Map<String, Set<String>> componentNamesByRepoMap;
	private Map<String, Component> componentsWithPatchesMap;
	protected Map<String, Set<Patch>> patchesByProductVersionMap;

	int highestPatchCountByRepo = 0;
	int highestPatchCountByComponent = 0;
	int highestPatchCountByProduct = 0;

	String highestPatchCountByRepoName;
	String highestPatchCountByComponentName;
	String highestPatchCountByComponentRepoName;
	String highestPatchCountByProductName;

	int totalPatchCount = 0;
	int totalPatchCountByRepo = 0;
	int totalPatchCountByComponent = 0;
	int totalPatchCountByProduct = 0;

	public Printer(Crawler crawler) {
		this.componentNamesByRepoMap = crawler.componentNamesByRepoMap;
		this.componentsWithPatchesMap = crawler.componentsWithPatchesMap;
		this.patchesByProductVersionMap = crawler.patchesByProductVersionMap;
	}

	/**
	 * 
	 */
	public void printAllPatches() {

		for (Map.Entry<String, Set<String>> entry : componentNamesByRepoMap.entrySet()) {
			doPrintPatchesByRepo(entry.getKey(), null);
			System.out.println();
		}

		System.out.println("Repository with the most number of patches (since IS 5.2.0): " + highestPatchCountByRepoName
				+ " (" + highestPatchCountByRepo + ")");
		System.out.println(
				"Component with the most number of patches (since IS 5.2.0): " + highestPatchCountByComponentName + " ("
						+ highestPatchCountByComponent + ") [" + highestPatchCountByComponentRepoName + "]");
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
					doPrintPatchesByRepo(entry.getKey(), version);
					System.out.println();
				}
			} else {
				System.out.println("No patches found for the given product version!");
			}
		}
	}

	public void printPatchesByComponentName(String componentName, String version) {
		Component comp = componentsWithPatchesMap.get(componentName);
		if (comp != null && !comp.getPatches().isEmpty()) {
			// each component has one ore more patches.
			System.out.println("|--" + comp.getRepoName());
			Map<String, Set<Patch>> productPatches = doprintPatchesByComponentName(comp, version);

			System.out.println();
			System.out.print("Summary:");
			printProductPatchCount(productPatches);

		}
	}

	private void printProductPatchCount(Map<String, Set<Patch>> productPatches) {
		System.out.print(
				"IS_5.1.0: " + (productPatches.containsKey("IS_5.1.0") ? productPatches.get("IS_5.1.0").size() : "0"));
		System.out.print(" | IS_5.2.0: "
				+ (productPatches.containsKey("IS_5.2.0") ? productPatches.get("IS_5.2.0").size() : "0"));
		System.out.print(" | IS_5.3.0: "
				+ (productPatches.containsKey("IS_5.3.0") ? productPatches.get("IS_5.3.0").size() : "0"));
		System.out.print(" | IS_5.4.0: "
				+ (productPatches.containsKey("IS_5.4.0") ? productPatches.get("IS_5.4.0").size() : "0"));
		System.out.print(" | IS_5.4.1: "
				+ (productPatches.containsKey("IS_5.4.1") ? productPatches.get("IS_5.4.1").size() : "0"));
		System.out.print(" | IS_5.5.0: "
				+ (productPatches.containsKey("IS_5.5.0") ? productPatches.get("IS_5.5.0").size() : "0"));
		System.out.print(" | IS_5.6.0: "
				+ (productPatches.containsKey("IS_5.6.0") ? productPatches.get("IS_5.6.0").size() : "0"));
		System.out.print(" | IS_5.7.0: "
				+ (productPatches.containsKey("IS_5.7.0") ? productPatches.get("IS_5.7.0").size() : "0"));
		System.out.println(" | IS_5.8.0: "
				+ (productPatches.containsKey("IS_5.8.0") ? productPatches.get("IS_5.8.0").size() : "0"));
	}

	/**
	 * 
	 * @param componentName
	 */
	private Map<String, Set<Patch>> doprintPatchesByComponentName(Component comp, String version) {

		List<Patch> patches = comp.getPatches();
		Map<String, Set<Patch>> productPatches = new HashMap<String, Set<Patch>>();
		int totalPatchCount = 0;
		for (Iterator<Patch> patchIterator = patches.iterator(); patchIterator.hasNext();) {
			Patch patch = patchIterator.next();
			Set<String> products = patch.getProductVersion();
			totalPatchCount++;
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

		System.out.print("|  |--" + ANSI_GREEN + comp.getComponentName());
		System.out.println(ANSI_RESET);

		if (productPatches != null && !productPatches.isEmpty()) {
			if (version != null) {
				for (Map.Entry<String, Set<Patch>> entry : productPatches.entrySet()) {
					String ver = entry.getKey();
					if (ver.equalsIgnoreCase(version)) {
						Set<Patch> pches = entry.getValue();
						System.out.print(
								"|  |  |--" + ANSI_PURPLE + ver + " (" + pches.size() + "/" + totalPatchCount + ")");
						System.out.println(ANSI_RESET);
						for (Iterator<Patch> iterator = pches.iterator(); iterator.hasNext();) {
							Patch patch = iterator.next();
							System.out.println("|  |  |  |--" + patch.getName() + " (" + patch.getJarVersion() + ")");
						}
					}
				}
			} else {
				for (Map.Entry<String, Set<Patch>> entry : productPatches.entrySet()) {
					String ver = entry.getKey();
					Set<Patch> pches = entry.getValue();
					System.out
							.print("|  |  |--" + ANSI_PURPLE + ver + " (" + pches.size() + "/" + totalPatchCount + ")");
					System.out.println(ANSI_RESET);
					for (Iterator<Patch> iterator = pches.iterator(); iterator.hasNext();) {
						Patch patch = iterator.next();
						System.out.println("|  |  |  |--" + patch.getName() + " (" + patch.getJarVersion() + ")");
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
	private void doPrintPatchesByRepo(String repoName, String version) {

		// find the all the components in the given repository, assuming its from wso2-extensions git org.
		Set<String> compNames = componentNamesByRepoMap.get(repoName);

		if (compNames != null && compNames.size() > 0) {
			// now we have a valid git repo - and we know all the components under that.

			// to keep track of all the patches provided under this repo.
			int totalPatches = 0;
			// to keep track of all the patches provided under this repo for the provided product version.
			int productPatchesByRepo = 0;

			if (version != null) {
				System.out.print("|--" + ANSI_CYAN + repoName + "(" + productPatchesByRepo + "/" + totalPatches + ")");
			} else {
				System.out.print("|--" + ANSI_CYAN + repoName + "(" + totalPatches + ")");
			}

			System.out.println(ANSI_RESET);

			// iterate through all the components in the repo to find patches under each component.
			for (Iterator<String> compIterator = compNames.iterator(); compIterator.hasNext();) {
				Component component = componentsWithPatchesMap.get(compIterator.next());
				if (component != null && !component.getPatches().isEmpty()) {
					doprintPatchesByComponentName(component, version);
				}
			}
		}
	}

	/**
	 * 
	 * @param string
	 */
	public void printPatchesByRepo(String repo) {
		printPatchesByRepo(repo, null);
	}
}
