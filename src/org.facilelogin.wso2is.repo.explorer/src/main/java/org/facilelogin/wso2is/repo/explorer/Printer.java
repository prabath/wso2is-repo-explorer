package org.facilelogin.wso2is.repo.explorer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.facilelogin.wso2is.repo.explorer.bean.Component;
import org.facilelogin.wso2is.repo.explorer.bean.Patch;

public class Printer {

	private Map<String, Set<String>> repos;
	private Map<String, Component> components;

	public Printer(Crawler crawler) {
		this.repos = crawler.repos;
		this.components = crawler.components;
	}

	/**
	 * 
	 */
	public void printAllPatches() {
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
	 * @param componentName
	 */
	public void printPatchesByComponentName(String componentName) {
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
	}

	/**
	 * 
	 * @param repo
	 */
	public void printPatchesByRepo(String repo) {
		String repoName = "https://github.com/wso2-extensions/" + repo;
		Set<String> compNames = repos.get(repoName);

		if (compNames == null || compNames.size() == 0) {
			repoName = "https://github.com/wso2/" + repo;
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
	}
}
