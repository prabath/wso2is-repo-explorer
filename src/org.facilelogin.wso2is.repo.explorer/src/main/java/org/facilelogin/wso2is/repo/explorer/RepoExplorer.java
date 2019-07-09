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

    private static final String WSO2_TREE_DIR = "/Users/prabathsiriwardana/git/wso2is/wso2.tree.dir";
    private static final String WSO2_EXT_TREE_DIR = "/Users/prabathsiriwardana/git/wso2is/wso2-extensions.tree.dir";
    private static final String PATCHES_TREE = "/Users/prabathsiriwardana/git/patches/unzipped/patches.tree";

    private static final String TREE_570_ = "~/identity-repos/5.7.0.tree";
    private static final String TREE_580_ = "~/identity-repos/5.8.0.tree";

    private static Map<String, Set<String>> repos = new HashMap<String, Set<String>>();
    private static Map<String, Component> components = new HashMap<String, Component>();

    public static void main(String[] args) {
	addRepo(WSO2_TREE_DIR, "https://github.com/wso2/");
	addRepo(WSO2_EXT_TREE_DIR, "https://github.com/wso2-extensions/");
	populatePatches(PATCHES_TREE);

	print();


    }

    private static void print() {

	for (Map.Entry<String, Set<String>> entry : repos.entrySet()) {

	    List<String> list = new ArrayList<>();
            int repoPatchCount = 0;

	    Set<String> values = entry.getValue();
	    for (Iterator<String> iterator = values.iterator(); iterator.hasNext();) {
		Component jar = components.get(iterator.next());
		List<String> patches = jar.getPatches();
		if (patches != null && patches.size() > 0) {
		    list.add("  |-" + jar.getComponentName() + " [" + patches.size() + "]");
		    for (Iterator<String> iterator2 = patches.iterator(); iterator2.hasNext();) {
			list.add("         |-" + iterator2.next());
			repoPatchCount++;
		    }
		}
	    }

	    if (!list.isEmpty()) {
		System.out.println(entry.getKey() + " (" + repoPatchCount + ")");
		for (String temp : list) {
		    System.out.println(temp);
		}

		System.out.println();

	    }
	}

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
