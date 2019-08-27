package com.example.al.example.Tree;

/**
 * @description:　树结构
 * @author: benhairui
 * @date: 2019/08/13 16:30:33
 */
public class Tree {

//********************************建立查找树 -- begin********************************************************

    /**
     * @Description: 根据数组建立树结构,　弊端: 依赖于给定的数组是不是平衡的,有一种解决方案, 类似于二分查找的，每次寻找中间节点作为该子树的根节点,　左侧的中间节点作为左儿子, 右侧的中间节点作为右儿子, 按照这个原则不断迭代
     * @Date: 19-8-15 上午10:09
     * @param:
     * @return: com.example.al.example.Tree.TreeNode
     **/
    public static TreeNode buildTreeWithArray(double[] array) {
        TreeNode root = new TreeNode();
        root.setValue(array[0]);

        TreeNode preNode = null;
        TreeNode currentNode = null;

        for (int i = 1; i < array.length; i++) {
            currentNode = root;

            TreeNode newNode = new TreeNode();
            newNode.setValue(array[i]);

            while (currentNode != null) {
                preNode = currentNode;
                if (newNode.getValue() >= currentNode.getValue()) {
                    currentNode = currentNode.getRightNode();
                } else {
                    currentNode = currentNode.getLeftNode();
                }
            }

            if (newNode.getValue() >= preNode.getValue()) {
                preNode.setRightNode(newNode);
            } else {
                preNode.setLeftNode(newNode);
            }
        }
        return root;
    }

//********************************建立查找树 -- end********************************************************

//********************************建立avl平衡树　-- begin********************************************************

    /**
     * @Description: 根据数组建立avl平衡树, 任意一颗节点的左儿子子树高度与右儿子子树高度相差不大于1
     * @Date: 19-8-15 上午11:26
     * @param:
     * @return: com.example.al.example.Tree.TreeNode
     **/
    public static TreeNode buildAvlTree(double[] array) {

        TreeNode rootNode = new TreeNode();
        rootNode.setValue(array[0]);

        TreeNode preNode = null;
        TreeNode currentNode = rootNode;


        for (int i = 1; i < array.length; i++) {
            currentNode = rootNode;

            TreeNode newNode = new TreeNode();
            newNode.setValue(array[i]);


            while (currentNode != null) {

                preNode = currentNode;

                if (newNode.getValue() > currentNode.getValue()) {
                    currentNode = currentNode.getRightNode();
                } else if (newNode.getValue() < currentNode.getValue()) {
                    currentNode = currentNode.getLeftNode();
                } else { //如果新值已经存在,那么不再插入
                    break;
                }
            }

            if (newNode.getValue() != preNode.getValue()) {
                if (newNode.getValue() > preNode.getValue()) {
                    preNode.setRightNode(newNode);
                } else {
                    preNode.setLeftNode(newNode);
                }
            } else {
                continue;
            }

            adjustTree(rootNode);
        }


        return rootNode;
    }


    /**
     * @Description: 从根节点进行树结构的调整
     * @Date: 19-8-15 下午1:50
     * @param: rootNode
     * @return: void
     **/
    public static int adjustTree(TreeNode rootNode) {

        if (rootNode != null) {
            TreeNode leftNode = rootNode.getLeftNode();
            TreeNode rightNode = rootNode.getRightNode();
            int leftNodeHeight = adjustTree(leftNode);
            int rightNodeHeight = adjustTree(rightNode);

            if (leftNodeHeight - rightNodeHeight > 1) {
                TreeNode leftLeftNode = null;
                TreeNode leftRightNode = null;
                if (leftNode != null) {
                    leftLeftNode = leftNode.getLeftNode();
                    leftRightNode = leftNode.getRightNode();
                }

                int leftLeftHeight = adjustTree(leftLeftNode);
                int leftRightHeight = adjustTree(leftRightNode);

                if (leftLeftHeight > leftRightHeight) { //左左型
                    rotateOfLeftLeft(rootNode);
                } else { //左右型
                    rotateLeftRight(rootNode);
                }
                return -1;
            } else if (rightNodeHeight - leftNodeHeight > 1) {
                TreeNode rightLeftNode = null;
                TreeNode rightRightNode = null;
                if (rightNode != null) {
                    rightLeftNode = rightNode.getLeftNode();
                    rightRightNode = rightNode.getRightNode();
                }

                int rightLeftHeight = adjustTree(rightLeftNode);
                int rightRightHeight = adjustTree(rightRightNode);

                if (rightRightHeight > rightLeftHeight) { //右右型
                    rotateOfRightRight(rootNode);
                } else {//右左型
                    rotateOfRightLeft(rootNode);
                }
            }
            //重新计算该节点的高度
            leftNode = rootNode.getLeftNode();
            rightNode = rootNode.getRightNode();
            leftNodeHeight = adjustTree(leftNode);
            rightNodeHeight = adjustTree(rightNode);
            return Math.max(leftNodeHeight, rightNodeHeight) + 1;
        } else {
            return 0;
        }
    }


