package org.hssc.fhir.rest.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import org.hl7.fhir.dstu3.model.Meta;
//import org.hssc.fhir.rest.provider.ConformanceProvider;
import org.hssc.fhir.rest.provider.*;
import org.hssc.fhir.rest.provider.RestfulPatientResourceProvider;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaConformanceProviderDstu1;
import ca.uhn.fhir.jpa.provider.JpaConformanceProviderDstu2;
import ca.uhn.fhir.jpa.provider.JpaSystemProviderDstu1;
import ca.uhn.fhir.jpa.provider.JpaSystemProviderDstu2;
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3;
import ca.uhn.fhir.jpa.provider.dstu3.JpaSystemProviderDstu3;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

public class JpaServerDemo extends RestfulServer {

	private static final long serialVersionUID = 1L;

	private WebApplicationContext myAppCtx;

	@SuppressWarnings("unchecked")
	@Override
	protected void initialize() throws ServletException {
		
		
		
		FhirVersionEnum fhirVersion = FhirVersionEnum.DSTU2;
		setFhirContext(new FhirContext(fhirVersion));
		
		List<IResourceProvider> providers = new ArrayList<IResourceProvider>();
		providers.add(new RestfulPatientResourceProvider());
		//providers.add(new OrganizationResourceProvider());
		setResourceProviders(providers);
		ConformanceProvider confProvider = new ConformanceProvider(this);
		setServerConformanceProvider(confProvider);
		
//		BasicSecurityInterceptor security_inter=new BasicSecurityInterceptor();
//		registerInterceptor(security_inter);
	

		/* 
		 * We want to support FHIR DSTU2 format. This means that the server
		 * will use the DSTU2 bundle format and other DSTU2 encoding changes.
		 *
		 * If you want to use DSTU1 instead, change the following line, and change the 2 occurrences of dstu2 in web.xml to dstu1
		 */
		
//
//		// Get the spring context from the web container (it's declared in web.xml)
//		myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
//
//		/* 
//		 * The BaseJavaConfigDstu2.java class is a spring configuration
//		 * file which is automatically generated as a part of hapi-fhir-jpaserver-base and
//		 * contains bean definitions for a resource provider for each resource type
//		 */
//		String resourceProviderBeanName;
//		resourceProviderBeanName = "myResourceProvidersDstu2";
//		
//		List<IResourceProvider> beans = myAppCtx.getBean(resourceProviderBeanName, List.class);
//		setResourceProviders(beans);
//		
//		/* 
//		 * The system provider implements non-resource-type methods, such as
//		 * transaction, and global history.
//		 */
//		Object systemProvider;
//		systemProvider = myAppCtx.getBean("mySystemProviderDstu2", JpaSystemProviderDstu2.class);
//		setPlainProviders(systemProvider);
//
//		/*
//		 * The conformance provider exports the supported resources, search parameters, etc for
//		 * this server. The JPA version adds resource counts to the exported statement, so it
//		 * is a nice addition.
//		 */
//		
//			IFhirSystemDao<Bundle, MetaDt> systemDao = myAppCtx.getBean("mySystemDaoDstu2", IFhirSystemDao.class);
//			JpaConformanceProviderDstu2 confProvider = new JpaConformanceProviderDstu2(this, systemDao,
//					myAppCtx.getBean(DaoConfig.class));
//			confProvider.setImplementationDescription("HSSC Server");
//			setServerConformanceProvider(confProvider);
//		
//
//		/*
//		 * Enable ETag Support (this is already the default)
//		 */
//		setETagSupport(ETagSupportEnum.ENABLED);
//
//		/*
//		 * This server tries to dynamically generate narratives
//		 */
		FhirContext ctx = getFhirContext();
		ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());
//
//		/*
//		 * Default to JSON and pretty printing
//		 */
//		setDefaultPrettyPrint(true);
//		setDefaultResponseEncoding(EncodingEnum.JSON);
//
//		/*
//		 * -- New in HAPI FHIR 1.5 --
//		 * This configures the server to page search results to and from
//		 * the database
//		 */
//		setPagingProvider(myAppCtx.getBean(DatabaseBackedPagingProvider.class));
//
//		/*
//		 * Load interceptors for the server from Spring (these are defined in FhirServerConfig.java)
//		 */
//		Collection<IServerInterceptor> interceptorBeans = myAppCtx.getBeansOfType(IServerInterceptor.class).values();
//		for (IServerInterceptor interceptor : interceptorBeans) {
//			this.registerInterceptor(interceptor);
//		}
//
//		/*
//		 * If you are hosting this server at a specific DNS name, the server will try to 
//		 * figure out the FHIR base URL based on what the web container tells it, but
//		 * this doesn't always work. If you are setting links in your search bundles that
//		 * just refer to "localhost", you might want to use a server address strategy:
//		 */
         setServerAddressStrategy(new HardcodedServerAddressStrategy("http://myaddress"));
//registerInterceptor(new ResponseHighlighterInterceptor());
		
		/*
		 * Tells the server to return pretty-printed responses by default
		 */
		setDefaultPrettyPrint(true);
	}
	
}
