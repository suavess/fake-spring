package com.suave.service;

import com.suave.spring.BeanNameAware;
import com.suave.spring.annotations.Autowired;
import com.suave.spring.annotations.Component;
import com.suave.spring.annotations.Scope;

/**
 * @author suave
 */
@Scope("prototype")
@Component
public class UserService implements BeanNameAware {

    private String beanName;

    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
