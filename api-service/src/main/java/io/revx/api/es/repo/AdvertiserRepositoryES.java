package io.revx.api.es.repo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;

@Repository
public interface AdvertiserRepositoryES extends ElasticsearchRepository<AdvertiserEntity, Integer> {

}
