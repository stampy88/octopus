package org.lisapark.octopus.designer.palette;

import com.google.common.collect.Lists;
import org.lisapark.octopus.core.Node;

import javax.swing.*;
import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class NodeListModel<T extends Node> extends AbstractListModel {

    private List<T> data = Lists.newArrayList();

    protected List<T> getData() {
        return data;
    }

    public void setData(List<T> newData) {
        this.data.clear();
        this.data.addAll(newData);
        fireContentsChanged(this, -1, -1);
    }

    @Override
    public int getSize() {
        List<T> nodes = getData();
        return nodes == null ? 0 : nodes.size();
    }

    @Override
    public Object getElementAt(int index) {
        List<T> nodes = getData();
        return nodes == null ? null : nodes.get(index);
    }

    static <T extends Node> NodeListModel<T> newNodeListModel() {
        return new NodeListModel<T>();
    }
}
