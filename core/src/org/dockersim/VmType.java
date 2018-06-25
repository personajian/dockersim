package org.dockersim;

import java.util.List;

/**
 *虚拟机类型实体类
 *
 * @Author personajian
 * @Date 2018/4/24 0024 10:43
 */
public class VmType {

    private int id;

    private String name;

    private String csp;

    protected double mips;

    protected long bw;

    protected double cost;

    private ResourceVector resourceVector;

    public ResourceVector getResourceVector() {
        return resourceVector;
    }

    public void setResourceVector(ResourceVector resourceVector) {
        this.resourceVector = resourceVector;
    }

    public VmType() {
    }

    public VmType(int id, String name, String csp, ResourceVector resourceVector,double cost) {
        this.id = id;
        this.name = name;
        this.csp = csp;
        this.resourceVector = resourceVector;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCsp() {
        return csp;
    }

    public void setCsp(String csp) {
        this.csp = csp;
    }

    public double getMips() {
        return mips;
    }

    public void setMips(double mips) {
        this.mips = mips;
    }

    public long getBw() {
        return bw;
    }

    public void setBw(long bw) {
        this.bw = bw;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "VmType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", csp='" + csp + '\'' +
                ", cost=" + cost +
                '}';
    }
}
