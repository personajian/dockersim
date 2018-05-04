package org.dockersim;

import java.util.List;

/**
 * Docker容器
 * @Author personajian
 * @Date 2018/4/24 0024 10:52
 * 负责Docker容器的创建和销毁
 */
public class DockerAllocator {

    private List<Docker> dockerList;

    public DockerAllocator(List<Docker> dockerList) {
        this.dockerList = dockerList;
    }

}
