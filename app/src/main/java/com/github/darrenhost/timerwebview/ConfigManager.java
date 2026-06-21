package com.github.darrenhost.timerwebview;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 配置管理类 - 负责读取、保存和验证应用配置
 * 使用 SharedPreferences 进行持久化存储
 */
public class ConfigManager {
    
    private static final String TAG = "ConfigManager";
    private static final String PREF_NAME = "timer_webview_config";
    
    // 配置键
    private static final String KEY_URL = "url";
    private static final String KEY_REFRESH_INTERVAL = "refresh_interval";
    private static final String KEY_JS_ENABLED = "js_enabled";
    private static final String KEY_CACHE_ENABLED = "cache_enabled";
    
    // 默认值
    private static final String DEFAULT_URL = "";
    private static final int DEFAULT_REFRESH_INTERVAL = 5; // 5 分钟
    private static final boolean DEFAULT_JS_ENABLED = true;
    private static final boolean DEFAULT_CACHE_ENABLED = true;
    
    // 配置验证
    private static final int MIN_REFRESH_INTERVAL = 0;
    private static final int MAX_REFRESH_INTERVAL = 120;
    
    private final SharedPreferences prefs;
    
    public ConfigManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 获取目标 URL
     */
    public String getUrl() {
        return prefs.getString(KEY_URL, DEFAULT_URL);
    }
    
    /**
     * 设置目标 URL
     */
    public void setUrl(String url) {
        prefs.edit().putString(KEY_URL, url).apply();
    }
    
    /**
     * 获取自动刷新间隔（分钟）
     */
    public int getRefreshInterval() {
        return prefs.getInt(KEY_REFRESH_INTERVAL, DEFAULT_REFRESH_INTERVAL);
    }
    
    /**
     * 设置自动刷新间隔
     */
    public void setRefreshInterval(int minutes) {
        prefs.edit().putInt(KEY_REFRESH_INTERVAL, minutes).apply();
    }
    
    /**
     * JavaScript 是否启用
     */
    public boolean isJsEnabled() {
        return prefs.getBoolean(KEY_JS_ENABLED, DEFAULT_JS_ENABLED);
    }
    
    /**
     * 设置 JavaScript 开关
     */
    public void setJsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_JS_ENABLED, enabled).apply();
    }
    
    /**
     * 缓存是否启用
     */
    public boolean isCacheEnabled() {
        return prefs.getBoolean(KEY_CACHE_ENABLED, DEFAULT_CACHE_ENABLED);
    }
    
    /**
     * 设置缓存开关
     */
    public void setCacheEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_CACHE_ENABLED, enabled).apply();
    }
    
    /**
     * 验证 URL 是否有效
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return url.startsWith("http://") || url.startsWith("https://");
    }
    
    /**
     * 验证刷新间隔是否有效
     */
    public static boolean isValidRefreshInterval(int interval) {
        return interval >= MIN_REFRESH_INTERVAL && interval <= MAX_REFRESH_INTERVAL;
    }
    
    /**
     * 保存完整配置
     */
    public void saveConfig(String url, int refreshInterval, boolean jsEnabled, boolean cacheEnabled) {
        prefs.edit()
            .putString(KEY_URL, url)
            .putInt(KEY_REFRESH_INTERVAL, refreshInterval)
            .putBoolean(KEY_JS_ENABLED, jsEnabled)
            .putBoolean(KEY_CACHE_ENABLED, cacheEnabled)
            .apply();
        Log.d(TAG, "配置已保存：URL=" + url + ", 间隔=" + refreshInterval + "分钟");
    }
    
    /**
     * 恢复默认配置
     */
    public void restoreDefaults() {
        prefs.edit()
            .putString(KEY_URL, DEFAULT_URL)
            .putInt(KEY_REFRESH_INTERVAL, DEFAULT_REFRESH_INTERVAL)
            .putBoolean(KEY_JS_ENABLED, DEFAULT_JS_ENABLED)
            .putBoolean(KEY_CACHE_ENABLED, DEFAULT_CACHE_ENABLED)
            .apply();
        Log.d(TAG, "已恢复默认配置");
    }
    
    /**
     * 检查配置是否损坏（用于异常处理）
     */
    public boolean isConfigValid() {
        int interval = getRefreshInterval();
        return isValidRefreshInterval(interval);
    }
}
