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
	protected Map<String, Set<String>> productsMap;

	public Printer(Crawler crawler) {
		this.repos = crawler.repos;
		this.components = crawler.components;
		this.productsMap = crawler.productsMap;
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
	public void printPatchesByProduct(String version) {

		if (version == null) {
			Set<String> keys = productsMap.keySet();
			for (Iterator<String> key = keys.iterator(); key.hasNext();) {
				String k = key.next();
				System.out.println(k + ":" + productsMap.get(k).size());
			}
		} else {
			Set<String> patchSet = productsMap.get(version);
			if (patchSet != null && !patchSet.isEmpty()) {
				System.out.println("Product Version: " + version);
				System.out.println("Number of Updates: " + patchSet.size());
				System.out.println();
				for (Iterator<String> patches = patchSet.iterator(); patches.hasNext();) {
					System.out.println(patches.next());
				}
			} else {
				System.out.println("No patches found for the given product version!");
			}
		}
	}

	/**
	 * 
	 * @param componentName
	 */
	public void printPatchesByComponentName(String componentName, String version) {
		Component comp = components.get(componentName);
		if (comp != null && !comp.getPatches().isEmpty()) {
			System.out.println("Repo Name: " + comp.getRepoName());
			System.out.println("Component Name: " + comp.getComponentName());
			System.out.println("Updates (" + comp.getPatches().size() + "): ");
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
		Set<String> compNames = repos.get(repoName);

		if (compNames == null || compNames.size() == 0) {
			// not found under wso2-extensions.
			// find the all the components in the given repository, assuming its from wso2 git org.
			repoName = "https://github.com/wso2/" + repo;
			compNames = repos.get(repoName);
		}

		if (compNames != null && compNames.size() > 0) {
			// now we have a valid git repo - and we know all the components under that.

			// to keep track of all the patches provided under this repo.
			int totalPatches = 0;
			// to keep track of all the patches provided under this repo for the provided product version.
			int productPatchesByRepo = 0;

			StringBuffer buffer = new StringBuffer();

			// iterate through all the components in the repo to find patches under each component.
			for (Iterator<String> compIterator = compNames.iterator(); compIterator.hasNext();) {
				Component component = components.get(compIterator.next());

				// find all the patches under this component.
				List<Patch> patches = component.getPatches();

				// initializes patch buffer for each component.
				StringBuffer patchBuffer = new StringBuffer();

				if (patches != null && patches.size() > 0) {
					// this component has one or more patches.

					int productPatchesByComponent = 0;
					patchBuffer = new StringBuffer();

					for (Iterator<Patch> patchIterator = patches.iterator(); patchIterator.hasNext();) {
						Patch patch = patchIterator.next();

						// we have a patch - increment the total repo patch count.
						totalPatches++;

						// find the applicable product version of this patch.
						// there can be cases where the same patch is applicable to more than one product.
						Set<String> products = patch.getProductVersion();

						StringBuffer verBuffer = new StringBuffer();

						if (products != null && !products.isEmpty()) {
							// we know the product(s), where this patch is applicable.
							for (Iterator<String> prodIterator = products.iterator(); prodIterator.hasNext();) {
								String prodVersion = prodIterator.next();
								if (version != null) {
									// we only need to filter the patches corresponding to the provided product version.
									if (version.equalsIgnoreCase(prodVersion)) {
										// we have patch applicable to the provided product version.
										productPatchesByRepo++;
										productPatchesByComponent++;
										patchBuffer.append(
												"         |-" + patch.getName() + " | " + patch.getJarVersion());
										patchBuffer.append("\n");
									}
								} else {
									// we do not worry about product version.
									verBuffer.append(prodVersion + " ");
								}
							}
						} else {
							verBuffer.append("No Product Version");
						}

						if (version == null) {
							patchBuffer.append("         |-" + patch.getName() + " | " + patch.getJarVersion() + " | "
									+ verBuffer.toString());
							patchBuffer.append("\n");
						}
					}

					if (version != null) {
						if (productPatchesByComponent > 0) {
							buffer.append("  |-" + component.getComponentName() + " [" + productPatchesByComponent + "/"
									+ patches.size() + "]");
							buffer.append("\n");
							buffer.append(patchBuffer.toString());
						}
					} else {
						buffer.append("  |-" + component.getComponentName() + " [" + patches.size() + "]");
						buffer.append("\n");
						buffer.append(patchBuffer.toString());
					}

				}
			}

			if (version != null) {
				System.out.println("Repo Name: " + repoName + "(" + productPatchesByRepo + "/" + totalPatches + ")");
			} else {
				System.out.println("Repo Name: " + repoName + "(" + totalPatches + ")");
			}

			System.out.println(buffer.toString());

		} else {
			System.out.println("No patches found for the given repo!");
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
