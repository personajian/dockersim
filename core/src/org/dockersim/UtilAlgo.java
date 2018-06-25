package org.dockersim;

/**
 * @Author personajian
 * @Date 2018/5/1 0001 15:45
 */
public class UtilAlgo {

    /**
     * 计算任务资源需求向量与VM剩余资源向量之间的相似性（KL-散度，相似熵）
     * KLD值越大，互补性越高
     * KLD值越小，相似性越高
     *
     * @Param
     * @Return
     */
    public static double Similarity1(ResourceVector resourceTask, ResourceVector resourceVM) {

        double similarity = 0.0;

        for (int i = 0; i < Parameters.RESOURE_NUM; i++) {
            similarity += Math.log(resourceVM.getRes().get(i)) - Math.log(resourceTask.getRes().get(i));
        }

        return similarity;
    }

    public static double Similarity2(ResourceVector resourceTask, ResourceVector resourceVM) {

        double similarity = 0.0;

        for (int i = 0; i < Parameters.RESOURE_NUM; i++) {
            similarity += Math.pow(resourceVM.getRes().get(i) - resourceTask.getRes().get(i), 2);
        }

        return similarity;
    }

    public static double Similarity3(ResourceVector resourceTask, ResourceVector resourceVM) {

        double temp0 = 0.0;
        double temp1 = 0.0;
        double temp2 = 0.0;

        for (int i = 0; i < Parameters.RESOURE_NUM; i++) {
            temp0 = resourceTask.getRes().get(i) * resourceVM.getRes().get(i);
            temp1 += Math.pow(resourceTask.getRes().get(i), 2);
            temp2 += Math.pow(resourceVM.getRes().get(i), 2);
        }

        double similarity = -(temp0 / (Math.pow(temp1, 1 / 2) * Math.pow(temp2, 1 / 2)));

        return similarity;
    }

}
