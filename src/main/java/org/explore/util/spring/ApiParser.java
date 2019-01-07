package org.explore.util.spring;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author neil
 * @email lirui10093622@163.com
 * @time 2019-01-07 18:42:51
 */
public class ApiParser {

    public static List<String> parse(RequestMappingHandlerMapping handlerMapping) {
        List<String> lines = new LinkedList<>();
        Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();
        int i = 1;
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo info = m.getKey();
            HandlerMethod handlerMethod = m.getValue();

            boolean jsonRequest = false;
            StringBuffer paramStr = new StringBuffer("");
            MethodParameter[] parameters = handlerMethod.getMethodParameters();
            if (parameters != null && parameters.length > 0) {
                for (MethodParameter parameter : parameters) {
                    RequestBody requestBody = parameter.getParameterAnnotation(RequestBody.class);
                    if (requestBody != null) {
                        jsonRequest = true;
                    }
                    Annotation[] annotations = parameter.getParameterAnnotations();
                    if (annotations != null && annotations.length > 0) {
                        paramStr.append(String.join(", ", Arrays.stream(annotations).map(e -> "@" + e.annotationType().getSimpleName()).collect(Collectors.toList()))).append(" ");
                    }
                    paramStr.append(parameter.getParameterType().getSimpleName()).append(" ").append("param" + parameter.getParameterIndex()).append(", ");
                }
                if (paramStr.length() > 0) {
                    paramStr.delete(paramStr.length() - 2, paramStr.length());
                }
            }

            String className = handlerMethod.getMethod().getDeclaringClass().getSimpleName();
            Method method = handlerMethod.getMethod();
            String httpMethodName = info.getMethodsCondition().toString();
            String url = info.getPatternsCondition().toString();
            lines.add(String.format("%5d: %10s %-50s json: %-6s %s", i, httpMethodName, url, jsonRequest, className + "." + method.getName() + "(" + paramStr + ")"));
            i++;
        }
        return lines;
    }
}