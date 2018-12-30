package com.cloud.controller;

import com.cloud.feign.FeignClient2;
import com.cloud.feign.UserFeignClient;
import com.cloud.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {

  @Autowired
  private UserFeignClient userFeignClient;

  @Autowired
  private FeignClient2 feignClient2;

  @GetMapping("/movie/{id}")
  public User findById(@PathVariable Long id) {
    return this.userFeignClient.findById(id);
  }

  @GetMapping("/{serviceName}")
  public String findServiceInfoFromEurekaByServiceName(@PathVariable String serviceName) {
    return this.feignClient2.findServiceInfoFromEurekaByServiceName(serviceName);
  }
}
