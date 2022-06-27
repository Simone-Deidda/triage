package it.prova.triage.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.triage.model.Dottore;
import it.prova.triage.model.Paziente;
import it.prova.triage.model.StatoPaziente;
import it.prova.triage.repository.PazienteRepository;

@Service
public class PazienteServiceImpl implements PazienteService {
	@Autowired
	private PazienteRepository pazienteRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Paziente> listAllElements() {
		return (List<Paziente>) pazienteRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Paziente findByNomeECognome(String string, String string2) {
		return pazienteRepository.findByNomeAndCognome(string, string2).orElse(null);
	}

	@Override
	@Transactional
	public Paziente inserisciNuovo(Paziente paziente) {
		paziente.setDataRegistrazione(new Date());
		paziente.setStato(StatoPaziente.IN_ATTESA_VISITA);
		return pazienteRepository.save(paziente);
	}

	@Override
	@Transactional(readOnly = true)
	public Paziente caricaSingoloElemento(long id) {
		return pazienteRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Paziente aggiorna(Paziente buildPazienteModel) {
		return pazienteRepository.save(buildPazienteModel);
	}

	@Override
	@Transactional
	public void rimuovi(Paziente paziente) {
		pazienteRepository.delete(paziente);
	}

	@Override
	@Transactional
	public void assegnaDottore(Dottore dottore, Paziente paziente) {
		dottore.setPazienteAttualmenteInVisita(paziente);
		paziente.setDottore(dottore);
	}

}
