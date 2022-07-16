package com.app.muro.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.app.muro.clients.ParametrosFeignClient;
import com.app.muro.models.Muro;
import com.app.muro.repository.MuroRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MuroService implements IMuroService {

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	MuroRepository mRepository;

	@Autowired
	ParametrosFeignClient pClient;

	private Double distanciaCoord(List<Double> pos1, List<Double> pos2) {
		// double radioTierra = 3958.75;//en millas
		Double lat1 = pos1.get(0);
		Double lat2 = pos2.get(0);
		Double lon1 = pos1.get(1);
		Double lon2 = pos2.get(1);
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return (double) 0;
		} else {
			Double theta = lon1 - lon2;
			Double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
					+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			dist = dist * 1.609344;
			return dist;
		}
	}

	private List<Double> distanciaMedia(List<Double> pos1, List<Double> pos2) {
		List<Double> lista = new ArrayList<Double>();
		Double lat1 = pos1.get(0);
		Double lon1 = pos1.get(1);
		Double lat2 = pos2.get(0);
		Double lon2 = pos1.get(1);

		Double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		lon1 = Math.toRadians(lon1);

		Double Bx = Math.cos(lat2) * Math.cos(dLon);
		Double By = Math.cos(lat2) * Math.sin(dLon);
		Double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2),
				Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
		Double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
		lat3 = Math.toDegrees(lat3);
		lon3 = Math.toDegrees(lon3);
		BigDecimal bdlat3 = new BigDecimal(lat3).setScale(5, RoundingMode.HALF_UP);
		lat3 = bdlat3.doubleValue();
		BigDecimal bdlon3 = new BigDecimal(lon3).setScale(5, RoundingMode.HALF_UP);
		lon3 = bdlon3.doubleValue();
		lista.add(lat3);
		lista.add(lon3);
		return lista;
	}

	@Override
	public List<Muro> encontrarMuros() {
		return mRepository.findAll();
	}

	@Override
	public Muro encontrarMuro(Integer codigo) {
		return mRepository.findByCodigoMuro(codigo);
	}

	@Override
	public void crearMuros(Muro muro) {
		muro.setLocalizacion(new ArrayList<Double>(Arrays.asList(
				new BigDecimal(muro.getLocalizacion().get(0)).setScale(5, RoundingMode.HALF_UP).doubleValue(),
				new BigDecimal(muro.getLocalizacion().get(1)).setScale(5, RoundingMode.HALF_UP).doubleValue())));
		List<Integer> listaLabelMuro = cbFactory.create("busqueda").run(() -> pClient.getLabelMuros(),
				e -> obtenerListaLabelMuro(e));
		muro.setCodigoMuro(listaLabelMuro.get(listaLabelMuro.size() - 1));
		mRepository.save(muro);
	}

	@Override
	public Integer crearMuroProyectos(Integer idProyecto, List<Double> localizacion) {
		Muro muroNew = new Muro();
		if (mRepository.findAll().isEmpty()) {
			List<Integer> listaLabelMuro = cbFactory.create("busqueda").run(() -> pClient.getLabelMuros(),
					e -> obtenerListaLabelMuro(e));
			muroNew.setCodigoMuro(listaLabelMuro.get(listaLabelMuro.size() - 1));
			if (cbFactory.create("busqueda").run(() -> pClient.editarLabelMurosManejo(), e -> errorConexion(e))) {
				log.info("Creacion Correcta");
			}
			muroNew.setLocalizacion(localizacion);
			List<Integer> listaPrimera = new ArrayList<Integer>();
			listaPrimera.add(idProyecto);
			muroNew.setIdProyectos(listaPrimera);
			mRepository.save(muroNew);
		} else {
			Boolean bandera1 = false;
			for (int i = 0; i < mRepository.findAll().size(); i++) {
				Double distancia = distanciaCoord(mRepository.findAll().get(i).getLocalizacion(), localizacion);
				if (distancia <= 1 && !bandera1) {
					muroNew = mRepository.findByCodigoMuro(i + 1);
					List<Integer> listaProyectos = muroNew.getIdProyectos();
					listaProyectos.add(idProyecto);
					muroNew.setIdProyectos(listaProyectos);
					if (listaProyectos.size() <= 4) {
						List<Double> listaNuevaLocalizacion = distanciaMedia(muroNew.getLocalizacion(), localizacion);
						muroNew.setLocalizacion(listaNuevaLocalizacion);
					}
					mRepository.save(muroNew);
					bandera1 = true;
				}
			}
			if (!bandera1) {
				List<Integer> lista = new ArrayList<Integer>();
				List<Integer> listaLabelMuro = cbFactory.create("busqueda").run(() -> pClient.getLabelMuros(),
						e -> obtenerListaLabelMuro(e));
				muroNew.setCodigoMuro(listaLabelMuro.get(listaLabelMuro.size() - 1));
				if (cbFactory.create("busqueda").run(() -> pClient.editarLabelMurosManejo(), e -> errorConexion(e))) {
					log.info("Creacion Correcta");
				}
				muroNew.setLocalizacion(localizacion);
				lista.add(idProyecto);
				muroNew.setIdProyectos(lista);
				mRepository.save(muroNew);
			}
		}
		return muroNew.getCodigoMuro();
	}

	@Override
	public void eliminarProyecto(Integer codigo, Integer idProyecto) {
		Muro muro = mRepository.findByCodigoMuro(codigo);
		List<Integer> listaProyectos = muro.getIdProyectos();
		listaProyectos.remove(idProyecto);
		if (listaProyectos.isEmpty())
			eliminarMuro(codigo);
		else {
			muro.setIdProyectos(listaProyectos);
			mRepository.save(muro);
		}
	}

	private void eliminarMuro(@PathVariable Integer codigo) {
		Muro muro = mRepository.findByCodigoMuro(codigo);
		String id = muro.getId();
		mRepository.deleteById(id);
		List<Muro> murosList = mRepository.findAll();
		List<Integer> busquedaLista = new ArrayList<Integer>();
		if (cbFactory.create("busqueda").run(() -> pClient.editarLabelMuroManejoDelete(murosList.size()),
				e -> errorConexion(e))) {
			log.info("Creacion Correcta");
		}
		List<Integer> listaLabel = pClient.getLabelMuros();
		for (int i = 0; i < murosList.size(); i++) {
			murosList.get(i).setCodigoMuro(listaLabel.get(i));
			mRepository.save(murosList.get(i));
			busquedaLista.add(listaLabel.get(i));
		}
	}

//  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //

	private List<Integer> obtenerListaLabelMuro(Throwable e) {
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio de parametrizacion no esta disponible");
	}

	private Boolean errorConexion(Throwable e) {
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio de parametrizacion no esta disponible");
	}
}
