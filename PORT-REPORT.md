# mtr_psd_lcd → Forge 1.20.1 + MTR 3 移植报告

- **移植对象**：`mtr_psd_lcd`（基于 MTR 的带 LCD 显示屏屏蔽门 / 信息顶板扩展模组）
- **源版本**：`mtr_psd_lcd+fabric1.19.2-MTR3.2.2 1.2.8.jar`（Fabric 1.19.2 + MTR 3.2.2，v1.2.8）
- **目标平台**：Minecraft **1.20.1** / **Forge 47.4.0** / **MTR 3.2.2-hotfix-2（Forge 构建）** + Architectury 9.2.14
- **产物**：`build/libs/mtr_psd_lcd-forge-mc1.20.1-mtr3-1.2.8.jar`
  （SHA-256 `49ABA99EE09881DC58D4D0822FFA7E29AD736512744BD3EC8E3AA3CC5FFB6B77`，含 §4.4 的屏蔽门模型修复）
- **实机验证环境**：Forge 1.20.1-47.4.15 + MTR-forge **3.6.3**（Yomi fork）+ Architectury 9.2.14
- **移植**：**DeepSeek**（DeepSeek Harness agent），2026-10-06
- **实机测试 / 发布维护**：**ChihayaAnonQWQ**（整合包内实机测试，测试期间未发现问题）
- **反馈渠道**：本移植为**非官方**版本；如出现问题**请勿提交给原仓库 / 原作者**，请使用本 fork 的 Issues

---

## 1. 为什么需要新移植（上游现状）

| 上游产物 | 平台 | MTR 版本 | 能否直接用 |
| --- | --- | --- | --- |
| GitHub `Zzztick365/mtr_psd_lcd` `main` | Fabric 1.20.1（源码） | **MTR 4.0.5** | ✗ 加载器与 MTR 大版本都不符 |
| Modrinth `forge1.20.1+1.2.7` | Forge 1.20.1 | **MTR 4**（`mods.toml` 要求 `[4.0.0,)`） | ✗ MTR 版本不符 |
| Modrinth `MTR3.22+Farbic1.19.2+1.2.8` | Fabric 1.19.2 | MTR 3.2.2 | ✗ 加载器与 MC 版本不符 |

上游**没有** Forge 1.20.1 + MTR 3 的组合，因此本次以 v1.2.8（MTR 3 代码路径）为功能基准，重新落到 Forge 1.20.1。

## 2. 技术路线

### 2.1 得到可读、可编译的源码

Fabric 生产 jar 中的 Minecraft 类型是 intermediary 名（`class_2248`、`method_10230`），无法直接编译到 Forge。步骤：

1. **自建映射**：`tools/gen_mojmap.py` 把 Mojang `client_mappings.txt`（obf ↔ Mojang 官方名）与 loom `mappings-srg.tiny`（obf ↔ intermediary ↔ yarn）按 `(类, 成员名, 描述符)` 拼接成 `intermediary → official` 的 tiny v2 映射（生成 78,242 条成员映射）。
2. **重映射**：`tiny-remapper` 用该映射把 Fabric jar 重映射到 Mojang 官方命名。
3. **反编译**：`forgeflower 2.0.629.0` 输出 101 个 `.java`（约 1 万行）。

> 踩坑记录：映射脚本最初把 `byte[]` 归一化成 `byte`，导致 NBT 的数组重载（`putIntArray` / `putByteArray` / `putLongArray`）被错映射到标量方法。修正后重跑全链路，并逐一核对差异（见 §4.2）。

### 2.2 Fabric API → MTR 3 映射层 / Forge

MTR 3 自带一套跨加载器映射层（`mtr.Registry`、`mtr.mappings.*`、`mtr.RegistryObject`），Forge 与 Fabric 构建的 `mtr.*` 类完全同名。因此绝大多数 Fabric API 调用可以换成 MTR 3 的等价物，代码结构与上游保持一致：

