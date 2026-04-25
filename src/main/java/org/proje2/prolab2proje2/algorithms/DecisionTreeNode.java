package org.proje2.prolab2proje2.algorithms;

public class DecisionTreeNode{

    DecisionTreeNode leftNode; //Left branch
    DecisionTreeNode rightNode; //Right branch
    String attribute; //Question to be asked

    boolean isLeaf; //Is last node at the branch?
    String resultCategory; //If last node at branch predicted category

    //Leaf Node Structure
    public DecisionTreeNode(String resultCategory){
        this.resultCategory=resultCategory;
        this.isLeaf=true;
    }

    //Branch Node Structure
    public DecisionTreeNode(String attribute , DecisionTreeNode leftNode , DecisionTreeNode rightNode){

        this.attribute=attribute;
        this.leftNode=leftNode;
        this.rightNode=rightNode;
        this.isLeaf=false;
    }
}