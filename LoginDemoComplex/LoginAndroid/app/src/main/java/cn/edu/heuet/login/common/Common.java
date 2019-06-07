package cn.edu.heuet.login.common;

public interface Common {
    /*
      如果使用的模拟器不是AndroidStudio自带的
      或者Genymotion模拟器
      而是像夜神、雷电等，就必须ipconfig一下
      String hostname = "172.31.84.21";
    */
    /*
    如果使用的模拟器是AndroidStudio自带的或者Genymotion模拟器，
    可以使用10.0.2.2来代表本机电脑IP
    这样做的好处就是不用每次断网重连后去ipconfig
     */
//    String hostname = "10.0.2.2";
//    String serverPort = "8090";
    /*
       如果想真机安装运行，可以自己租服务器，
       也可以使用花生壳等内网穿透工具获得公网域名
       如下所示
     */
    String hostname = "littlecurl.imwork.net";
    String serverPort = "35304";

    String getOtpCodeURL = "http://"+hostname+":"+serverPort+"/user/getOtp";
    String loginURL = "http://"+hostname+":"+serverPort+"/user/login";
    String registerURL = "http://"+hostname+":"+serverPort+"/user/register";

}