| 上游（Fabric） | 本移植（Forge / MTR 3） |
| --- | --- |
| `ModInitializer` / `ClientModInitializer` | `@Mod PSDForge` + `PSDForgeClient`（`FMLClientSetupEvent`） |
| `Registry.register(Registry.BLOCK/ITEM, …)` | `mtr.mappings.DeferredRegisterHolder` + `mtr.RegistryObject`（懒求值） |
| `FabricBlockEntityTypeBuilder.create(...)` | `mtr.mappings.RegistryUtilities.getBlockEntityType(...)` |
| `BlockRenderLayerMap.INSTANCE.putBlock` | `mtr.mappings.RegistryUtilitiesClient.registerRenderType` |
| `BlockEntityRendererRegistry.register` | `mtr.mappings.RegistryUtilitiesClient.registerTileEntityRenderer` |
| `FabricItemGroupBuilder.create(...)` | `DeferredRegisterHolder<CreativeModeTab>` + `CreativeModeTab.builder()` |
| `ServerPlayConnectionEvents.JOIN` | `mtr.Registry.registerPlayerJoinEvent` |
| `ServerPlayNetworking.registerGlobalReceiver` / `.send` | `mtr.Registry.registerNetworkReceiver` / `mtr.Registry.sendToPlayer` |
| `ClientPlayNetworking.registerGlobalReceiver` / `.send` | `mtr.mappings.NetworkUtilities.registerReceiverS2C` / `.sendToServer` |
| `PacketByteBufs.create()` | `new FriendlyByteBuf(Unpooled.buffer())` |
| `FabricLoader.getInstance().getGameDir()` | `FMLPaths.GAMEDIR.get()` |
| `FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT` | `FMLEnvironment.dist.isDedicatedServer()` |

**关键坑**：MTR 3 的 `DeferredRegisterHolder` 由 Architectury 支撑，而 Architectury 按 mod id 查找 mod 事件总线。必须先执行
`mtr.forge.mappings.ForgeUtilities.registerModEventBus(MOD_ID, modEventBus)`（MTR 自身 Forge 入口同样如此），否则构造期直接抛
`Can't get event bus for mod 'mtr_psd_lcd' because it was not registered!`。

**第二个坑**：Forge 1.20.1 里 `Block` 的构造函数会向方块注册表写入 intrusive holder，注册表冻结后就会抛
`Registry is already frozen`。所以所有方块/物品的 supplier 必须保持惰性——不能在 `register()` 阶段（如旧代码的日志行）
提前调用 `Blocks.PSD_X.get()`。

### 2.3 1.19.2 → 1.20.1 API 改动

| 1.19.2 写法 | 1.20.1 写法 |
| --- | --- |
| `Registry.BLOCK` / `Registry.ITEM` / `Registry.BLOCK_ENTITY_TYPE` | `BuiltInRegistries.BLOCK` / `.ITEM` / `.BLOCK_ENTITY_TYPE` |
| `Properties.of(Material.METAL, MapColor.X)`（`Material` 已移除） | `Properties.of().mapColor(MapColor.X)` |
| `Screen#render(PoseStack, int, int, float)` | `Screen#render(GuiGraphics, int, int, float)` |
| 屏幕内 `drawCenteredString(...)`（`Screen` 的旧辅助方法） | `guiGraphics.drawCenteredString(font, …)` |
| `new Button(x, y, w, h, Component, onPress)` | `Button.builder(Component, onPress).bounds(x, y, w, h).build()` |
| `CompoundTag.putIntArray(String, int)`（映射错位） | `CompoundTag.putInt(String, int)` |
| 反编译产物里的 `A$B` 内嵌类写法 | 源码要求的 `A.B` |

除上述之外，模组自身逻辑（LCD 排版、线路图生成、纹理缓存、自定义文字/图片的存档与分包协议）**原样保留**。

## 3. 目录与构建

