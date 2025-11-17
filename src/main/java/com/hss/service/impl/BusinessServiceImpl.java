package com.hss.service.impl;

import com.hss.domian.BusinessInfo;
import com.hss.service.BusinessService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService {

    private List<BusinessInfo> businessInfos;

    @PostConstruct
    public void init(){
        businessInfos = new ArrayList<>();
    }

    @Override
    public void save(BusinessInfo businessInfo) {
        businessInfos.add(businessInfo);
    }

    @Override
    public List<BusinessInfo> findBusinessList(String openid) {
        return businessInfos;
    }
}
