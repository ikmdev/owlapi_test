/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.api.test.ontology;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Annotation;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Declaration;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ImportsDeclaration;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Literal;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubClassOf;
import static org.semanticweb.owlapi.model.parameters.AxiomAnnotations.CONSIDER_AXIOM_ANNOTATIONS;
import static org.semanticweb.owlapi.model.parameters.AxiomAnnotations.IGNORE_AXIOM_ANNOTATIONS;
import static org.semanticweb.owlapi.model.parameters.Imports.EXCLUDED;
import static org.semanticweb.owlapi.model.parameters.Imports.INCLUDED;

import java.io.File;
import java.io.FileOutputStream;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.api.test.baseclasses.TestBase;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLRuntimeException;

/**
 * @author Matthew Horridge, The University of Manchester, Information Management Group
 * @since 3.0.0
 */
class OntologyContainsAxiomTestCase extends TestBase {

    @Nonnull
    private static RDFXMLDocumentFormat createRDFXMLFormat() {
        RDFXMLDocumentFormat format = new RDFXMLDocumentFormat();
        // This test case relies on certain declarations being in certain
        // ontologies. The default
        // behaviour is to add missing declarations. Therefore, this needs to be
        // turned off.
        format.setAddMissingTypes(false);
        return format;
    }

    @Nonnull
    private static TurtleDocumentFormat createTurtleOntologyFormat() {
        TurtleDocumentFormat format = new TurtleDocumentFormat();
        format.setAddMissingTypes(false);
        return format;
    }

    @Test
    void testOntologyContainsPlainAxiom() {
        OWLAxiom axiom = SubClassOf(A, B);
        OWLOntology ont = create("testont");
        ont.getOWLOntologyManager().addAxiom(ont, axiom);
        assertTrue(ont.containsAxiom(axiom));
        assertTrue(ont.containsAxiom(axiom, EXCLUDED, IGNORE_AXIOM_ANNOTATIONS));
    }

    @Test
    void testOntologyContainsAnnotatedAxiom() {
        OWLLiteral annoLiteral = Literal("value");
        OWLAnnotation anno = Annotation(AP, annoLiteral);
        OWLAxiom axiom = SubClassOf(A, B, singleton(anno));
        OWLOntology ont = create("testont");
        ont.getOWLOntologyManager().addAxiom(ont, axiom);
        assertTrue(ont.containsAxiom(axiom));
        assertTrue(ont.containsAxiom(axiom, EXCLUDED, IGNORE_AXIOM_ANNOTATIONS));
        assertFalse(ont.containsAxiom(axiom.getAxiomWithoutAnnotations()));
        assertTrue(ont.containsAxiom(axiom.getAxiomWithoutAnnotations(), EXCLUDED,
            IGNORE_AXIOM_ANNOTATIONS));
    }

    @Test
    void testOntologyContainsAxiomsForRDFXML1() {
        RDFXMLDocumentFormat format = createRDFXMLFormat();
        runTestOntologyContainsAxioms1(format);
    }

    @Test
    void testOntologyContainsAxiomsForOWLXML1() {
        runTestOntologyContainsAxioms1(new OWLXMLDocumentFormat());
    }

    @Test
    void testOntologyContainsAxiomsForOWLFunctionalSyntax1() {
        runTestOntologyContainsAxioms1(new FunctionalSyntaxDocumentFormat());
    }

    @Test
    void testOntologyContainsAxiomsForTurtleSyntax1() {
        TurtleDocumentFormat format = createTurtleOntologyFormat();
        runTestOntologyContainsAxioms1(format);
    }

