package com.suave.service;

import com.suave.spring.FakeApplicationContext;

/**
 * @author suave
 */
public class Test {

    public static void main(String[] args) {
        FakeApplicationContext fakeApplicationContext = new FakeApplicationContext(AppConfig.class);
        UserService  userService = (UserService) fakeApplicationContext.getBean("userService");
    }

}
