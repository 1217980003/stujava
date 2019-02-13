# WebSocket实现Java后台消息推送

## 1.什么是WebSocket

　　WebSocket协议是基于TCP的一种新的网络协议。它实现了浏览器与服务器全双工(full-duplex)通信——允许服务器主动发送信息给客户端。

## 2.实现原理

　　在实现websocket连线过程中，需要通过浏览器发出websocket连线请求，然后服务器发出回应，这个过程通常称为“握手” 。在 WebSocket API，浏览器和服务器只需要做一个握手的动作，然后，浏览器和服务器之间就形成了一条快速通道。两者之间就直接可以数据互相传送。

　　　　　　　　　　　　　　　　　　　　　　　![img](https://images2017.cnblogs.com/blog/1318474/201802/1318474-20180201103651390-1585236943.png) 

## 3.优点

　　在以前的消息推送机制中，用的都是 Ajax 轮询（polling），在特定的时间间隔由浏览器自动发出请求，将服务器的消息主动的拉回来，这种方式是非常消耗资源的，因为它本质还是http请求，而且显得非常笨拙。而WebSocket 在浏览器和服务器完成一个握手的动作，在建立连接之后，服务器可以主动传送数据给客户端，客户端也可以随时向服务器发送数据。

## 4.WebSocket和Socket的区别

### 　　1.WebSocket:

1. websocket通讯的建立阶段是依赖于http协议的。最初的握手阶段是http协议，握手完成后就切换到websocket协议，并完全与http协议脱离了。
2. 建立通讯时，也是由客户端主动发起连接请求，服务端被动监听。
3. 通讯一旦建立连接后，通讯就是“全双工”模式了。也就是说服务端和客户端都能在任何时间自由得发送数据，非常适合服务端要主动推送实时数据的业务场景。
4. 交互模式不再是“请求-应答”模式，完全由开发者自行设计通讯协议。
5. 通信的数据是基于“帧(frame)”的，可以传输文本数据，也可以直接传输二进制数据，效率高。当然，开发者也就要考虑封包、拆包、编号等技术细节。

### 2.Socket:

1. 服务端监听通讯，被动提供服务；客户端主动向服务端发起连接请求，建立起通讯。
2. 每一次交互都是：客户端主动发起请求（request），服务端被动应答（response）。
3. 服务端不能主动向客户端推送数据。
4. 通信的数据是基于文本格式的。二进制数据（比如图片等）要利用base64等手段转换为文本后才能传输。

## 5.WebSocket客户端：

```
ar websocket = null;
var host = document.location.host; 
var username = "${loginUsername}"; // 获得当前登录人员的userName 
 // alert(username)
//判断当前浏览器是否支持WebSocket 
if ('WebSocket' in window) { 
    alert("浏览器支持Websocket")
    websocket = new WebSocket('ws://'+host+'/mm-dorado/webSocket/'+username); 
} else { 
    alert('当前浏览器 Not support websocket') 
} 
 
//连接发生错误的回调方法 
websocket.onerror = function() { 
　　alert("WebSocket连接发生错误")
   setMessageInnerHTML("WebSocket连接发生错误"); 
};  
   
//连接成功建立的回调方法 
websocket.onopen = function() {
　　alert("WebSocket连接成功") 
   setMessageInnerHTML("WebSocket连接成功"); 
} 
   
//接收到消息的回调方法 
websocket.onmessage = function(event) {
    alert("接收到消息的回调方法") 
    alert("这是后台推送的消息："+event.data);
　　　　 websocket.close();
　　　　alert("webSocket已关闭！")
} 
   
//连接关闭的回调方法 
websocket.onclose = function() { 
    setMessageInnerHTML("WebSocket连接关闭"); 
} 
   
//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。 
window.onbeforeunload = function() { 
    closeWebSocket(); 
} 
   
//关闭WebSocket连接 
function closeWebSocket() { 
    websocket.close(); 
} 
 
//将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }
```

## 6.WebSocket服务端（java后台）：

### 　　　　1.核心类：

```
package com.mes.util;
 
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
 
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
 
import com.google.gson.JsonObject;
 
import net.sf.json.JSONObject;
@ServerEndpoint("/webSocket/{username}")  
    public class WebSocket { 
        private static int onlineCount = 0; 
        private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>(); 
        private Session session; 
        private String username; 
           
        @OnOpen 
        public void onOpen(@PathParam("username") String username, Session session) throws IOException { 
       
            this.username = username; 
            this.session = session; 
               
            addOnlineCount(); 
            clients.put(username, this);
            System.out.println("已连接");
        } 
       
        @OnClose 
        public void onClose() throws IOException { 
            clients.remove(username); 
            subOnlineCount(); 
        } 
       
        @OnMessage 
        public void onMessage(String message) throws IOException { 
       
            JSONObject jsonTo = JSONObject.fromObject(message); 
            String mes = (String) jsonTo.get("message");
             
            if (!jsonTo.get("To").equals("All")){ 
                sendMessageTo(mes, jsonTo.get("To").toString()); 
            }else{ 
                sendMessageAll("给所有人"); 
            } 
        } 
       
        @OnError 
        public void onError(Session session, Throwable error) { 
            error.printStackTrace(); 
        } 
       
        public void sendMessageTo(String message, String To) throws IOException { 
            // session.getBasicRemote().sendText(message); 
            //session.getAsyncRemote().sendText(message); 
            for (WebSocket item : clients.values()) { 
                if (item.username.equals(To) ) 
                    item.session.getAsyncRemote().sendText(message); 
            } 
        } 
           
        public void sendMessageAll(String message) throws IOException { 
            for (WebSocket item : clients.values()) { 
                item.session.getAsyncRemote().sendText(message); 
            } 
        } 
       
        public static synchronized int getOnlineCount() { 
            return onlineCount; 
        } 
       
        public static synchronized void addOnlineCount() { 
            WebSocket.onlineCount++; 
        } 
       
        public static synchronized void subOnlineCount() { 
            WebSocket.onlineCount--; 
        } 
       
        public static synchronized Map<String, WebSocket> getClients() { 
            return clients; 
        } 
}
```

### 　　2.在自己代码中的调用：

```
WebSocket ws = new WebSocket();
JSONObject jo = new JSONObject();
jo.put("message", "这是后台返回的消息！");
jo.put("To",invIO.getIoEmployeeUid());
ws.onMessage(jo.toString());
```

## 7.所需maven依赖：

```
<!-- webSocket 开始-->
<dependency>
    <groupId>javax.websocket</groupId>
    <artifactId>javax.websocket-api</artifactId>
    <version>1.1</version>
    <scope>provided</scope>
</dependency>
 
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>7.0</version>
    <scope>provided</scope>
</dependency>
<!-- webSocket 结束-->
```