```
port/mtr-psd-lcd-forge/
├── build.gradle              # ForgeGradle 6.0.54 + MixinGradle（用于重映射依赖的 refmap）
├── gradle.properties         # MC 1.20.1 / Forge 47.4.0 / MTR 1.20.1-3.2.2-hotfix-2
├── src/main/java/com/mtrpsdlcd/**   # 101 个移植后源码文件
└── src/main/resources/       # assets（205 项）/ data（69 项）/ META-INF/mods.toml
```

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
cd port\mtr-psd-lcd-forge
.\gradlew.bat build                     # 产出 build/libs/mtr_psd_lcd-forge-mc1.20.1-mtr3-1.2.8.jar
.\gradlew.bat runServer -PmtrRuntimeTest   # 开发服务端冒烟（自动带上 MTR 3 + Architectury）
.\gradlew.bat runClient -PmtrRuntimeTest   # 有图形环境时做目视验证
```

- 运行时依赖（由 MTR 自身 `mods.toml` 强制）：**MTR 3.2.2-hotfix-2（Forge）** 与 **Architectury API 9.2.14+forge**。
- `mods.toml` 依赖区间：`mtr` = `[1.20.1-3.2.2-hotfix-2,1.20.1-4.0.0)`，即接受 MTR 3.2.x～3.6.x，拒绝 MTR 4。
- 诊断日志：开发环境默认开启；生产环境可用 `-Dmtrpsdlcd.debug=true` 打开。

## 4. 验证

### 4.1 编译与打包

- `gradlew compileJava` → **BUILD SUCCESSFUL**（零错误；仅上游遗留的 `ResourceLocation(String,String)` 弃用告警）。
- `gradlew clean build` → **BUILD SUCCESSFUL**，产物 `mtr_psd_lcd-forge-mc1.20.1-mtr3-1.2.8.jar`
  （348,402 字节，404 项，Java 17，reobf 完成；SHA-256 `49ABA99EE09881DC58D4D0822FFA7E29AD736512744BD3EC8E3AA3CC5FFB6B77`）。
- 产物内引用 `net.fabricmc` 的类：**0 个**。
- 类清单与上游 Fabric jar 逐项比对：118 → 116，差异**恰好**是 4 个 Fabric 入口类被 2 个 Forge 入口类取代，其余 114 个类一一对应。

### 4.2 映射正确性复核

修正映射脚本后重跑「重映射 → 反编译」全链路，并与第一次产物做逐文件差异比对，得到**唯一**的净差异集合
（`putIntArray→putInt`、`putByteArray→putByte`、`putLongArray→putLong`、`readByteArray`、`writeByteArray`、`StateDefinition.Builder#add`），
其余 96 个文件逐字节一致——即映射修正没有引入其它改名。

### 4.3 服务端启动冒烟（Forge 1.20.1 + MTR 3.2.2 + Architectury 9.2.14）

```
[mtr_psd_lcd] ModRegistry: blocks registered
[mtr_psd_lcd] ModRegistry: block entity types registered
[mtr_psd_lcd] ModRegistry: items registered
[mtr_psd_lcd] ItemGroups: creative tab registered (visible items=22, hidden=6)
[modloading-worker-0/INFO] [dev.architectury.networking.forge.NetworkManagerImpl]: Registering C2S receiver with id mtr_psd_lcd:custom_text
[mtr_psd_lcd] ModRegistry: server receiver for the custom text/image upload packet registered, channel=mtr_psd_lcd:custom_text
[mtr_psd_lcd] PSDCustomTextStore loaded dir=…\run-server\psd_lcd_server wasSet=false text="" image=- bytes=0
[mtr_psd_lcd] ModRegistry: join push source for the custom text/image store registered, channel=mtr_psd_lcd:custom_text
[mtr_psd_lcd] ModRegistry: entries owned by this mod: blocks=41 block_entity_types=32 items=28
[Server thread/INFO] [minecraft/DedicatedServer]: Done (2.516s)! For help, type "help"
```

- 注册数量（`FMLCommonSetupEvent` 阶段统计，注册表事件已执行完毕）与源码逐项核对一致：
  方块注册调用 41 处 / 物品 28 处 / 方块实体类型 32 处 = 运行时 `blocks=41 block_entity_types=32 items=28`。
