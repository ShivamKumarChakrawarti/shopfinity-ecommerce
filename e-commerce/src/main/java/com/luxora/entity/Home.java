package com.luxora.entity;

import lombok.Data;

import java.util.*;

@Data
public class Home {
    private List<HomeCategory> grid;

    private List<HomeCategory> shopByCategories;

    private List<HomeCategory> electricCategories;

    private List<HomeCategory> dealCategories;

    private List<Deal> deals;
}
