测试版README，待完善......

-------
## 前言
### 实现效果如下
![登录注册实现图](https://img-blog.csdnimg.cn/20190608012517767.gif)

项目源代码GitHub地址(源码中有详细注释)：[https://github.com/littlecurl/LoginDemo](https://github.com/littlecurl/LoginDemo)

apk下载体验地址（2019年7月之后服务器到期就不能体验了）：...明天我把它放服务器再更新
<hr />
### 交代一些资源

我曾经写过一篇受大家欢迎的博客：

[【Android】实现登录、注册、数据库操作（极简洁）](<https://blog.csdn.net/midnight_time/article/details/80792255>)

现在这篇文章是原先那篇的升级版，实现了前后端分离。

配套的后端服务器的笔记如下：

1. [【JavaEE】电商秒杀项目·第2章·基础项目搭建](<https://blog.csdn.net/midnight_time/article/details/90717676>)
2. [【JavaEE】电商秒杀项目·第3章·用户模块开发](<https://blog.csdn.net/midnight_time/article/details/91048543>)

后端视频链接：【慕课网 · 免费课】龙虾三少[《SpringBoot构建电商基础秒杀项目》](https://www.imooc.com/learn/1079)

我就是跟着三少的视频学的注册登录的后端服务，墙裂推荐大家去听一听三少的课，讲的是相当的棒！听课的时候，记得把宿舍门关好，这样你就像我一样也是三少的关门弟子了！O(∩_∩)O哈哈~，三少还有一个升级课程，是收费的，条件允许的小伙伴可以去参观一下[《Java电商秒杀系统深度优化 从容应对亿级流量挑战》](https://s.imooc.com/SJ09s6y)

另外要想真正学会我这个登录注册的Demo推荐你看几本书：

1. 《Spring 实战（第四版）》

2. 《SpringBoot实战》

3. 《看透SpringMVC源代码分析与实践》

4. 《第一行代码（第二版）》

### 闲谈几句

话说去年的这个时候刚刚接触Android，有幸在一开始就看到郭神的《第一行代码（第二版）》，然后我连着一个星期，每天最早都是晚上三点睡，白天八点醒，中间不睡觉的学习Android，因为我习惯在看书的时候关上宿舍门，所以我也算是郭神的关门弟子了，O(∩_∩)O哈哈~

我在开发这个项目之前，看过大量的书籍和博客，都没有适合的源码。于是我带着问题去问郭神，我问了两个问题，郭神答了两个问题，因此我很受鼓舞，于是，又是几乎通宵了一周，做出来现在这个Demo，我就是想做出来一个别人认为是奇葩，实际是因为我没钱租服务器，而迫不得已用自己电脑当服务器来学习的项目。

![郭霖聊天截图](https://img-blog.csdnimg.cn/2019060801290369.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L21pZG5pZ2h0X3RpbWU=,size_16,color_FFFFFF,t_70)


### 本文知识点

1. Gradle依赖
2. Android布局
3. 利用正则表达式进行输入校验
4. 自动登录的实现
5. okhttp异步发送POST请求
6. Gson解析okhttp回调响应的JSON数据
7. 在子线程中更新UI，自动填写验证码
8. 内网穿透，真机安装运行体验

如果把这个版本的登录注册掌握了，基本的前后端联合开发就可以入门了。

## 正文

### 一、Gradle依赖

#### 1、Gradle版本号问题

本来Android布局应该是在第一的位置来写的，但是Gradle依赖和打开项目的时间有关系，为了能节省大家打开一个项目的时间，我把Gradle放在第一位来讲了。

我用加粗的格式说一句来表示强调：**当你从网上下载下来一个项目时，先别急着用AndroidStudio打开。**

在打开一个新项目之前，最好先手动修改两个版本号。如下图所示![2927](https://img-blog.csdnimg.cn/20190608013048285.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L21pZG5pZ2h0X3RpbWU=,size_16,color_FFFFFF,t_70)

![0915](https://img-blog.csdnimg.cn/20190608013104864.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L21pZG5pZ2h0X3RpbWU=,size_16,color_FFFFFF,t_70)

对应上面两个文件，在目录中显示如下

![4702](https://img-blog.csdnimg.cn/2019060801294494.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L21pZG5pZ2h0X3RpbWU=,size_16,color_FFFFFF,t_70)
怎么改呢？很简单，就是你先打开一个你**以前写过的项目**，进去看看你电脑能轻松打开的项目的这两个版本号分别是多少，然后再用**记事本**打开从网上下载下来对应的文件，**修改**版本号为你本地已有的版本号，**最后再打开项目**。

之所以这样做是因为，如果本地没有对应的Gradle，下载的话很慢，而且一旦下载，很难暂停。不如花两分钟手动修改一下版本号节省时间。

当然，对于某些项目，有可能修改之后，导致无法编译。那样的话，没法，只能下载指定的Gradle版本了。

最后，有些人不在乎浪费时间，比如我，我经常忘记修改，导致我电脑现在几乎下遍了所有的Gradle版本，然后现在可以随意打开项目了。

#### 2、Gradle的作用

如果学过JavaEE的同学应该都知道Maven，它可以帮我们管理依赖的jar包，实现自动导入。Gradle的作用和Maven一样，只不过Maven使用XML语言来实现的，Gradle是用Groovy语言来实现的。后者明显比前者简洁许多。Android中导入jar包依赖的地方如下，比如我这个登录注册就使用了两个第三方库okhttp和Gson，这些库都可以在Maven仓库：[http://mvnrepository.com](http://mvnrepository.com)搜索到。别忘了修改后需要点一下右上角的Sync Now才能生效。

![5136](https://img-blog.csdnimg.cn/20190608013133227.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L21pZG5pZ2h0X3RpbWU=,size_16,color_FFFFFF,t_70)
#### 3、手动刷新Gradle依赖

如果你的项目里，出现了大面积的红字，有可能就是Gradle没有工作或者工作到一半不小心被你中途暂停了。解决方法就是如下，重新让Gradle跑一下，编译项目。

![3572](https://img-blog.csdnimg.cn/20190608013147319.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L21pZG5pZ2h0X3RpbWU=,size_16,color_FFFFFF,t_70)
#### 4、查看具体依赖的jar包

有时候我们会遇到jar包冲突的bug，或者说就是想单纯的看看我那简单的一句implementations到底导入了多少个jar包，这也是为什么Groovy语言的Gradle和XML语言的Maven相比，前者就能如此简洁。这时候，就可以去AndroidStudio右侧边栏，点开Gradle工具条，然后找到app底下的AndroidDependencies，双击运行之后，就可以去控制台查看你这个项目到底依赖了多少个jar包。
![5823](https://img-blog.csdnimg.cn/20190608013155968.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L21pZG5pZ2h0X3RpbWU=,size_16,color_FFFFFF,t_70)
学习Gradle，道阻且长，不过对于目前这个项目，我上面介绍的已然够用了。

### 二、Android布局

Android布局开发有两种方式，Design和Text
![2253](https://img-blog.csdnimg.cn/20190608013220501.png)
我最近才发现有些同学因为没跟对老师，每次写布局的时候都是直接手敲代码，原因就是老师就这么教的。

其实那样并不好，一方面，虽说常用的控件就那么几个但每个控件的属性可是一大堆的，直接写代码容易漏掉某个属性；另一方面，通过Design可视化进行布局，能够获得及时反馈，而且一拖即成会感觉很爽。

所以推荐大家首先是要Design方式拖拉出来界面，如果需要微调的话，或者拖拉不能实现了，再使用代码进行改动。

因为这个属于操作性比较强的，如果有机会，我会录个视频分享给大家，这里我就给大家贴一张图吧

![4428](https://img-blog.csdnimg.cn/20190608013231454.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L21pZG5pZ2h0X3RpbWU=,size_16,color_FFFFFF,t_70)
这次登录界面我用的是ConstraintLayout，以实现不同分辨率手机的自适应。注册界面因为需要填写的有点多，用的是ScrollView+LinearLayout实现了滚动布局。

[ConstraintLayout官方文档](<https://developer.android.google.cn/reference/android/support/constraint/ConstraintLayout>)

[ScrollView官方文档](<https://developer.android.google.cn/reference/android/widget/ScrollView?hl=en>)

[LinearLayout官方文档](<https://developer.android.google.cn/reference/kotlin/android/widget/LinearLayout?hl=en>)

官方文档不容易看懂，最好的学习方法就是上手实践一下。

### 三、利用正则表达式进行输入校验

本来这个小标题应该是：观察者模式校验前端输入数据

我本想根据AndroidStudio自带的LoginActivity写写呢MVVM模式的，但是研究了一天没搞懂。

所以下面按我实现的写了。

在登录界面，我们需要输入账号和密码

本来我想实现账号既可以是用户名也可以是手机号还可以是邮箱，但是考虑到有点麻烦，我就只实现了手机号验证，而且是非严格的正则校验，代码如下：

```java
// 校验账号不能为空且必须是中国大陆手机号（宽松模式匹配）
private boolean isTelphoneValid(String account) {
    if (account == null) {
        return false;
    }
    // 首位为1, 第二位为3-9, 剩下九位为 0-9, 共11位数字
    String pattern = "^[1]([3-9])[0-9]{9}$";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(account);
    return m.matches();
}
```

密码要求不少于6位

```java
// 校验密码不少于6位
private boolean isPasswordValid(String password) {
    return password != null && password.trim().length() > 5;
}
```

而且我实现了EditText的setOnFocusChangeListener方法，根据输入数据的类型调用对应的方法进行校验。这样就可以在输入框改变焦点的时候进行校验，判断输入格式是否正确，如果错误，则会在右侧出现错误提示。

代码如下:

```java
private void setOnFocusChangeErrMsg(EditText editText,String inputType, String errMsg){
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        if (!hasFocus){
                            if(inputType == "phone"){
                                if (isTelphoneValid(inputStr)){
                                    editText.setError(null);
                                }else {
                                    editText.setError(errMsg);
                                }
                            }
                            if (inputType == "password"){
                                if (isPasswordValid(inputStr)){
                                    editText.setError(null);
                                }else {
                                    editText.setError(errMsg);
                                }
                            }
                        }
                    }
                }
        );
    }
```

实现效果如下：

![5456](https://img-blog.csdnimg.cn/20190608013250169.png)


#### 四、自动登录的实现

自动登录的原理就是，用户第一登录时会有一个token为null，如果登录成功，那么就在本地存一个token，关掉应用(不是退出登录)再次打开的时候，判断token是否正确，如果正确就自动登录。

这里本地存放token使用了Android自带的SharedPreferences。

在onCreate里面获取SharedPreferences对象，这里的语法有点奇怪，我什么都没有呢就进行get，但是Android 就是这么规定的。下面这句代码相当于声明了一个小型数据库，名为login_info，模式是私有模式，应该是会对数据进行加密，这种小型数据库只能存放k,v键值对。

```java
sp = getSharedPreferences("login_info",MODE_PRIVATE);
```

往sp内存放数据需要先获取一个SharedPreferences.Editor对象editor，然后执行下面这样的代码

```java
editor = sp.edit();
editor.putString("token","token_value");
editor.putString("telphone",telphone);
editor.commit(); // 或者 editor.apply();
```

#### 五、okhttp异步发送POST请求

这次大动干戈的将前后端进行分离，主要就是依靠像okhttp这样的框架来实现的。

因为请求URL属于耗时操作，所以开一个线程，避免耗时操作在子线程中进行。

okhttp异步POST请求，总共5步，如下代码所示：

```java
private void asyncValidate(final String account, final String password){
        new Thread( new Runnable() {
            @Override
            public void run() {
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2、构建请求体requestBody
                RequestBody requestBody = new FormBody.Builder()
                        .add("telphone", account)
                        .add("password", password)
                        .build();
                // 3、发送请求，因为要传密码，所以用POST方式
                Request request = new Request.Builder()
                        .url(loginURL)
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue( new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // ...
                    }

                    @Override
                    public void onResponse(Call call, Response response)
                        // ...
                    }
                });

            }
        }).start();
    }
```

这里，我用的这个版本okhttp要求API level 21+ 但夜神模拟器API是19

#### 六、Gson解析okhttp回调响应的JSON数据

后端Controller层的方法上都加了@ResponseBody的注解，功能就是把返回值封装成JSON格式字符串。

所以，okhttp回调接收的是一个JSON格式的字符串。这个字符串被封装在response对象的body里。

我们要做的就三步

```java
// 1、获取response对象的body里的字符串
String responseBodyStr = response.body().string();

// 2、将其解析为JSON对象
JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);

// 3、使用JSON对象获取具体值
String status = responseBodyJSONObject.get("status").getAsString();
```

这样就能解析形如下面的JSON数据

```json
{
    "status" : "success",
    "data" : null
}
```

#### 七、在子线程中更新UI，自动填写验证码

在子线程中更新UI的方式有好多，大家可以自行百度，大致有四种，我综合考虑了一下简洁、易懂与适用性，选择了runOnUiThread这种方式

```java
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
});
```

在子线程中更新UI，将后端返回的验证码解析并填充到对应输入框

```java
/* 在子线程中更新UI ，实现自动填充验证码 */
private void setTextInThread(EditText editText,String otpCode){
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            editText.setText(otpCode);
        }
    });
}
```

#### 八、内网穿透，真机安装运行体验

电脑模拟器运行的IP说明：

如果使用AndroidStudio自带或者Genymotion模拟器，可以使用10.0.0.2来指明本机IP地址。Android里127.0.0.1和localhost值得是模拟器的地址，而非你电脑的地址。

如果使用夜神、雷电等模拟器需要打开cmd然后执行ipconfig命令，查看并使用本地电脑的IP地址。

真机运行的IP说明：

两种方式，一种就是自己租服务器，把后台代码放到远程服务器里跑，这样就有公网IP了。

另一种就是还是使用自己电脑，使用花生壳等工具进行内网穿透，这样也可以拥有公网IP。

有机会的话，我会录个视频，来专门说说这件事。


#### 九、待完成

1. 防止疯狂登录不存在的用户
2. 防止疯狂注册用户
3. 实现分布式Session
4. ......














