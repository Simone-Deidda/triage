package it.prova.triage.service;

import java.util.List;

import it.prova.triage.model.Dottore;
import it.prova.triage.model.Paziente;

public interface PazienteService {

	List<Paziente> listAllElements();

	Paziente findByNomeECognome(String string, String string2);

	Paziente inserisciNuovo(Paziente paziente);

	Paziente caricaSingoloElemento(long id);

	Paziente aggiorna(Paziente buildPazienteModel);

	void rimuovi(Paziente paziente);

	void assegnaDottore(Dottore dottore, Paziente paziente);

}
