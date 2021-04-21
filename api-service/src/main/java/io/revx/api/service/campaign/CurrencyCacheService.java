package io.revx.api.service.campaign;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.advertiser.CurrencyEntity;
import io.revx.api.mysql.repo.advertiser.CurrencyRepository;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.service.CacheService;


@Component
public class CurrencyCacheService {
  
  @Autowired
  CacheService cacheService;
  
  @Autowired
  CurrencyRepository currency;

  
  
 @SuppressWarnings("unchecked")
public CurrencyEntity fetchCurrencyByCode(String currencyCode) throws Exception {
    
   
   Set<DashboardFilters> filters = new HashSet<>();
   filters.add(new DashboardFilters("currencyCode",currencyCode));
    List<BaseEntity> listofCurrency=null;
    String cacheKey = getCacheKey();
    CurrencyEntity result = null;
    
    
    List<BaseEntity> listData = cacheService.fetchListCachedEntityData(cacheKey,
        filters,null);

    if (listData == null) {
      listofCurrency=(List<BaseEntity>)(List<?>)currency.findAll();
    }
    
    if (listofCurrency!=null && !listofCurrency.isEmpty()) {
      cacheService.populateCache(cacheKey, listofCurrency, 86400000L,CurrencyEntity.class);
      listData = cacheService.fetchListCachedEntityData(cacheKey,
          filters, null);
    }
    
    if(listData !=null && listData.size()>0)
      result= (CurrencyEntity)listData.get(0);
    
    return result;
  }
 
 @SuppressWarnings("unchecked")
public CurrencyEntity fetchCurrencyByName(String currencyName) throws Exception {
    
   
   Set<DashboardFilters> filters = new HashSet<>();
   filters.add(new DashboardFilters("currencyName",currencyName));
    List<BaseEntity> listofCurrency=null;
    String cacheKey = getCacheKey();
    CurrencyEntity result = null;
    
    
    List<BaseEntity> listData = cacheService.fetchListCachedEntityData(cacheKey,
        filters,null);

    if (listData == null) {
      listofCurrency=(List<BaseEntity>)(List<?>)currency.findAll();
    }
    
    if (listofCurrency!=null && !listofCurrency.isEmpty()) {
      cacheService.populateCache(cacheKey, listofCurrency, 86400000L,CurrencyEntity.class);
      listData = cacheService.fetchListCachedEntityData(cacheKey,
          filters, null);
    }
    
    if(listData !=null && listData.size()>0)
      result= (CurrencyEntity)listData.get(0);
    
    return result;
  }

private String getCacheKey() {
  
  return ApiConstant.CURRENCY_CACHE_KEY;
}
 
 
  
}
