package org.semanticweb.owlapi.atomicdecomposition;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.api.test.baseclasses.TestBase;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

class ModuleAnnotationsTestCase extends TestBase {

    private final OWLAnnotationProperty a = df.getOWLAnnotationProperty(IRI.create("urn:test:a"));
    private final OWLAnnotationProperty b = df.getOWLAnnotationProperty(IRI.create("urn:test:b"));
    private final OWLAnnotationProperty replaced =
        df.getOWLAnnotationProperty(iri("urn:test:", "term_replaced_by"));
    OWLDeclarationAxiom dA = df.getOWLDeclarationAxiom(a);
    OWLDeclarationAxiom dR = df.getOWLDeclarationAxiom(replaced);
    OWLDeclarationAxiom dB = df.getOWLDeclarationAxiom(b);
    OWLAnnotationAssertionAxiom ax =
        df.getOWLAnnotationAssertionAxiom(replaced, a.getIRI(), b.getIRI());
    Set<OWLEntity> entities = new HashSet<>(Arrays.asList(a));

    @Test
    void shouldNotAddAnnotations() {
        Set<OWLAxiom> expected = new HashSet<>();
        IRI iri = IRI.create("urn:test:noanns");
        OWLOntology o = create(iri);
        Set<OWLAxiom> axioms = new HashSet<>(Arrays.asList(dR, dA, dB, ax));
        o.getOWLOntologyManager().addAxioms(o, axioms);
        Set<OWLAxiom> module = new SyntacticLocalityModuleExtractor(m,
            m.getOntologyLoaderConfiguration().withSkipModuleAnnotations(true), iri, axioms,
            uk.ac.manchester.cs.owlapi.modularity.ModuleType.STAR, false).extract(entities);
        assertEquals(expected, module);
    }

    @Test
    void shouldAddAnnotations() {
        Set<OWLAxiom> expected = new HashSet<>();
        expected.add(ax);
        expected.add(dA);
        IRI iri = IRI.create("urn:test:anns");
        OWLOntology o = create(iri);
        Set<OWLAxiom> axioms = new HashSet<>(Arrays.asList(dR, dA, dB, ax));
        o.getOWLOntologyManager().addAxioms(o, axioms);
        Set<OWLAxiom> module = new SyntacticLocalityModuleExtractor(m, iri, axioms,
            uk.ac.manchester.cs.owlapi.modularity.ModuleType.STAR, false).extract(entities);
        assertEquals(expected, module);
    }
}
