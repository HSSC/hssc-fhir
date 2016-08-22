/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hssc.fhir.rest.service;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.naming.*;
import com.sun.mdm.index.ejb.master.MasterControllerRemote;
import com.sun.mdm.index.master.ProcessingException;
import com.sun.mdm.index.master.UserException;
import com.sun.mdm.index.master.search.enterprise.EOGetOptions;
import com.sun.mdm.index.master.search.enterprise.EOSearchCriteria;
import com.sun.mdm.index.master.search.enterprise.EOSearchOptions;
import com.sun.mdm.index.master.search.enterprise.EOSearchResultIterator;
import com.sun.mdm.index.master.search.enterprise.EOSearchResultRecord;
import com.sun.mdm.index.objects.EnterpriseObject;
import com.sun.mdm.index.objects.ObjectNode;
import com.sun.mdm.index.objects.SBR;
import com.sun.mdm.index.objects.SystemObject;
import com.sun.mdm.index.objects.epath.EPath;
import com.sun.mdm.index.objects.epath.EPathArrayList;
import com.sun.mdm.index.objects.epath.EPathException;
import com.sun.mdm.index.objects.exception.ObjectException;
import com.sun.mdm.index.objects.patient.AddressObject;
import com.sun.mdm.index.objects.patient.PatientObject;
import com.sun.mdm.index.objects.patient.PhoneObject;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Patient.Communication;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.param.DateParam;

import java.util.Enumeration;
//import ca.uhn.example.config;


public class MpiPatient {
    
    public static MasterControllerRemote mc = null;
    private static Properties props = new Properties();
    
    public MpiPatient() throws Exception {}
    
    private static void lookupMasterController() {

        ResourceBundle rb = ResourceBundle.getBundle("connection");
        Enumeration<String> keys = rb.getKeys();
        while (keys.hasMoreElements())
        {
            String key = keys.nextElement();
            String value = rb.getString(key);
            props.put(key, value);
        }
        
        rb = ResourceBundle.getBundle("logging");
        keys = rb.getKeys();
        while (keys.hasMoreElements())
        {
            String key = keys.nextElement();
            String value = rb.getString(key);
            props.put(key, value);
        }
        
        String initialContextFactory = props.getProperty("INITIAL_CONTEXT_FACTORY");
        String hostName = props.getProperty("HOSTNAME");
        String port = props.getProperty("PORT");
        String securityPrincipal = props.getProperty("USERNAME");
        String securityCredentials = props.getProperty("PASSWORD");
        String jndiName = props.getProperty("JNDI_NAME");
        
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,  initialContextFactory);
        props.setProperty(Context.PROVIDER_URL, "t3://" + hostName + ":" + port);
        props.setProperty(Context.SECURITY_PRINCIPAL, securityPrincipal);
        props.setProperty(Context.SECURITY_CREDENTIALS, securityCredentials);
        
