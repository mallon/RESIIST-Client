package com.axellience.resiist.client.extractiontomodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Expression;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.InstanceValue;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Slot;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;

import com.axellience.resiist.client.acquisitiontointegration.acquisition.DataSource;
import com.axellience.resiist.client.acquisitiontointegration.integration.IntegrationPlatform;
import com.fasterxml.jackson.databind.MappingIterator;

public class ExtractionToModel
{
    private IntegrationPlatform integrationPlatform = IntegrationPlatform.getInstance();
    private DataSource          dataSource          = new DataSource();

    private static final Logger LOGGER   = Logger.getLogger(ExtractionToModel.class.getName());
    private static String       XMI_FILE = "modelGenerated.xmi";

    private Map<String, InstanceSpecification> instances;
    private Map<String, Class>                 classes;
    private Package                            model;

    public void connexionToModelingPlatform(String login, String password)
    {
        integrationPlatform.connexion(login, password);
    }

    public void createModelWithCSV(String objectAFilePath, String relationFilePath,
                                   String objectBFilePath)
    {
        dataSource.readCSVFiles(objectAFilePath, relationFilePath, objectBFilePath);

        instances = new HashMap<>();
        classes = new HashMap<>();
        model = UMLFactory.eINSTANCE.createPackage();

        LOGGER.info("Starting feeding model with infrastructure data...");
        Class infrastructure = feedModelWithInfrastructure(dataSource.getMappingIterator());

        LOGGER.info("Starting feeding model with data sources data...");
        Class sourceDonnees = feedModelWithDataSources(dataSource.getThirdMappingIterator());

        LOGGER.info("Starting feeding model with infrastructures and data sources relations...");
        createAssociationBetweenClasses(infrastructure, sourceDonnees);
        feedModelWithInfraDataSourceRelations(dataSource.getSecondMappingIterator());

        LOGGER.info("Starting saving created model as an XMI file...");
        saveModelToXMI();
    }

    private Class feedModelWithInfrastructure(MappingIterator<Map<String, String>> mappingIterator)
    {
        Class infrastructure = generateInfraClassAndAttribute();

        while (mappingIterator.hasNext()) {
            Map<String, String> line = mappingIterator.next();
            createInstance(line.get("nom"), infrastructure);
            createTrainStationSlots(infrastructure.getName(),
                                    line.get("nom"),
                                    line.get("heure ouverture"),
                                    line.get("heure fermeture"));
        }

        return infrastructure;
    }

    private void createInstance(String instanceName, Class classe)
    {
        InstanceSpecification instanceSpecification =
                UMLFactory.eINSTANCE.createInstanceSpecification();
        instanceSpecification.setName(instanceName);
        instanceSpecification.getClassifiers().add(classe);
        model.getPackagedElements().add(instanceSpecification);

        instances.put(instanceName, instanceSpecification);
        classes.put(instanceName, classe);
    }

    private void createTrainStationSlots(String className, String nameInfra, String heureDebInfra,
                                         String heureFinInfra)
    {
        Class classe = classes.get(className);
        EList<Property> classProp = classe.getAllAttributes();

        InstanceSpecification instanceSpecification = instances.get(nameInfra);

        Slot name = UMLFactory.eINSTANCE.createSlot();
        name.setDefiningFeature(classProp.get(0));
        name.getValues().add(createExpression(nameInfra));
        instanceSpecification.getSlots().add(name);

        Slot heureDeb = UMLFactory.eINSTANCE.createSlot();
        heureDeb.setDefiningFeature(classProp.get(1));
        heureDeb.getValues().add(createExpression(heureDebInfra));
        instanceSpecification.getSlots().add(heureDeb);

        Slot heureFin = UMLFactory.eINSTANCE.createSlot();
        heureFin.setDefiningFeature(classProp.get(2));
        heureFin.getValues().add(createExpression(heureFinInfra));
        instanceSpecification.getSlots().add(heureFin);

        model.getPackagedElements().add(instanceSpecification);
    }

    private Expression createExpression(String value)
    {
        Expression expression = UMLFactory.eINSTANCE.createExpression();
        expression.setSymbol(value);
        return expression;
    }

    private Class feedModelWithDataSources(MappingIterator<Map<String, String>> mappingIterator)
    {
        Class sourceDonnees = generateSourceDonneeClassAndAttribute();

        while (mappingIterator.hasNext()) {
            Map<String, String> line = mappingIterator.next();
            createInstance(line.get("nom"), sourceDonnees);
            createTrainsSlots(sourceDonnees.getName(),
                              line.get("nom"),
                              line.get("importance"),
                              line.get("capcité pers."),
                              line.get("type"));
        }

        return sourceDonnees;
    }

    private void createTrainsSlots(String className, String nameSource, String importance,
                                   String capacity, String type)
    {
        Class classe = classes.get(className);
        EList<Property> classProp = classe.getAllAttributes();

        InstanceSpecification instanceSpecification = instances.get(nameSource);

        Slot name = UMLFactory.eINSTANCE.createSlot();
        name.setDefiningFeature(classProp.get(0));
        name.getValues().add(createExpression(nameSource));
        instanceSpecification.getSlots().add(name);

        Slot importSlot = UMLFactory.eINSTANCE.createSlot();
        importSlot.setDefiningFeature(classProp.get(1));
        importSlot.getValues().add(createExpression(importance));
        instanceSpecification.getSlots().add(importSlot);

        Slot formatSlot = UMLFactory.eINSTANCE.createSlot();
        formatSlot.setDefiningFeature(classProp.get(2));
        formatSlot.getValues().add(createExpression(capacity));
        instanceSpecification.getSlots().add(formatSlot);

        Slot freqSlot = UMLFactory.eINSTANCE.createSlot();
        freqSlot.setDefiningFeature(classProp.get(3));
        freqSlot.getValues().add(createExpression(type));
        instanceSpecification.getSlots().add(freqSlot);

        model.getPackagedElements().add(instanceSpecification);
    }