- 模组构造、注册表事件、MTR 与 Architectury 混入（refmap 经 MixinGradle 重映射）全部正常，服务器进入 `Done (...)`。
- 自定义文字/图片上传通道 `mtr_psd_lcd:custom_text` 已在 Architectury 网络层注册（C2S 接收器）。
- 服务器存档目录 `run-server/psd_lcd_server` 被正确初始化（自定义文字/图片的服务端持久化目录）。

### 4.4 实机反馈修复：屏蔽门「模型缺失（紫黑格）」

首次实机测试（Forge 1.20.1 + MTR-forge 3.6.3）发现 **10 种屏蔽门方块全部渲染为紫黑缺失模型**，客户端日志实测：

```
Exception evaluating model definition: 'mtr_psd_lcd:psd_door#end=true,facing=north,half=upper,side=left,unlocked=false'
java.lang.RuntimeException: Unknown property 'temp' on 'Block{mtr_psd_lcd:psd_door}'
（640 次 Unable to bake model + 640 次 Exception evaluating model definition）
```

**根因**：上游（MTR 3 Fabric 版）的 10 个门 blockstate 是 multipart，且 **32 个分支的 `when` 全部带 `"temp": "true"`**。而 MTR 的门方块在
`createBlockStateDefinition` 里只注册 `end / facing / half / side / unlocked` 五个属性——`TEMP` 字段虽然存在（3.6.3），**却从未注册**
（MTR 3.2.2 Forge 连字段都没有），因此在 Forge 上 `temp` 恒为非法属性：整个 blockstate 求值即抛异常，方块回退成缺失模型。
MTR 自己的门 blockstate 是 `{"variants": {"": {"model": "minecraft:block/air"}}}`——门**完全由方块实体渲染器 `RenderPSDAPGDoor` 绘制**（本移植已为全部 10 种门注册该渲染器）。

**修复**：把这 10 个门 blockstate 改为与 MTR 完全一致的空模型，交回 BER 渲染（`door_tile_entity_migration` 那套模型只是 MTR 旧版美术的静态复制品，
不含 LCD 内容，保留在 jar 中不影响）。原始文件存于 `port/notes/blockstates-before-door-fix/`。

**修复验证**（`gradlew runClient` 开发客户端完整跑完资源重载，按日志核对）：

| 指标 | 修复前（用户客户端日志） | 修复后（开发客户端日志） |
| --- | --- | --- |
| `Unknown property 'temp'` | 1280 | **0** |
| `Unable to bake model` | 640 | **0** |
| `Exception evaluating model definition` | 640 | **0** |
| `mtr_psd_lcd` 相关模型错误 | 640 | **0** |

另对全部资源做了静态校验：1,397 条 blockstate→模型引用、105 个模型文件的 `parent`、64 条贴图引用（含 33 条 `mtr:` 命名空间贴图）**全部可解析**，
`mtr:` 贴图在 MTR 3.2.2/3.6.3 jar 中均存在——即除 `temp` 条件外没有其它模型/贴图缺口。

### 4.5 实机反馈：凹槽屏线路图只显示 3 个站（**有意偏离上游的一处改动**）

实机反馈「LCD 顶板只能显示 3 个站，1.19.2 上能显示整条线路」。定位与结论：

**根因（上游设计，不是移植缺陷）**：凹槽屏一族（`MyPSDTopLcd14/17/18/20/21/22`，其中 `Lcd18/20/21/22 extends Lcd14`）走
`drawGroovedScreen → generateCustomSingleRouteMap → drawRouteMapInBand`，该方法把显示区间**写死为「本站 ±1 / +3」**：

```java
if (currentIndex <= 0)        { spanStart = 0;                           spanEnd = Math.min(n - 1, 3); }
else if (currentIndex >= n-1) { spanStart = Math.max(0, currentIndex-2); spanEnd = n - 1; }
else                          { spanStart = Math.max(0, currentIndex-1); spanEnd = Math.min(n - 1, currentIndex+3); }
```

三份独立证据完全一致，说明 1.19.2 上同一块面板也只能显示 ≤5 站：

