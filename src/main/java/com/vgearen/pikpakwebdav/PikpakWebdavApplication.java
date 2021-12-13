package com.vgearen.pikpakwebdav;

import com.vgearen.pikpakwebdav.store.PikpakDriverFileSystemStore;
import net.sf.webdav.WebdavServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
public class PikpakWebdavApplication {

    public static void main(String[] args) {
        SpringApplication.run(PikpakWebdavApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<WebdavServlet> myServlet(){
        ServletRegistrationBean<WebdavServlet> servletRegistrationBean = new ServletRegistrationBean<>(new WebdavServlet(), "/*");
        Map<String, String> inits = new LinkedHashMap<>();
        inits.put("ResourceHandlerImplementation", PikpakDriverFileSystemStore.class.getName());
        inits.put("rootpath", "./");
        inits.put("storeDebug", "1");
        servletRegistrationBean.setInitParameters(inits);
        return servletRegistrationBean;
    }
}
