package org.facilelogin.wso2is.repo.explorer;

import java.util.ArrayList;
import java.util.List;

public class Component {

    private String repoName;
    private String componentName;
    private List<String> patches = new ArrayList<String>();

    public String getRepoName() {
	return repoName;
    }

    public void setRepoName(String repoName) {
	this.repoName = repoName;
    }

    public String getComponentName() {
	return componentName;
    }

    public void setComponentName(String componentName) {
	this.componentName = componentName;
    }

    public List<String> getPatches() {
	return patches;
    }

    public void addPatch(String patch) {
	this.patches.add(patch);
    }

}
