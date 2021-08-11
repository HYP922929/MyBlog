package com.hyp.service;

import com.hyp.dao.UserRepository;
import com.hyp.po.User;
import com.hyp.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser (String username,String password){
        User user = userRepository.findByUsernameAndPassword(username, MD5Util.code(password));
        return user;
    }
}
