package com.app.muro.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.muro.models.Muro;

public interface MuroRepository extends MongoRepository<Muro, String> {

	@RestResource(path = "find-muro")
	public Muro findByCodigoMuro(@Param("codigoMuro") Integer codigoMuro);
	
	@RestResource(path = "exists-muro")
	public Boolean existsByCodigoMuro(@Param("codigoMuro") Integer codigoMuro);

}
