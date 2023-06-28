open module org.semanticweb.owlapi.impl {

	requires com.github.benmanes.caffeine;
	requires com.google.common;
	requires javax.annotation;
	requires javax.inject;
	requires org.slf4j;

	requires transitive org.semanticweb.owlapi;

	exports org.semanticweb.owlapi.reasoner.impl;
	exports org.semanticweb.owlapi.reasoner.structural;
	exports uk.ac.manchester.cs.owl.owlapi;
	exports uk.ac.manchester.cs.owl.owlapi.concurrent;
	exports uk.ac.manchester.cs.owl.owlapi.util.collections;

	provides org.semanticweb.owlapi.model.OWLDataFactory with uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
//	provides org.semanticweb.owlapi.model.OWLOntologyFactory with uk.ac.manchester.cs.owl.owlapi.OWLOntologyFactoryImpl;
//	provides org.semanticweb.owlapi.model.OWLOntologyManager with uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;

}