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
package org.semanticweb.owlapi.rdf.model;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.io.RDFNode;
import org.semanticweb.owlapi.io.RDFResource;
import org.semanticweb.owlapi.io.RDFResourceBlankNode;
import org.semanticweb.owlapi.io.RDFResourceIRI;
import org.semanticweb.owlapi.io.RDFTriple;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.google.common.collect.Sets;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health Informatics Group
 * @since 2.0.0
 */
public class RDFGraph implements Serializable {

    private static final Set<IRI> skippedPredicates =
        Sets.newHashSet(OWLRDFVocabulary.OWL_ANNOTATED_TARGET.getIRI());
    private static final long serialVersionUID = 40000L;
    @Nonnull
    private final Map<RDFResource, Set<RDFTriple>> triplesBySubject = new HashMap<>();
    @Nonnull
    private final Set<RDFResourceBlankNode> rootAnonymousNodes = new HashSet<>();
    @Nonnull
    private final Set<RDFTriple> triples = new HashSet<>();
    @Nonnull
    private final Map<RDFNode, RDFNode> remappedNodes = new HashMap<>();

    /**
     * Determines if this graph is empty (i.e. whether or not it contains any triples).
     * 
     * @return {@code true} if the graph contains triples, otherwise {@code false}
     * @since 3.5
     */
    public boolean isEmpty() {
        return triples.isEmpty();
    }

    /**
     * @param triple triple to add
     */
    public void addTriple(@Nonnull RDFTriple triple) {
        checkNotNull(triple, "triple cannot be null");
        // Reset the computation of root anon nodes
        rootAnonymousNodes.clear();
        triples.add(triple);
        Set<RDFTriple> tripleSet = triplesBySubject.get(triple.getSubject());
        if (tripleSet == null) {
            tripleSet = new LinkedHashSet<>();
            triplesBySubject.put(triple.getSubject(), tripleSet);
        }
        tripleSet.add(triple);
    }

    /**
     * @param subject subject
     * @return sorted triples
     */
    public Collection<RDFTriple> getTriplesForSubject(RDFNode subject) {
        Set<RDFTriple> set = triplesBySubject.get(subject);
        if (set == null) {
            // check if the node is remapped
            RDFNode rdfNode = remappedNodes.get(subject);
            if (rdfNode == null) {
                return Collections.emptyList();
            }
            // else return the triples for the remapped node
            return getTriplesForSubject(rdfNode);
        }
        return set;
    }

    /**
     * @return for each triple with a blank node object that is shared with other triples, compute a
     *         remapping of the node.
     */
    public Map<RDFTriple, RDFResourceBlankNode> computeRemappingForSharedNodes() {
        Map<RDFTriple, RDFResourceBlankNode> toReturn = new HashMap<>();
        Map<RDFNode, List<RDFTriple>> sharers = new HashMap<>();
        for (RDFTriple t : triples) {
            if (t.getObject().isAnonymous() && !t.getObject().isAxiom()
                && !t.getObject().isIndividual() && notInSkippedPredicates(t.getPredicate())) {
                List<RDFTriple> list = sharers.get(t.getObject());
                if (list == null) {
                    list = new ArrayList<>(2);
                    sharers.put(t.getObject(), list);
                }
                list.add(t);
            }
        }
        for (Map.Entry<RDFNode, List<RDFTriple>> e : sharers.entrySet()) {
            if (e.getValue().size() > 1) {
                // found reused blank nodes
                for (RDFTriple t : e.getValue()) {
                    RDFResourceBlankNode bnode =
                        new RDFResourceBlankNode(IRI.create(NodeID.nextAnonymousIRI()),
                            e.getKey().isIndividual(), e.getKey().shouldOutputId(), false);
                    remappedNodes.put(bnode, e.getKey());
                    toReturn.put(t, bnode);
                }
            }
        }
        forceIdOutputForIndividualsInMultipleTriples();
        return toReturn;
    }

