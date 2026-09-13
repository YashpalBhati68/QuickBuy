package com.yashpal.service;

import com.yashpal.model.Home;
import com.yashpal.model.HomeCategory;

import java.util.List;

public interface HomeService {

    Home creatHomePageData(List<HomeCategory> categories);

}
