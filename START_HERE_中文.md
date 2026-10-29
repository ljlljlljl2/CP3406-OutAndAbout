# 下一步：在你的电脑运行

1. 解压这份 ZIP，保留原来的老师模板，不要覆盖。
2. Android Studio 选择 File → Open，打开包含 settings.gradle.kts 的文件夹。
3. 等待 Gradle 同步完成。如果提示安装 SDK 36，按提示安装。
4. 顶部选择模拟器或已连接的 Android 手机，点击绿色三角形 Run。
5. 如果出现红色报错，把 Build 窗口里的完整错误文字发来。
6. 按 TESTING.md 实际测试，记录结果。这里没有完成编译或模拟器验证，不能直接当作已验证的最终提交。

## 提交前还需完成
- 在你自己的 GitHub 仓库中保存代码并提交真实的后续修改、测试和修复；不要伪造之前的开发历史。
- 确保讲师和课程协调员有仓库访问权限。
- 亲自理解代码并按照课程规则确认 AI 辅助使用范围。
- 根据 REFLECTION_GUIDE.md 写真实的 500–1000 词、1–2 页英文反思，导出 PDF。
- 根据学校指定格式提交 AI 使用声明；AI_DECLARATION_DRAFT.md 是待核对的草稿。
- 完成测试后，从 Android Studio 导出项目 ZIP，提交 ZIP、反思 PDF、GitHub 链接及所需声明。

## 代码阅读顺序
WeatherData.kt（获取天气和生成提示）→ WeatherViewModel.kt（管理状态）→ MainActivity.kt（显示界面）。
