package it.prova.triage.dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;

import it.prova.triage.model.Paziente;
import it.prova.triage.model.StatoPaziente;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PazienteDTO {
	private Long id;
	private String nome;
	private String cognome;
	private Date dataRegistrazione;
	private String codiceFiscale;
	private StatoPaziente stato;
	private DottoreDTO dottore;

	public PazienteDTO(Long id, String nome, String cognome, Date dataRegistrazione, String codiceFiscale,
			StatoPaziente stato) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.dataRegistrazione = dataRegistrazione;
		this.codiceFiscale = codiceFiscale;
		this.stato = stato;
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

	public Date getDataRegistrazione() {
		return dataRegistrazione;
	}

	public void setDataRegistrazione(Date dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public StatoPaziente getStato() {
		return stato;
	}

	public void setStato(StatoPaziente stato) {
		this.stato = stato;
	}

	public DottoreDTO getDottore() {
		return dottore;
	}

	public void setDottore(DottoreDTO dottore) {
		this.dottore = dottore;
	}

	public static List<PazienteDTO> createPazienteDTOListFromModelList(List<Paziente> listAllElements) {
		return listAllElements.stream().map(paziente -> PazienteDTO.buildPazienteDTOFromModel(paziente))
				.collect(Collectors.toList());
	}

	public static PazienteDTO buildPazienteDTOFromModel(Paziente paziente) {
		PazienteDTO result = new PazienteDTO(paziente.getId(), paziente.getNome(), paziente.getCognome(),
				paziente.getDataRegistrazione(), paziente.getCodiceFiscale(), paziente.getStato());
		
		if (paziente.getDottore() != null) {
			result.setDottore(DottoreDTO.buildDottoreDTOFromModel(paziente.getDottore()));
		}
		return result;
	}

	public Paziente buildPazienteModel() {
		return new Paziente(nome, cognome, codiceFiscale);
	}
}
