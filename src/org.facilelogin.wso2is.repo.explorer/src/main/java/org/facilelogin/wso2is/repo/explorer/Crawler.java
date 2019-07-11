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

	private static final String WSO2_TREE_DIR = "/identity-repos/.repodata/wso2-components";
	private static final String WSO2_EXT_TREE_DIR = "/identity-repos/.repodata/wso2-extensions-components";
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

	protected Map<String, Set<String>> repos;
	protected Map<String, Component> components;

	private Map<String, Set<String>> productVersions = new HashMap<String, Set<String>>();
	protected Map<String, Set<String>> productsMap = new HashMap<String, Set<String>>();
	private List<String> skipRepos = new ArrayList<>();

	/**
	 * 
	 * @param repos
	 * @param productVersions
	 * @param components
	 */
	public Crawler(Map<String, Set<String>> repos, Map<String, Component> components) {
		this.repos = repos;
		this.components = components;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public void populateData() throws IOException {
		skipRepos.add("identity-test-integration");
		addRepo(WSO2_TREE_DIR, "https://github.com/wso2/");
		addRepo(WSO2_EXT_TREE_DIR, "https://github.com/wso2-extensions/");
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
							}
							Component comp = components.get(jar);
							if (comp != null) {
								comp.addPatch(new Patch(patchName, jarVersion, products));
								for (Iterator<String> iterator = products.iterator(); iterator.hasNext();) {
									String prod = (String) iterator.next();

									if (productsMap.containsKey(prod)) {
										productsMap.get(prod).add(patchName);
									} else {
										Set<String> patchSet  = new HashSet<String>();
										patchSet.add(patchName);
										productsMap.put(prod, patchSet);
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
