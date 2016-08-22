package org.hssc.fhir.rest.provider;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.model.dstu2.resource.Conformance.Rest;
import ca.uhn.fhir.model.dstu2.valueset.RestfulConformanceModeEnum;
import ca.uhn.fhir.rest.annotation.Metadata;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.provider.dstu2.ServerConformanceProvider;

public class ConformanceProvider extends ServerConformanceProvider{
	
	private RestfulServer myRestFulServer ;
	
	public ConformanceProvider() {
		super();
		super.setCache(false);
		
	}
	
	public ConformanceProvider(RestfulServer rs) {
		myRestFulServer=rs;
		
	}
	
	  @Override
	  @Metadata
		public Conformance getServerConformance(HttpServletRequest rs) {
		    Conformance retVal = new Conformance();
		   retVal=super.getServerConformance(rs);
		   retVal.setPublisher("HSSC");
		   retVal.setCopyright("HSSC");
		   retVal.setDescription("Test Conformance Statement");
		 	  
		   
//   Rest rest = retVal.addRest();
//	rest.setMode(RestfulConformanceModeEnum.SERVER);
		//rs.getResourceBindings();
		   return retVal;
	  }

}
