package com.atguigu.crowd.filter;

import com.atguigu.crowd.constant.AccessPassResources;
import com.atguigu.crowd.constant.CrowdConstant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Component
public class CrowdAccessFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String path = request.getServletPath();
        boolean flag = AccessPassResources.PASS_RES_SET.contains(path);
        if(flag){
            return false;
        }
        return !AccessPassResources.judgeCurrentServletPathWetherStaticResource(path);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpSession session = request.getSession();
        Object loginMember = session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);

        if(loginMember==null){
            HttpServletResponse response = requestContext.getResponse();
            session.setAttribute(CrowdConstant.Message,CrowdConstant.MESSAGE_ACCESS_FORBIDDEN);
            try {
                response.sendRedirect("/auth/member/to/login/page");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
