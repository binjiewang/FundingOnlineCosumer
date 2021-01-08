package com.atguigu.crowd.handler;

import com.atguigu.crowd.api.RedisRemoteService;
import com.atguigu.crowd.config.ShortMessageProperties;
import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.util.CrowdUtil;
import com.atguigu.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

@Controller
public class MemberHandler {

    @Autowired
    private ShortMessageProperties shortMessageProperties;

    @Autowired
    private RedisRemoteService redisRemoteService;

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
    public String register(){
        return "redirect:/auth/member/to/login/page";
    }
}
