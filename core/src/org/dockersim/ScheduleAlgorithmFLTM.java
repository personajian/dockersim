package org.dockersim;

/**
 * @Author personajian
 * @Date 2018/6/25 0025 14:40
 */
public class ScheduleAlgorithmFLTM extends ScheduleAlgorithm {
    @Override
    public void runSchedule() {

    }

    @Override
    public double getTotalCost() {

        double cost = 0.0;

        for(MicroFlow microFlow: getMicroFlows()){
            for(Task task:microFlow.getTaskList()){
                ResourceVector rv = task.getResourceVector();
                cost += Parameters.PRICECPU*task.getRunTime()*60*rv.getRes().get(0);
                cost += Parameters.PRICECPU*task.getRunTime()*60*rv.getRes().get(1);
            }
        }

        return cost;
    }



}
