# 使用Maven搭建SpringMVC[已验证]



1、新建Maven项目，选择webapp，如下图，点击next，输入GroupId和ArtifactId(即项目名称)后点击Finish。

![img](https://images2015.cnblogs.com/blog/148021/201706/148021-20170610234711606-1506978960.png)

![img](https://images2015.cnblogs.com/blog/148021/201706/148021-20170610234718731-360404578.png)

2、此时项目会报错，如下：

![img](https://images2015.cnblogs.com/blog/148021/201706/148021-20170610234903450-2053638463.png)

通过提示信息可知是由于找不到HttpServlet类，可通过导入Tomcat到工作目录或者通过Maven添加HttpServlet类所在的servlet-api.jar。

```
1 <dependency>
2 <groupId>javax.servlet</groupId>
3 <artifactId>javax.servlet-api</artifactId>
4 <version>3.0.1</version>
5 <scope>compile</scope>
6 </dependency>
```



3、现在通过maven添加SpringMVC所需jar包，点击pom.xml中的Add按钮，在中间输入框中输入spring-webmvc：

![img](https://images.cnblogs.com/OutliningIndicators/ContractedBlock.gif) View Code

 

4、修改web.xml



```
 1 <?xml version="1.0" encoding="UTF-8"?>
 2 <web-app version="3.0" xmlns="http://java.sun.com/xml/ns/javaee"
 3     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 4     xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd">
 5 
 6     <servlet>
 7         <servlet-name>spring</servlet-name>
 8         <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
 9     </servlet>
10 
11     <servlet-mapping>
12         <servlet-name>spring</servlet-name>
13         <url-pattern>/</url-pattern>
14     </servlet-mapping>
15 
16 </web-app>
```



<servlet-name>属性随意，只要上下一致即可，url-pattern中的"/"为拦截所有请求。

5、如上配置会自动去WEB-INF下寻找'servlet-name'-servlet.xml（此处对应为spring-servlet.xml），具体内容如下：



```
 1 <?xml version="1.0" encoding="UTF-8"?>
 2 <beans xmlns="http://www.springframework.org/schema/beans"
 3     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
 4     xmlns:tx="http://www.springframework.org/schema/tx" xmlns:mvc="http://www.springframework.org/schema/mvc"
 5     xsi:schemaLocation="http://www.springframework.org/schema/beans 
 6        http://www.springframework.org/schema/beans/spring-beans.xsd 
 7        http://www.springframework.org/schema/context 
 8        http://www.springframework.org/schema/context/spring-context.xsd 
 9        http://www.springframework.org/schema/tx 
10        http://www.springframework.org/schema/tx/spring-tx.xsd
11           http://www.springframework.org/schema/mvc
12        http://www.springframework.org/schema/mvc/spring-mvc.xsd">
13 
14     <!-- 配置扫描的包 -->
15     <context:component-scan base-package="com.springdemo.*" />
16 
17     <!-- 注册HandlerMapper、HandlerAdapter两个映射类 -->
18     <mvc:annotation-driven />
19 
20     <!-- 访问静态资源 -->
21     <mvc:default-servlet-handler />
22     
23     <!-- 视图解析器 -->
24     <bean
25         class="org.springframework.web.servlet.view.InternalResourceViewResolver">
26         <property name="prefix" value="/WEB-INF/view/"></property>
27         <property name="suffix" value=".jsp"></property>
28     </bean>
29     
30 </beans>
```



　此配置会自动扫描com.springdemo下的所有包中的含有注解的类（如@Controller, @Service等）；<mvc:annotation-driven />会注册两个映射类，负责将请求映射到类和方法中；因为配置的spring是拦截所有请求，所以需要配置<mvc:default-servlet-handler />，来让静态资源通过（如js, css文件等）；视图解析器是将Controller类返回的视图名加上配置的前后缀进行展示。

　　在WEB-INF下创建view文件夹，并在其中创建jsp页面：

创建com.springdemo.controller包（包路径需被配置的扫描路径能扫描到），在其中创建controller类，并加上注解：



```
 1 package com.springdemo.controller;
 2 
 3 import org.springframework.stereotype.Controller;
 4 import org.springframework.web.bind.annotation.RequestMapping;
 5 
 6 @Controller
 7 @RequestMapping("/demo")
 8 public class DemoController {
 9 
10     @RequestMapping("/index")
11     public String index(){
12         return "demo";
13     }
14 }
```



当请求index时会映射到此方法中，返回的字符串 demo会被配置拼接为 WEB-INF/view/demo.jsp，并展示出来。

6、在tomcat中部署运行，访问http://localhost:8080/SpringDemo/index，则可见如下界面，说明配置成功。

![img](https://images2015.cnblogs.com/blog/148021/201706/148021-20170610235704481-550371473.png)



代码见：https://github.com/gitxiaowenbo/springmvc02.git

具体使用可见官方文档：http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/

