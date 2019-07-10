package org.facilelogin.wso2is.repo.explorer;

import java.util.ArrayList;
import java.util.List;

public class Component {

	private String repoName;
	private String componentName;
	private List<Patch> patches = new ArrayList<Patch>();

	public Component(String repoName, String componentName, Patch patch) {
		super();
		this.repoName = repoName;
		this.componentName = componentName;
		this.patches.add(patch);
	}

	public Component(String repoName, String componentName) {
		super();
		this.repoName = repoName;
		this.componentName = componentName;
	}

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

	public List<Patch> getPatches() {
		return patches;
	}

	public void addPatch(Patch patch) {
		this.patches.add(patch);
	}

}
