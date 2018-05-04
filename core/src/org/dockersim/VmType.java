package org.dockersim;

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

    protected int vcpu;

    protected int ram;

    protected long bw;

    protected double cost;

    public VmType() {
    }

    public VmType(int id, String name, String csp, double mips, int vcpu, int ram, long bw, double cost) {
        this.id = id;
        this.name = name;
        this.csp = csp;
        this.vcpu = vcpu;
        this.ram = ram;
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

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getVcpu() {
        return vcpu;
    }

    public void setVcpu(int vcpu) {
        this.vcpu = vcpu;
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
                ", vcpu=" + vcpu +
                ", ram=" + ram +
                ", cost=" + cost +
                '}';
    }
}