    /**
     * @Description: 左左型子树进行右旋
     * @Date: 19-8-15 下午2:05
     * @param:
     * @return: void
     **/
    public static void rotateOfLeftLeft(TreeNode currentNode) {
        //拷贝根节点
        TreeNode tempNode = new TreeNode();
        tempNode.setValue(currentNode.getValue());
        tempNode.setRightNode(currentNode.getRightNode());

        TreeNode leftSonNode = currentNode.getLeftNode();
        //当前节点的值替换为左儿子的节点的值
        currentNode.setValue(leftSonNode.getValue());
        currentNode.setLeftNode(leftSonNode.getLeftNode());

        //左儿子的右子树挂到原来的currentNode上,即拷贝的中间节点的左子树上
        tempNode.setLeftNode(leftSonNode.getRightNode());
        currentNode.setRightNode(tempNode);
    }

    /**
     * @Description: 右右型子树进行左旋
     * @Date: 19-8-15 下午2:18
     * @param: currentNode
     * @return: com.example.al.example.Tree.TreeNode
     **/
    public static void rotateOfRightRight(TreeNode currentNode) {

        //拷贝根节点
        TreeNode tempNode = new TreeNode();
        tempNode.setValue(currentNode.getValue());
        tempNode.setLeftNode(currentNode.getLeftNode());

        //将右孩子的值赋值给根节点
        TreeNode rightSonNode = currentNode.getRightNode();
        currentNode.setValue(rightSonNode.getValue());
        currentNode.setRightNode(rightSonNode.getRightNode());

        //右儿子的左子树作为原来根节点的右儿子
        tempNode.setRightNode(rightSonNode.getLeftNode());
        //原来的根节点作为现在根节点的左儿子
        currentNode.setLeftNode(tempNode);
    }

    /**
     * @Description: 左右型(LR)子树进行旋转, 先左旋转后右旋转
     * @Date: 19-8-15 下午3:14
     * @param: currentNode
     * @return: com.example.al.example.Tree.TreeNode
     **/
    public static void rotateLeftRight(TreeNode currentNode) {
        //先左旋
        TreeNode leftSonNode = currentNode.getLeftNode();
        TreeNode rightGrandSonNode = leftSonNode.getRightNode();
        TreeNode leftGrandGrandSonNode = rightGrandSonNode.getLeftNode();

        currentNode.setLeftNode(rightGrandSonNode);
        rightGrandSonNode.setLeftNode(leftSonNode);
        leftSonNode.setRightNode(leftGrandGrandSonNode);

        rotateOfLeftLeft(currentNode);

    }


    /**
     * @Description: 右左型(RL)子树进行旋转, 先右旋转后左旋转
     * @Date: 19-8-15 下午2:37
     * @param: currentNode
     * @return: com.example.al.example.Tree.TreeNode
     **/
    public static void rotateOfRightLeft(TreeNode currentNode) {

        //先右旋
        TreeNode rightSonNode = currentNode.getRightNode();
        TreeNode leftGrandSonNode = rightSonNode.getLeftNode();
        TreeNode rightGrandGrandSonNode = leftGrandSonNode.getRightNode();
        currentNode.setRightNode(leftGrandSonNode);
        leftGrandSonNode.setRightNode(rightSonNode);
        rightSonNode.setLeftNode(rightGrandGrandSonNode);

        //再左旋
        rotateOfRightRight(currentNode);
    }


