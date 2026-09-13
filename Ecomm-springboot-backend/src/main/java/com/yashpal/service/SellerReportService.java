package com.yashpal.service;

import com.yashpal.model.Seller;
import com.yashpal.model.SellerReport;

public interface SellerReportService {
    SellerReport getSellerReport(Seller seller);
    SellerReport updateSellerReport( SellerReport sellerReport);

}
