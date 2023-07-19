open module org.semanticweb.owlapi.parsers {

	requires java.xml;

	requires javax.annotation;
	requires javax.inject;

	requires com.google.common;
	requires org.slf4j;

	requires transitive org.semanticweb.owlapi;

//	exports org.semanticweb.owlapi.functional;
	exports org.semanticweb.owlapi.functional.parser;
	exports org.semanticweb.owlapi.functional.renderer;

}