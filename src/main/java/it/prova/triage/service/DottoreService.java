package it.prova.triage.service;

import java.util.List;

import it.prova.triage.model.Dottore;

public interface DottoreService {

	Dottore findByNomeECognome(String string, String string2);

	Dottore inserisciNuovo(Dottore dottore);

	List<Dottore> listAllElements();

	Dottore caricaSingoloElemento(long id);

	Dottore aggiorna(Dottore buildDottoreModel);

	void rimuovi(Dottore dottore);

}
