package com.app.muro.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.app.muro.models.Muro;

@FeignClient(name = "app-recomendacion")
public interface RecomendacionesFeignClient {

	@PostMapping("/recomendaciones/muro/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean anadirMuro(@RequestBody Muro muro);

	@DeleteMapping("/recomendaciones/muro/eliminar/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean deleteMuro(@RequestParam("nombre") Integer nombre);
}
