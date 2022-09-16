package com.map.restaurant.good.config;

import org.springframework.jdbc.support.JdbcUtils;

import java.util.HashMap;

public class LowerHashMap extends HashMap {
    // 참고
    // https://ini8262.tistory.com/97
    @Override
    public Object put(Object key, Object value) {
        return super.put(JdbcUtils.convertUnderscoreNameToPropertyName((String) key), value);
    }
}
