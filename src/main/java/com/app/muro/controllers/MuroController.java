package com.app.muro.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.muro.clients.BusquedaFeignClient;
import com.app.muro.clients.ParametrosFeignClient;
import com.app.muro.clients.RecomendacionesFeignClient;
import com.app.muro.models.Muro;
import com.app.muro.models.Proyectos;
import com.app.muro.repository.MuroRepository;
import com.app.muro.services.IMuroService;

@RestController
public class MuroController {

	private final Logger logger = LoggerFactory.getLogger(MuroController.class);

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	MuroRepository murosRepository;

	@Autowired
	ParametrosFeignClient pClient;

	@Autowired
	BusquedaFeignClient bClient;

	@Autowired
	IMuroService mService;

	@Autowired
	RecomendacionesFeignClient rClient;

	@GetMapping("/muros/listar/")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Muro> getMuros() {
		return murosRepository.findAll();
	}

	@GetMapping("/muros/buscar/{codigo}")
	@ResponseStatus(code = HttpStatus.FOUND)
	public Muro getMuroCodigo(@PathVariable Integer codigo) throws InterruptedException {
		return murosRepository.findByCodigoMuro(codigo);
	}

	@PostMapping("/muros/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public String crearMuros(@RequestBody @Validated Muro muro) {
		muro.setLocalizacion(new ArrayList<Double>(Arrays.asList(
				new BigDecimal(muro.getLocalizacion().get(0)).setScale(5, RoundingMode.HALF_UP).doubleValue(),
				new BigDecimal(muro.getLocalizacion().get(1)).setScale(5, RoundingMode.HALF_UP).doubleValue())));
		try {
			List<Integer> listaLabelMuro = pClient.getLabelMuros();
			muro.setCodigoMuro(listaLabelMuro.get(listaLabelMuro.size() - 1));
		} catch (Exception e) {
			List<Muro> listaLabel2 = murosRepository.findAll();
			muro.setCodigoMuro(listaLabel2.size() + 1);
		}
		murosRepository.save(muro);
		if (cbFactory.create("busqueda").run(() -> bClient.editarMuro(muro.getCodigoMuro()), e -> errorConexion(e))) {
			logger.info("Creacion Correcta");
		}
		if (cbFactory.create("proyecto").run(() -> rClient.anadirMuro(muro), e -> errorConexion(e))) {
			logger.info("Proyecto enviado");
		}
		return "Muro: " + muro.getCodigoMuro() + " Creado";
	}

	public Boolean errorConexion(Throwable e) {
		logger.info(e.getMessage());
		return false;
	}

	@PostMapping("/muros/proyectos/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Integer crearMurosProyectos(@RequestBody Proyectos proyectos) throws IOException {
		try {
			Muro muro = new Muro();
			if (murosRepository.findAll().isEmpty()) {
				try {
					List<Integer> listaLabelMuro = pClient.getLabelMuros();
					muro.setCodigoMuro(listaLabelMuro.get(listaLabelMuro.size() - 1));
				} catch (Exception e) {
					List<Muro> listaLabel2 = murosRepository.findAll();
					muro.setCodigoMuro(listaLabel2.size() + 1);
				}
				if (cbFactory.create("busqueda").run(() -> pClient.editarLabelMurosManejo(), e -> errorConexion(e))) {
					logger.info("Creacion Correcta");
				}
				muro.setLocalizacion(proyectos.getLocalizacion());
				List<String> listaPrimera = new ArrayList<String>();
				listaPrimera.add(proyectos.getNombre());
				muro.setNombreProyectos(listaPrimera);
				murosRepository.save(muro);
				return muro.getCodigoMuro();
			} else {
				Boolean bandera1 = false;
				for (int i = 0; i < murosRepository.findAll().size(); i++) {
					Double distancia = mService.distanciaCoord(murosRepository.findAll().get(i).getLocalizacion(),
							proyectos.getLocalizacion());
					if (distancia <= 1 && !bandera1) {
						muro = murosRepository.findByCodigoMuro(i + 1);
						List<String> listaProyectos = muro.getNombreProyectos();
						listaProyectos.add(proyectos.getNombre());
						muro.setNombreProyectos(listaProyectos);
						if (listaProyectos.size() <= 4) {
							List<Double> listaNuevaLocalizacion = mService.distanciaMedia(muro.getLocalizacion(),
									proyectos.getLocalizacion());
							muro.setLocalizacion(listaNuevaLocalizacion);
						}
						murosRepository.save(muro);
						bandera1 = true;
					}
				}
				if (!bandera1) {
					List<String> lista = new ArrayList<String>();
					try {
						List<Integer> listaLabelMuro = pClient.getLabelMuros();
						muro.setCodigoMuro(listaLabelMuro.get(listaLabelMuro.size() - 1));
					} catch (Exception e) {
						List<Muro> listaLabel2 = murosRepository.findAll();
						muro.setCodigoMuro(listaLabel2.size() + 1);
					}
					if (cbFactory.create("busqueda").run(() -> pClient.editarLabelMurosManejo(),
							e -> errorConexion(e))) {
						logger.info("Creacion Correcta");
					}
					muro.setLocalizacion(proyectos.getLocalizacion());
					lista.add(proyectos.getNombre());
					muro.setNombreProyectos(lista);
					murosRepository.save(muro);

				}
				Integer cod = muro.getCodigoMuro();
				if (cbFactory.create("busqueda").run(() -> bClient.editarMuro(cod), e -> errorConexion(e))) {
					logger.info("Creacion Correcta");
				}
				return muro.getCodigoMuro();
			}
		} catch (Exception e2) {
			throw new IOException("Error crear proyectos, muro: " + e2.getMessage());
		}

	}

	@PutMapping("/muros/proyecto/eliminar/{codigo}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean eliminarProyecto(@PathVariable("codigo") Integer codigo, @RequestParam("nombre") String nombre)
			throws IOException {
		try {
			Muro muro = murosRepository.findByCodigoMuro(codigo);
			List<String> listaProyectos = muro.getNombreProyectos();
			listaProyectos.remove(nombre);
			if (listaProyectos.isEmpty()) {
				eliminarMuro(codigo);
			} else {
				muro.setNombreProyectos(listaProyectos);
				murosRepository.save(muro);
			}
			return true;
		} catch (Exception e) {
			throw new IOException("Error eliminar proyectos, muro: " + e.getMessage());
		}

	}

	@DeleteMapping("/muros/eliminarMuro/{codigo}")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarMuro(@PathVariable Integer codigo) {
		Muro muro = murosRepository.findByCodigoMuro(codigo);
		String id = muro.getId();
		murosRepository.deleteById(id);
		List<Muro> murosList = murosRepository.findAll();
		List<Integer> busquedaLista = new ArrayList<Integer>();
		if (cbFactory.create("busqueda").run(() -> pClient.editarLabelMuroManejoDelete(murosList.size()),
				e -> errorConexion(e))) {
			logger.info("Creacion Correcta");
		}
		List<Integer> listaLabel = pClient.getLabelMuros();
		for (int i = 0; i < murosList.size(); i++) {
			murosList.get(i).setCodigoMuro(listaLabel.get(i));
			murosRepository.save(murosList.get(i));
			busquedaLista.add(listaLabel.get(i));
		}
		if (cbFactory.create("busqueda").run(() -> bClient.eliminarMuro(busquedaLista), e -> errorConexion(e))) {
			logger.info("Creacion Correcta");
		}
		if (cbFactory.create("busqueda").run(() -> rClient.deleteMuro(codigo), e -> errorConexion(e))) {
			logger.info("Creacion Correcta");
		}
	}
}