    private void runTestOntologyContainsAxioms1(@Nonnull OWLDocumentFormat format) {
        OWLOntology ont1 = create("testont1A");
        @Nonnull
        IRI ont1iri = ont1.getOntologyID().getOntologyIRI().get();
        OWLOntology ont2 = create("testont2A");
        @Nonnull
        IRI ont2iri = ont2.getOntologyID().getOntologyIRI().get();
        OWLImportsDeclaration ont2import = ImportsDeclaration(ont1iri);
        ont1.getOWLOntologyManager().applyChange(new AddImport(ont2, ont2import));
        OWLAxiom axannoPropdecl = Declaration(AP);
        ont1.getOWLOntologyManager().addAxiom(ont1, axannoPropdecl);
        OWLAnnotation inont1anno = Annotation(AP, ont1iri);
        OWLAnnotation inont2anno = Annotation(AP, ont2iri);
        OWLAxiom axAdecl = Declaration(A, singleton(inont1anno));
        ont1.getOWLOntologyManager().addAxiom(ont1, axAdecl);
        OWLAxiom axBdecl = Declaration(B, singleton(inont2anno));
        ont2.getOWLOntologyManager().addAxiom(ont2, axBdecl);
        OWLAxiom axAsubB = SubClassOf(A, B, singleton(inont2anno));
        ont2.getOWLOntologyManager().addAxiom(ont2, axAsubB);
        // annoProp is in ont1 and in the import closure of ont2
        assertTrue(containsConsiderEx(ont1, axannoPropdecl));
        assertFalse(containsConsiderEx(ont2, axannoPropdecl));
        assertTrue(containsConsider(ont2, axannoPropdecl));
        // A is in ont1 and in the import closure of ont2
        assertTrue(containsConsiderEx(ont1, axAdecl));
        assertFalse(containsConsiderEx(ont2, axAdecl));
        assertTrue(containsConsider(ont2, axAdecl));
        // B is in only in ont2
        assertFalse(containsConsider(ont1, axBdecl));
        assertTrue(containsConsiderEx(ont2, axBdecl));
        assertTrue(containsConsider(ont2, axBdecl));
        // A is a subclass of B is in only in ont2
        assertFalse(containsConsider(ont1, axAsubB));
        assertTrue(containsConsiderEx(ont2, axAsubB));
        assertTrue(containsConsider(ont2, axAsubB));
        @Nonnull
        File savedLocation1 = save(format, ont1, "testont1A.owl");
        @Nonnull
        File savedLocation2 = save(format, ont2, "testont2A.owl");
        OWLOntology ont1L = loadOntologyFromFile(savedLocation1, m1);
        OWLOntology ont2L = loadOntologyFromFile(savedLocation2, m1);
        // annoProp is in ont1 and in the import closure of ont2
        assertTrue(containsConsiderEx(ont1L, axannoPropdecl));
        assertFalse(containsConsiderEx(ont2L, axannoPropdecl));
        assertTrue(containsConsider(ont2L, axannoPropdecl));
        // A is in ont1 and in the import closure of ont2
        assertTrue(containsConsiderEx(ont1L, axAdecl));
        assertFalse(containsConsiderEx(ont2L, axAdecl));
        assertTrue(containsConsider(ont2L, axAdecl));
        // B is in only in ont2
        assertFalse(containsConsider(ont1L, axBdecl));
        assertTrue(containsConsiderEx(ont2L, axBdecl));
        assertTrue(containsConsider(ont2L, axBdecl));
        // A is a subclass of B is in only in ont2
        assertFalse(containsConsider(ont1L, axAsubB));
        assertTrue(containsConsiderEx(ont2L, axAsubB));
        assertTrue(containsConsider(ont2L, axAsubB));
    }

    protected File save(OWLDocumentFormat format, OWLOntology ont1, String name) {
        try {
            File savedLocation1 = new File(folder, name);
            FileOutputStream out1 = new FileOutputStream(savedLocation1);
            StreamDocumentTarget writer1 = new StreamDocumentTarget(out1);
            ont1.getOWLOntologyManager().saveOntology(ont1, format, writer1);
            return savedLocation1;
        } catch (Exception ex) {
            throw new OWLRuntimeException(ex);
        }
    }

    boolean containsConsider(OWLOntology o, @Nonnull OWLAxiom ax) {
        return o.containsAxiom(ax, INCLUDED, CONSIDER_AXIOM_ANNOTATIONS);
    }

