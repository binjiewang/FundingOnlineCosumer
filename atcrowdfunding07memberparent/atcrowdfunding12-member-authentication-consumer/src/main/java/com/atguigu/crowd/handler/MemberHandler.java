package com.atguigu.crowd.handler;

import com.atguigu.crowd.api.MySQLRemoteService;
import com.atguigu.crowd.api.RedisRemoteService;
import com.atguigu.crowd.config.ShortMessageProperties;
import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.entity.po.MemberPO;
import com.atguigu.crowd.entity.vo.MemberLoginVO;
import com.atguigu.crowd.entity.vo.MemberVO;
import com.atguigu.crowd.util.CrowdUtil;
import com.atguigu.crowd.util.ResultEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Controller
public class MemberHandler {

    @Autowired
    private ShortMessageProperties shortMessageProperties;

    @Autowired
    private RedisRemoteService redisRemoteService;

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @ResponseBody
    @RequestMapping("auth/member/send/short/message.json")
    public ResultEntity<String> sendMessage(@RequestParam("phoneNum") String phone) {
        ResultEntity<String> messageResultEntity = CrowdUtil.sendShortMessage(phone, shortMessageProperties.getContext());

        if (ResultEntity.SUCCESS.equals(messageResultEntity.getResult())) {
            //获取验证码成功后存入redis
            String key = CrowdConstant.REDIS_CODE_PREFIX +phone;
            String value = messageResultEntity.getData();
            ResultEntity<String> setRedisResultEntity = redisRemoteService.setRedisKeyValueRemoteWithTimeout(key, value,5, TimeUnit.MINUTES);
            if(ResultEntity.SUCCESS.equals(setRedisResultEntity.getResult())){
                return ResultEntity.successWithoutData();
            }else{
                return setRedisResultEntity;
            }
        } else {
            return messageResultEntity;
        }
    }

    @RequestMapping("/auth/do/member/register")
    public String register(MemberVO memberVO, ModelMap modelMap){
        //1.获取手机号
        String phoneNum = memberVO.getPhoneNum();
        //2.验证码校验，验证码需要删除？
        String key = CrowdConstant.REDIS_CODE_PREFIX+phoneNum;
        ResultEntity<String> keyRemote = redisRemoteService.getRedisStringValueByKeyRemote(key);

        String result = keyRemote.getResult();
        if(ResultEntity.FAILED.equals(result)) {
            modelMap.addAttribute(CrowdConstant.Message, keyRemote.getMessage());
            return "member-reg";
        }
        String redisCode = keyRemote.getData();
        if(redisCode == null) {
            modelMap.addAttribute(CrowdConstant.Message, CrowdConstant.MESSAGE_CODE_NOT_EXISTS);
            return "member-reg";
        }

        String code = memberVO.getCode();
        if(!(code != null && code.equals(redisCode))){
            modelMap.addAttribute("message","验证码错误");
            return "member-reg";
        }
//        redisRemoteService.removeRedisKeyRemote(key);

        //3.密码加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String userpswd = memberVO.getUserpswd();
        String encodePswd = encoder.encode(userpswd);
        //4.保存用户
        memberVO.setUserpswd(encodePswd);
        MemberPO memberPO = new MemberPO();
        BeanUtils.copyProperties(memberVO,memberPO);

        ResultEntity<String> saveMemberResultEntity = mySQLRemoteService.saveMember(memberPO);

        if(ResultEntity.FAILED.equals(saveMemberResultEntity.getResult())) {
            modelMap.addAttribute(CrowdConstant.Message, saveMemberResultEntity.getMessage());
            return "member-reg";
        }

        return "redirect:http://www.binjiewang.com/auth/member/to/login/page";
    }

    /**
     * 会员登录
      * @param loginacct
     * @param userpswd
     * @param modelMap
     * @return
     */
    @RequestMapping("/auth/member/do/login")
    public String doLogin(@RequestParam("loginacct")String loginacct,
                          @RequestParam("userpswd")String userpswd,
                          ModelMap modelMap,
                          HttpSession httpSession){
        //1.从数据库中根据账号取出用户对象
        ResultEntity<MemberPO> loginAcctRemote = mySQLRemoteService.getMemberPOByLoginAcctRemote(loginacct);
        if(ResultEntity.FAILED.equals(loginAcctRemote.getResult())){
            modelMap.addAttribute(CrowdConstant.Message,loginAcctRemote.getMessage());
            return "member-login";
        }
        MemberPO memberPO = loginAcctRemote.getData();
        if(memberPO==null){
            modelMap.addAttribute(CrowdConstant.Message,CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }
        //2.比对密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(userpswd, memberPO.getUserpswd());
        if(!matches){
            modelMap.addAttribute(CrowdConstant.Message,CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }
        MemberLoginVO memberLoginVO = new MemberLoginVO();
        BeanUtils.copyProperties(memberPO, memberLoginVO);
        //3.成功后将信息存入MemberLoginVo
        httpSession.setAttribute("loginMember",memberLoginVO);

        return "member-center";
    }

    /**
     * 注销
     */
    @RequestMapping("/auth/member/logout")
    public String doLogout(HttpSession httpSession){
        httpSession.invalidate();
        return "redirect:http://www.binjiewang.com/";
    }
}
