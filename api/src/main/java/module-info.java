open module org.semanticweb.owlapi {

	requires com.github.benmanes.caffeine;
	requires java.desktop;
	requires javax.inject;
	requires com.google.common;
	requires java.xml;
	requires javax.annotation;
	requires org.slf4j;
	requires java.base;

	exports org.semanticweb.owlapi;
	exports org.semanticweb.owlapi.annotations;
	exports org.semanticweb.owlapi.change;
	exports org.semanticweb.owlapi.expression;
	exports org.semanticweb.owlapi.formats;
	exports org.semanticweb.owlapi.io;
	exports org.semanticweb.owlapi.model;
	exports org.semanticweb.owlapi.model.parameters;
	exports org.semanticweb.owlapi.profiles;
	exports org.semanticweb.owlapi.profiles.violations;
	exports org.semanticweb.owlapi.reasoner;
	exports org.semanticweb.owlapi.reasoner.knowledgeexploration;
	exports org.semanticweb.owlapi.search;
	exports org.semanticweb.owlapi.util;
	exports org.semanticweb.owlapi.util.mansyntax;
	exports org.semanticweb.owlapi.utilities;
	exports org.semanticweb.owlapi.vocab;

}