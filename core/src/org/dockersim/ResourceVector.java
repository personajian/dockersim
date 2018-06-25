package org.dockersim;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author personajian
 * @Date 2018/5/28 0028 15:15
 */
public class ResourceVector {

    private List<Double> res = new ArrayList<>(Parameters.RESOURE_NUM);

    public List<Double> getRes() {
        return res;
    }

    public void setRes(List<Double> res) {
        this.res = res;
    }

    public ResourceVector() {
        for (int i = 0; i < Parameters.RESOURE_NUM; i++) {
            res.add(0.0);
        }
    }

    public ResourceVector(ResourceVector rv) {
        for (int i = 0; i < Parameters.RESOURE_NUM; i++) {
            res.add(rv.getRes().get(i));
        }
    }


    public ResourceVector(double r0, double r1){
        res.add(r0);
        res.add(r1);
    }

    public boolean isGreatter(ResourceVector rv){
        boolean flag = true;
        for (int i = 0; i < Parameters.RESOURE_NUM; i++) {
            if(res.get(i) < rv.getRes().get(i)){
                flag = false;
                break;
            }
        }
        return flag;
    }
    
    
    public static ResourceVector differResource(ResourceVector rv1,ResourceVector rv2){

        ResourceVector rv = new ResourceVector(rv1);

        for (int i = 0; i < Parameters.RESOURE_NUM; i++) {
            rv.getRes().set(i,rv.getRes().get(i) - rv2.getRes().get(i));
        }

        return rv;
    }

    public static ResourceVector addResource(ResourceVector rv1,ResourceVector rv2){

        ResourceVector rv = new ResourceVector(rv1);

        for (int i = 0; i < Parameters.RESOURE_NUM; i++) {
            rv.getRes().set(i,rv.getRes().get(i) + rv2.getRes().get(i));
        }

        return rv;
    }
}
