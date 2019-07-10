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

	private static final String WSO2_TREE_DIR = "/identity-repos/.repodata/wso2-components";
	private static final String WSO2_EXT_TREE_DIR = "/identity-repos/.repodata/wso2-extensions-components";
	private static final String PATCHES_TREE = "/identity-repos/.repodata/updates";

	private static final String TREE_580_ = "/is580";
	private static final String TREE_570_ = "/is570";
	private static final String TREE_560_ = "/is560";
	private static final String TREE_550_ = "/is550";
	private static final String TREE_541_ = "/is541";
	private static final String TREE_540_ = "/is540";
	private static final String TREE_530_ = "/is530";
	private static final String TREE_520_ = "/is520";
	private static final String TREE_510_ = "/is510";
	private static final String TREE_500_ = "/is500";
	private static final String TREE_460_ = "/is460";

	private static Map<String, Set<String>> repos = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> productVersions = new HashMap<String, Set<String>>();
	private static Map<String, Component> components = new HashMap<String, Component>();
	private static List<String> skipRepos = new ArrayList<>();

	public static void main(String[] args) {

		skipRepos.add("identity-test-integration");

		addRepo(WSO2_TREE_DIR, "https://github.com/wso2/");
		addRepo(WSO2_EXT_TREE_DIR, "https://github.com/wso2-extensions/");
		populateProducts(TREE_460_, "IS_4.6.0");
		populateProducts(TREE_500_, "IS_5.0.0");
		populateProducts(TREE_510_, "IS_5.1.0");
		populateProducts(TREE_520_, "IS_5.2.0");
		populateProducts(TREE_530_, "IS_5.3.0");
		populateProducts(TREE_540_, "IS_5.4.0");
		populateProducts(TREE_541_, "IS_5.4.1");
		populateProducts(TREE_550_, "IS_5.5.0");
		populateProducts(TREE_560_, "IS_5.6.0");
		populateProducts(TREE_570_, "IS_5.7.0");
		populateProducts(TREE_580_, "IS_5.8.0");

		populatePatches(PATCHES_TREE);

		if (args.length == 2 && "-j".equals(args[0]) && !args[1].isEmpty()) {
			String componentName = args[1];
			Component comp = components.get(componentName);
			if (comp != null && !comp.getPatches().isEmpty()) {
				System.out.println("Repo Name: " + comp.getRepoName());
				System.out.println("Component Name: " + comp.getComponentName());
				System.out.println("Patches (" + comp.getPatches().size() + "): ");
				List<Patch> patches = comp.getPatches();
				for (Iterator<Patch> iterator2 = patches.iterator(); iterator2.hasNext();) {
					Patch patch = iterator2.next();
					Set<String> products = patch.getProductVersion();
					StringBuffer buffer = new StringBuffer();
					if (products != null && !products.isEmpty()) {
						for (Iterator<String> iterator = products.iterator(); iterator.hasNext();) {
							buffer.append(iterator.next() + " ");
						}
					} else {
						buffer.append("No Product Version");
					}

					System.out.println(patch.getName() + " | " + patch.getJarVersion() + " | " + buffer.toString());
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
					List<Patch> patches = jar.getPatches();
					if (patches != null && patches.size() > 0) {
						System.out.println("  |-" + jar.getComponentName() + " [" + patches.size() + "]");

						for (Iterator<Patch> iterator2 = patches.iterator(); iterator2.hasNext();) {
							Patch patch = iterator2.next();

							Set<String> products = patch.getProductVersion();

							StringBuffer buffer = new StringBuffer();
							if (products != null && !products.isEmpty()) {
								for (Iterator<String> prod = products.iterator(); prod.hasNext();) {
									buffer.append(prod.next() + " ");
								}
							} else {
								buffer.append("No Product Version");
							}

							System.out.println("         |-" + patch.getName() + " | " + patch.getJarVersion() + " | "
									+ buffer.toString());
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
				List<Patch> patches = jar.getPatches();
				if (patches != null && patches.size() > 0) {

					if (patches.size() > topCompPatchCount) {
						topCompPatchCount = patches.size();
						topCompPatchCountName = jar.getComponentName();
						topCompPatchCountRepoName = jar.getRepoName();
					}

					list.add("  |-" + jar.getComponentName() + " [" + patches.size() + "]");
					for (Iterator<Patch> iterator2 = patches.iterator(); iterator2.hasNext();) {
						Patch patch = iterator2.next();
						list.add("         |-" + patch.getName() + " (" + patch.getJarVersion() + ")");
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

		BufferedReader reader = null;
		try {
			// read all the git repo from the provided file.
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

								if (repos.containsKey(repoName)) {
									if (!repos.get(repoName).contains(componentName)) {
										repos.get(repoName).add(componentName);
										components.put(componentName, new Component(repoName, componentName));
									}
								} else {
									Set<String> componentSet;
									componentSet = new HashSet<String>();
									componentSet.add(componentName);
									repos.put(repoName, componentSet);
									components.put(componentName, new Component(repoName, componentName));
								}
							}
						}
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * @param filePath
	 */
	private static void populateProducts(String filePath, String version) {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
				if (line != null && line.length() > 2) {
					if (line.startsWith("org.wso2.carbon") && line.endsWith(".jar")) {
						line.replaceAll("-", "_");
						if (productVersions.containsKey(line)) {
							if (line.startsWith("org.wso2.carbon.user.mgt_")) {
								productVersions.get(line).add(version);
							} else {
								productVersions.get(line).add(version);
							}
						} else {
							Set<String> versions = new HashSet<String>();
							versions.add(version);
							productVersions.put(line, versions);
						}
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * @param filePath
	 */
	private static void populatePatches(String filePath) {

		BufferedReader reader = null;
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
							String jarVersion = null;
							jar.replaceAll("-", "_");
							Set<String> products = productVersions.get(jar);
							if (jar.indexOf("_") > 0) {
								String[] parts = jar.split("_");
								jar = parts[0];
								jarVersion = parts[1];
								jarVersion = jarVersion.substring(0, jarVersion.indexOf(".jar"));

								// jar.substring(0, jar.indexOf("_"));
							}

							Component comp = components.get(jar);
							if (comp != null) {
								comp.addPatch(new Patch(patchName, jarVersion, products));
							}
						}
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