    /**
     * @Description: 根据value寻找其位置, 打印路径或者找到它的左儿子和右儿子,正常遍历,将每次转移的节点记录下来
     * @Date: 19-8-16 上午9:19
     * @param: value
     * @return: void
     **/
    public static void searchNode(double value) {

    }


    /**
     * @Description: 如果被删除的节点有右儿子, 那么在右儿子的字数上寻找最小的节点(node = node.left)作为替换的值(因为整棵树要保持平衡), 替换的值有有有节点, 那么挂到它父亲的左节点上, 否则, 父亲的左儿子直接为null
     * @Date: 19-8-16 上午9:20
     * @param: value
     * @return: void
     **/
    public static void deleteNode(double value) {

    }

//********************************建立avl平衡树　-- end********************************************************

//********************************建立红黑树  -- begin********************************************************


    //1. 节点是红色或者黑色
    //2. 根节点一定是黑色
    //3. 每个叶节点都是黑色的空节点(NIL节点)
    //4. 每个红节点的两个子节点都是黑色的
    //5. 从任一节点到其每个叶子节点的所有路径都包含相同数目的黑色节点

    /**
     * @Description: 建立红黑树
     * @Date: 19-8-16 下午7:39
     * @param:
     * @return: void
     **/
    public static BRTreeNode buildBRTreeNode(double[] array) {

        BRTreeNode rootNode = new BRTreeNode();
        rootNode.setNum(array[0]);
        rootNode.setColor(Color.Black); //根节点为黑色

        BRTreeNode preNode = null;
        BRTreeNode currentNode = null;

        for (int i = 1; i < array.length; i++) {

            BRTreeNode newNode = new BRTreeNode();
            newNode.setNum(array[i]);

            while (currentNode != null) {

                preNode = currentNode;
                if (newNode.getNum() > currentNode.getNum()) {
                    currentNode = currentNode.getRightNode();
                } else if (newNode.getNum() < currentNode.getNum()) {
                    currentNode = currentNode.getLeftNode();
                } else {
                    break;
                }
            }

            if (newNode.getNum() != preNode.getNum()) {
                if (newNode.getNum() > preNode.getNum()) {
                    preNode.setRightNode(newNode);
                    newNode.setDir(Direction.RIGHT);
                } else {
                    preNode.setLeftNode(newNode);
                    newNode.setDir(Direction.LEFT);
                }
                newNode.setParentNode(preNode);//设置父亲节点,方便回溯
            } else { //相等的话后续的红黑树调整不再继续
                continue;
            }


            //红黑树的调整

            adjustBRTree(newNode);

            rootNode.setColor(Color.Black);
        }


        return rootNode;
    }


    /**
     * @Description: 红黑树调整
     * @Date: 19-8-16 下午7:41
     * @param: brTreeNode
     * @return: void
     **/
    public static void adjustBRTree(BRTreeNode currentNode) {

        while (currentNode.getParentNode().getParentNode() != null) {
            //祖父节点不能为空作为终止条件
            //获得父亲节点
            BRTreeNode parentNode = currentNode.getParentNode();
            //祖父节点
            BRTreeNode grandParentNode = parentNode.getParentNode();
            //叔叔节点
            BRTreeNode uncleNode = null;

            if (parentNode.getColor() == Color.Red) {
                if (parentNode.getDir() == Direction.LEFT) {//父亲节点在左侧
                    uncleNode = grandParentNode.getRightNode();
                    if (uncleNode.getColor() == Color.Red) { //叔叔节点是红色的,不分左右还在,叔叔节点和父亲节点变为黑色,祖父节点变为红色
                        parentNode.setColor(Color.Black);
                        uncleNode.setColor(Color.Black);
                        grandParentNode.setColor(Color.Red);
                        currentNode = grandParentNode;  //节点向上迭代

                    } else { //叔叔节点是黑色的, 分左右还在
                        if (currentNode.getDir() == Direction.LEFT) { //当前节点在左侧,
                            rotateOfBRLeftLeft(grandParentNode, parentNode, currentNode); //直接退出?
                        } else {
                            rotateOfBRLeftRight(grandParentNode, parentNode, currentNode);
                        }
                        break;
                    }
                } else {//父亲节点在右侧
                    uncleNode = grandParentNode.getLeftNode();
                    if (uncleNode.getColor() == Color.Red) {
                        parentNode.setColor(Color.Black);
                        uncleNode.setColor(Color.Black);
                        grandParentNode.setColor(Color.Red);

                        currentNode = grandParentNode;  //节点向上迭代
                    } else {
                        if (currentNode.getDir() == Direction.RIGHT) {
                            rotateOfBRRightRight(grandParentNode, parentNode, currentNode);
                        } else {
                            rotateOfBRRightLeft(grandParentNode, parentNode, currentNode);
                        }
                    }
                    break;
                }
            } else {
                //如果父节点是黑色的,那么不做任何处理,直接挂接即可
                break;
            }
        }
    }


