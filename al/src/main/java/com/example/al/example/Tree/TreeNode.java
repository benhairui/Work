package com.example.al.example.Tree;

/**
 * @description:
 * @author: benhairui
 * @date: 2019/08/15 10:05:20
 */

public class TreeNode {

    /**
     * @Description: 该节点的值
     * @Date: 19-8-15 上午10:05
     * @param: null
     * @return:
     **/
    double value;

    /**
     * @Description: 左儿子
     * @Date: 19-8-15 上午10:06
     * @param: null
     * @return:
     **/
    TreeNode leftNode;

    /**
     * @Description: 右儿子
     * @Date: 19-8-15 上午10:06
     * @param: null
     * @return:
     **/
    TreeNode rightNode;


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public TreeNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(TreeNode leftNode) {
        this.leftNode = leftNode;
    }

    public TreeNode getRightNode() {
        return rightNode;
    }

    public void setRightNode(TreeNode rightNode) {
        this.rightNode = rightNode;
    }
}
