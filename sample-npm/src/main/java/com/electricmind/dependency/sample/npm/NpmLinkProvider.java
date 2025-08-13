package com.electricmind.dependency.sample.npm;

import com.electricmind.dependency.Node;
import com.electricmind.dependency.graph.LinkProvider;

public class NpmLinkProvider extends LinkProvider {

	@Override
	public String getLinkForNode(Node<?> node) {
		String baseUrl = "https://www.npmjs.com/package/";
		return baseUrl + node.getName();
	}
}
