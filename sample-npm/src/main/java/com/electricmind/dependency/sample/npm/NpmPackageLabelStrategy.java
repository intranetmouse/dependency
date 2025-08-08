package com.electricmind.dependency.sample.npm;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.electricmind.dependency.Node;
import com.electricmind.dependency.graph.TextLabel;
import com.electricmind.dependency.graph.TextLabelOption;
import com.electricmind.dependency.graph.TextLabelStrategy;
import com.electricmind.dependency.graph.shape.StereotypeProvider;

public class NpmPackageLabelStrategy extends TextLabelStrategy implements StereotypeProvider {
	
	boolean simple = false;
	int count = 0;

	@Override
	public void populate(Node<?> node, TextLabel textLabel, Point2D upperLeft, OutputStream outputStream) throws IOException {
		if (this.simple) {
			super.populate(node, textLabel, upperLeft, outputStream);
		} else {
			String name = node.getName();
			if (hasPrefix(name)) {
				textLabel.drawStringSvg(StringUtils.substringBefore(name, "/") + "/", 0, upperLeft, outputStream);
				textLabel.drawStringSvg(StringUtils.substringAfter(name, "/"), 1, upperLeft, outputStream);
			} else {
				int labelNumber = 0;
				if (count == 3) {
					labelNumber = 1;
				} else if (count == 2 && node.getItem() instanceof NpmPackageName) {
					labelNumber = 0;					
				} else if (count == 2) {
					labelNumber = 1;					
				}
				textLabel.drawStringSvg(name, labelNumber, upperLeft, outputStream);
			}
			if (node.getItem() instanceof NpmPackageName) {
				String version = ((NpmPackageName) node.getItem()).getVersion();
				textLabel.drawStringSvg(version, count-1, upperLeft, outputStream);
			}
		}
	}

	
	@Override
	public void initialize(Graphics2D graphics, TextLabel textLabel, List<Node<?>> nodes) {
		List<String> firstPart = new ArrayList<>();
		List<String> secondPart = new ArrayList<>();
		List<String> versions = new ArrayList<>();
		for (Node<?> node : nodes) {
			String name = node.getName();
			if (hasPrefix(name)) {
				firstPart.add(StringUtils.substringBefore(name, "/") + "/");
				secondPart.add(StringUtils.substringBefore(name, "/"));
			} else {
				secondPart.add(name);
			}
			if (node.getItem() instanceof NpmPackageName) {
				versions.add(((NpmPackageName) node.getItem()).getVersion());
			}
		}
		
		if (!firstPart.isEmpty() && !versions.isEmpty()) {	
			count = 3;
			textLabel.initialize(graphics, 
					new TextLabelOption(Font.PLAIN, firstPart), 
					new TextLabelOption(Font.BOLD, secondPart),
					new TextLabelOption(Font.PLAIN, versions));
		} else if (!firstPart.isEmpty()) {
			count = 2;
			textLabel.initialize(graphics, 
					new TextLabelOption(Font.PLAIN, firstPart), 
					new TextLabelOption(Font.BOLD, secondPart));
		} else if (!versions.isEmpty()) {
			count = 2;
			textLabel.initialize(graphics, 
					new TextLabelOption(Font.BOLD, secondPart),
					new TextLabelOption(Font.PLAIN, versions)); 
		} else {
			count = 1;
			simple = true;
		}
	}


	private boolean hasPrefix(String name) {
		return name.contains("/");
	}


	@Override
	public String getStereotype(Node<?> node) {
		return "NPM Package";
	}
}
