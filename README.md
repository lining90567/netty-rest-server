# netty-rest-server
基于Netty实现的高性能RESTful服务框架

## 基于Netty开发
netty-rest-server是基于Netty开发的高性能RESTful框架，提供控制器注解、全局异常控制器、拦截器等功能。

## 主要注解
注解名称参考了Spring MVC，编译理解和记忆

- @RestController
- @RequestMapping
- @GetMapping
- @PostMapping
- @DeleteMapping
- @PutMapping
- @PatchMapping
- @JsonResponse
- @RequestParam
- @PathVariable
- @RequestBody
- @UploadFile
- @UrlEncodedForm
- @RequestHeader

## controller示例：
```java
//默认为单例，singleton = false表示启用多例。
//@RestController(singleton = false)
@RestController
@RequestMapping("/users")
public class UserController {
    
    @GetMapping("")
    @JsonResponse
    public ResponseEntity<User> listUser() {
        // 查询用户
        User user = new User();
        user.setId(1);
        user.setName("Leo");
        user.setAge((short)18);
        return ResponseEntity.ok().build(user);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") int id, @RequestBody String body) {
        // 更新用户
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable int id) {
        // 删除用户
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @PostMapping("")
    public ResponseEntity<?> postMethod(@RequestBody String body) {
        // 添加用户
        JSONObject json = JSONObject.parseObject(body);
        User user = new User();
        user.setId(json.getIntValue("id"));
        user.setName(json.getString("name"));
        user.setAge(json.getShortValue("age"));
        return ResponseEntity.status(HttpStatus.CREATED).build(user);
    }

}
```

## 拦截器示例：跨域控制器
```java
public final class CorsInterceptor implements Interceptor {

    @Override
    public boolean preHandle(FullHttpRequest request, HttpResponse response) throws Exception {
        // 使用axios发送cookie，这里不能用*，需要使用Web前端地址，如：http://localhost:8080
        // response.getHeaders().put("Access-Control-Allow-Origin", "*");
        response.getHeaders().put("Access-Control-Allow-Origin", System.getProperty("http.origin"));
        response.getHeaders().put("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, PATCH");
        response.getHeaders().put("Access-Control-Max-Age", "3600");
        response.getHeaders().put("Access-Control-Allow-Headers", "Content-Type,X-Token");
        response.getHeaders().put("Access-Control-Allow-Credentials", "true");
        return true;
    }

    @Override
    public void postHandle(FullHttpRequest request, HttpResponse response) throws Exception {
    }

    @Override
    public void afterCompletion(FullHttpRequest request, HttpResponse response) {
    }

}
```

## 启动服务
```java
@Test  
public void test() {  
    // 忽略指定url  
    WebServer.getIgnoreUrls().add("/favicon.ico");  
      
    // 全局异常处理  
    WebServer.setExceptionHandler(new ExceptionController());  
      
    // 设置监听端口号  
    WebServer server = new WebServer(2006);  
      
    // 设置Http最大内容长度（默认 为10M）  
    server.setMaxContentLength(1024 * 1024 * 50);  
      
    // 设置Controller所在包  
    server.setControllerBasePackage("org.leo.web.controller");  
      
    // 添加拦截器，按照添加的顺序执行。  
    // 跨域拦截器  
    server.addInterceptor(new CorsInterceptor(), "/不用拦截的url");  
      
    try {  
        server.start();  
    } catch (InterruptedException e) {  
        e.printStackTrace();  
    }  
}  
```

## 访问服务
http://localhost:2006/users

## 测试代码
src/test/java

## 典型应用
[leo-im](https://github.com/lining90567/leo-im-server/)

