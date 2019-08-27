package com.example.al.example;

import java.util.Random;

/**
 * @description: 堆排序
 * @author: benhairui
 * @date: 2019/08/12 15:51:26
 */
public class HeapUtils {
    private static void replace(float[] data, int i, int j) {
        if (i == j) {
            return;
        }

        float temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }


    /**
     * @Description: 对获得堆进行从大到小进行排序
     * @Date: 19-8-12 下午4:08
     * @param: data
     * @return: float[]
     **/
    public static float[] heapSort(float[] data) {

        for(int i = 0;i<data.length - 1;i++){
            createMindHeap(data,data.length - 1 - i);
            replace(data,0,data.length - 1 - i);
        }

        return data;
    }

    private static void createMindHeap(float[] data, int lastIndex) {
        int startIndex = (lastIndex - 1) / 2;
        for (int i = startIndex; i >= 0; i--) {
            int k = i;
            while (2 * k + 1 <= lastIndex) {
                int smallIndex = 2 * k + 1;
                if (smallIndex + 1 <= lastIndex) {

                    if (data[smallIndex] > data[smallIndex + 1]) {
                        smallIndex++;
                    }
                }

                if (data[k] > data[smallIndex]) {
                    replace(data, k, smallIndex);
                    k = smallIndex;
                } else {
                    break;
                }
            }
        }
    }

    public static float[] getMaxNumber(int count, float[] data) {
        float[] maxNumberArr = new float[count];
        for (int i = 0; i < data.length; i++) {
//            System.out.println(i);
            if (data[i] > maxNumberArr[0]) {
                maxNumberArr[0] = data[i];
                createMindHeap(maxNumberArr,count - 1);
            }
        }
        return maxNumberArr;
    }

    public static void print(float[] data){
        for(int i = 0;i<data.length;i++){
            System.out.println(data[i] + "\t");
        }
        System.out.println();
    }

    public static void main(String[] args){
        float arr[]= new float[1000000];
        for (int i=0;i<1000000;i++){
            Random random=new Random();
            float v = random.nextFloat() * 50f;
            arr[i]=v;
        }
        long start=System.currentTimeMillis();
        float[] maxNumber = HeapUtils.getMaxNumber(10, arr);
        System.out.println("heapSort之前: ");
        print(maxNumber);
        HeapUtils.heapSort(maxNumber);
        System.out.println("heapSort之后: ");
        HeapUtils.print(maxNumber);
        System.out.println(System.currentTimeMillis()-start);
    }


}
