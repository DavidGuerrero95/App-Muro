package com.app.muro.models;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "muro")
@Data
@NoArgsConstructor
public class Muro {

	@Id
	@JsonIgnore
	private String id;

	@Indexed(unique = true)
	private Integer codigoMuro;

	@NotBlank(message = "ubicacion cannot be null")
	private List<Double> localizacion;

	private List<Integer> idProyectos;

}