package org.palladiosimulator.simexp.dsl.smodel.ui.outline;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.AbstractOutlineNode;

public class TextNode extends AbstractOutlineNode {

    public TextNode(IOutlineNode parent, Image image, Object text, boolean isLeaf) {
        super(parent, image, text, isLeaf);
    }

    public TextNode(IOutlineNode parent, ImageDescriptor imageDescriptor, Object text, boolean isLeaf) {
        super(parent, imageDescriptor, text, isLeaf);
    }

}
