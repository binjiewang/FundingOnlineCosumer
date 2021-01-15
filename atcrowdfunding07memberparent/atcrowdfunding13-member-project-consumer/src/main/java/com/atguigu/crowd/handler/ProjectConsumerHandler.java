package com.atguigu.crowd.handler;

import com.atguigu.crowd.config.UploadProperties;
import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.entity.vo.ProjectVO;
import com.atguigu.crowd.util.CrowdUtil;
import com.atguigu.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/create/project/information")
    public String saveProjectBasicInfo(ProjectVO projectVO,
                                       MultipartFile headerPicture,
                                       List<MultipartFile>  detailPictureList,
                                       HttpSession httpSession,
                                       ModelMap modelMap
                                       ) throws IOException {
        //上传头图
        boolean headerPictureIsEmpty = headerPicture.isEmpty();
        if(headerPictureIsEmpty){
            modelMap.addAttribute(CrowdConstant.Message,"头图为空");
            return "project-launch";
        }

        ResultEntity<String> headerResultEntity = CrowdUtil.uploadFile2(headerPicture.getInputStream(),headerPicture.getOriginalFilename(), uploadProperties.getServerFilesPath());

        if(ResultEntity.FAILED.equals(headerResultEntity.getResult())){
            modelMap.addAttribute(CrowdConstant.Message,"头图上传失败");
            return "project-launch";
        }else{
            String headerPath = headerResultEntity.getData();
            projectVO.setHeaderPicturePath(headerPath);
        }

        //上传明细图
        if(detailPictureList==null||detailPictureList.size()==0){
            modelMap.addAttribute(CrowdConstant.Message,"详情图不能为空");
            return "project-launch";
        }

        //临时保存明细路径
        List<String> detailPath = new ArrayList<String>();

        for (MultipartFile multipartFile : detailPictureList) {
            if(multipartFile.isEmpty()){
                modelMap.addAttribute(CrowdConstant.Message,"详情图为空");
                return "project-launch";
            }
            ResultEntity<String> detailResultEntity = CrowdUtil.uploadFile2(multipartFile.getInputStream(),multipartFile.getOriginalFilename(), uploadProperties.getServerFilesPath());
            if(ResultEntity.SUCCESS.equals(detailResultEntity.getResult())){
                detailPath.add(detailResultEntity.getData());
            }else{
                modelMap.addAttribute(CrowdConstant.Message,"详情图上传失败");
                return "project-launch";
            }
        }

        projectVO.setDetailPicturePathList(detailPath);

        //将信息存入session
        httpSession.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT,projectVO);
        //进入下一步收集界面
        return "redirect:http://www.binjiewang.com/project/return/info/page";
    }
}
