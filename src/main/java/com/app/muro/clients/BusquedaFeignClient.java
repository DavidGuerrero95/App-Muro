package com.app.muro.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-busqueda")
public interface BusquedaFeignClient {

	@PutMapping("/busqueda/muro/editar/")
	public Boolean editarMuro(@RequestParam Integer nombre);

	@PutMapping("/busqueda/muro/eliminar/")
	public Boolean eliminarMuro(@RequestParam List<Integer> listaMuro);

}
