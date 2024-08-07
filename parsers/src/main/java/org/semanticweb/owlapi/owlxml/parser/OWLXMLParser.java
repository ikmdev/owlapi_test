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
package org.semanticweb.owlapi.owlxml.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormatFactory;
import org.semanticweb.owlapi.io.AbstractOWLParser;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormatFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.util.SAXParsers;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health Informatics Group
 * @since 2.0.0
 */
public class OWLXMLParser extends AbstractOWLParser {

    private static final long serialVersionUID = 40000L;

    @Nonnull
    @Override
    public String getName() {
        return "OWLXMLParser";
    }

    @Override
    public OWLDocumentFormatFactory getSupportedFormat() {
        return new OWLXMLDocumentFormatFactory();
    }

    @Override
    public OWLDocumentFormat parse(OWLOntologyDocumentSource documentSource, OWLOntology ontology,
        OWLOntologyLoaderConfiguration configuration) throws IOException {
        InputSource isrc = null;
        try {
            isrc = getInputSource(documentSource, configuration);
            OWLXMLParserHandler handler = new OWLXMLParserHandler(ontology, configuration);
            SAXParsers.initParserWithOWLAPIStandards(null, configuration.getEntityExpansionLimit())
                .parse(isrc, handler);
            if (!handler.atLeastOneTagFound()) {
                throw new OWLXMLParserException(handler,
                    "No known tags in the input: is the file an OWL/XML ontology?");
            }
            OWLXMLDocumentFormat format = new OWLXMLDocumentFormat();
            format.copyPrefixesFrom(handler.getPrefixName2PrefixMap());
            String base = handler.getBase().toString();
            // do not override existing default prefix
            if (base != null && format.getDefaultPrefix() == null) {
                format.setDefaultPrefix(XMLUtils.iriWithTerminatingHash(base));
            }
            return format;
        } catch (SAXException e) {
            // General exception
            throw new OWLParserException(e);
        } finally {
            if (isrc != null) {
                try (InputStream in = isrc.getByteStream(); Reader r = isrc.getCharacterStream()) {
                }
            }
        }
    }
}