    /**
     * @Description: 红黑树, 左左型, 右旋
     * @Date: 19-8-17 下午5:17
     * @param: brTreeNode
     * @return: void
     **/
    public static void rotateOfBRLeftLeft(BRTreeNode grandParentNode, BRTreeNode parentNode, BRTreeNode currentNode) {

        BRTreeNode rightSonNode = parentNode.getRightNode();

        BRTreeNode tempBRTreeNode = new BRTreeNode();
        tempBRTreeNode.setNum(grandParentNode.getNum());
        tempBRTreeNode.setDir(Direction.RIGHT);
        tempBRTreeNode.setColor(Color.Red);
        tempBRTreeNode.setLeftNode(parentNode.getRightNode());
        tempBRTreeNode.setRightNode(grandParentNode.getRightNode());

        if (rightSonNode != null) {
            rightSonNode.setParentNode(tempBRTreeNode);
            rightSonNode.setDir(Direction.LEFT);
        }

        grandParentNode.setNum(parentNode.getNum());
        grandParentNode.setLeftNode(currentNode);
        currentNode.setParentNode(grandParentNode);

        grandParentNode.setRightNode(tempBRTreeNode);
        tempBRTreeNode.setParentNode(grandParentNode);
    }

    /**
     * @Description: 红黑树, 右右型, 左旋
     * @Date: 19-8-17 下午6:00
     * @param: grandParentNode
     * @param: parentNode
     * @param: currentNode
     * @return: void
     **/
    public static void rotateOfBRRightRight(BRTreeNode grandParentNode, BRTreeNode parentNode, BRTreeNode currentNode) {
        BRTreeNode leftSonNode = parentNode.getLeftNode();

        BRTreeNode tempBRTreeNode = new BRTreeNode();
        tempBRTreeNode.setNum(grandParentNode.getNum());
        tempBRTreeNode.setDir(Direction.LEFT);
        tempBRTreeNode.setColor(Color.Red);
        tempBRTreeNode.setRightNode(leftSonNode);
        tempBRTreeNode.setLeftNode(grandParentNode.getLeftNode());

        if (leftSonNode != null) {
            leftSonNode.setParentNode(tempBRTreeNode);
            leftSonNode.setDir(Direction.RIGHT);
        }

        grandParentNode.setNum(parentNode.getNum());
        grandParentNode.setRightNode(currentNode);
        currentNode.setParentNode(grandParentNode);


        grandParentNode.setLeftNode(tempBRTreeNode);
        tempBRTreeNode.setParentNode(grandParentNode);
    }

    /**
     * @Description: 红黑树, 左右型, 左旋
     * @Date: 19-8-17 下午6:00
     * @param: grandParentNode
     * @param: parentNode
     * @param: currentNode
     * @return: void
     **/
    public static void rotateOfBRLeftRight(BRTreeNode grandParentNode, BRTreeNode parentNode, BRTreeNode currentNode) {

        //左旋
        grandParentNode.setLeftNode(currentNode);
        currentNode.setParentNode(grandParentNode);
        currentNode.setDir(Direction.LEFT);
        currentNode.setLeftNode(parentNode);
        parentNode.setParentNode(currentNode);


        BRTreeNode sonBRTreeNode = currentNode.getLeftNode();
        sonBRTreeNode.setDir(Direction.RIGHT);

        parentNode.setRightNode(sonBRTreeNode);
        sonBRTreeNode.setParentNode(parentNode);

        //右旋
        rotateOfBRLeftLeft(grandParentNode, currentNode, parentNode);

    }


