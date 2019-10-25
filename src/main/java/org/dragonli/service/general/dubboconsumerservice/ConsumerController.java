package org.dragonli.service.general.dubboconsumerservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dragonli.tools.general.DataCachePool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/")
public class ConsumerController {
    @Autowired
    private ConsumerInvokerManager invoker;

    @PostMapping
    public Map<String, Object> debug(HttpServletResponse response, HttpServletRequest request,
            @RequestBody JSONObject para) throws Exception {

        String interfaceName = para.getString("interface");
        String methodName = para.getString("method");
        JSONArray parameters = para.getJSONArray("parameters");
        String group = para.containsKey("group") ? para.getString("group") : null;
        group = group != null && !"".equals(group = group.trim()) ? group : null;

        if (interfaceName == null || !(interfaceName instanceof String) || methodName == null ||
                !(methodName instanceof String) || parameters == null || !(parameters instanceof JSONArray)) {
            throw new Exception("para`s error!");
        }

//        return new JSONObject();
        return DubboConsumerUtil.invoke(invoker, interfaceName, methodName, parameters, group);
    }
}
