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

public class RepoExplorer {

	private static final String WSO2_TREE_DIR = "/identity-repos/.repodata/wso2.tree.dir";
	private static final String WSO2_EXT_TREE_DIR = "/identity-repos/.repodata/wso2-extensions.tree.dir";
	private static final String PATCHES_TREE = "/identity-repos/.repodata/patch.tree";

	private static final String TREE_580_ = "/5.8.0.tree";
	private static final String TREE_570_ = "/5.7.0.tree";
	private static final String TREE_560_ = "/5.6.0.tree";
	private static final String TREE_550_ = "/5.5.0.tree";
	private static final String TREE_541_ = "/5.4.1.tree";
	private static final String TREE_540_ = "/5.4.0.tree";
	private static final String TREE_530_ = "/5.3.0.tree";
	private static final String TREE_520_ = "/5.2.0.tree";

	private static Map<String, Set<String>> repos = new HashMap<String, Set<String>>();
	private static Map<String, Component> components = new HashMap<String, Component>();
	private static List<String> skipRepos = new ArrayList<>();

	public static void main(String[] args) {

		skipRepos.add("identity-test-integration");

		addRepo(WSO2_TREE_DIR, "https://github.com/wso2/");
		addRepo(WSO2_EXT_TREE_DIR, "https://github.com/wso2-extensions/");
		populatePatches(PATCHES_TREE);

		if (args.length == 2 && "-j".equals(args[0]) && !args[1].isEmpty()) {
			String componentName = args[1];
			Component comp = components.get(componentName);
			if (comp != null && !comp.getPatches().isEmpty()) {
				System.out.println("Repo Name: " + comp.getRepoName());
				System.out.println("Component Name: " + comp.getComponentName());
				System.out.println("Patches (" + comp.getPatches().size() + "): ");
				List<String> patches = comp.getPatches();
				for (Iterator<String> iterator2 = patches.iterator(); iterator2.hasNext();) {
					System.out.println(iterator2.next());
				}
			} else {
				System.out.println("No patches found for the given component!");
			}
		} else if (args.length == 2 && "-r".equals(args[0]) && !args[1].isEmpty()) {
			String repoName = "https://github.com/wso2-extensions/" + args[1];
			Set<String> compNames = repos.get(repoName);

			if (compNames == null || compNames.size() == 0) {
				repoName = "https://github.com/wso2/" + args[1];
				compNames = repos.get(repoName);
			}

			if (compNames != null && compNames.size() > 0) {
				
				System.out.println("Repo Name: " + repoName);
				System.out.println();

				
				for (Iterator<String> iterator = compNames.iterator(); iterator.hasNext();) {
					Component jar = components.get(iterator.next());
					List<String> patches = jar.getPatches();
					if (patches != null && patches.size() > 0) {
						System.out.println("  |-" + jar.getComponentName() + " [" + patches.size() + "]");
						for (Iterator<String> iterator2 = patches.iterator(); iterator2.hasNext();) {
							System.out.println("         |-" + iterator2.next());
						}
					}
				}
			} else {
				System.out.println("No patches found for the given repo!");
			}

		} else {
			printAllPatches();
		}
	}

	private static void printAllPatches() {

		int topRepoPatchCount = 0;
		String topRepoPatchCountName = null;
		int topCompPatchCount = 0;
		String topCompPatchCountName = null;
		String topCompPatchCountRepoName = null;

		for (Map.Entry<String, Set<String>> entry : repos.entrySet()) {

			List<String> list = new ArrayList<>();
			int repoPatchCount = 0;

			Set<String> values = entry.getValue();
			for (Iterator<String> iterator = values.iterator(); iterator.hasNext();) {
				Component jar = components.get(iterator.next());
				List<String> patches = jar.getPatches();
				if (patches != null && patches.size() > 0) {

					if (patches.size() > topCompPatchCount) {
						topCompPatchCount = patches.size();
						topCompPatchCountName = jar.getComponentName();
						topCompPatchCountRepoName = jar.getRepoName();
					}

					list.add("  |-" + jar.getComponentName() + " [" + patches.size() + "]");
					for (Iterator<String> iterator2 = patches.iterator(); iterator2.hasNext();) {
						list.add("         |-" + iterator2.next());
						repoPatchCount++;
					}
				}
			}

			if (repoPatchCount > topRepoPatchCount) {
				topRepoPatchCount = repoPatchCount;
				topRepoPatchCountName = entry.getKey();
			}

			if (!list.isEmpty()) {
				System.out.println(entry.getKey() + " (" + repoPatchCount + ")");
				for (String temp : list) {
					System.out.println(temp);
				}

				System.out.println();

			}
		}

		System.out.println("Repository with the most number of patches (since IS 5.2.0): " + topRepoPatchCountName
				+ " (" + topRepoPatchCount + ")");
		System.out.println("Component with the most number of patches (since IS 5.2.0): " + topCompPatchCountName + " ("
				+ topCompPatchCount + ") [" + topCompPatchCountRepoName + "]");

	}

	/**
	 * 
	 * @param filePath
	 * @param prefix
	 */
	private static void addRepo(String filePath, String prefix) {

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
				if (line != null && line.length() > 2) {
					line = line.replace("./", "");
					if (line.indexOf("/") > 0) {
						String repoName = line.substring(0, line.indexOf("/"));
						if (line.indexOf("org.wso2.carbon") > 0) {
							String componentName = line.substring(line.indexOf("org.wso2.carbon"), line.length());
							if (componentName.indexOf("/") > 0) {
								componentName = componentName.substring(0, componentName.indexOf("/"));
							}

							if (!skipRepos.contains(repoName)) {
								if (prefix != null) {
									repoName = prefix + repoName;
								}

								if (repos.containsKey(repoName)) {
									if (!repos.get(repoName).contains(componentName)) {
										repos.get(repoName).add(componentName);
										Component comp = new Component();
										comp.setComponentName(componentName);
										comp.setRepoName(repoName);
										components.put(componentName, comp);
									}
								} else {
									Set<String> componentSet;
									componentSet = new HashSet<String>();
									componentSet.add(componentName);
									repos.put(repoName, componentSet);
									Component comp = new Component();
									comp.setComponentName(componentName);
									comp.setRepoName(repoName);
									components.put(componentName, comp);
								}
							}
						}
					}
				}
			}
			reader.close();
		} catch (

		IOException e) {
			e.printStackTrace();
		}
	}

	private static void populatePatches(String filePath) {

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
				if (line != null && line.length() > 2) {
					line = line.replace("./", "");
					if (line.indexOf("/") > 0) {
						String patchName = line.substring(0, line.indexOf("/"));
						if (line.indexOf("org.wso2.carbon") > 0 && line.endsWith(".jar")) {
							String jar = line.substring(line.indexOf("org.wso2.carbon"), line.length());
							if (jar.indexOf("_") > 0) {
								jar = jar.substring(0, jar.indexOf("_"));
							}

							Component comp = components.get(jar);
							if (comp != null) {
								comp.addPatch(patchName);
							}
						}
					}
				}
			}
			reader.close();
		} catch (

		IOException e) {
			e.printStackTrace();
		}
	}

}