        Context ctx = null;
        try {
            ctx = new InitialContext(props);
            //System.out.println("Initial Context: " + ctx.toString());
            mc = (MasterControllerRemote) ctx.lookup(jndiName);
        } catch (NamingException ne) {
           ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {ctx.close();}
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


 public Patient getMpiPatient(String euid)
 {
	 
	   try
   {
	  if(MpiPatient.mc==null)	   
		  lookupMasterController();
 
      String systemCode = null; 
       SBR sbr = null;
       sbr = MpiPatient.mc.getSBR(euid);
       System.out.println("SBR Object fields: "+ sbr.getFieldNames());
              
       if (sbr != null)
       {
           PatientObject patient = null;
           patient = (PatientObject)sbr.getObject();
           systemCode = sbr.getSystemCode();
           Patient fhir_pat=convertMPIPatient_FHIRPatient(patient,euid);
           return fhir_pat;
       } 
     
   } catch (Exception e)
   {
       Date exceptionTime = new Date();
       System.err.println("####<" + exceptionTime.toString() + ">");
       e.printStackTrace();
   }
	return null;
}
 
 public List<Patient> searchMpiPatient(String last_name,String first_name,String gender,String dob) throws ProcessingException, UserException, RemoteException, ParseException
 {
	 List<Patient> pat_list=new ArrayList<Patient>();
	 SystemObject sysobj=new SystemObject();
     PatientObject patobj=new PatientObject();
     if(MpiPatient.mc==null)
    	 lookupMasterController();
     patobj.setLastName(last_name);
     patobj.setFirstName(first_name);
     String genderCode=null;
     Date birthdate=null;
     if (gender != null)
     {  
    	System.out.println("*********IN SearchMPI*** Gender Value: " + gender); 
    	genderCode=convertGender_Str_to_Code(gender); 
      }
     if(dob !=null)
     {
    	 SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
         birthdate=df.parse(dob);
     }
     patobj.setBirthDate(birthdate);
     patobj.setGender(genderCode);
     sysobj.setChildType("Patient");
     sysobj.setObject(patobj);
     
     EOSearchCriteria search=new EOSearchCriteria(sysobj);
     EPathArrayList epaths=getFieldstoReturn();
     
     EOSearchOptions options=new EOSearchOptions("PHONETIC-SEARCH",epaths);
     EOSearchResultIterator iterator= mc.searchEnterpriseObject(search,options);
     EOSearchResultRecord record = null;
     
     while(iterator.hasNext())
    {
   	  record = iterator.next();
   	  String euid=record.getEUID();
   	  Patient fhir_pat=convertMPIPatient_FHIRPatient(((PatientObject)(record.getObject())),euid);
//   	  IdDt fhir_id= new IdDt();
//   	  fhir_id.setValueAsString(record.getEUID());
//   	  fhir_pat.setId(fhir_id);
   	// fhir_pat.setId(record.getEUID());
   	 pat_list.add(fhir_pat);
    }
     return pat_list;
  
 }
 
 
 
 public Patient convertMPIPatient_FHIRPatient(PatientObject mpipatient,String euid) throws ObjectException
 { 
	 
	 Patient fhir_pat=new Patient();
	 fhir_pat.setId(euid);
	 fhir_pat.addIdentifier();
	 fhir_pat.getIdentifier().get(0).setSystem("SSN");
	 fhir_pat.getIdentifier().get(0).setValue(mpipatient.getSSN());
	 fhir_pat.addName().addGiven(mpipatient.getFirstName());
	 fhir_pat.getName().get(0).addFamily(mpipatient.getLastName());
	 
	 if(mpipatient.getGender() != null )
	 {
	 if (mpipatient.getGender().equals("11"))
		 fhir_pat.setGender(AdministrativeGenderEnum.FEMALE);
	 else if(mpipatient.getGender().equals("12"))
		 fhir_pat.setGender(AdministrativeGenderEnum.MALE);
	 else if(mpipatient.getGender().equals("13"))
		 fhir_pat.setGender(AdministrativeGenderEnum.UNKNOWN);
	 else if(mpipatient.getGender().equals("14"))
		 fhir_pat.setGender(AdministrativeGenderEnum.UNKNOWN);
	 else if(mpipatient.getGender().equals("15"))
		 fhir_pat.setGender(AdministrativeGenderEnum.OTHER);
	 else {}
	 }
	 
	 
	 if(mpipatient.getStatus() != null)
		 fhir_pat.setActive(mpipatient.getStatus().equalsIgnoreCase("A"));
	 
	 if(mpipatient.getBirthDate() != null)
		 fhir_pat.setBirthDate(new DateDt(mpipatient.getBirthDate()));
	  
	 if(mpipatient.getMaritalStatus() != null)
	 {
		Integer maritalCode=new Integer(mpipatient.getMaritalStatus());
		switch(maritalCode)
		{
			case(11):fhir_pat.setMaritalStatus(MaritalStatusCodesEnum.D);break;
			case(12):fhir_pat.setMaritalStatus(MaritalStatusCodesEnum.T);break;
			case(16):fhir_pat.setMaritalStatus(MaritalStatusCodesEnum.S);break;
			case(17):fhir_pat.setMaritalStatus(MaritalStatusCodesEnum.UNK);break;
			case(18):fhir_pat.setMaritalStatus(MaritalStatusCodesEnum.W);break;
			case(19):fhir_pat.setMaritalStatus(MaritalStatusCodesEnum.M);break;
			case(20):fhir_pat.setMaritalStatus(MaritalStatusCodesEnum.L);break;
			case(21):fhir_pat.setMaritalStatus(MaritalStatusCodesEnum.L);break;
			case(22):fhir_pat.setMaritalStatus(MaritalStatusCodesEnum.M);break;	
			case(24):fhir_pat.setMaritalStatus(MaritalStatusCodesEnum.T);break;
		}
	 }
	 if(mpipatient.getDeathDate() != null)
	 {
		 fhir_pat.setDeceased(new DateTimeDt(mpipatient.getDeathDate()));
	 }
	 else
	 {
		 if(mpipatient.getDeathIndicator() != null)
		 {
			 if(mpipatient.getDeathIndicator().equalsIgnoreCase("Y"))
				fhir_pat.setDeceased(new BooleanDt(true));
		 }
	
	 }
	  
	 /* This is the naive implementation for the communication.languages.This is out of scope for the POC */
//	 if(mpipatient.getLanguage() != null && mpipatient.getLanguageCodeSys() != null)
//	 {
//		  List<Communication> languages=new ArrayList<Communication>();
//		  languages.add(new Communication().setLanguage(new CodeableConceptDt(mpipatient.getLanguageCodeSys(),mpipatient.getLanguage())));
//		  fhir_pat.setCommunication(languages);
//	 }
	 List<AddressDt> addr_list=new ArrayList<AddressDt>();
	 
	 Iterator addr_iter=null;
	 if(mpipatient.getAddress() != null)
		 addr_iter=mpipatient.getAddress().iterator();
	  while(addr_iter != null && addr_iter.hasNext())
	  	{
		  	AddressObject addr = (AddressObject)addr_iter.next();
		  	AddressDt addr_dt=new AddressDt();
		  	if(addr.getAddressLine1() != null)
		  		addr_dt.addLine(addr.getAddressLine1());
		  	if(addr.getCity() != null)
		  		addr_dt.setCity(addr.getCity());
		  	if(addr.getPostalCode() !=null)
		  		addr_dt.setPostalCode(addr.getPostalCode());
		  	if(addr.getStateCode() !=null)
		  		addr_dt.setState(addr.getStateCode());
		  	if(addr.getCountryCode() !=null)
		  		addr_dt.setCountry(addr.getCountryCode());
		  	if(addr.getAddressType() != null && addr.getAddressType().equalsIgnoreCase("HOME"))
		      addr_dt.setUse(AddressUseEnum.HOME);
			addr_list.add(addr_dt);
	  	}
	  fhir_pat.setAddress(addr_list);
	  
	  List<ContactPointDt> phone_list=new ArrayList<ContactPointDt>();
	  Iterator phone_iter=null;
	  if(mpipatient.getPhone() != null)
		  phone_iter= mpipatient.getPhone().iterator();
      while(phone_iter !=null && phone_iter.hasNext())
      {
    	  PhoneObject phone=(PhoneObject) phone_iter.next();
    	  ContactPointDt phone_dt=new ContactPointDt();
     	  if(phone.getPhoneNum() != null)
    	 phone_dt.setValue(phone.getPhoneNum());
    	  if(phone.getPhoneType().equalsIgnoreCase("HOME"))
    		  phone_dt.setUse(ContactPointUseEnum.HOME);
    	  else if(phone.getPhoneType().equalsIgnoreCase("WORK"))
    		  phone_dt.setUse(ContactPointUseEnum.WORK);
    	  else if(phone.getPhoneType().equalsIgnoreCase("MOBILE"))
    		  phone_dt.setUse(ContactPointUseEnum.MOBILE);
    	  else {}
    	  phone_dt.setSystem(ContactPointSystemEnum.PHONE);
    	  
    	  phone_list.add(phone_dt);
      }
   
      if(mpipatient.getEmail() != null)
      {
    	ContactPointDt email= new ContactPointDt();
    	email.setSystem(ContactPointSystemEnum.EMAIL);
    	email.setValue(mpipatient.getEmail());
    	phone_list.add(email);
      }
      
      fhir_pat.setTelecom(phone_list);
      
      fhir_pat.setManagingOrganization(new ResourceReferenceDt("HSSC"));
      
      
  return fhir_pat;	 
 }
 
 
public String convertGender_Str_to_Code(String gender_str)
{
	   String gender_code=null;
     	if(gender_str.equalsIgnoreCase("female"))
     		gender_code="11";
       	else if(gender_str.equalsIgnoreCase("male"))
     		gender_code="12";
      	else if(gender_str.equalsIgnoreCase("other"))
     		gender_code="15";
     	else if(gender_str.equalsIgnoreCase("unknown"))
     		gender_code="14";
     	else if(gender_str.equalsIgnoreCase("indeterminate"))
     		gender_code="13"; 
     	else {}
     	return gender_code;
    			
}

public EPathArrayList getFieldstoReturn() throws EPathException
{
	EPathArrayList epaths=new EPathArrayList();
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".EUID");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".LastName");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".FirstName");
    epaths.add("Enterprise.SystemSBR." + "Patient" +  ".Gender");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".SSN");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Status");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".BirthDate");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".MaritalStatus");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".DeathIndicator");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".DeathDate");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Language");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Email");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Address" + ".AddressLine1");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Address" + ".PostalCode");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Address" + ".CountryCode");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Address" + ".StateCode");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Address" + ".City");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Address" + ".AddressType");
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Phone" + ".PhoneType" );
    epaths.add("Enterprise.SystemSBR." + "Patient" + ".Phone" + ".PhoneNum" );
             return epaths;
}
 
 }
   