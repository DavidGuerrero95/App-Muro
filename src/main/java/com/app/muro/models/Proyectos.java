package com.app.muro.models;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "proyectos")
public class Proyectos {

	@Id
	private String id;

	@NotBlank(message = "Name cannot be null")
	@Size(max = 50)
	@Indexed(unique = true)
	private String nombre;

	@Indexed(unique = true)
	private Integer codigoProyecto;

	@NotBlank(message = "palabras clave cannot be null")
	private List<String> palabrasClave;

	@NotBlank(message = "ubicacion cannot be null")
	private List<Double> localizacion;

	@NotBlank(message = "resumen cannot be null")
	private String resumen;

	@NotBlank(message = "objetivos cannot be null")
	private String objetivos;

	@NotBlank(message = "descripcion cannot be null")
	private String descripcion;

	@NotBlank(message = "principales itos cannot be null")
	private String principalItos;

	@NotBlank(message = "presupuesto itos cannot be null")
	private Long presupuesto;

	private List<String> cronograma;

	@NotBlank(message = "enabled cannot be null")
	private Boolean enabled;

	private Integer estadoProyecto;

	private List<Integer> proyectoDesarrollo;

	private Integer muro;

	private String creador;

	private LocalDate fecha;

	public Proyectos() {
	}

	public Proyectos(String nombre, Integer codigoProyecto, List<String> palabrasClave, List<Double> localizacion,
			String resumen, String objetivos, String descripcion, String principalItos, Long presupuesto,
			List<String> cronograma, Boolean enabled, Integer estadoProyecto, List<Integer> proyectoDesarrollo,
			Integer muro, String creador, LocalDate fecha) {
		super();
		this.nombre = nombre;
		this.codigoProyecto = codigoProyecto;
		this.palabrasClave = palabrasClave;
		this.localizacion = localizacion;
		this.resumen = resumen;
		this.objetivos = objetivos;
		this.descripcion = descripcion;
		this.principalItos = principalItos;
		this.presupuesto = presupuesto;
		this.cronograma = cronograma;
		this.enabled = enabled;
		this.estadoProyecto = estadoProyecto;
		this.proyectoDesarrollo = proyectoDesarrollo;
		this.muro = muro;
		this.creador = creador;
		this.fecha = fecha;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getCodigoProyecto() {
		return codigoProyecto;
	}

	public void setCodigoProyecto(Integer codigoProyecto) {
		this.codigoProyecto = codigoProyecto;
	}

	public List<String> getPalabrasClave() {
		return palabrasClave;
	}

	public void setPalabrasClave(List<String> palabrasClave) {
		this.palabrasClave = palabrasClave;
	}

	public List<Double> getLocalizacion() {
		return localizacion;
	}

	public void setLocalizacion(List<Double> localizacion) {
		this.localizacion = localizacion;
	}

	public String getResumen() {
		return resumen;
	}

	public void setResumen(String resumen) {
		this.resumen = resumen;
	}

	public String getObjetivos() {
		return objetivos;
	}

	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getPrincipalItos() {
		return principalItos;
	}

	public void setPrincipalItos(String principalItos) {
		this.principalItos = principalItos;
	}

	public Long getPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(Long presupuesto) {
		this.presupuesto = presupuesto;
	}

	public List<String> getCronograma() {
		return cronograma;
	}

	public void setCronograma(List<String> cronograma) {
		this.cronograma = cronograma;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getEstadoProyecto() {
		return estadoProyecto;
	}

	public void setEstadoProyecto(Integer estadoProyecto) {
		this.estadoProyecto = estadoProyecto;
	}

	public List<Integer> getProyectoDesarrollo() {
		return proyectoDesarrollo;
	}

	public void setProyectoDesarrollo(List<Integer> proyectoDesarrollo) {
		this.proyectoDesarrollo = proyectoDesarrollo;
	}

	public Integer getMuro() {
		return muro;
	}

	public void setMuro(Integer muro) {
		this.muro = muro;
	}

	public String getCreador() {
		return creador;
	}

	public void setCreador(String creador) {
		this.creador = creador;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

}
