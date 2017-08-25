/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.util;

import com.mipyykko.highscore.domain.Player;

/**
 *
 * @author pyykkomi
 */
public class CountObject implements Comparable<CountObject> {
    Object object;
    Long count;

    protected CountObject() {}
    
    public CountObject(Object object, Long count) {
        this.object = object;
        this.count = count;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public int compareTo(CountObject o) {
        return -this.count.compareTo(o.count);
    }

    
}
