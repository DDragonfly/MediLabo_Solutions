package com.openclassrooms.notes.config;
import org.springframework.data.mongodb.MongoDatabaseFactory;


import com.openclassrooms.notes.entity.Note;
import com.openclassrooms.notes.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final NoteRepository noteRepository;
    private final MongoDatabaseFactory mongoDatabaseFactory;

    public DataInitializer(NoteRepository noteRepository, MongoDatabaseFactory mongoDatabaseFactory) {
        this.noteRepository = noteRepository;
        this.mongoDatabaseFactory = mongoDatabaseFactory;
    }

    @Override
    public void run(String... args) {
        System.out.println("MongoDB database in use: " + mongoDatabaseFactory.getMongoDatabase().getName());

        if (noteRepository.count() > 0) {
            return;
        }

        // ===== patId 1 - TestNone =====
        save(1, "TestNone", "Le patient déclare qu'il se sent très bien. " + "Poids égal ou inférieur au poids recommandé");

        // ===== patId 2 - TestBorderline =====
        save(2, "TestBorderline", "Le patient déclare qu'il ressent beaucoup de stress au travail. "
        + "Il se plaint également que son audition est anormale dernièrement");
        save(2, "TestBorderline", "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois. "
                + "Il remarque également que son audition continue d'être anormale");

        // ===== patId 3 - TestInDanger =====
        save(3, "TestInDanger", "Le patient déclare qu'il fume depuis peu");
        save(3, "TestInDanger", "Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière. "
                + "Il se plaint également de crises d’apnée respiratoire anormales. "
                + "Tests de laboratoire indiquant un taux de cholestérol LDL élevé");

        // ===== patId 4 - TestEarlyOnset =====
        save(4, "TestEarlyOnset", "Le patient déclare qu'il lui est devenu difficile de monter les escaliers. "
                + "Il se plaint également d’être essoufflé. "
                + "Tests de laboratoire indiquant que les anticorps sont élevés. "
                + "Réaction aux médicaments");
        save(4, "TestEarlyOnset", "Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps");
        save(4, "TestEarlyOnset", "Le patient déclare avoir commencé à fumer depuis peu. "
                + "Hémoglobine A1C supérieure au niveau recommandé");
        save(4, "TestEarlyOnset", "Taille, Poids, Cholestérol, Vertige et Réaction");

    }

    private void save(int patId, String patient, String noteText) {
        Note n = new Note();
        n.setPatId(patId);
        n.setPatient(patient);
        n.setNote(noteText);
        noteRepository.save(n);
    }
}
