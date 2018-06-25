package org.workflow;

/**
 * @Author personajian
 * @Date 2018/5/31 0031 11:14
 */
public class EnumTest {

    public enum WorkflowName{CyberShake,Sipht}

    public static void main(String[] args) {
        for(WorkflowName workflowName : WorkflowName.values()){
            System.out.println(workflowName);
            System.out.println(workflowName.name());
            System.out.println(workflowName.toString());
        }
    }

}
