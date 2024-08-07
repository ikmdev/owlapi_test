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
package org.semanticweb.owlapi.util.mansyntax;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.OWLOntologyChecker;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.model.HasOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.HasOntologyLoaderConfigurationProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.util.OntologyAxiomPair;

/**
 * Interface for a parser able to parse Manchester OWL Syntax. This covers Protege use of the
 * parser.
 * 
 * @author ignazio
 */
public interface ManchesterOWLSyntaxParser
    extends HasOntologyLoaderConfigurationProvider, HasOntologyLoaderConfiguration {

    /**
     * @param s String to parse
     */
    void setStringToParse(@Nonnull String s);

    /**
     * @param defaultOntology ontology to use to resolve classes and entities during parsing
     */
    void setDefaultOntology(@Nonnull OWLOntology defaultOntology);

    /**
     * @return frames
     */
    @Nonnull
    Set<OntologyAxiomPair> parseFrames();

    /**
     * Parsing "Inline" Axioms.
     * 
     * @return axiom
     * @throws OWLParserException parsing error
     */
    @Nonnull
    OWLAxiom parseAxiom();

    /**
     * Parsing "Inline" class Axioms.
     * 
     * @return axiom
     * @throws OWLParserException parsing error
     * @deprecated use {@link #parseAxiom()}. {@link #parseClassAxiom()} only casts the result of
     *             {@link #parseAxiom()} to {@code OWLClassAxiom}.
     */
    @Deprecated
    @Nonnull
    OWLClassAxiom parseClassAxiom();

    /**
     * Parses an OWL class expression that is represented in Manchester OWL Syntax.
     * 
     * @return The parsed class expression
     * @throws OWLParserException If a class expression could not be parsed.
     */
    @Nonnull
    OWLClassExpression parseClassExpression();

    /**
     * @return class frames (parsing with EOF true)
     * @throws OWLParserException parsing error
     */
    @Nonnull
    Set<OntologyAxiomPair> parseClassFrameEOF();

    /**
     * @return value partition frames
     * @throws OWLParserException parsing error
     */
    @Nonnull
    Set<OntologyAxiomPair> parseValuePartitionFrame();

    /**
     * @return datatype frames
     * @throws OWLParserException parsing error
     */
    @Nonnull
    Set<OntologyAxiomPair> parseDatatypeFrame();

    /**
     * @return class frames
     * @throws OWLParserException parsing error
     */
    @Nonnull
    Set<OntologyAxiomPair> parseClassFrame();

    /**
     * @return object property frames
     * @throws OWLParserException parsing error
     */
    @Nonnull
    Set<OntologyAxiomPair> parseObjectPropertyFrame();

    /**
     * @return individual frames
     * @throws OWLParserException parsing error
     */
    @Nonnull
    Set<OntologyAxiomPair> parseIndividualFrame();

    /**
     * @return data property frames
     * @throws OWLParserException parsing error
     */
    @Nonnull
    Set<OntologyAxiomPair> parseDataPropertyFrame();

    /**
     * @return annotation frames
     * @throws OWLParserException parsing error
     */
    @Nonnull
    Set<OntologyAxiomPair> parseAnnotationPropertyFrame();

    /**
     * @param datatype datatype to use, if one exists in the context. If null, the datatype will be
     *                 decided by the literal itself.
     * @return parsed literal
     */
    @Nonnull
    OWLLiteral parseLiteral(@Nullable OWLDatatype datatype);

    /**
     * @param owlEntityChecker entity checker
     */
    void setOWLEntityChecker(@Nonnull OWLEntityChecker owlEntityChecker);

    /**
     * @param owlOntologyChecker ontology checker
     */
    void setOWLOntologyChecker(@Nonnull OWLOntologyChecker owlOntologyChecker);

    /**
     * @return object property chain
     */
    @Nonnull
    List<OWLObjectPropertyExpression> parseObjectPropertyChain();

    /**
     * @param ont ontology
     * @return format
     * @throws OWLParserException        parsing error
     * @throws UnloadableImportException import error
     */
    @Nonnull
    ManchesterSyntaxDocumentFormat parseOntology(@Nonnull OWLOntology ont);

    /**
     * @return list of class expressions
     */
    @Nonnull
    Set<OWLClassExpression> parseClassExpressionList();

    /**
     * @return list of object properties
     * @throws OWLParserException if a parser exception is raised
     */
    @Nonnull
    Set<OWLObjectPropertyExpression> parseObjectPropertyList();

    /**
     * @return list of object properties
     * @throws OWLParserException if a parser exception is raised
     */
    @Nonnull
    Set<OWLDataProperty> parseDataPropertyList();

    /**
     * @return parsed list of individuals
     */
    @Nonnull
    Set<OWLIndividual> parseIndividualList();

    /**
     * @return parsed list of data ranges
     */
    @Nonnull
    Set<OWLDataRange> parseDataRangeList();

    /**
     * @return parsed list of annotation properties
     */
    @Nonnull
    Set<OWLAnnotationProperty> parseAnnotationPropertyList();

    /**
     * @return data range
     */
    @Nonnull
    OWLDataRange parseDataRange();

    /**
     * @return property list (object or data)
     */
    @Nonnull
    Set<OWLPropertyExpression> parsePropertyList();

    /**
     * @return list of rule frames
     */
    @Nonnull
    List<OntologyAxiomPair> parseRuleFrame();

    /**
     * @return IRI for a SWRL variable
     * @throws OWLParserException if a parser exception is raised
     */
    @Nonnull
    IRI parseVariable();

    /**
     * Convenience method equivalent to {@code setStringToParse("string"}; parseClassexpression();}
     * 
     * @param s String to parse
     * @return parsed class expression
     */
    @Nonnull
    OWLClassExpression parseClassExpression(String s);
}