    /**
     * @Description: 红黑树, 右左型, 右旋
     * @Date: 19-8-17 下午6:24
     * @param: grandParentNode
     * @param: parentNode
     * @param: currentNode
     * @return: void
     **/
    public static void rotateOfBRRightLeft(BRTreeNode grandParentNode, BRTreeNode parentNode, BRTreeNode currentNode) {

        //右旋
        grandParentNode.setRightNode(currentNode);
        currentNode.setParentNode(grandParentNode);
        currentNode.setDir(Direction.RIGHT);
        currentNode.setRightNode(parentNode);
        parentNode.setParentNode(currentNode);

        BRTreeNode sonBRTreeNode = currentNode.getRightNode();
        sonBRTreeNode.setDir(Direction.LEFT);

        parentNode.setLeftNode(sonBRTreeNode);
        sonBRTreeNode.setParentNode(parentNode);

        //左旋
        rotateOfBRRightRight(grandParentNode, currentNode, parentNode);
    }


    /**
     * @Description: 红黑树删除相关节点
     * @Date: 19-8-23 下午5:37
     * @param: rootNode 红黑树的根节点
     * @param: deleteValue 被删除的节点的值
     * @return: void
     **/
    public static void deleteTreeNode(BRTreeNode rootNode, double deleteValue) {

        BRTreeNode currentNode = rootNode;

        while (currentNode != null) {
            if (currentNode.getNum() == deleteValue) {
                //删除节点
                //调整树结构
            } else if (deleteValue > currentNode.getNum()) {
                currentNode = currentNode.getRightNode();
            } else {
                currentNode = currentNode.getLeftNode();
            }
        }
    }


    /**
     * @Description: 删除节点的逻辑
     * @Date: 19-8-23 下午5:42
     * @param: deletedNode
     * @return: void
     **/
    public static void deleteTreeNode(BRTreeNode deletedNode){

        if(deletedNode.getColor() == Color.Red){
            //如果被删除的节点是红色的,那么直接用后继节点进行替换,颜色不用改
            if(deletedNode.getLeftNode() == null && deletedNode.getRightNode() != null){ //左子节点为空,　右子节点补位空
                deletedNode.getRightNode().setParentNode(deletedNode.getParentNode());
                deletedNode.getParentNode().setRightNode(deletedNode.getRightNode());
            }else if(deletedNode.getRightNode() == null && deletedNode.getLeftNode() != null){//做子节点不为空，右子节点为空
                deletedNode.getLeftNode().setParentNode(deletedNode.getParentNode());
                deletedNode.getParentNode().setLeftNode(deletedNode.getLeftNode());
            }else if(deletedNode.getLeftNode() == null && deletedNode.getRightNode() == null){//左右子节点都为空

                if(deletedNode.getDir() == Direction.LEFT){
                    deletedNode.getParentNode().setLeftNode(null);
                }else{
                    deletedNode.getParentNode().setRightNode(null);
                }
            }else{//左右子节点都不为空,在右子树找到继子节点进行替换,继子节点成为被删除的节点
                BRTreeNode rightNode = deletedNode.getRightNode();
                BRTreeNode successor = getSuccessor(rightNode);//successor是被删除的节点

                successor.getRightNode().setParentNode(successor.getParentNode());
                successor.getParentNode().setLeftNode(successor.getRightNode());


                double tempValue = successor.getNum();
                successor.setNum(deletedNode.getNum());
                deletedNode.setNum(tempValue);

                deleteTreeNode(successor); //需要对继子节点进行递归删除
            }
        }else{
            BRTreeNode brotherNode = null;
            BRTreeNode parentNode = deletedNode.getParentNode();
            if(deletedNode.getDir() == Direction.LEFT){
                brotherNode = parentNode.getRightNode();
            }else{
                brotherNode = parentNode.getLeftNode();
            }

            if(brotherNode == null){
                //不可能为空,为空的话, 从parentNode到叶子节点的黑色节点数不相同
            }else if(brotherNode.getColor() == Color.Red){
                if(brotherNode.getLeftNode() != null || brotherNode.getRightNode() != null){//如果兄弟节点的子节点不为空,那么它的子节点必为黑色,同时两个节点都会存在,原因同样是,黑色节点数要相同
                    //左旋
                    rotateOfBRRightRight(parentNode,brotherNode,brotherNode.getRightNode());
                    deleteTreeNode(deletedNode);
                }else{//子节点不可能为空,原因同样是不能满足黑色节点的个数
                    ;
                }
            }else if(brotherNode.getColor() == Color.Black){
                if (brotherNode.getLeftNode() != null && brotherNode.getLeftNode().getColor() == Color.Red && brotherNode.getRightNode() == null && brotherNode.getRightNode().getColor() == Color.Black){//左子节点不为空,　右子节点为空, 左孩子为红色,右孩子为黑色
                    rotateOfBRRightLeft(parentNode,brotherNode,brotherNode.getLeftNode());
                }else if(brotherNode.getLeftNode() == null && brotherNode.getRightNode() != null && brotherNode.getRightNode().getColor() == Color.Red){//右孩子为红色,左孩子无论红色还是黑色
                    rotateOfBRRightRight(parentNode,brotherNode,brotherNode.getRightNode());
                }else{//左右子节点为空或者都不为空,该节点
                    brotherNode.setColor(Color.Red);
                    deleteTreeNode(parentNode);
                }
            }
        }
    }