    boolean containsConsiderEx(OWLOntology o, @Nonnull OWLAxiom ax) {
        return o.containsAxiom(ax, EXCLUDED, CONSIDER_AXIOM_ANNOTATIONS);
    }

    @Test
    void testOntologyContainsAxiomsForRDFXML2() {
        runTestOntologyContainsAxioms2(createRDFXMLFormat());
    }

    @Test
    void testOntologyContainsAxiomsForOWLXML2() {
        runTestOntologyContainsAxioms2(new OWLXMLDocumentFormat());
    }

    @Test
    void testOntologyContainsAxiomsForOWLFunctionalSyntax2() {
        runTestOntologyContainsAxioms2(new FunctionalSyntaxDocumentFormat());
    }

    @Test
    void testOntologyContainsAxiomsForTurtleSyntax2() {
        runTestOntologyContainsAxioms2(createTurtleOntologyFormat());
    }

    private void runTestOntologyContainsAxioms2(@Nonnull OWLDocumentFormat format) {
        OWLOntology ont1 = create("testont1B");
        IRI ont1iri = ont1.getOntologyID().getOntologyIRI().get();
        OWLOntology ont2 = create("testont2B");
        IRI ont2iri = ont2.getOntologyID().getOntologyIRI().get();
        assert ont1iri != null;
        assert ont2iri != null;
        OWLImportsDeclaration ont2import = ImportsDeclaration(ont1iri);
        ont2.getOWLOntologyManager().applyChange(new AddImport(ont2, ont2import));
        OWLAxiom axAnnoPropDecl = Declaration(AP);
        ont1.getOWLOntologyManager().addAxiom(ont1, axAnnoPropDecl);
        OWLAnnotation inOnt1Anno = Annotation(AP, ont1iri);
        OWLAnnotation inOnt2Anno = Annotation(AP, ont2iri);
        OWLAxiom axADecl = Declaration(A, singleton(inOnt1Anno));
        ont1.getOWLOntologyManager().addAxiom(ont1, axADecl);
        OWLAxiom axBDecl = Declaration(B, singleton(inOnt2Anno));
        ont2.getOWLOntologyManager().addAxiom(ont2, axBDecl);
        OWLAxiom axAsubB = SubClassOf(A, B, singleton(inOnt2Anno));
        ont2.getOWLOntologyManager().addAxiom(ont2, axAsubB);
        // annoProp is in ont1 and in the import closure of ont2
        assertTrue(containsConsiderEx(ont1, axAnnoPropDecl));
        assertFalse(containsConsiderEx(ont2, axAnnoPropDecl));
        assertTrue(containsConsider(ont2, axAnnoPropDecl));
        // A is in ont1 and in the import closure of ont2
        assertTrue(containsConsiderEx(ont1, axADecl));
        assertFalse(containsConsiderEx(ont2, axADecl));
        assertTrue(containsConsider(ont2, axADecl));
        // B is in only in ont2
        assertFalse(containsConsider(ont1, axBDecl));
        assertTrue(containsConsiderEx(ont2, axBDecl));
        assertTrue(containsConsider(ont2, axBDecl));
        // A is a subclass of B is in only in ont2
        assertFalse(containsConsider(ont1, axAsubB));
        assertTrue(containsConsiderEx(ont2, axAsubB));
        assertTrue(containsConsider(ont2, axAsubB));
        @Nonnull
        File savedLocation1 = save(format, ont1, "testont1B.owl");
        @Nonnull
        File savedLocation2 = save(format, ont2, "testont2B.owl");
        loadOntologyFromFile(savedLocation1, m1);
        OWLOntology ont2L = loadOntologyFromFile(savedLocation2, m1);
        for (OWLOntology importedOntology : ont2L.getImports()) {
            for (OWLAxiom importedAxiom : importedOntology.getAxioms()) {
                assert importedAxiom != null;
                assertTrue(containsConsiderEx(importedOntology, importedAxiom));
                assertFalse(containsConsiderEx(ont2L, importedAxiom));
            }
        }
    }
}