| 来源 | 该处逻辑 |
| --- | --- |
| 用户提供的 1.19.2 v1.2.8 jar（`javap -c` 字节码，可见 `iconst_1/iconst_3`） | 本站 ±1 / +3 |
| 官方 Forge 1.20.1 **MTR 4** v1.2.7（反编译） | 本站 ±1 / +3 |
| 本移植 | 本站 ±1 / +3 |

另用用户存档验证数据本身完整：`Line 3‖外环` 共 **16 个站台**（`DumpMtrRoutes` 读取 `platform_ids`），即只是显示区间受限。

**当时的改动**：把该区间改为**整条线路**（`spanStart = 0; spanEnd = n - 1;`），只影响凹槽屏一族；
`lcd12`（整幅玻璃线路图）与 `lcd13`（每线路一张完整线路图）走 `generateRouteMap`，本来就是全线，未受影响。
站名以 -45° 斜排并自动缩放（最小 0.6 倍），16 站时较密但可读。

> ⚠️ **该改动已在后续回退**：用户比对官方 MTR 4 构建后确认官方就是「本站 ±1 / +3」，要求改回，详见 **§4.7**。

### 4.6 实机反馈：顶板上的横线 / 竖缝（**第二处有意偏离上游**）

用户反馈屏蔽门顶板 / 信息顶板上有一条**横线横穿站名与图标**，随后又指出方块交界处还有一条**竖缝**。

**最终根因（贴图，不是渲染代码也不是模型几何）**：MTR 自带的顶板贴图本身画着灰色接缝线：

| 贴图 | 内容 |
| --- | --- |
| `mtr:block/psd_top.png` | 64×64，白色 4032 px + **灰色 64 px（一整行横线）** |
| `mtr:block/psd_top_edge.png` | 64×64，白色 3969 px + **灰色 127 px（一条横线 + 一条竖线）** |

我们这批顶板模型直接引用了这两张贴图，而模型各面的 UV 把贴图整幅铺开（如 `[0,5.12,8]→[6,16,16]` 的 `east` 面 `uv:[0,0,16,15]`），
于是贴图里那两行灰线就落在面板中部，看起来「横穿文字」，竖线则落在方块交界处。

**定位过程**（可复现）：先用面板上已知高度的元素（黄色线路色带 `0.90625–0.9375`、LED 白底 `0.68–0.9`）对截图做像素标定，
得到 `屏幕Y = 532 + 512 × y`，算出灰线在 **y≈0.36**；再用 `-Dmtrpsdlcd.dumpmaps=true` 导出生成的方向牌贴图，
确认**线不在动态贴图里**；最后逐张核对静态贴图，在 MTR 的 `psd_top_edge.png` 中找到那 127 个灰色像素。

**本次改动（方案 A：只在自身命名空间内解决，MTR 资源零改动）**：

1. 新增 `mtr_psd_lcd:block/psd_top_clean.png`、`mtr_psd_lcd:block/psd_top_edge_clean.png`（把上述灰线像素涂白，其余不变）；
2. **只替换「正面」（`east`，即文字/LCD 所在的那一面）**：23 个模型中，凡含几何的模型其 `east` 面改用 `#front`（干净贴图），
   其余面（`west`/`up`/`down`/`north`/`south`）恢复 `#particle`（MTR 原贴图，**接缝线保留**）；纯贴图覆盖型（`elements` 为空的 8 个模型）全部恢复 MTR 原贴图。
   示例（`psd_top_left_edge.json`）：

   ```json
   "textures": { "particle": "mtr:block/psd_top_edge", "front": "mtr_psd_lcd:block/psd_top_edge_clean" }
   // [0,5.12,8]→[6,16,16]  up/west = #particle（保留线）   east = #front（去线）
   ```

   正面方向的判定是自动的：取模型中「x 方向零厚度且只有单个面」的薄片元素（凹槽底面），其面朝向即正面，全部 6 个含几何模型一致得到 `east`。

副作用：若某资源包重绘 `mtr:block/psd_top*`，本模组顶板的**正面**不再跟随（其余面仍跟随）；这是用户确认接受的取舍。

