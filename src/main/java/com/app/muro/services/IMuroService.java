package com.app.muro.services;

import java.util.List;

import com.app.muro.models.Muro;

public interface IMuroService {

	public List<Muro> encontrarMuros();

	public Muro encontrarMuro(Integer codigo);

	public void crearMuros(Muro muro);

	public Integer crearMuroProyectos(Integer idProyecto, List<Double> localizacion);

	public void eliminarProyecto(Integer codigo, Integer idProyecto);
}
