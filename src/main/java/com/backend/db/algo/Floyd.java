package com.backend.db.algo;

import java.util.Arrays;

public class Floyd {
    private int[] vertexData; // 记录顶点数据
    private double[][] dis; // 记录最短路径
    private int[][] pre; // 记录前驱顶点

    /**
     * 构造方法，初始化
     * @param vertexData 顶点数据
     * @param weight 权值
     */
    public Floyd(int[] vertexData, double[][] weight) {
        this.vertexData = vertexData;
        this.dis = weight;

        // 初始化前驱顶点，默认初始化前驱顶点为其本身
        this.pre = new int[vertexData.length][vertexData.length];
        for (int i = 0; i < pre.length; i++) {
            Arrays.fill(pre[i],i);
        }
    }

    /**
     * 弗洛伊德算法，经过层层遍历，就会得到每个点到达每个点的最短距离
     */
    public void floydAlgorithm(){
        double len = 0;
        // k表示中间顶点
        for (int k = 0; k < dis.length; k++) {
            // i表示开始顶点
            for (int i = 0; i < dis.length; i++) {
                // j表示到达顶点
                for (int j = 0; j < dis.length; j++) {
                    // 从i出发，经过k，到达j的距离
                    len = dis[i][k] + dis[k][j];
                    // 如果i到j的距离大于i经过k到达j的距离，那么就将i经过k到达j的距离赋值
                    if (len < dis[i][j]){
                        dis[i][j] = len;
                        pre[i][j] = pre[k][j]; // 获得新的前驱节点
                    }
                }
            }
        }
    }

    // 展现出最终结果
    public void showRoad(){
        for (int i = 0; i < dis.length; i++) {
            for (int j = 0; j < dis.length; j++) {
                System.out.print(vertexData[i]+"->"+vertexData[j]+":"+dis[i][j]+"   ");
            }
            System.out.println();
        }
    }

    public int[] getVertexData() {
        return vertexData;
    }

    public double[][] getDis() {
        return dis;
    }
}