> ⚠️ **回归与修复记录**：第一版"整库批量替换"用的是**前缀替换**（`mtr:block/psd_top` → `mtr_psd_lcd:block/psd_top_clean`），> **误伤了 4 个模型的 `parent` 字段**（`mtr:block/psd_top_left_base` → `mtr_psd_lcd:block/psd_top_clean_left_base`，`_right` 同理），
> 导致 `psd_top_left_2/3`、`psd_top_right_2/3` 加载失败：**新放置**的信息顶板（该状态正用这几个模型，如 `psd_top_lcd18` 的 `persistent=none` 分支）
> 退化成"缺失材质"紫黑格立方体，用刷子改成 `route` 后走另一个模型才恢复正常——现象与用户反馈完全一致。
> **修复**：对照上游 1.19.2 jar 的原始 `parent` 还原；jar 内 **105 个模型 parent 引用校验 0 缺失**；
> 并补做审计：`psd_top_clean*` 只允许出现在 `textures.*` 与面 `texture` 字段中（当前 9 处，全部合法）。

此外，`RenderPSDTop.renderAdditional` 中上游为「玻璃显示区」画的两条白色横条（`0.88–0.9`、`0.68–0.7`，`WHITE_TEXTURE`）也一并去除，
它们与 LED 白底同色、本不可见；左右端头 0.02 宽的竖封边保留。

#### 4.6.1 最终方案（第二版，按实机反馈收敛）

第一版只处理了"自带贴图"的模型，实机仍见两处问题：**正面残留一条小线段**、**背面接缝线比 MTR 多**。最终方案：

| 位置 | 处理 | 依据 |
| --- | --- | --- |
| **正面 `east`** | 全部改用干净贴图 `#front`（无线） | 文字/LCD 所在面 |
| **背面 `west`** | 用 MTR 原贴图，但**只有主体板**保留那条线（`uv [16,0,0,15]`），其余小块的背面映射到无线条的白色条（`uv [16,15,0,16]`） | 完全照抄 MTR `psd_top_left_base`：其主体板 `uv [16,0,0,15]`、底部块 `uv [16,15,0,16]`，所以 **MTR 背面也只有一条线** |
| 上/下/侧面、端面 | 保持 MTR 原贴图（线保留） | 用户要求"只砍文字正面" |

补做的两处遗漏：

1. **基础模型**（`psd_top_left_base`、`psd_top_right_base`、`psd_top_left_persistent_base`、`psd_top_right_persistent_base`）自身不写 `textures`，
   贴图变量由子模型提供，因此第一版漏改 → 现在这些 base 模型自己声明 `{particle: MTR原贴图, front: 干净贴图}`（子模型只覆盖 `particle`，`front` 沿继承生效）。
2. `psd_top_left_2/3`、`psd_top_right_2/3` 的父级是 **MTR 自己的** `mtr:block/psd_top_*_base`，子模型无法逐面覆盖其贴图 →
   新建本模组副本 `psd_top_left_base_clean.json` / `psd_top_right_base_clean.json`（4 个元素，`east`→`#front`、`west` 按上表 UV），并把上述 4 个模型的父级重指过去。

贴图线条位置（实测）：`psd_top.png` 与 `psd_top_edge.png` 均为 64×64，灰线在**第 29 行**（UV `v≈7.25`）；`psd_top_edge.png` 另有一条**竖线在 u=0**。

#### 4.6.2 背面线条高度归一（第三版）

实机反馈：正面对了，但**背面各块的线高低不一、还多出一条**。原因：跑道上不同方块用不同模型，而每个模型的背面 UV 各不相同 → 线高不同。

基准取 **MTR 自己的 `mtr:block/psd_top_left_base`**：其主体板 `west` 面 `uv [16,0,0,15]`、纹理线在 `v=7.25`，
即线落在该板（y 1→16）的 `7.25/15` 处 → **y = 8.75**（方块高度的 **54.7%**）。

改动：本模组每个含 `west` 面的模型，取"包含 y=8.75 且最高"的元素作为背面主线面，令其 `uv` 的 `v2 = 7.25 / ((y_top-8.75)/(y_top-y_bottom))`，
使线**精确落在 y=8.75**；其余 `west` 面统一映射纯白条 `[16,15,0,16]`（不含线）。结果（19 个模型全部归一到 y=8.75）：

