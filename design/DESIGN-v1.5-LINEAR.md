# TimerWebView v1.5 - Linear 风格设计

## 📁 文件位置

- **HTML 设计稿**: `design/TimerWebView-v1.5-linear.html`
- **draw.io 设计图**: `design/TimerWebView-v1.5-design.drawio`
- **PNG 预览图**: `/data/gswy/00_临时文件/TimerWebView-v1.5-*.png`

## 🎨 Linear 设计系统特点

### 1. 深色主题
- **背景**: `#08090a` (营销黑) → `#0f1011` (面板黑)
- **文字**: `#f7f8f8` (主) → `#d0d6e0` (次) → `#8a8f98` (三)
- **边框**: `rgba(255,255,255,0.05)` (微妙) → `rgba(255,255,255,0.08)` (标准)

### 2. 字体系统
- **主字体**: Inter Variable (Google Fonts)
- **等宽字体**: JetBrains Mono
- **OpenType**: `cv01`, `ss03` (几何变体)
- **字重**: 400 (阅读) / 510 (强调) / 590 (粗体)

### 3. 品牌色
- **主色**: `#5e6ad2` (Indigo)
- **强调**: `#7170ff` (Violet)
- **悬停**: `#828fff` (Light Violet)

### 4. 组件样式

#### 按钮
- **Primary**: `#5e6ad2` 背景，白色文字，6dp 圆角
- **Outline**: 透明背景，`#5e6ad2` 边框和文字
- **Icon**: `rgba(255,255,255,0.03)` 背景，36x36dp

#### 倒计时徽章
- **背景**: `rgba(94, 106, 210, 0.15)` (半透明 Indigo)
- **边框**: `rgba(94, 106, 210, 0.3)`
- **文字**: `#7170ff` (Violet)
- **字体**: JetBrains Mono (等宽)

#### 输入框
- **背景**: `rgba(255,255,255,0.02)`
- **边框**: `rgba(255,255,255,0.08)`
- **聚焦**: 边框变 `#5e6ad2`
- **圆角**: 6dp

#### 开关
- **开启**: `#5e6ad2` 背景
- **尺寸**: 44x24dp
- **圆角**: 12dp (全圆)

## 📐 布局结构

```
┌─────────────────────────────────────┐
│ Status Bar (44px) - 黑色            │
│ 18:56                    5G 🔋      │
├─────────────────────────────────────┤
│ App Header (60px) - 面板黑          │
│ TimerWebView  [00:17] [⛶] [⚙]      │
├─────────────────────────────────────┤
│                                     │
│  WebView Container (flex: 1)        │
│  白色背景                           │
│                                     │
│       🤖                            │
│   网页无法打开                      │
│   http://www.123.com/               │
│   [net::ERR_NAME_NOT_RESOLVED]      │
│           [重试]                    │
│                                     │
│      [下次刷新：00:17]              │
│                                     │
└─────────────────────────────────────┘
```

## 🎯 设计改进点

### vs v1.4 (Bootstrap)
| 特性 | v1.4 Bootstrap | v1.5 Linear |
|------|----------------|-------------|
| 背景 | 浅灰 `#f8f9fa` | 深黑 `#08090a` |
| 主色 | 蓝色 `#0d6efd` | Indigo `#5e6ad2` |
| 字体 | System UI | Inter + JetBrains Mono |
| 边框 | 实色 `#dee2e6` | 半透明白色 |
| 按钮 | 实心 | 半透明背景 |
| 阴影 | 黑色阴影 | 白色边框 + 亮度阶梯 |
| 圆角 | 6dp 统一 | 2-12dp 分级 |

### Linear 特色
1. **深色原生**: 不是"深色模式"，而是"黑暗作为媒介"
2. **亮度阶梯**: 通过背景透明度表现层次 (`0.02` → `0.04` → `0.05`)
3. **微妙边框**: `rgba(255,255,255,0.05)` 如月光下的铁丝网
4. **单一强调色**: Indigo 仅用于 CTA 和交互元素
5. **等宽字体**: JetBrains Mono 用于倒计时和技术内容
6. **字重 510**: Linear 标志性字重 (介于 400 和 500 之间)

## 🔧 使用方法

### 本地查看
```bash
# 用浏览器打开
firefox /home/kevin/TimerWebView/design/TimerWebView-v1.5-linear.html

# 或用 Chrome
google-chrome /home/kevin/TimerWebView/design/TimerWebView-v1.5-linear.html
```

### 部署到 GitHub Pages
```bash
cd /home/kevin/TimerWebView
git add design/TimerWebView-v1.5-linear.html
git commit -m "Add Linear style design mockup"
git push origin master
```

然后访问：`https://darrenhost.github.io/TimerWebView/design/TimerWebView-v1.5-linear.html`

## 📱 实现到 Android

### colors.xml
```xml
<color name="linear_bg_marketing">#08090a</color>
<color name="linear_bg_panel">#0f1011</color>
<color name="linear_bg_surface">#191a1b</color>
<color name="linear_text_primary">#f7f8f8</color>
<color name="linear_text_secondary">#d0d6e0</color>
<color name="linear_brand_indigo">#5e6ad2</color>
<color name="linear_brand_violet">#7170ff</color>
<color name="linear_border_subtle">#1AFFFFFF</color>
<color name="linear_border_standard">#14FFFFFF</color>
```

### themes.xml
```xml
<style name="Theme.TimerWebView.Linear" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
    <item name="colorPrimary">@color/linear_brand_indigo</item>
    <item name="android:statusBarColor">#000000</item>
    <item name="android:navigationBarColor">#0f1011</item>
    <item name="android:windowBackground">@color/linear_bg_marketing</item>
</style>
```

### 组件样式
- **按钮**: 6dp 圆角，`#5e6ad2` 背景
- **输入框**: `rgba(255,255,255,0.02)` 背景，`rgba(255,255,255,0.08)` 边框
- **倒计时徽章**: 半透明 Indigo 背景，等宽字体
- **卡片**: 8dp 圆角，微妙白色边框

## 🎨 设计原则

### Do ✅
- 使用 Inter 字体 + `cv01`, `ss03` OpenType 特性
- 字重 510 作为默认强调
- 深色背景使用半透明白色边框
- Indigo 仅用于 CTA 和交互元素
- 使用亮度阶梯表现层次

### Don't ❌
- 不要用纯白 (`#ffffff`) 文字 - 用 `#f7f8f8`
- 不要用实心背景 - 用半透明 `rgba(255,255,255,0.02-0.05)`
- 不要用暖色调 - 保持冷灰 + Indigo
- 不要用黑色阴影 - 用白色边框
- 不要跳过 OpenType 特性

## 🔗 参考链接

- **Linear 官网**: https://linear.app
- **Linear 设计系统**: https://linear.app/design
- **Inter 字体**: https://rsms.me/inter/
- **JetBrains Mono**: https://www.jetbrains.com/lp/mono/