    private Class generateInfraClassAndAttribute()
    {
        String className = "Gares";
        Class infrastructure = UMLFactory.eINSTANCE.createClass();
        infrastructure.setName(className);
        model.getPackagedElements().add(infrastructure);
        classes.put(className, infrastructure);

        Type string = UMLFactory.eINSTANCE.createDataType();
        string.setName("String");
        model.getPackagedElements().add(string);

        Property nom = UMLFactory.eINSTANCE.createProperty();
        nom.setName("Nom");
        nom.setType(string);

        infrastructure.getOwnedAttributes().add(nom);

        Type date = UMLFactory.eINSTANCE.createDataType();
        date.setName("Date");
        model.getPackagedElements().add(date);

        Property heureDeb = UMLFactory.eINSTANCE.createProperty();
        heureDeb.setName("Heure ouverture");
        heureDeb.setType(date);

        infrastructure.getOwnedAttributes().add(heureDeb);

        Property heureFin = UMLFactory.eINSTANCE.createProperty();
        heureFin.setName("Heure fermeture");
        heureFin.setType(date);

        infrastructure.getOwnedAttributes().add(heureFin);

        return infrastructure;
    }

    private Class generateSourceDonneeClassAndAttribute()
    {
        String className = "Trains";
        Class sourceDonnees = UMLFactory.eINSTANCE.createClass();
        sourceDonnees.setName(className);
        model.getPackagedElements().add(sourceDonnees);
        classes.put(className, sourceDonnees);

        Type string = UMLFactory.eINSTANCE.createDataType();
        string.setName("String");
        model.getPackagedElements().add(string);

        Property nom = UMLFactory.eINSTANCE.createProperty();
        nom.setName("Nom");
        nom.setType(string);

        sourceDonnees.getOwnedAttributes().add(nom);

        Type integer = UMLFactory.eINSTANCE.createDataType();
        integer.setName("Integer");
        model.getPackagedElements().add(integer);

        Property importance = UMLFactory.eINSTANCE.createProperty();
        importance.setName("Importance");
        importance.setType(integer);

        sourceDonnees.getOwnedAttributes().add(importance);

        Property format = UMLFactory.eINSTANCE.createProperty();
        format.setName("Capacité personnes");
        format.setType(integer);

        sourceDonnees.getOwnedAttributes().add(format);

        Property frequence = UMLFactory.eINSTANCE.createProperty();
        frequence.setName("Type");
        frequence.setType(string);

        sourceDonnees.getOwnedAttributes().add(frequence);

        return sourceDonnees;
    }

    private void createAssociationBetweenClasses(Class infrastructure, Class sourceDonnees)
    {
        Association association = UMLFactory.eINSTANCE.createAssociation();

        association.setName(infrastructure.getName() + "_" + sourceDonnees.getName());
        association.getOwnedEnds().add(createAssociationProperty(infrastructure.getName()));
        association.getOwnedEnds().add(createAssociationProperty(sourceDonnees.getName()));

        model.getPackagedElements().add(association);
    }

    private Property createAssociationProperty(String propertyName)
    {
        Property property = UMLFactory.eINSTANCE.createProperty();
        property.setName(propertyName);
        property.setType(classes.get(propertyName));
        return property;
    }

    private void feedModelWithInfraDataSourceRelations(MappingIterator<Map<String, String>> mappingIterator)
    {
        while (mappingIterator.hasNext()) {
            Map<String, String> line = mappingIterator.next();
            createRelationBetweenInstances(line.get("identification gare"),
                                           line.get("identification train"));
        }
    }

    private void createRelationBetweenInstances(String nameInstanceOne, String nameInstanceTwo)
    {
        InstanceSpecification instanceSpecification =
                UMLFactory.eINSTANCE.createInstanceSpecification();
        instanceSpecification.setName(nameInstanceOne + "_" + nameInstanceTwo);

        Slot slotOne = UMLFactory.eINSTANCE.createSlot();
        slotOne.getValues().add(createInstanceValue(nameInstanceOne));

        Slot slotTwo = UMLFactory.eINSTANCE.createSlot();
        slotTwo.getValues().add(createInstanceValue(nameInstanceTwo));

        instanceSpecification.getSlots().add(slotOne);
        instanceSpecification.getSlots().add(slotTwo);

        model.getPackagedElements().add(instanceSpecification);
    }

    private InstanceValue createInstanceValue(String instanceName)
    {
        InstanceValue instance = UMLFactory.eINSTANCE.createInstanceValue();
        instance.setInstance(instances.get(instanceName));
        return instance;
    }

    private void saveModelToXMI()
    {
        String fileName = XMI_FILE;

        ResourceSet resSet = new ResourceSetImpl();
        resSet.getResourceFactoryRegistry()
              .getExtensionToFactoryMap()
              .put("xmi", new XMIResourceFactoryImpl());
        URI uri = URI.createURI(fileName);
        Resource resource = resSet.createResource(uri);
        resource.getContents().add(model);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        options.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,
                    XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
        options.put(XMIResource.OPTION_USE_XMI_TYPE, true);

        try {
            resource.save(options);
            LOGGER.log(Level.INFO, "***Model correctly generated***");
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "***XMI file saving problem !***");
        }
    }
}