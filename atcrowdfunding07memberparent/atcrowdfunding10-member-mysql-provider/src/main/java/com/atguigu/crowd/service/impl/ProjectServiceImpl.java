package com.atguigu.crowd.service.impl;

import com.atguigu.crowd.entity.po.*;
import com.atguigu.crowd.entity.vo.*;
import com.atguigu.crowd.mapper.*;
import com.atguigu.crowd.service.api.ProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectPOMapper projectPOMapper;
    @Autowired
    private ReturnPOMapper returnPOMapper;
    @Autowired
    private ProjectItemPicPOMapper projectItemPicPOMapper;
    @Autowired
    private MemberLaunchInfoPOMapper memberLaunchInfoPOMapper;
    @Autowired
    private MemberConfirmInfoPOMapper memberConfirmInfoPOMapper;


    @Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void saveProject(ProjectVO projectVO, Integer loginVOId) {
        //保存项目
        ProjectPO projectPO = new ProjectPO();
        BeanUtils.copyProperties(projectVO,projectPO);
        projectPO.setMemberid(loginVOId);
        //项目时间
        String createDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        projectPO.setCreatedate(createDate);
        projectPO.setStatus(0);
        projectPOMapper.insertSelective(projectPO);
        Integer projectPOId = projectPO.getId();

        //保存项目分类
        List<Integer> typeIdList = projectVO.getTypeIdList();
        for (Integer typeId : typeIdList) {
            projectPOMapper.insertTypeRelationship(projectPOId,typeId);
        }

        //保存标签
        List<Integer> tagIdList = projectVO.getTagIdList();
        for (Integer tagId : tagIdList) {
            projectPOMapper.insertTagRelationship(projectPOId,tagId);
        }
        //保存详情图片
        List<String> detailPicturePathList = projectVO.getDetailPicturePathList();
        for (String path : detailPicturePathList) {
            ProjectItemPicPO projectItemPicPO = new ProjectItemPicPO(null,projectPOId,path);
            projectItemPicPOMapper.insertSelective(projectItemPicPO);
        }

        //保存项目发起人
        MemberLauchInfoVO memberLauchInfoVO = projectVO.getMemberLauchInfoVO();
        MemberLaunchInfoPO memberLaunchInfoPO = new MemberLaunchInfoPO();
        BeanUtils.copyProperties(memberLauchInfoVO, memberLaunchInfoPO);
        memberLaunchInfoPO.setMemberid(loginVOId);
        memberLaunchInfoPOMapper.insert(memberLaunchInfoPO);

        //保存回报信息
        List<ReturnVO> returnVOList = projectVO.getReturnVOList();
        for (ReturnVO returnVO : returnVOList) {
            ReturnPO returnPO = new ReturnPO();
            BeanUtils.copyProperties(returnVO,returnPO);
            returnPO.setProjectid(projectPOId);
            returnPOMapper.insertSelective(returnPO);
        }
        //保存确认信息
        MemberConfirmInfoVO memberConfirmInfoVO = projectVO.getMemberConfirmInfoVO();
        MemberConfirmInfoPO memberConfirmInfoPO = new MemberConfirmInfoPO();
        memberConfirmInfoPO.setMemberid(loginVOId);
        BeanUtils.copyProperties(memberConfirmInfoVO,memberConfirmInfoPO);
        memberConfirmInfoPOMapper.insert(memberConfirmInfoPO);

    }

    public List<PortalTypeVO> selectTypeList() {
        List<PortalTypeVO> portalTypeVOList = projectPOMapper.selectPortalTypeVOList();
        return portalTypeVOList;
    }
}
