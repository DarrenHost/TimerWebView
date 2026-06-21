package com.github.darrenhost.timerwebview;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 设置页面 - 配置应用参数
 */
public class SettingsActivity extends AppCompatActivity {
    
    // UI 组件
    private TextInputEditText urlInput;
    private TextInputEditText refreshIntervalInput;
    private SwitchMaterial jsSwitch;
    private SwitchMaterial cacheSwitch;
    private Button restoreDefaultButton;
    private Button saveButton;
    private TextView versionText;
    
    // 配置管理
    private ConfigManager configManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // 初始化配置管理器
        configManager = new ConfigManager(this);
        
        // 初始化视图
        initViews();
        
        // 加载当前配置
        loadCurrentConfig();
        
        // 设置按钮监听
        setupListeners();
    }
    
    /**
     * 初始化视图
     */
    private void initViews() {
        urlInput = findViewById(R.id.urlInput);
        refreshIntervalInput = findViewById(R.id.refreshIntervalInput);
        jsSwitch = findViewById(R.id.jsSwitch);
        cacheSwitch = findViewById(R.id.cacheSwitch);
        restoreDefaultButton = findViewById(R.id.restoreDefaultButton);
        saveButton = findViewById(R.id.saveButton);
        versionText = findViewById(R.id.versionText);
        
        // 显示版本号
        displayAppVersion();
    }
    
    /**
     * 显示应用版本号
     */
    private void displayAppVersion() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionText.setText(getString(R.string.app_version) + ": v" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            versionText.setText(getString(R.string.app_version) + ": v1.0");
        }
    }
    
    /**
     * 加载当前配置到 UI
     */
    private void loadCurrentConfig() {
        // URL
        String currentUrl = configManager.getUrl();
        if (!TextUtils.isEmpty(currentUrl)) {
            urlInput.setText(currentUrl);
        }
        
        // 刷新间隔
        int currentInterval = configManager.getRefreshInterval();
        refreshIntervalInput.setText(String.valueOf(currentInterval));
        
        // JavaScript 开关
        jsSwitch.setChecked(configManager.isJsEnabled());
        
        // 缓存开关
        cacheSwitch.setChecked(configManager.isCacheEnabled());
    }
    
    /**
     * 设置按钮监听
     */
    private void setupListeners() {
        // 恢复默认配置
        restoreDefaultButton.setOnClickListener(v -> {
            configManager.restoreDefaults();
            loadCurrentConfig();
            Toast.makeText(this, R.string.default_restored, Toast.LENGTH_SHORT).show();
        });
        
        // 保存配置
        saveButton.setOnClickListener(v -> {
            if (saveConfig()) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish(); // 返回主页
            }
        });
    }
    
    /**
     * 保存配置
     * @return 是否保存成功
     */
    private boolean saveConfig() {
        // 获取 URL
        String url = urlInput.getText() != null ? urlInput.getText().toString().trim() : "";
        
        // 验证 URL
        if (!TextUtils.isEmpty(url) && !ConfigManager.isValidUrl(url)) {
            urlInput.setError(getString(R.string.invalid_url));
            urlInput.requestFocus();
            return false;
        }
        
        // 获取刷新间隔
        int refreshInterval;
        try {
            String intervalStr = refreshIntervalInput.getText() != null ? 
                refreshIntervalInput.getText().toString().trim() : "5";
            refreshInterval = TextUtils.isEmpty(intervalStr) ? 5 : Integer.parseInt(intervalStr);
        } catch (NumberFormatException e) {
            refreshInterval = 5;
        }
        
        // 验证刷新间隔
        if (!ConfigManager.isValidRefreshInterval(refreshInterval)) {
            refreshIntervalInput.setError(getString(R.string.refresh_interval_hint));
            refreshIntervalInput.requestFocus();
            return false;
        }
        
        // 获取开关状态
        boolean jsEnabled = jsSwitch.isChecked();
        boolean cacheEnabled = cacheSwitch.isChecked();
        
        // 保存配置
        configManager.saveConfig(url, refreshInterval, jsEnabled, cacheEnabled);
        
        return true;
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
