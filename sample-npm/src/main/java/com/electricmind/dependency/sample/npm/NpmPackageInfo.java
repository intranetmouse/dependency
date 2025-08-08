package com.electricmind.dependency.sample.npm;

public class NpmPackageInfo {

	NpmPackageName name;
	DependencyType dependencyType;
	String license;
	
	public NpmPackageInfo() {
	}

	public NpmPackageInfo(NpmPackageName name, String license) {
		this.name = name;
		this.license = license;
	}

	public DependencyType getDependencyType() {
		return this.dependencyType;
	}

	public void setDependencyType(DependencyType dependencyType) {
		this.dependencyType = dependencyType;
	}

	public String getLicense() {
		return this.license;
	}

	public NpmPackageName getName() {
		return this.name;
	}
}
