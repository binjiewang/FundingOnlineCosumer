package com.atguigu.crowd.handler;

import com.atguigu.crowd.api.MySQLRemoteService;
import com.atguigu.crowd.config.UploadProperties;
import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.entity.vo.*;
import com.atguigu.crowd.util.CrowdUtil;
import com.atguigu.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProjectConsumerHandler {

    @Autowired
    private UploadProperties uploadProperties;
    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/create/project/information")
    public String saveProjectBasicInfo(ProjectVO projectVO,
                                       MultipartFile headerPicture,
                                       List<MultipartFile> detailPictureList,
                                       HttpSession httpSession,
                                       ModelMap modelMap
    ) throws IOException {
        //上传头图
        boolean headerPictureIsEmpty = headerPicture.isEmpty();
        if (headerPictureIsEmpty) {
            modelMap.addAttribute(CrowdConstant.Message, "头图为空");
            return "project-launch";
        }

        ResultEntity<String> headerResultEntity = CrowdUtil.uploadFile2(headerPicture.getInputStream(), headerPicture.getOriginalFilename(), uploadProperties.getServerFilesPath());

        if (ResultEntity.FAILED.equals(headerResultEntity.getResult())) {
            modelMap.addAttribute(CrowdConstant.Message, "头图上传失败");
            return "project-launch";
        } else {
            String headerPath = headerResultEntity.getData();
            projectVO.setHeaderPicturePath(headerPath);
        }

        //上传明细图
        if (detailPictureList == null || detailPictureList.size() == 0) {
            modelMap.addAttribute(CrowdConstant.Message, "详情图不能为空");
            return "project-launch";
        }

        //临时保存明细路径
        List<String> detailPath = new ArrayList<String>();

        for (MultipartFile multipartFile : detailPictureList) {
            if (multipartFile.isEmpty()) {
                modelMap.addAttribute(CrowdConstant.Message, "详情图为空");
                return "project-launch";
            }
            ResultEntity<String> detailResultEntity = CrowdUtil.uploadFile2(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), uploadProperties.getServerFilesPath());
            if (ResultEntity.SUCCESS.equals(detailResultEntity.getResult())) {
                detailPath.add(detailResultEntity.getData());
            } else {
                modelMap.addAttribute(CrowdConstant.Message, "详情图上传失败");
                return "project-launch";
            }
        }

        projectVO.setDetailPicturePathList(detailPath);

        //将信息存入session
        httpSession.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT, projectVO);
        //进入下一步收集界面
        return "redirect:http://www.binjiewang.com/project/return/info/page";
    }

    /**
     * 回报图片保存
     *
     * @param returnPicture
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/create/upload/return/picture.json")
    public ResultEntity uploadReturnPicture(@RequestParam("returnPicture") MultipartFile returnPicture) throws IOException {
        ResultEntity<String> resultEntity = CrowdUtil.uploadFile2(returnPicture.getInputStream(), returnPicture.getOriginalFilename(), uploadProperties.getServerFilesPath());
        return resultEntity;
    }

    /**
     * 保存回报明细
     *
     * @param returnVO
     * @param httpSession
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/create/save/return.json")
    public ResultEntity saveReturn(ReturnVO returnVO, HttpSession httpSession) {
        try {
            ProjectVO projectVO = (ProjectVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
            if (projectVO == null) {
                return ResultEntity.failed("之前保存的项目丢失");
            }
            List<ReturnVO> returnVOList = projectVO.getReturnVOList();
            if (returnVOList == null || returnVOList.size() == 0) {
                returnVOList = new ArrayList<ReturnVO>();
                projectVO.setReturnVOList(returnVOList);
            }
            returnVOList.add(returnVO);
            httpSession.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT, projectVO);
            return ResultEntity.successWithoutData();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    @RequestMapping("/create/confirm")
    public String confirm(MemberConfirmInfoVO memberConfirmInfoVO, ModelMap modelMap, HttpSession httpSession) {
        ProjectVO projectVO = (ProjectVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
        if (projectVO == null) {
            modelMap.addAttribute(CrowdConstant.Message, "数据丢失");
            return "project-confirm";
        }
        projectVO.setMemberConfirmInfoVO(memberConfirmInfoVO);

        MemberLoginVO memberLoginVO = (MemberLoginVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
        Integer loginVOId = memberLoginVO.getId();

        //远程保存项目
        ResultEntity saveResultEntity = mySQLRemoteService.saveProjectVORemote(projectVO,loginVOId);

        if(ResultEntity.FAILED.equals(saveResultEntity.getResult())){
            modelMap.addAttribute(CrowdConstant.Message, "保存失败");
            return "project-confirm";
        }
        //移除临时存储项目
        httpSession.removeAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);

        return "redirect:http://www.binjiewang.com/project/create/success";
    }


}
