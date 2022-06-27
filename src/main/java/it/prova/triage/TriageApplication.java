package it.prova.triage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.prova.triage.model.Dottore;
import it.prova.triage.model.Paziente;
import it.prova.triage.service.DottoreService;
import it.prova.triage.service.PazienteService;

@SpringBootApplication
public class TriageApplication implements CommandLineRunner{
	@Autowired
	private PazienteService pazienteService;
	@Autowired
	private DottoreService dottoreService;

	public static void main(String[] args) {
		SpringApplication.run(TriageApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (pazienteService.findByNomeECognome("Camilla", "Ferri") == null) {
			pazienteService.inserisciNuovo(new Paziente("Camilla", "Ferri", "XXXYYY"));
		}
		
		if (dottoreService.findByNomeECognome("Gaetano", "Di Pasquale") == null) {
			dottoreService.inserisciNuovo(new Dottore("Gaetano", "Di Pasquale"));
		}
	}

}
