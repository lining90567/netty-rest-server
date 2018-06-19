package org.leo.web.controller;

import org.leo.web.User;
import org.leo.web.annotation.DeleteMapping;
import org.leo.web.annotation.GetMapping;
import org.leo.web.annotation.JsonResponse;
import org.leo.web.annotation.PathVariable;
import org.leo.web.annotation.PostMapping;
import org.leo.web.annotation.PutMapping;
import org.leo.web.annotation.RequestBody;
import org.leo.web.annotation.RequestMapping;
import org.leo.web.annotation.RestController;
import org.leo.web.rest.HttpStatus;
import org.leo.web.rest.ResponseEntity;

import com.alibaba.fastjson.JSONObject;

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
