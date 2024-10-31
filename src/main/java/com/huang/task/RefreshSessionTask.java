package com.huang.task;

import com.huang.controller.SessionController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshSessionTask {

    private final SessionController sessionController;

    @Scheduled(cron = "0 0 12 * * 3")
    public void refreshSession() {
        log.info("定时任务正在执行....");
        sessionController.refreshSession();
        log.info("定时任务执行完毕....");
    }
}
