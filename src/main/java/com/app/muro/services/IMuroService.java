package com.app.muro.services;

import java.util.List;

public interface IMuroService {

	public Double distanciaCoord(List<Double> pos1, List<Double> pos2);

	public List<Double> distanciaMedia(List<Double> pos1, List<Double> pos2);
}
