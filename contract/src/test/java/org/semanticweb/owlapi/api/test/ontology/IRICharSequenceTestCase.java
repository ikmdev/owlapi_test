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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.api.test.baseclasses.TestBase;
import org.semanticweb.owlapi.model.IRI;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group
 * @since 3.3.0
 */
class IRICharSequenceTestCase extends TestBase {

    static final String abcString = "http://owlapi.sourceforge.net#ABC";
    static final IRI abc = iri("http://owlapi.sourceforge.net#", "ABC");

    @Test
    void testCharAt() {
        for (int v = 0; v < abcString.length(); v++) {
            assertEquals(abcString.charAt(v), abc.charAt(v));
        }
    }

    @Test
    void testCharAtNoRemainder() {
        String str = "http://owlapi.sourceforge.net";
        IRI iri = iri(str, "");
        for (int v = 0; v < str.length(); v++) {
            assertEquals(str.charAt(v), iri.charAt(v));
        }
    }

    @Test
    void testCharAtNoPrefix() {
        String str = "#ABC";
        IRI iri = iri("#", "ABC");
        for (int v = 0; v < str.length(); v++) {
            assertEquals(str.charAt(v), iri.charAt(v));
        }
    }

    @Test
    void testSubSequence() {
        for (int v = 0; v < abcString.length(); v++) {
            for (int j = v; j < abcString.length(); j++) {
                assertEquals(abcString.subSequence(v, j), abc.subSequence(v, j));
            }
        }
    }

    @Test
    void testLength() {
        assertEquals(33, abc.length());
    }

    @Test
    void testLengthNoRemainder() {
        assertEquals(29, iri("http://owlapi.sourceforge.net", "").length());
    }

    @Test
    void testLengthNoPrefix() {
        assertEquals(4, iri("#", "ABC").length());
    }
}