    protected void forceIdOutputForIndividualsInMultipleTriples() {
        // Some individuals might need to appear in mutliple triples although they do not appear in
        // multiple positions in the axioms.
        // An example of such a situation is an anonymous individual as object of an annotated
        // annotation - in the RDF graph, this individual will appear in two places because of
        // reification.
        Map<RDFResourceBlankNode, List<RDFResourceBlankNode>> anonIndividualsInMultipleTriples =
            new HashMap<>();
        for (RDFTriple t : triples) {
            if (t.getObject().isAnonymous() && t.getObject().isIndividual()) {
                List<RDFResourceBlankNode> list =
                    anonIndividualsInMultipleTriples.get(t.getObject());
                if (list == null) {
                    list = new ArrayList<>(2);
                    anonIndividualsInMultipleTriples.put((RDFResourceBlankNode) t.getObject(),
                        list);
                }
                list.add((RDFResourceBlankNode) t.getObject());
            }
        }
        for (Map.Entry<RDFResourceBlankNode, List<RDFResourceBlankNode>> e : anonIndividualsInMultipleTriples
            .entrySet()) {
            if (e.getValue().size() > 1) {
                // individuals that need their id outputted
                e.getValue().forEach(o -> o.setIdRequiredForIndividual(true));
            }
        }
    }

    /**
     * @param predicate predicate to check for inclusion
     * @return true if the predicate IRI is not in the set of predicates that should be skipped from
     *         blank node reuse analysis.
     */
    private static boolean notInSkippedPredicates(RDFResourceIRI predicate) {
        return !skippedPredicates.contains(predicate.getIRI());
    }

    /**
     * @return root anonymous nodes
     */
    @Nonnull
    public Set<RDFResourceBlankNode> getRootAnonymousNodes() {
        if (rootAnonymousNodes.isEmpty()) {
            rebuildAnonRoots();
        }
        return rootAnonymousNodes;
    }

    private void rebuildAnonRoots() {
        rootAnonymousNodes.clear();
        for (RDFTriple triple : triples) {
            if (triple.getSubject().isAnonymous()) {
                rootAnonymousNodes.add((RDFResourceBlankNode) triple.getSubject());
            }
        }
        for (RDFTriple triple : triples) {
            if (triple.getObject().isAnonymous() && !triple.isSubjectSameAsObject()) {
                rootAnonymousNodes.remove(triple.getObject());
            }
        }
    }

    /**
     * @param w writer to write to
     * @throws IOException if exceptions happen
     */
    public void dumpTriples(@Nonnull Writer w) throws IOException {
        checkNotNull(w, "w cannot be null");
        for (Set<RDFTriple> set : triplesBySubject.values()) {
            for (RDFTriple triple : set) {
                w.write(triple.toString());
                w.write("\n");
            }
        }
        w.flush();
    }

    /**
     * @return all triples in an unmodifiable set
     */
    public Set<RDFTriple> getAllTriples() {
        return Collections.unmodifiableSet(triples);
    }

    @Override
    public String toString() {
        return triples.stream().map(Object::toString)
            .collect(Collectors.joining(",\n                     ", "triples            : ", "\n"))
            + triplesBySubject.entrySet().stream().map(Object::toString).collect(
                Collectors.joining(",\n                     ", "triplesBySubject   : ", "\n"))
            + rootAnonymousNodes.stream().map(Object::toString).collect(
                Collectors.joining(",\n                     ", "rootAnonymousNodes : ", "\n"))
            + remappedNodes.entrySet().stream().map(Object::toString).collect(
                Collectors.joining(",\n                     ", "remappedNodes      : ", ""));
    }

    /**
     * @param node node
     * @return subjects for resource
     */
    public List<RDFResource> getSubjectsForObject(RDFResource node) {
        List<RDFResource> current = triples.stream().filter(p -> p.getObject().equals(node))
            .map(RDFTriple::getSubject).collect(Collectors.toList());
        Set<RDFResource> visited = new HashSet<>();
        List<RDFResource> next = new ArrayList<>();
        boolean change = true;
        while (change) {
            change = false;
            for (RDFResource n : current) {
                if (visited.add(n)) {
                    List<RDFResource> l = triples.stream().filter(p -> p.getObject().equals(n))
                        .map(RDFTriple::getSubject).collect(Collectors.toList());
                    if (l.size() > 0) {
                        change = true;
                        next.addAll(l);
                    } else {
                        next.add(n);
                    }
                }
            }
            current = next;
            next = new ArrayList<>();
        }
        return current;
    }
}
