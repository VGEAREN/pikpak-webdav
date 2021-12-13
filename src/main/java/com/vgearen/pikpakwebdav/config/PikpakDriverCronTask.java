package com.vgearen.pikpakwebdav.config;


import com.vgearen.pikpakwebdav.model.PFile;
import com.vgearen.pikpakwebdav.store.PikpakDriverClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PikpakDriverCronTask {

    @Autowired
    private PikpakDriverClientService pikpakDriverClientService;

    /**
     * 每隔5分钟请求一下接口，保证token不过期
     */
    @Scheduled(initialDelay = 30 * 1000, fixedDelay = 5 * 60 * 1000)
    public void accessToken() {
        try {
            PFile root = pikpakDriverClientService.getPFileByPath("/");
            pikpakDriverClientService.getPFiles(root.getFileId());
        } catch (Exception e) {
            // nothing
        }

    }
}
