package io.revx.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.mysql.entity.WhitelabelingEntity;
import io.revx.api.mysql.repo.WhiteLablingRepository;
import io.revx.core.response.ApiResponseObject;

import java.util.List;

@Component
public class WhiteLablingService {

    private static final String DEFAULT_SUBDOMAIN = "default";

    @Autowired
    WhiteLablingRepository whiteLablingRepository;

    public ApiResponseObject<WhitelabelingEntity> findBySubDomain(String subdomain) {
        ApiResponseObject<WhitelabelingEntity> rep = new ApiResponseObject<>();
        List<WhitelabelingEntity> entity = whiteLablingRepository.findBySubDomain(subdomain);
        if (entity == null || entity.isEmpty())
            entity = findDefault();
        rep.setRespObject(entity.get(0));
        return rep;
    }

    public ApiResponseObject<WhitelabelingEntity> findByLicenseeId(int licenseeId) {
        ApiResponseObject<WhitelabelingEntity> rep = new ApiResponseObject<>();
        List<WhitelabelingEntity> entity = whiteLablingRepository.findByLicenseeId(licenseeId);
        if (entity == null || entity.isEmpty())
            entity = findDefault();
        rep.setRespObject(entity.get(0));
        return rep;
    }

    private List<WhitelabelingEntity> findDefault() {
        return whiteLablingRepository.findBySubDomain(DEFAULT_SUBDOMAIN);
    }

}
