package com.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.platform.common.result.Result;
import com.platform.entity.LoginLog;
import com.platform.entity.Module;
import com.platform.entity.OperationLog;
import com.platform.mapper.LoginLogMapper;
import com.platform.mapper.ModuleMapper;
import com.platform.mapper.OperationLogMapper;
import com.platform.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserMapper userMapper;
    private final ModuleMapper moduleMapper;
    private final OperationLogMapper operationLogMapper;
    private final LoginLogMapper loginLogMapper;

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.online-window-minutes:30}")
    private int onlineWindowMinutes;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        long userCount = userMapper.selectCount(null);
        stats.put("userCount", userCount);

        LambdaQueryWrapper<Module> moduleWrapper = new LambdaQueryWrapper<>();
        long moduleCount = moduleMapper.selectCount(moduleWrapper);
        stats.put("moduleCount", moduleCount);

        LambdaQueryWrapper<OperationLog> logWrapper = new LambdaQueryWrapper<>();
        long logCount = operationLogMapper.selectCount(logWrapper);
        stats.put("operationLogCount", logCount);

        LambdaQueryWrapper<LoginLog> loginLogWrapper = new LambdaQueryWrapper<>();
        long loginLogCount = loginLogMapper.selectCount(loginLogWrapper);
        stats.put("loginLogCount", loginLogCount);

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(onlineWindowMinutes);
        LambdaQueryWrapper<LoginLog> onlineWrapper = new LambdaQueryWrapper<>();
        onlineWrapper.ge(LoginLog::getCreatedAt, threshold);
        onlineWrapper.eq(LoginLog::getStatus, 1);
        List<LoginLog> recentLogins = loginLogMapper.selectList(onlineWrapper);
        long onlineCount = recentLogins.stream()
                .map(LoginLog::getUsername)
                .distinct()
                .count();
        stats.put("onlineUserCount", onlineCount);

        return Result.success(stats);
    }

    @GetMapping("/system-info")
    public Result<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("platformName", "内网万用平台");
        info.put("version", appVersion);
        info.put("env", System.getProperty("spring.profiles.active", "development"));
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("osName", System.getProperty("os.name"));
        info.put("uptime", formatUptime(getUptime()));
        return Result.success(info);
    }

    private static final long startTime = System.currentTimeMillis();

    private static long getUptime() {
        return System.currentTimeMillis() - startTime;
    }

    private static String formatUptime(long millis) {
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        if (hours > 0) {
            return hours + "小时" + minutes + "分钟";
        }
        return minutes + "分钟";
    }
}
