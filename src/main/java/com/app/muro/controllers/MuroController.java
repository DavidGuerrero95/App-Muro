package com.app.muro.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.app.muro.models.Muro;
import com.app.muro.repository.MuroRepository;
import com.app.muro.services.IMuroService;

@RestController
public class MuroController {

	@Autowired
	MuroRepository mRepository;

	@Autowired
	IMuroService mService;

//  ****************************	MURO	***********************************  //

	// CREAR MURO
	@PostMapping("/muros/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean crearMuros(@RequestBody @Validated Muro muro) {
		mService.crearMuros(muro);
		return true;
	}

	// MICROSERVICIO PROYECTOS -> ELIMINAR PROYECTO
	@PutMapping("/muros/proyecto/eliminar/{codigo}")
	public Boolean eliminarProyecto(@PathVariable("codigo") Integer codigo,
			@RequestParam("idProyecto") Integer idProyecto) throws IOException {
		try {
			mService.eliminarProyecto(codigo, idProyecto);
			return true;
		} catch (Exception e) {
			throw new IOException("Error eliminar proyectos, muro: " + e.getMessage());
		}
	}

	// MICROSERVICIO PROYECTOS -> CREAR
	@PostMapping("/muros/proyectos/crear/")
	public Integer crearMurosProyectos(@RequestParam("idProyecto") Integer idProyecto,
			@RequestParam("localizacion") List<Double> localizacion) throws IOException {
		try {
			return mService.crearMuroProyectos(idProyecto, localizacion);
		} catch (Exception e2) {
			throw new IOException("Error crear proyectos, muro: " + e2.getMessage());
		}
	}

	// MICROSERVICIO RECOMENDACIONES -> LISTAR
	// LISTAR
	@GetMapping("/muros/listar/")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Muro> getMuros() {
		return mService.encontrarMuros();
	}

	// BUSCAR
	@GetMapping("/muros/buscar/{codigo}")
	@ResponseStatus(code = HttpStatus.FOUND)
	public Muro getMuroCodigo(@PathVariable("codigo") Integer codigo) throws IOException {
		if (mRepository.existsByCodigoMuro(codigo))
			return mService.encontrarMuro(codigo);
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El Muro no existe");
	}

}