    /**
     * @Description: 对要删除的节点进行红黑树结构调整
     * @Date: 19-8-23 下午5:43
     * @param:
     * @return: void
     **/
    public static void adjustBRTree(){

    }


    public static BRTreeNode getSuccessor(BRTreeNode currentNode){
        BRTreeNode parentNode = null;
        while(currentNode != null){
            parentNode = currentNode;
            currentNode = currentNode.getLeftNode();
        }

        return parentNode;
    }




//********************************建立红黑树  -- end********************************************************


    /**
     * @Description: 得到树的高度
     * @Date: 19-8-15 上午11:29
     * @param: rootNode
     * @return: int
     **/
    public static int getTreeHeight(TreeNode rootNode) {

        if (rootNode != null) {
            int leftNodeHeight = getTreeHeight(rootNode.getLeftNode());
            int rightNodeHeight = getTreeHeight(rootNode.getRightNode());

            return Math.max(leftNodeHeight, rightNodeHeight) + 1;
        } else {
            return 0;
        }
    }


    /**
     * @Description: 遍历树节点, 中序遍历
     * @Date: 19-8-15 上午10:42
     * @param:
     * @return: void
     **/
    public static void display(TreeNode rootNode) {

        if (rootNode != null) {
            display(rootNode.getLeftNode());
            System.out.println(rootNode.getValue());
            display(rootNode.getRightNode());
        }
    }


    public static void display2(TreeNode rootNode) {
        if (rootNode != null) {
            TreeNode left = rootNode.getLeftNode();
            TreeNode right = rootNode.getRightNode();

            display2(left);
            display2(right);
        }
    }


    /**
     * @Description: 根据前序和中序遍历获得后序遍历
     * @Date: 19-8-13 下午4:31
     * @param: root, 当前树的根节点
     * @return: void
     **/
    public void buildTree(int[] pre, int[] mid, Object root) {


    }


    public static void main(String[] args) {


//        double[] num = {8, 2, 5, 4, 3, 4.5, 6, 5.5, 7};
//
//        TreeNode rootNode = buildTreeWithArray(num);
//
//        display(rootNode);
//
//        display2(rootNode);
//
//        int height = getTreeHeight(rootNode);
//
//        System.out.println(height);

        double[] num = {10, 9, 8, 7, 8.5, 8.4};
//        double[] num = {7,8,9,10,11,8.5,8.6,8.7,8.4,8.41,8.55,8.53,8.56,8.57};

        TreeNode rootNode = buildAvlTree(num);

        display(rootNode);

    }


}
