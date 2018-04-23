package net.foxdenstudio.sponge.foxguard.plugin.object.path.element;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class StructuredGroupElement extends GroupElement {

    private Map<String, IPathElement> fixedChildren;

    protected StructuredGroupElement(Map<String, IPathElement> fixedChildren) {
        this.children = new HashMap<>(fixedChildren);
        this.fixedChildren = ImmutableMap.copyOf(fixedChildren);
    }

    @Override
    public boolean remove(IPathElement path) {
        if (this.fixedChildren.containsValue(path)) return false;
        return super.remove(path);
    }
}