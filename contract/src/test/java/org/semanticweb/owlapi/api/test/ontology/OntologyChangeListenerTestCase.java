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
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubClassOf;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.api.test.baseclasses.TestBase;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 * @author Matthew Horridge, The University of Manchester, Bio-Health Informatics Group
 * @since 3.1.0
 */
class OntologyChangeListenerTestCase extends TestBase {

    @Test
    void testOntologyChangeListener() {
        OWLOntology ont = create("ont");
        OWLSubClassOfAxiom ax = SubClassOf(A, B);
        final Set<OWLAxiom> impendingAdditions = new HashSet<>();
        final Set<OWLAxiom> impendingRemovals = new HashSet<>();
        final Set<OWLAxiom> additions = new HashSet<>();
        final Set<OWLAxiom> removals = new HashSet<>();
        ont.getOWLOntologyManager().addImpendingOntologyChangeListener(impendingChanges -> {
            for (OWLOntologyChange change : impendingChanges) {
                if (change.isAddAxiom()) {
                    impendingAdditions.add(change.getAxiom());
                } else if (change.isRemoveAxiom()) {
                    impendingRemovals.add(change.getAxiom());
                }
            }
        });
        ont.getOWLOntologyManager().addOntologyChangeListener(changes -> {
            for (OWLOntologyChange change : changes) {
                if (change.isAddAxiom()) {
                    additions.add(change.getAxiom());
                } else if (change.isRemoveAxiom()) {
                    removals.add(change.getAxiom());
                }
            }
        });
        ont.getOWLOntologyManager().addAxiom(ont, ax);
        assertTrue(additions.contains(ax));
        assertTrue(impendingAdditions.contains(ax));
        ont.getOWLOntologyManager().removeAxiom(ont, ax);
        assertTrue(removals.contains(ax));
        assertTrue(impendingRemovals.contains(ax));
        // test that no op changes are not broadcasted
        removals.clear();
        ont.getOWLOntologyManager().removeAxiom(ont, ax);
        assertFalse(removals.contains(ax));
        assertTrue(impendingRemovals.contains(ax));
    }
}
