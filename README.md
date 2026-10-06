# Mtr_psd_LCD — Forge 1.20.1 / MTR 3 移植版

> **非官方移植**：本仓库是 **[Zzztick365/mtr_psd_lcd](https://github.com/Zzztick365/mtr_psd_lcd)**（Fabric 1.19.2 + MTR 3）的
> Forge 1.20.1 移植分支。原作版权归原作者 **Zzztick365** 所有，遵循 **MIT** 许可（原始版权行保留在 [LICENSE](LICENSE) 中）。
> 署名与分工见 [CREDITS.md](CREDITS.md)，移植技术细节与所有改动见 [PORT-REPORT.md](PORT-REPORT.md)。

为 MTR（Minecraft Transit Railway）的**站台屏蔽门 / 半高安全门**提供 **LCD 信息显示**的附属模组：
顶板、屏蔽门与玻璃上可以显示本站名、下一站、线路图、班次与提示语等动态内容。

## 运行环境

| 依赖 | 版本 |
| --- | --- |
| Minecraft | **1.20.1** |
| Forge | **47.x**（开发用 47.4.0，实机验证 47.4.15） |
| MTR（Minecraft Transit Railway） | **3.x Forge 构建**，版本区间 `[1.20.1-3.2.2-hotfix-2, 1.20.1-4.0.0)`（实机 3.6.3） |
| Architectury API | **9.2.14+**（MTR 3 Forge 构建的强制依赖） |

> ⚠️ **不能与官方 MTR 4 版本共存**：两者的 `mod id` 都是 `mtr_psd_lcd`，同时放入 `mods/` 会在启动时报错。
> 使用 MTR 4（官方 `forge1.20.1+1.2.7`）时请使用官方版本，本分支面向 **MTR 3** 服务器/整合包。

## 构建

```bash
# JDK 17 或 21 均可（产物为 Java 17 字节码）
./gradlew clean build            # Windows: gradlew.bat clean build
# 产物：build/libs/mtr_psd_lcd-forge-mc1.20.1-mtr3-1.2.8.jar
```

开发环境运行（需要本地放好 MTR / Architectury）：

```bash
./gradlew runClient -PmtrRuntimeTest
```

可选诊断开关：

| 开关 | 作用 |
| --- | --- |
| `-Dmtrpsdlcd.debug=true` | 输出详细日志（开发环境默认开启） |
| `-Dmtrpsdlcd.dumpmaps=true` | 把生成的线路图 / 面板贴图导出到 `<gamedir>/psd-debug/*.png` |

构建前建议先跑模型校验脚本（检查贴图引用、`uv` 长度、blockstate→模型解析等，已多次拦住实机故障）：

```bash
python tools/validate_models.py
```

## 与原版的差异

除"Fabric → Forge"这一根本性差异外，本移植对以下行为做了**有意改动**（每项都在 [PORT-REPORT.md](PORT-REPORT.md) 里记录了原因与验证过程）：

1. **屏蔽门类方块改为不烘焙模型**：原版 10 个门 blockstate 依赖 `temp` 属性，1.20.1 的 MTR 门并不注册它，
   会造成 640 次「模型烘焙失败」并显示紫黑格 → 改为 `minecraft:block/air`，门体继续由 MTR 的 BER 绘制；
2. **顶板正面接缝线**：MTR 自带的 `psd_top_edge` 贴图里画有一条横线 + 一条竖线，原版模型铺 UV 后正好压在文字上；
   本移植在**自身命名空间**新增两张"干净"贴图（`psd_top_clean` / `psd_top_edge_clean`），
   仅在**正面（east）**使用，背面按 MTR 原模型的 UV 手法保留**唯一一条**接缝线（与 MTR 顶板同高，y=8.75），其余面保持原样；
3. **原版信息顶板（`psd_top`）绘制门槛放宽**：原版仅当正下方是屏蔽门/玻璃时才绘制内容，
   现在只要能取到站台数据就绘制（与 MTR 原版顶板行为一致）；
4. **凹槽屏线路图区间**：保持**官方行为**（上一个站 + 本站 + 后 3 站）；
   需要整条线路请使用 `lcd12`（整幅玻璃线路图）或 `lcd13`（每线路一张完整图）。

## 下载

预编译 jar 见本仓库 **Releases** 页面（或自行按上面的步骤构建）。

## 反馈

请在本仓库的 [Issues](https://github.com/ChihayaAnonQWQ/mtr_psd_lcd/issues) 反馈 —— **不要**打扰原作者。
