# Introduction

之前我用Flask做过一个简单的博客，技术栈为Flask+Vue。但是当时有些考虑有所不周，例如基本没有加密解密、没有进行负载测试、一些管理功能等于无等等等。由于我想要在Java这条道路上走下去，所以我决定用Spring来重新写一个博客系统。

我对于网站的安全性、数据的安全性有着一定的痴迷，常常想如果部署在阿里云上有一天把我挂掉怎么办？如果数据分在GitHub上，有一天微软也响应懂王的号召，不让我们用了怎么办？虽然我的站点没什么价值，但人心叵测，有人来攻击我怎么办？我想要有一个好的体验，速度上有所保障，支持一定人数的同时访问，服务器宕机怎么办？

我也曾一度想要使用静态站点，但是静态站点总让我感到不舒服。曾经我用过hexo搭建，但是感觉每天上传GitHub简直污染了我的小格子。这是我不能忍受的。

我也想不到不使用静态站点的充分理由，现在看来大体有这么几个考虑：

- 我想要让内容不在服务器本地，进而减少服务器压力，例如文章、分类等数据放到GitHub上。服务器只存储Vue的dist。但是这样的话何不把dist也放到一个托管商呢？
  - 我也觉得这样可以
  - 托管商可能速度不达标（GitHub），CDN加速的话有缓存时间（七牛云等厂商）
  - 动态内容不能设置，我需要一些动态内容，例如虽然现在不必要可能之后想要的用户登录注册
  - 我想要方便的扩展新的功能，例如我可能加上新的页面，仓库展示页面、刷题展示页面、文章待读、待做事项、推荐其他人的文章（+自己的推荐理由等）等

就目前的设计：

- 本地服务器存放的数据：
  - 文本分类数据
  - 文章List数据、history数据
- 其他站点的数据：
  - 每一篇具体文章的内容
- 为了扩展一些新的功能，我暂时决定如果不涉及Spring，每次替换dist。如果涉及Spring则每次重启服务器

# 准备的功能

服务器的功能：

1. ❌基本文章列表分页

2. ❌标签查询，按标签检索文章，标签树

   1. ❌根据标签衍生出来的其他页面（这些不在标签页中列出）：

      日记、随想（说说）、wiki、使用指南（搭建各种框架等的样板代码）

   2. ❌划分适用人群：

      给初学者看的、给进阶者看的、给高手看的、给阅读源码的人看的、给单纯了解原理的人看的、给单纯怎么用的人用的

3. ❌时间线：展示文章的时间线、自定义加入时间线的元素

4. ❌小页面：

   1. ❌友链、推荐博客/站点
   2. ❌个人的repo项目
   3. ❌个人的目标板、读书计划/神功之路、todo list
   4. ❌个人的成就版、GitHub小绿格子、刷题记录

5. ❌短消息：类似说说的功能

6. ❌自己的评论组件：需要 oauth 支持、需要聚合多个站点的评论、站点的评论

   1. ❌检测评论中的关键字，例如一些形如：博主哪个地方打错字了（让他按照git请求去修改），又例如没营养的废话：楼主写的真好啊，收藏mark了，不欢迎这样的评论，另外引战评论也一并不采取。

7. ❌自己的评分组件（对文章评分）：聚合多站点的评分、博客程序使用者可以自定义评分策略

8. ❌文章修改系统：

   1. 由别人来修改文章并等待审核

9. ❌增强的MD显示UI：

   1. ❌图片轮播

   2. ❌文章末尾显示文章待写内容等列表

   3. ❌显示这么一句话：`提醒：本文最后更新于 1465 天前，文中所描述的信息可能已发生改变，请谨慎使用。`

   4. ❌文章某些文字自动加上颜色高亮、某些代码行自动的高亮

   5. ❌对重要代码进行展示，对不重要的代码进行隐藏

      即可以折叠代码块，初始概览只显示重要的一部分，展开后显示所有代码。对重要代码部分实行行高亮。

   6. ❌声明文章都发表在何处（哪些站点）

   7. ❌不同设备上UI的显示

      在网页上，[android](https://github.com/noties/Markwon)上、`ipad`上的文章预览功能

   8. ❌夜间模式

10. ❌[前端UI采用动态组件实现插件化](https://cn.vuejs.org/v2/guide/components-dynamic-async.html)

11. ❌全文搜索

12. ❌好的SEO方案

13. ❌非功能性需求：

    1. ❌崩溃恢复数据
    2. ❌自动部署、更新脚本
    3. ❌一定并发访问量的支撑
    4. ❌安全性的要求

书写端/写作者终端的功能：

1. ❌站点统计数据：博文的数据、每个博客的数据
2. ❌即时的通知：有新评论的通知、文章修改通知
3. ❌对不同的存储位置上传博文的能力：向各个云服务器上传、向对象存储上传、向各大博客站点上传
4. ❌多图床管理：动态替换图片链接：当图片链接失效后动态替换掉（更换成其他图床的链接）
   1. ❌对多图床的管理、图床分组聚合
5. ❌对服务器的数据的管理：CRUD标签/隶属、CRUD文章LOCATION、CRUD权限和用户、CRUD一系列的小页面（友链、repo项目、目标板、成就版、todo list）
6. ❌对多地部署的博客的管理：在局域网的树莓派上、阿里云主机上、国外主机上等的
7. ❌备份下载服务器数据库
8. ❌基于本地文件系统、本地文件夹内操作GIT系统、使用客户端操作GIT系统的不同情况的处理
9. ❌文章上传预处理：
   1. ❌计算标签权重、进行标签去重
   2. ❌文章的加密、某种文字函数可以检测是不是我写的文章
   3. ❌字数统计：只统计看到的字数，类似结构化的控制信息（‘/’、‘-’、‘=’）不会被统计。
   4. ❌抽取标签的语句：抽取 `todo、code、策略、引用、警告` 等这样的自定义的待抽取Tag
   5. ❌标准检查和更正
   6. ❌对于需要处理的信息，在最后一步应当给出提示并且记录日志
   7. ❌替换图片为指定图床的链接
   8. ❌分别上传md和渲染好的html到不同的指定位置
   9. ❌系列聚合检查
   10. ❌文章声明选取：选取开源协议、适用人群等
10. ❌方便的命令行和UI操作：一切操作应当既可以用命令行也可以用可视化操作
11. ❌根据这些需求自己写的MD编辑器（类似typora的所见即所得编辑器）
    1. ❌自己的扩展语法需求（应当有合适的显示方式），包括但不限于：
       - ❌近义词提示：词语的多种称呼（直接内存 、又名本地内存）
12. 

