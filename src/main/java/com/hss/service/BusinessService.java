package com.hss.service;

import com.hss.domian.BusinessInfo;

import java.util.List;

public interface BusinessService {

    public void save(BusinessInfo businessInfo);

    public List<BusinessInfo> findBusinessList(String openid);
}
