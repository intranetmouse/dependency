package com.electricmind.dependency.sample.npm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.electricmind.dependency.DependencyManager;
import com.electricmind.dependency.Layer;
import com.electricmind.dependency.Node;

class BillOfMaterialsExporter {

	static void createSpreadsheet(DependencyManager<NpmPackageName> dependencies, Map<String, NpmPackageInfo> infoMap, 
			Map<String, DependencyType> typeMap) throws IOException {
		
		try (OutputStream output = new FileOutputStream("./target/npmArtifactDependencies.csv")) {
			output.write("Level,".getBytes("utf-8"));
			output.write("Name,".getBytes("utf-8"));
			output.write("Version,".getBytes("utf-8"));
			output.write("Type,".getBytes("utf-8"));
			output.write("License,".getBytes("utf-8"));
			output.write("Dependencies".getBytes("utf-8"));
			output.write("\n".getBytes("utf-8"));
			
			List<Layer<Node<NpmPackageName>>> layers = dependencies.getLayeredGraph().getLayers();
			Collections.reverse(layers);
			
			typeMap = DependencyTypeResolver.determineDependencyTypes(dependencies, typeMap);
			for (Layer<Node<NpmPackageName>> layer : layers) {
				for (Node<NpmPackageName> node : layer.getContents()) {
					
					String packageName = node.getName();
					NpmPackageInfo info = infoMap.get(packageName);
					
					String dependenciesComment = "";
					DependencyType type = typeMap.get(packageName);
					
					if (type == DependencyType.Main) {
						dependenciesComment = "Top level";
					} else {
						dependenciesComment = "Depended upon by " + node.getAfferentCouplings().stream().map(c -> c.getItem().getName()).collect(Collectors.joining(", "));
					}
					
					output.write(("" + layer.getLevel()).getBytes("utf-8"));
					output.write(",".getBytes("utf-8"));
					output.write(node.getItem().getName().getBytes("utf-8"));
					output.write(",".getBytes("utf-8"));
					output.write(node.getItem().getVersion().getBytes("utf-8"));
					output.write(",".getBytes("utf-8"));
					if (type != null) {
						output.write(("\"" + type.name() + "\"").getBytes("utf-8"));
					} else {
						output.write("\"\"".getBytes("utf-8"));
					}
					output.write(",".getBytes("utf-8"));
					if (info != null) {
						if (StringUtils.isNotEmpty(info.getLicense())) {							
							output.write(("\"" + info.getLicense() + "\"").getBytes("utf-8"));
						}
					} else {
						output.write("\"\"".getBytes("utf-8"));
					}
					output.write(",".getBytes("utf-8"));
					output.write(("\"" + dependenciesComment + "\"").getBytes("utf-8"));
					output.write("\n".getBytes("utf-8"));
				}				
			}
		}
	}
}
