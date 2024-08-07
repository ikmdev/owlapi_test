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
package org.semanticweb.owlapi.model;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group Date:
 *         18/02/2014
 */
class SKOSVocabularyTestCase {
    static Collection<Object[]> getData() {
        List<Object[]> data = new ArrayList<>();
        for (SKOSVocabulary v : SKOSVocabulary.values()) {
            data.add(new Object[] {v});
        }
        return data;
    }

    @ParameterizedTest
    @MethodSource("getData")
    void getPrefixedNameShouldStartWithSkosPrefixName(SKOSVocabulary vocabulary) {
        assertThat(vocabulary.getPrefixedName(), startsWith(Namespaces.SKOS.getPrefixName()));
        assertThat(vocabulary.getIRI().toString(), startsWith(Namespaces.SKOS.getPrefixIRI()));
    }
}
