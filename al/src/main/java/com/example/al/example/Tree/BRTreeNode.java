package com.example.al.example.Tree;

/**
 * @description:　红黑树
 * @author: benhairui
 * @date: 2019/08/16 19:31:26
 */
public class BRTreeNode {

    /**
     * @Description: 当前节点的值
     * @Date: 19-8-16 下午7:33
     * @param: null
     * @return:
     **/
    private double num;

    /**
     * @Description: 当前节点的颜色
     * @Date: 19-8-16 下午7:47
     * @param: null
     * @return:
     **/
    private Color color;

    /**
     * @Description: 记录是父亲的左儿子还是右儿子
     * @Date: 19-8-16 下午8:00
     * @param: null
     * @return:
     **/
    private Direction dir;

    /**
     * @Description: 左儿子节点
     * @Date: 19-8-16 下午7:33
     * @param: null
     * @return:
     **/
    private BRTreeNode leftNode;

    /**
     * @Description: 右儿子节点
     * @Date: 19-8-16 下午7:33
     * @param: null
     * @return:
     **/
    private BRTreeNode rightNode;

    /**
     * @Description: 父亲节点
     * @Date: 19-8-16 下午7:33
     * @param: null
     * @return:
     **/
    private BRTreeNode parentNode;




    public void setNum(double num) {
        this.num = num;
    }

    public double getNum() {
        return num;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public Direction getDir() {
        return dir;
    }

    public void setLeftNode(BRTreeNode leftNode) {
        this.leftNode = leftNode;
    }

    public BRTreeNode getLeftNode() {
        return leftNode;
    }


    public void setRightNode(BRTreeNode rightNode) {
        this.rightNode = rightNode;
    }

    public BRTreeNode getRightNode() {
        return rightNode;
    }

    public void setParentNode(BRTreeNode parentNode) {
        this.parentNode = parentNode;
    }

    public BRTreeNode getParentNode() {
        return parentNode;
    }
}

enum Color {
    Red,Black
}


enum Direction{
    LEFT,RIGHT
}
