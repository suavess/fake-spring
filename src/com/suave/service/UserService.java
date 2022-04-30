package com.suave.service;

import com.suave.spring.annotations.Autowired;
import com.suave.spring.annotations.Component;
import com.suave.spring.annotations.Scope;

/**
 * @author suave
 */
@Scope("prototype")
@Component
public class UserService {

    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println(orderService);
    }
}
