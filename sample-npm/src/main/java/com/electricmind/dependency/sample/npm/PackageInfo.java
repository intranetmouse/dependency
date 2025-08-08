package com.electricmind.dependency.sample.npm;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageInfo {
	
	String name;
	String version;
	String license;
	Map<String, String> dependencies;
	Map<String, String> devDependencies;

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getDependencies() {
		return this.dependencies == null ? Collections.emptyMap() : this.dependencies;
	}

	public void setDependencies(Map<String, String> dependencies) {
		this.dependencies = dependencies;
	}

	public String getLicense() {
		return this.license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public Map<String, String> getDevDependencies() {
		return this.devDependencies == null ? Collections.emptyMap() : this.devDependencies;
	}

	public void setDevDependencies(Map<String, String> devDependencies) {
		this.devDependencies = devDependencies;
	}
}
