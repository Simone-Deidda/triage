package it.prova.triage.dto;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;

import it.prova.triage.model.Dottore;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DottoreDTO {
	private Long id;
	private String nome;
	private String cognome;

	private PazienteDTO pazienteAttualmenteInVisita;

	public DottoreDTO(Long id, String nome, String cognome) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public PazienteDTO getPazienteAttualmenteInVisita() {
		return pazienteAttualmenteInVisita;
	}

	public void setPazienteAttualmenteInVisita(PazienteDTO pazienteAttualmenteInVisita) {
		this.pazienteAttualmenteInVisita = pazienteAttualmenteInVisita;
	}

	public static List<DottoreDTO> createDottoreDTOListFromModelList(List<Dottore> listAllElements) {
		return listAllElements.stream().map(dottore -> DottoreDTO.buildDottoreDTOFromModel(dottore))
				.collect(Collectors.toList());
	}

	public static DottoreDTO buildDottoreDTOFromModel(Dottore dottore) {
		return new DottoreDTO(dottore.getId(), dottore.getNome(), dottore.getCognome());
	}

	public Dottore buildDottoreModel() {
		return new Dottore(id, nome, cognome);
	}

}
