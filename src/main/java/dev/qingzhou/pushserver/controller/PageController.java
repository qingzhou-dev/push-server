package dev.qingzhou.pushserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping(value = {
            "/{path:[^\\.]*}",           // 匹配一级路径，如 /login, /dashboard
            "/**/{path:[^\\.]*}"         // 匹配多级路径，如 /system/users, /user/profile/edit
    })
    public String redirect() {
        // 转发到根目录的 index.html，让 Vue Router 在前端接管路由
        return "forward:/index.html";
    }

}
