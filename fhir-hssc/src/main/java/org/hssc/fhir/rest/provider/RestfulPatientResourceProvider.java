package org.hssc.fhir.rest.provider;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hssc.fhir.rest.service.MpiPatient;

import com.sun.mdm.index.master.ProcessingException;
import com.sun.mdm.index.master.UserException;
import com.sun.mdm.index.objects.patient.PatientObject;

//import ca.uhn.example.services.MpiPatient;
import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.SimpleSetter.Parameter;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.IssueSeverityEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;

import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

/**
 * All resource providers must implement IResourceProvider
 */
public class RestfulPatientResourceProvider implements IResourceProvider {
 
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }
     
    /**
     * The "@Read" annotation indicates that this method supports the
     * read operation. Read operations should return a single resource
     * instance.
     *
     * @param theId
     *    The read operation takes one parameter, which must be of type
     *    IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return
     *    Returns a resource matching this identifier, or null if none exists.
     * @throws Exception 
     */
    @Read()
    
    public Patient getResourceById(@IdParam IdDt theId) throws Exception {
       
    	
        MpiPatient patient=new MpiPatient();
        Patient Fhir_Patient=new Patient();
        Fhir_Patient=patient.getMpiPatient(theId.getIdPart());
  	  	return Fhir_Patient;
    }
 
    /**
     * The "@Search" annotation indicates that this method supports the
     * search operation. You may have many different method annotated with
     * this annotation, to support many different search criteria. This
     * example searches by family name.
     *
     * @param theFamilyName
     *    This operation takes one parameter which is the search criteria. It is
     *    annotated with the "@Required" annotation. This annotation takes one argument,
     *    a string containing the name of the search criteria. The datatype here
     *    is StringParam, but there are other possible parameter types depending on the
     *    specific search criteria.
     * @return
     *    This method returns a list of Patients. This list may contain multiple
     *    matching resources, or it may also be empty.
     * @throws Exception 
     */
    
    
   
    @Search()
    @Description(shortDefinition="My search",formalDefinition="Default Search")
    public List<Patient> getPatient(@RequiredParam(name = Patient.SP_FAMILY) StringParam theFamilyName,@RequiredParam(name = Patient.SP_GIVEN) StringParam theGivenName,@OptionalParam(name = Patient.SP_GENDER) StringParam theGender,@OptionalParam(name= Patient.SP_BIRTHDATE) StringParam birthdate) throws Exception 
    {
          List<Patient> pat_list=new ArrayList<Patient>();
    	MpiPatient mpi_pat=new MpiPatient();
    	if (theGender != null && birthdate != null)
    	{
    		System.out.println("***********MY COMMENT***********" + theGender.getValue());
    		pat_list=mpi_pat.searchMpiPatient(theFamilyName.getValue(),theGivenName.getValue(),theGender.getValue(),birthdate.getValue());
    	}
    	else if(theGender != null)
    		pat_list=mpi_pat.searchMpiPatient(theFamilyName.getValue(),theGivenName.getValue(),theGender.getValue(),null);
       	else if(birthdate != null)
    		pat_list=mpi_pat.searchMpiPatient(theFamilyName.getValue(),theGivenName.getValue(),null,birthdate.getValue());	
    	else
    		pat_list=mpi_pat.searchMpiPatient(theFamilyName.getValue(),theGivenName.getValue(),null,null);
    	
    	return pat_list;
    }
    
//    @Search()
//    public List<Patient> getPatientWithDOB(@RequiredParam(name = Patient.SP_FAMILY) StringParam theFamilyName,@RequiredParam(name = Patient.SP_GIVEN) StringParam theGivenName,@RequiredParam(name = Patient.SP_BIRTHDATE) StringParam DateofBirth) throws Exception 
//    {
//        List<Patient> pat_list=new ArrayList<Patient>();
//    	MpiPatient mpi_pat=new MpiPatient();
//    	pat_list=mpi_pat.searchMpiPatientbyDob(theFamilyName.getValue(),theGivenName.getValue(),DateofBirth.getValue());
//       	return pat_list;
//    }
// 
}