# NOTICE — 移植声明 / Port notice

本仓库是 **[Zzztick365/mtr_psd_lcd](https://github.com/Zzztick365/mtr_psd_lcd)** 的 fork，
上游原始作品遵循 **MIT** 许可（原文见 [LICENSE](LICENSE)，版权行为
`Copyright (c) 2026 psd_only contributors`，**未做任何改动**）。

## 移植（Forge 1.20.1 + MTR 3）

- **代码基线**：上游 **1.19.2 + MTR 3 的 v1.2.8** 发布版（上游 `main` 面向 Fabric 1.20.1 + MTR 4.0.5，二者是不同发布线）；
- **移植**：**DeepSeek**（反编译 → 映射 → 重写加载器 / 注册 / 渲染层，编写校验与打包脚本，逐项定位并修复实机问题）；
- **实机测试 / 仓库与发布维护**：**ChihayaAnonQWQ**（整合包内实机测试，测试期间未发现问题）。

> **移植由 DeepSeek 完成，经 ChihayaAnonQWQ 实机测试未出现问题。**

## 反馈

本项目**不是**官方发布：

- **如出现问题请勿提交给原仓库 / 原作者**；
- 请在本 fork 的 [Issues](https://github.com/ChihayaAnonQWQ/mtr_psd_lcd/issues) 反馈（附 `logs/latest.log` 与截图更便于定位）。

重新分发时，请保留 `LICENSE` 中的原始版权声明以及本文件与 [CREDITS.md](CREDITS.md) 中的署名信息。
