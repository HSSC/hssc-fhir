# hssc-fhir
HSSC Java Server API for HL7 FHIR

This project uses the HAPI FHIR implementation to build a RESTFul FHIR Server that interacts with HSSC's OHMPI! 

To build and run this project, download or Clone the project from Github. The project can be built using the below commands

   i) cd fhir-hssc 
  
  ii) mvn install 
  
  This will create a war file that can be deployed to any server.
  
  You can run it within maven using 
  mvn jetty:run
  
  You can now access the Patients in OHMPI using the FHIR Server as:
  http://localhost:8080/fhir-hssc-Maven-Webapp/fhir/Patient/ResearchID
  
  where the ResearchID is the patient's euid in OHMPI 
  
  Ex: http://localhost:8080/fhir-hssc-Maven-Webapp/fhir/Patient/123456789
  
   Note: This project contains jar dependencies for accessing Oracle's Master Person Index. The project build will fail if these dependencies are not foud in your local maven repository! 
  
  The missing jar files can be added to your local maven repository using the below command:
  
  mvn install:install-file -Dfile=path_to_your_file/file_name -DgroupId=yourgroupID
  -DartifactId=yourArtifactID -Dversion=yourVersion  -Dpackaging=jar
  
  The groupId,artifactId and version information is available in the project's pom.xml file
  
