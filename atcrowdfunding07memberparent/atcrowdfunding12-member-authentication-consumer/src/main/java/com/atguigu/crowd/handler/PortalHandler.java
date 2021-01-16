package com.atguigu.crowd.handler;

import com.atguigu.crowd.api.MySQLRemoteService;
import com.atguigu.crowd.entity.po.ProjectPO;
import com.atguigu.crowd.entity.vo.PortalProjectVO;
import com.atguigu.crowd.entity.vo.PortalTypeVO;
import com.atguigu.crowd.util.CrowdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PortalHandler {

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/")
    public String showPortalPage(ModelMap modelMap) {
        // 加载首页数据
        List<PortalTypeVO> portalTypeVOList = mySQLRemoteService.getPortalTypeRemote();
        /*List<PortalTypeVO> portalTypeVOList = new ArrayList<PortalTypeVO>();
        PortalTypeVO vo = new PortalTypeVO();
        vo.setId(1);
        vo.setName("科技");
        vo.setRemark("科技改变未来");

        PortalTypeVO vo2 = new PortalTypeVO();
        vo2.setId(2);
        vo2.setName("科技2");
        vo2.setRemark("科技改变未来2");

        List<PortalProjectVO> portalProjectVOList = new ArrayList<PortalProjectVO>();
        PortalProjectVO projectVO = new PortalProjectVO();
        projectVO.setProjectId(1);
        projectVO.setDeployDate("2020-01-01");
        projectVO.setProjectName("名字");
        projectVO.setMoney(1000);
        projectVO.setPercentage(41);
        projectVO.setSupporter(1234);
        portalProjectVOList.add(projectVO);
        vo.setPortalProjectVOList(portalProjectVOList);

        PortalProjectVO projectVO2 = new PortalProjectVO();
        projectVO2.setProjectId(2);
        projectVO2.setDeployDate("2021-01-01");
        projectVO2.setProjectName("名字");
        projectVO2.setMoney(10000);
        projectVO2.setPercentage(88);
        projectVO2.setSupporter(12345);
        portalProjectVOList.add(projectVO2);
        vo2.setPortalProjectVOList(portalProjectVOList);

        portalTypeVOList.add(vo);
        portalTypeVOList.add(vo2);*/

        modelMap.addAttribute("portalData",portalTypeVOList);

        return "portal";
    }
}
