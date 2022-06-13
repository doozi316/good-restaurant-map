package com.map.restaurant.good.controller;

import com.map.restaurant.good.dao.TestDAO;
import com.map.restaurant.good.dto.TestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DBConnectTestCtrl {

    @Autowired
    private TestDAO testDAO;

    @GetMapping("/hello")
    public List<TestDTO> test() {
        return testDAO.getTestData();
    }
}
