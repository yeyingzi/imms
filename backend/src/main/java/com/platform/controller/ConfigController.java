package com.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.platform.common.result.Result;
import com.platform.entity.Config;
import com.platform.mapper.ConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/configs")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigMapper configMapper;

    private static final Map<String, Object> DEFAULTS = new HashMap<>();
    static {
        DEFAULTS.put("platformName", "内网万用平台");
        DEFAULTS.put("logo", "");
        DEFAULTS.put("themeColor", "#409eff");
        DEFAULTS.put("loginTimeout", 120);
        DEFAULTS.put("passwordMinLength", 6);
        DEFAULTS.put("maxLoginFailures", 5);
        DEFAULTS.put("lockoutDuration", 15);
        DEFAULTS.put("logRetentionDays", 90);
    }

    private static final Set<String> NUMBER_KEYS = Set.of(
        "loginTimeout", "passwordMinLength", "maxLoginFailures",
        "lockoutDuration", "logRetentionDays"
    );

    @GetMapping
    public Result<Map<String, Object>> list() {
        try {
            LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
            List<Config> configs = configMapper.selectList(wrapper);

            Map<String, Object> result = new HashMap<>(DEFAULTS);
            Map<String, String> descriptions = new HashMap<>();

            for (Config config : configs) {
                if (config.getConfigKey() != null) {
                    String key = config.getConfigKey();
                    String value = config.getConfigValue();
                    if (NUMBER_KEYS.contains(key)) {
                        try {
                            result.put(key, Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            result.put(key, DEFAULTS.getOrDefault(key, 0));
                        }
                    } else {
                        result.put(key, value);
                    }

                    if (config.getDescription() != null && !config.getDescription().isEmpty()) {
                        descriptions.put(key, config.getDescription());
                    }
                }
            }

            result.put("_descriptions", descriptions);

            return Result.success(result);
        } catch (Exception e) {
            log.error("Error loading configs", e);
            return Result.error("配置加载失败: " + e.getMessage());
        }
    }

    @GetMapping("/{key}")
    public Result<Object> get(@PathVariable String key) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, key);
        Config config = configMapper.selectOne(wrapper);

        if (config != null) {
            return Result.success(config.getConfigValue());
        }

        Object defaultValue = DEFAULTS.get(key);
        return Result.success(defaultValue != null ? defaultValue.toString() : null);
    }

    @PutMapping
    public Result<?> update(@RequestBody Map<String, Object> configs) {
        for (Map.Entry<String, Object> entry : configs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";

            LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Config::getConfigKey, key);
            Config existing = configMapper.selectOne(wrapper);

            if (existing != null) {
                existing.setConfigValue(value);
                configMapper.updateById(existing);
            } else {
                Config config = new Config();
                config.setConfigKey(key);
                config.setConfigValue(value);
                configMapper.insert(config);
            }
        }

        return Result.success("配置更新成功", null);
    }
}