| 模型 | 背面主线元素 | uv v2 |
| --- | --- | --- |
| `psd_top_left/right_base`、`psd_top_left/right_edge` | y 5.12→16 | 10.88 |
| `psd_top_left/right_persistent_base` | y 8.5→16 | 7.5 |
| `psd_top_left/right_base_clean` | y 1→16 | 15.0 |
| `psd_top_lcd14_groove_*_base` | y 1.6→15.5 | 14.93 |
| `psd_glass_*_base`、`psd_pillar` | y 0→16 | 16.0 |
| `psd_top_box_full_2` | y 1→16 | 15.0 |

#### 4.6.5 回归：`Expected 4 uv values, found: 3` → 一批面板变黑紫格

给 4 个 base 模型（`psd_top_left/right_base`、`psd_top_left/right_persistent_base`）写背面主线 UV 时，
把数组写成了 **3 个数**（`[16, 0, 10.88]`，漏掉镜像端的 0），MC 校验 `BlockFaceUV` 直接抛
`JsonParseException: Expected 4 uv values, found: 3` → 模型加载失败 → 所有使用它们的面板（顶板、玻璃、LCD16 等）退化为"缺失模型"紫黑格。

**修复**：补齐为 4 个数（`[16, 0, 0, v2]`）。

**防再犯**：新增构建前校验脚本 **`port/tools/validate_models.py`**（退出码非 0 即硬性失败），检查项：

1. 每个 blockstate 引用的模型能沿 parent 链解析；
2. 每个面都有 `texture`，且 `#变量` 在整条链上有定义；
3. `from`/`to` 为 3 个数、`uv` **必须恰好 4 个数**；
4. 每个贴图路径真实存在（本模组或 MTR jar 内）；
5. 与上游 1.19.2 jar 的差异清单（仅供确认，当前 19 项，全部是顶板家族 + 2 个新增干净副本）。

当前运行结果：**硬性检查失败 0 项**。

#### 4.6.4 原版信息顶板（`psd_top`）不显示内容

`RenderPSDTop.renderAdditional` 的上游绘制门槛是：

```java
if (currentBlockBelow instanceof BlockPSDAPGDoorBase || standaloneModule) { ...绘制内容... }
```

`standaloneModule` = `block instanceof IStandaloneTopModule`，只有 `lcd8/9/10/11/12/13/14/21` 实现该接口；
**原版信息顶板（`MyPSDTop` 本体）没有实现**，所以当它正下方不是屏蔽门/玻璃时（例如悬空、或装在柱子/其他方块上）**只画色带、不画任何内容** → 看起来"显示没了"。

**改动**（有意放宽，向 MTR 原版顶板行为看齐）：新增

```java
boolean vanillaTop = block.getClass() == MyPSDTop.class;   // 仅原版信息顶板
if (currentBlockBelow instanceof BlockPSDAPGDoorBase || standaloneModule || vanillaTop) { ... }
```

即：原版信息顶板只要能取到站台数据就绘制内容，不再要求正下方必须是门/玻璃。其余 LCD 型号行为不变。

#### 4.6.3 竖线去除 + 误伤模型还原（第四版）

1. **背面多出的竖线**：来自 `mtr:block/psd_top_edge` 贴图 **u=0 处的那一列灰线**（`psd_top.png` 没有竖线，`psd_top_edge.png` 有）。
   凡在本模组模型中使用该原贴图的面，其 `uv` 的 u 区间若含 0，则把 0 改为 **0.5**（3% 偏移，肉眼无感）→ 竖线消失。
   涉及：`psd_top_left_edge.json`、`psd_top_right_edge.json`。
2. **误伤还原**：早期"整库批量替换"脚本把 **`psd_glass_*`（4 个 base + 16 个子模型）、`psd_pillar`、`psd_door_1`** 也一起改了
   （玻璃正面被换成不透明白贴图、门模型 `particle` 被改），导致"高站台玻璃变不透明且背面带线"。
   **这 21 个模型已逐字节从上游 1.19.2 jar 还原**，并加了**差异审计**：把本模组每个模型与上游逐字段比对，
   确认现在与上游存在差异的**只剩顶板家族 `psd_top*`** 以及新增的 `psd_top_left_base_clean` / `psd_top_right_base_clean`。

