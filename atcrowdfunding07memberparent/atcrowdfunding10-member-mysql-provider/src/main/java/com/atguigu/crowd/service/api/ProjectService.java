package com.atguigu.crowd.service.api;

import com.atguigu.crowd.entity.vo.PortalTypeVO;
import com.atguigu.crowd.entity.vo.ProjectVO;

import java.util.List;

public interface ProjectService {
    void saveProject(ProjectVO projectVO, Integer loginVOId);

    List<PortalTypeVO> selectTypeList();
}
