package cn.edu.heuet.login.constant;

/*
这些内容按理说都应该在配置文件中，方便一些我直接放接口这里了
 */
public class NetConstant {
    /********************** 本地调试：本机内网域名 http *****************************/
    /*
    如果使用的模拟器是AndroidStudio自带的或者Genymotion模拟器，
    可以使用10.0.2.2来代表本机电脑IP
    这样做的好处就是不用每次断网重连后去ipconfig

    否则，像夜神、雷电等，就必须ipconfig一下
    String hostname = "172.31.84.21";
     */
    private static String getOtpCodeURL = "http://10.0.2.2:8090" + "/user/getOtp";
    private static String loginURL = "http://10.0.2.2:8090" + "/user/login";
    private static String registerURL = "http://10.0.2.2:8090" + "/user/register";
    private static String createItemURL = "http://10.0.2.2:8090/item/create";
    private static String getItemListURL = "http://10.0.2.2:8090/item/list";
    private static String submitOrderURL = "http://10.0.2.2:8090/order/createorder";
    /*
       如果想真机安装运行，可以自己租服务器，
       也可以使用花生壳、ngrok等内网穿透工具获得公网域名
     */
    /********************** 本地调试：花生壳公网域名 http  *****************************/
//    String hostname = "littlecurl.imwork.net";
//    String serverPort = "35304";
//    String getOtpCodeURL = "http://"+hostname+":"+serverPort+"/user/getOtp";
//    String loginURL = "http://"+hostname+":"+serverPort+"/user/login";
//    String registerURL = "http://"+hostname+":"+serverPort+"/user/register";

    /********************** 本地调试：ngrok公网域名 https  *****************************/
//    private static String getOtpCodeURL = "https://e1198c79.ngrok.io/user/getOtp";
//    private static String loginURL = "https://e1198c79.ngrok.io/user/login";
//    private static String registerURL = "https://e1198c79.ngrok.io/user/register";

    /********************** 远程服务器调试：阿里服务器腾讯公网域名 http  *****************************/

//    String hostname = "www.qiudong.xyz";
//    String serverPort = "8080";
//    String projectName = "LoginServer";
//    String getOtpCodeURL = "http://"+hostname+":"+serverPort+"/"+projectName+"/user/getOtp";
//    String loginURL = "http://"+hostname+":"+serverPort+"/"+projectName+"/user/login";
//    String registerURL = "http://"+hostname+":"+serverPort+"/"+projectName+"/user/register";

    /********************** 远程服务器调试：阿里服务器腾讯公网域名 https  *****************************/
    // 还没整明白如何给服务器安装证书呢
    public static String getGetOtpCodeURL() {
        return getOtpCodeURL;
    }

    public static String getLoginURL() {
        return loginURL;
    }

    public static String getRegisterURL() {
        return registerURL;
    }

    public static String getCreateItemURL() {
        return createItemURL;
    }

    public static String getGetItemListURL() {
        return getItemListURL;
    }

    public static String getSubmitOrderURL() {
        return submitOrderURL;
    }
}
