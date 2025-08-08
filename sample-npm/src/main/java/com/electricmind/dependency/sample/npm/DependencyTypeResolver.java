package com.electricmind.dependency.sample.npm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.electricmind.dependency.Coupling;
import com.electricmind.dependency.DependencyManager;
import com.electricmind.dependency.Layer;
import com.electricmind.dependency.Node;

public class DependencyTypeResolver {

	static Map<String, DependencyType> determineDependencyTypes(DependencyManager<NpmPackageName> dependencies, Map<String, DependencyType> initialTypeMap) {
		Map<String, DependencyType> result = new HashMap<>(initialTypeMap);
		
		List<Layer<Node<NpmPackageName>>> layers = dependencies.getLayeredGraph().getLayers();
		Collections.reverse(layers);

		for (int times = 3; times > 0; times--) {
			for (Layer<Node<NpmPackageName>> layer : layers) {
				for (Node<NpmPackageName> node : layer.getContents()) {
					
					String packageName = node.getName();
					
					DependencyType type = result.get(packageName);
					if (type == null) {
						DependencyType temp = DependencyType.Development;
						for (Coupling<NpmPackageName> coupling : node.getAfferentCouplings()) {
							DependencyType dependencyType = initialTypeMap.get(coupling.getItem().getName());
							if (dependencyType == null) {
								// possibly a cyclic dependency
								if (times > 1 && type == DependencyType.Development) {
									temp = null;
								}
							} else if (dependencyType == DependencyType.Runtime || dependencyType == DependencyType.Main) {
								temp = DependencyType.Runtime;
							}
						}
						
						type = temp;
						result.put(packageName, temp);
					}
				}
			}
		}
		
		return result;
	}
}
