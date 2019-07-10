package org.facilelogin.wso2is.repo.explorer;

import java.util.Set;

public class Patch {

	private String name;
	private String jarVersion;
	private Set<String> prioducts;

	public Patch(String name, String jarVersion, Set<String> prioducts) {
		super();
		this.name = name;
		this.jarVersion = jarVersion;
		this.prioducts = prioducts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJarVersion() {
		return jarVersion;
	}

	public void setJarVersion(String jarVersion) {
		this.jarVersion = jarVersion;
	}

	public Set<String> getProductVersion() {
		return prioducts;
	}

	public void setProductVersion(Set<String> prioducts) {
		this.prioducts = prioducts;
	}

}
