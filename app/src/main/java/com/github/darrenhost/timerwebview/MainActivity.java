package com.github.darrenhost.timerwebview;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 主页面 - WebView 容器 + 定时刷新功能 + 全屏模式
 */
public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    
    // UI 组件
    private WebView webView;
    private ProgressBar progressBar;
    private LinearLayout errorView;
    private LinearLayout noUrlView;
    private TextView countdownText;
    private Button retryButton;
    private Button goSettingsButton;
    
    // 配置管理
    private ConfigManager configManager;
    
    // 定时刷新
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    private int countdownSeconds = 0;
    private boolean isAutoRefreshEnabled = false;
    private boolean isAppInBackground = false;
    
    // 全屏模式
    private boolean isFullscreen = false;
    private int fullscreenTapCount = 0;
    private Handler fullscreenHandler = new Handler(Looper.getMainLooper());
    private Runnable resetTapRunnable;
    
    // 网络状态
    private NetworkReceiver networkReceiver;
    
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化工具类
        configManager = new ConfigManager(this);
        
        // 检查配置是否损坏，自动恢复默认
        if (!configManager.isConfigValid()) {
            configManager.restoreDefaults();
        }
        
        setContentView(R.layout.activity_main);
        
        // 初始化 UI
        initViews();
        
        // 初始化 WebView
        initWebView();
        
        // 注册网络状态监听
        registerNetworkReceiver();
        
        // 加载页面
        loadUrl();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        isAppInBackground = false;
        webView.onResume();
        
        // 恢复定时器
        if (isAutoRefreshEnabled && webView.getVisibility() == View.VISIBLE) {
            startAutoRefresh();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        isAppInBackground = true;
        webView.onPause();
        
        // 暂停定时器
        stopAutoRefresh();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoRefresh();
        unregisterReceiver(networkReceiver);
        webView.destroy();
    }
    
    /**
     * 初始化视图
     */
    private void initViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        errorView = findViewById(R.id.errorView);
        noUrlView = findViewById(R.id.noUrlView);
        countdownText = findViewById(R.id.countdownText);
        retryButton = findViewById(R.id.retryButton);
        goSettingsButton = findViewById(R.id.goSettingsButton);
        
        // 重试按钮
        retryButton.setOnClickListener(v -> loadUrl());
        
        // 去设置按钮
        goSettingsButton.setOnClickListener(v -> openSettings());
        
        // WebView 点击监听 - 用于退出全屏
        webView.setOnTouchListener((v, event) -> {
            if (isFullscreen) {
                handleFullscreenTap();
            }
            return false;
        });
    }
    
    /**
     * 处理全屏点击
     */
    private void handleFullscreenTap() {
        fullscreenTapCount++;
        
        if (fullscreenTapCount >= 5) {
            exitFullscreen();
            fullscreenTapCount = 0;
        } else {
            int remainingTaps = 5 - fullscreenTapCount;
            Toast.makeText(this, getString(R.string.tap_to_exit, remainingTaps), Toast.LENGTH_SHORT).show();
            
            // 重置计数器（5 秒内不再点击则重置）
            if (resetTapRunnable != null) {
                fullscreenHandler.removeCallbacks(resetTapRunnable);
            }
            resetTapRunnable = () -> fullscreenTapCount = 0;
            fullscreenHandler.postDelayed(resetTapRunnable, 5000);
        }
    }
    
    /**
     * 初始化 WebView 配置
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = webView.getSettings();
        
        // JavaScript 配置
        settings.setJavaScriptEnabled(configManager.isJsEnabled());
        settings.setDomStorageEnabled(true);
        
        // 自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        
        // 缩放配置
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        
        // 缓存配置
        if (configManager.isCacheEnabled()) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        
        // 允许加载 HTTP
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        // WebViewClient - 所有跳转内部打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (errorCode != WebViewClient.ERROR_HOST_LOOKUP) {
                    showErrorView();
                }
            }
        });
        
        // WebChromeClient - 进度条
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    
    /**
     * 加载 URL
     */
    private void loadUrl() {
        String url = configManager.getUrl();
        
        if (url == null || url.trim().isEmpty()) {
            // URL 为空，显示提示
            webView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
            noUrlView.setVisibility(View.VISIBLE);
            countdownText.setVisibility(View.GONE);
            return;
        }
        
        // 验证 URL 格式
        if (!ConfigManager.isValidUrl(url)) {
            Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_LONG).show();
            noUrlView.setVisibility(View.VISIBLE);
            return;
        }
        
        // 检查网络
        if (!isNetworkAvailable()) {
            showErrorView();
            return;
        }
        
        // 隐藏错误视图，显示 WebView
        noUrlView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        
        // 加载页面
        progressBar.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
        
        // 启动自动刷新
        startAutoRefresh();
    }
    
    /**
     * 显示错误视图
     */
    private void showErrorView() {
        webView.setVisibility(View.GONE);
        noUrlView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        countdownText.setVisibility(View.GONE);
        stopAutoRefresh();
    }
    
    /**
     * 开始自动刷新
     */
    private void startAutoRefresh() {
        stopAutoRefresh(); // 先停止之前的
        
        int intervalSeconds = configManager.getRefreshInterval();
        
        if (intervalSeconds <= 0 || isAppInBackground) {
            isAutoRefreshEnabled = false;
            countdownText.setVisibility(View.GONE);
            return;
        }
        
        isAutoRefreshEnabled = true;
        countdownSeconds = intervalSeconds;
        updateCountdownDisplay();
        countdownText.setVisibility(View.VISIBLE);
        
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAppInBackground && isAutoRefreshEnabled) {
                    countdownSeconds--;
                    
                    if (countdownSeconds <= 0) {
                        // 刷新页面
                        webView.reload();
                        countdownSeconds = intervalSeconds;
                        Toast.makeText(MainActivity.this, "页面已自动刷新", Toast.LENGTH_SHORT).show();
                    }
                    
                    updateCountdownDisplay();
                    handler.postDelayed(this, 1000);
                }
            }
        };
        
        handler.post(refreshRunnable);
    }
    
    /**
     * 停止自动刷新
     */
    private void stopAutoRefresh() {
        if (refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
            refreshRunnable = null;
        }
        isAutoRefreshEnabled = false;
        countdownText.setVisibility(View.GONE);
    }
    
    /**
     * 更新倒计时显示
     */
    private void updateCountdownDisplay() {
        int minutes = countdownSeconds / 60;
        int seconds = countdownSeconds % 60;
        String text = String.format("下次刷新：%02d:%02d", minutes, seconds);
        countdownText.setText(text);
    }
    
    /**
     * 手动刷新
     */
    private void manualRefresh() {
        if (isNetworkAvailable()) {
            webView.reload();
            if (isAutoRefreshEnabled) {
                startAutoRefresh(); // 重置倒计时
            }
            Toast.makeText(this, "已手动刷新", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 打开设置页面
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
    /**
     * 切换全屏模式
     */
    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
    }
    
    /**
     * 进入全屏模式
     */
    private void enterFullscreen() {
        isFullscreen = true;
        fullscreenTapCount = 0;
        
        // 隐藏 ActionBar (TitleBar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // 隐藏状态栏和导航栏
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        
        Toast.makeText(this, R.string.fullscreen, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 退出全屏模式
     */
    private void exitFullscreen() {
        isFullscreen = false;
        fullscreenTapCount = 0;
        
        // 显示状态栏和导航栏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        
        // 显示 ActionBar (TitleBar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        
        Toast.makeText(this, R.string.exit_fullscreen, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.menu_refresh) {
            manualRefresh();
            return true;
        } else if (id == R.id.menu_settings) {
            openSettings();
            return true;
        } else if (id == R.id.menu_fullscreen) {
            toggleFullscreen();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 检查网络是否可用
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
    
    /**
     * 注册网络状态接收器
     */
    private void registerNetworkReceiver() {
        networkReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }
    
    /**
     * 网络状态广播接收器
     */
    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable()) {
                // 网络恢复
                if (errorView.getVisibility() == View.VISIBLE) {
                    loadUrl();
                }
            } else {
                // 网络断开
                if (webView.getVisibility() == View.VISIBLE) {
                    showErrorView();
                }
            }
        }
    }
}
