/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.util;

/**
 *
 * @author pyykkomi
 */
public class Pager {
    private int current;
    private int total;
    private int perPage;

    public Pager(int current, int total, int perPage) {
        this.current = current;
        this.total = total;
        this.perPage = perPage;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }
}
