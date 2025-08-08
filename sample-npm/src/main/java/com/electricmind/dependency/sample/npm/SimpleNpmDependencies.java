package com.electricmind.dependency.sample.npm;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.electricmind.dependency.DependencyManager;
import com.electricmind.dependency.graph.Grapher;
import com.electricmind.dependency.graph.shape.ArtifactNpmStereotypeShapeProvider;
import com.electricmind.dependency.graph.shape.ArtifactShape;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleNpmDependencies {

	public static void main(String[] args) throws Exception {

		try (InputStream input = SimpleNpmDependencies.class.getResourceAsStream("/package-lock.json")) {
			PackageLock packageLock = new ObjectMapper().readValue(input, PackageLock.class);
			Map<String, NpmPackageName> nameMap = assembleNameMap(packageLock);
			Map<String, NpmPackageInfo> infoMap = assembleInfoMap(packageLock);

			DependencyManager<NpmPackageName> dependencies = assembleDependencies(packageLock, nameMap);
			Map<String, DependencyType> typeMap = DependencyTypeResolver.determineDependencyTypes(
					dependencies, assembleInitialTypeMap(packageLock));
			
			createGraphDiagram(assembleRuntimeDependencies(packageLock, nameMap, typeMap));
			BillOfMaterialsExporter.createSpreadsheet(dependencies, infoMap, typeMap);
		}
	}

	private static Map<String, DependencyType> assembleInitialTypeMap(PackageLock packageLock) {
		Map<String, DependencyType> initialTypeMap = new HashMap<>();
		PackageInfo info = packageLock.getPackages().get("");
		initialTypeMap.put(info.getName(), DependencyType.Main);
		info.getDevDependencies().keySet().forEach(k -> {
			initialTypeMap.put(k, DependencyType.Development);
		});
		info.getDependencies().keySet().forEach(k -> {
			initialTypeMap.put(k, DependencyType.Runtime);
		});
		return initialTypeMap;
	}

	private static DependencyManager<NpmPackageName> assembleDependencies(PackageLock packageLock, Map<String, NpmPackageName> map) {
		DependencyManager<NpmPackageName> dependencies = new DependencyManager<>();

		for (Map.Entry<String, PackageInfo> entry : packageLock.getPackages().entrySet()) {
			String name = entry.getKey();				
			PackageInfo info = entry.getValue();
			name = "".equals(name) ? info.getName() : StringUtils.substringAfter(name, "node_modules/");
			NpmPackageName packageName = new NpmPackageName(name, info.getVersion());

			dependencies.add(packageName);
			for (String dependency : info.getDependencies().keySet()) {
				NpmPackageName dependencyName = map.get(dependency);
				if (dependencyName != null) {
					dependencies.add(packageName, dependencyName);
				}
			}
		}
		return dependencies;
	}

	private static DependencyManager<NpmPackageName> assembleRuntimeDependencies(PackageLock packageLock, Map<String, NpmPackageName> map, Map<String, DependencyType> typeMap) {
		DependencyManager<NpmPackageName> dependencies = new DependencyManager<>();

		for (Map.Entry<String, PackageInfo> entry : packageLock.getPackages().entrySet()) {
			String name = entry.getKey();				
			PackageInfo info = entry.getValue();
			name = "".equals(name) ? info.getName() : StringUtils.substringAfter(name, "node_modules/");
			NpmPackageName packageName = new NpmPackageName(name, info.getVersion());

			DependencyType type = typeMap.get(name);
			if ("jest-cli".endsWith(name)) {
				System.out.println("Dependency type: " + type);
			}
			if (type != DependencyType.Development) {
				dependencies.add(packageName);
				for (String dependency : info.getDependencies().keySet()) {
					NpmPackageName dependencyName = map.get(dependency);
					if (dependencyName != null && typeMap.get(dependency) != DependencyType.Development) {
						dependencies.add(packageName, dependencyName);
					}
				}
			}
		}
		return dependencies;
	}

	private static Map<String, NpmPackageName> assembleNameMap(PackageLock packageLock) {
		Map<String, NpmPackageName> result = new HashMap<>();

		for (Map.Entry<String, PackageInfo> entry : packageLock.getPackages().entrySet()) {
			String name = entry.getKey();				
			if (!"".equals(name)) {
				PackageInfo info = entry.getValue();
				NpmPackageName packageName = new NpmPackageName(StringUtils.substringAfter(name, "node_modules/"), info.getVersion());
				result.put(packageName.getName(), packageName);
			}
		}
		return result;
	}

	private static Map<String, NpmPackageInfo> assembleInfoMap(PackageLock packageLock) {
		Map<String, NpmPackageInfo> infoMap = new HashMap<>();
		
		for (Map.Entry<String, PackageInfo> entry : packageLock.getPackages().entrySet()) {
			String name = entry.getKey();				
			if (!"".equals(name)) {
				PackageInfo info = entry.getValue();
				NpmPackageName packageName = new NpmPackageName(StringUtils.substringAfter(name, "node_modules/"), info.getVersion());
				NpmPackageInfo packageInfo = new NpmPackageInfo(packageName, info.getLicense());
				infoMap.put(packageName.getName(), packageInfo);
			}
		}
		return infoMap;
	}
	
	private static void createGraphDiagram(DependencyManager<NpmPackageName> dependencies) throws IOException {
		Grapher<NpmPackageName> grapher = new Grapher<>(dependencies);
		grapher.getPlot().setShadowColor(new Color(0f, 0f, 0f, 0.4f));
		grapher.getPlot().setLayerAlternatingColor(new Color(203, 219, 252));
		grapher.getPlot().setUseWeights(false);
		ArtifactShape<NpmPackageName> shape = new ArtifactShape<>();
		shape.setLabelStrategy(new NpmPackageLabelStrategy());
		shape.setStereotypeShapeProvider(new ArtifactNpmStereotypeShapeProvider());
		grapher.setShape(shape);
		try (OutputStream output = new FileOutputStream("./target/npmArtifactGraph.svg")) {
			grapher.createSvg(output);
		}
	}
}
