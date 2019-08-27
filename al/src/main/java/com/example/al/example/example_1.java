package com.example.al.example;

import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: benhairui
 * @date: 2019/08/08 16:44:25
 */
public class example_1 {

    /**
     * @Description: 1-9 的数, 组成三个三位数，他们的数字之间成1:2:3的倍数关系
     * @Date: 19-8-8 下午5:24
     * @param:
     * @return: void
     **/
    public static void example(){
        int[] nums = {1,2,3,4,5,6,7,8,9};

        int[] preTwo = {1,2,3};
        Set<String> set = new HashSet<>();
        for(int i = 0;i<preTwo.length;i++){
            int firstPre = preTwo[i];
            for(int j = 0;j<nums.length;j++){
                int secPre = nums[j];
                if((firstPre == 3 && nums[j] < firstPre) || (firstPre < 3 && nums[j] != firstPre)) {
                    secPre = nums[j];
                    for (int k = 0; k < nums.length; k++) {
                        if (nums[k] == secPre || nums[k] == firstPre) {
                            continue;
                        }
                        int threePre = nums[k];

                        int first = firstPre * 100 + secPre * 10 + threePre;
                        int second = first * 2;
                        int third = first * 3;
                        String strFirst = String.valueOf(first);
                        String strSecond = String.valueOf(second);
                        String strThird = String.valueOf(third);
//                    if(strFirst.equals("327")){
//                        System.out.println("fsfs");
//                    }
                        collectChar(strFirst, set);
                        collectChar(strSecond, set);
                        collectChar(strThird, set);

//                    System.out.println(strFirst+","+strSecond+","+strThird + ",   " + set);

                        if (set.size() == nums.length) {
                            for(int m = 0;m<nums.length;m++){
                                set.remove(String.valueOf(nums[m]));
                            }
                            if(set.size()>0){
                                continue;
                            }
                            System.out.println(strFirst+","+strSecond+","+strThird + ",   " + set);
                        }
                        set.clear();
                    }
                }
            }
        }

    }

    public static void collectChar(String str, Set<String> set){
        for(int m = 0; m<str.length();m++){
            set.add(String.valueOf(str.charAt(m)));
        }
    }


    public static void main(String[] args){
        example();
    }
}
