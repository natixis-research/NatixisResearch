package com.natixis.natixisresearch.app.network.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.natixis.natixisresearch.app.network.bean.type.NodeType;

import java.util.List;

/**
 * Created by Thibaud on 07/04/2017.
 */
public class ResearchNode {
    @JsonProperty("NodeID")
    private String nodeID;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("NodeType")
    private int nodeType;
    @JsonProperty("Nodes")
    private List<ResearchNode> nodes;
    @JsonProperty("Doc")
    private ResearchDocument document;

    boolean hasSubNodesParsed = false;
    boolean hasSubNodes = false;

    public boolean hasSubNodes() {
        //Check if subnodes are real subnodes or if it's a leaf with media like document.
        if (!hasSubNodesParsed) {
            if(nodes!=null && nodes.size()>0) {
                for (ResearchNode node : nodes) {
                    if (node.getNodeType() == NodeType.NODE) {
                        hasSubNodesParsed = true;
                        hasSubNodes = true;
                        return hasSubNodes;
                    }
                }
            }
            hasSubNodesParsed = true;
            hasSubNodes = false;
        }
        return hasSubNodes;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public NodeType getNodeType() {
        return NodeType.forValue(nodeType);
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public List<ResearchNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ResearchNode> nodes) {
        this.nodes = nodes;
    }

    public ResearchDocument getDocument() {
        return document;
    }

    public void setDocument(ResearchDocument document) {
        this.document = document;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchNode)) return false;

        ResearchNode that = (ResearchNode) o;

        if (nodeType != that.nodeType) return false;
        if (document != null ? !document.equals(that.document) : that.document != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (!nodeID.equals(that.nodeID)) return false;
        if (nodes != null ? !nodes.equals(that.nodes) : that.nodes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeID.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + nodeType;
        result = 31 * result + (nodes != null ? nodes.hashCode() : 0);
        result = 31 * result + (document != null ? document.hashCode() : 0);
        return result;
    }
}