### 4.7 回退：凹槽屏线路图区间改回官方行为

§4.5 中曾按用户要求把 `drawRouteMapInBand` 的显示区间从「本站 ±1 / +3」改为**整条线路**；用户随后比对了官方 Forge 1.20.1 MTR 4
构建的实机画面，确认官方就是「上一个站 + 本站 + 后 3 站」，要求改回。**本次已回退为上表所列的上游三分支逻辑**（起点站 `0…3`、终点站 `n-3…n-1`、其余 `currentIndex-1 … currentIndex+3`）。
需要全线显示时使用 `lcd12`（整幅玻璃线路图）或 `lcd13`（每线路一张完整图），它们走 `generateRouteMap`，本就是全线。

### 4.8 `mtr_psd_lcd:psd_lcd`（LCD方块）为何右键/刷子/剪刀均无反应

用户提问该方块的用途。结论：**纯装饰方块，上游亦然，非移植缺陷**。

| 检查项 | 结果 |
| --- | --- |
| 本移植 `block/MyPSDLCD.java` | `extends Block`，11 行：无方块实体、无 blockstate 属性、无 `use()`、无 tick |
| 上游 `mtr_psd_lcd-fabric-1.19.2-1.2.8.jar`（`javap`） | `MyPSDLCD extends net.minecraft.class_2248`（= `Block`），同样无交互 |
| 模型 | `block/cube`：4 侧面 `psd_lcd.png`（青色菱形 + 中间深色方格带），顶/底 `psd_lcd_top_bottom.png`（纯白） |
| 获取 | 无序合成 `psd_glass` + `glowstone_dust` → 1（`data/mtr_psd_lcd/recipes/lcd_block.json`）；有掉落表 |
| 为何刷子无效 | 刷子作用于**带方块实体**、实现 MTR 接口的面板（顶板/玻璃/屏蔽门），装饰方块不在其列 |

**决定（用户 2026-10-06 选择）**：保持原样，不新增功能。

### 4.9 未验证项

- **客户端渲染**：LCD 面板/顶板的动态贴图渲染、屏蔽门动画、自定义文字/图片选择界面（AWT 文件对话框）仍需实机目视确认；
  本次已用开发客户端跑完资源重载并确认零模型烘焙错误（见 §4.4），但**未做画面目视核对**。
- 大网络下的分包上传压力、跨机联机联调未做。

## 5. 兼容性与注意事项

1. **不能与官方 MTR 4 版本共存**：`mod id` 相同（`mtr_psd_lcd`），`PSDForge` 启动时会扫描 `mods/` 并在发现多个同 id 版本时直接报错（与上游行为一致）。
2. **必须安装 Architectury API**（MTR 3 Forge 构建的强制依赖）。
3. MTR 4 的用户应使用官方 `forge1.20.1+1.2.7`，本移植面向 MTR 3 服务器/整合包。
4. 版权与署名：模组本体按上游 **MIT** 授权，`mods.toml` 的 `authors` 保留原作者 **Zzztick365**；
   移植由 **DeepSeek** 完成、**ChihayaAnonQWQ** 实机测试并维护仓库；本移植为**非官方移植版**，
   **如出现问题请勿提交给原仓库 / 原作者**，请反馈到本移植仓库。

## 6. 复现用工具

| 工具 | 用途 |
| --- | --- |
| `port/tools/gen_mojmap.py` | 生成 intermediary → Mojang 官方名映射 |
| `port/tools/apply_symbol_fixes.py` | 批量替换映射表覆盖不到的残留符号 |
| `tiny-remapper 0.10.3` + `mapping-io 0.5.1` + ASM 9.7.1 | jar 重映射 |
| `forgeflower 2.0.629.0` | 反编译 |
| `tools/verify_artifacts.py`（沿用上游移植树） | 产物结构校验 |
