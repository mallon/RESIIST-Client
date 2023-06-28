package com.axellience.resiist.client.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.genmymodel.gmmf.common.GenMyModelBinaryResourceImpl;
import org.opengroup.archimate.Referenceable;
import org.opengroup.archimate.impl.ArchimatePackageImpl;

import com.genmymodel.archimatediag.impl.ArchimatediagPackageImpl;
import com.genmymodel.ecoreonline.graphic.GMMUtil;
import com.genmymodel.ecoreonline.graphic.crossreferencedatapter.GmmCrossReferenceAdapter;

public class ModelUtils
{

    private ModelUtils()
    {

    }

    public static Resource importArchimateModel(String projectId, byte[] data) throws IOException
    {
        ArchimatePackageImpl.init();
        ArchimatediagPackageImpl.init();

        GenMyModelBinaryResourceImpl resource =
                new GenMyModelBinaryResourceImpl(URI.createURI("genmymodel://"
                                                               + projectId
                                                               + "/archimate_imported"));
        InputStream inputStream = new ByteArrayInputStream(data);
        resource.load(inputStream, Collections.emptyMap());

        GmmCrossReferenceAdapter crossReferenceAdapter = new GmmCrossReferenceAdapter();
        resource.eAdapters().add(crossReferenceAdapter);

        return resource;
    }

    public static String getFrequency(EModelElement modelElement)
    {
        EMap<String, String> details = getElementDetails(modelElement);
        if (!details.containsKey(ResiistConstants.FREQUENCY))
            throw new ResourceNotFoundException("ResiistConstants.FREQUENCY not found");

        return details.get(ResiistConstants.FREQUENCY);
    }

    public static String getObjectsToDetect(EModelElement modelElement)
    {
        EMap<String, String> details = getElementDetails(modelElement);

        return details.get(ResiistConstants.OBJECTS_TO_DETECT);
    }

    public static boolean hasFrequency(EModelElement modelElement)
    {
        if (hasAnnotation(modelElement)) {
            EMap<String, String> details = getElementDetails(modelElement);
            return details.containsKey(ResiistConstants.FREQUENCY)
                   && !details.get(ResiistConstants.FREQUENCY).isEmpty();
        }
        return false;
    }

    public static boolean hasInterpretationRules(EModelElement modelElement)
    {
        if (hasAnnotation(modelElement)) {
            EMap<String, String> details = getElementDetails(modelElement);
            return details.containsKey(ResiistConstants.PSEUDO_CODE_INTERPRETATION_RULES)
                   && !details.get(ResiistConstants.PSEUDO_CODE_INTERPRETATION_RULES).isEmpty();
        }
        return false;
    }

    public static boolean hasSimulationPath(EModelElement modelElement)
    {
        if (hasAnnotation(modelElement)) {
            EMap<String, String> details = getElementDetails(modelElement);
            return details.containsKey(ResiistConstants.SIMULATION_FILE_PATH)
                   && !details.get(ResiistConstants.SIMULATION_FILE_PATH).isEmpty();
        }
        return false;
    }

    public static boolean hasGAMASimulationPath(EModelElement modelElement)
    {
        if (hasAnnotation(modelElement)) {
            EMap<String, String> details = getElementDetails(modelElement);
            return details.containsKey(ResiistConstants.GAMA_SIMULATION_FILE_PATH)
                   && !details.get(ResiistConstants.GAMA_SIMULATION_FILE_PATH).isEmpty();
        }
        return false;
    }

    public static boolean hasVideoSimulationPath(EModelElement modelElement)
    {
        if (hasAnnotation(modelElement)) {
            EMap<String, String> details = getElementDetails(modelElement);
            return details.containsKey(ResiistConstants.SIMULATION_FILE_PATH)
                   && details.get(ResiistConstants.SIMULATION_FILE_PATH)
                             .endsWith(ResiistConstants.SIMULATION_VIDEO_FILE_NAME);
        }
        return false;
    }

    public static EMap<String, String> getElementDetails(EModelElement modelElement)
    {
        Optional<EAnnotation> gmmAnnotation = GMMUtil.getGmmAnnotation(modelElement);
        if (!gmmAnnotation.isPresent())
            throw new ResourceNotFoundException("Element not found");

        return gmmAnnotation.get().getDetails();
    }

    public static boolean hasAnnotation(EModelElement modelElement)
    {
        return GMMUtil.getGmmAnnotation(modelElement).isPresent();
    }

    public static String getSimulationFilePath(EModelElement modelElement)
    {
        EMap<String, String> details = getElementDetails(modelElement);
        if (!details.containsKey(ResiistConstants.SIMULATION_FILE_PATH))
            throw new ResourceNotFoundException("Simulation file path not found");

        return details.get(ResiistConstants.SIMULATION_FILE_PATH);
    }

    public static String getGAMASimulationFilePath(EModelElement modelElement)
    {
        EMap<String, String> details = getElementDetails(modelElement);
        return details.get(ResiistConstants.GAMA_SIMULATION_FILE_PATH);
    }

    public static String getElementName(EModelElement modelElement)
    {
        if (modelElement instanceof ENamedElement) {
            return ((ENamedElement) modelElement).getName();
        }
        return null;
    }

    public static Optional<String> getName(EObject eObject)
    {
        EStructuralFeature nameFeature = eObject.eClass().getEStructuralFeature("name");
        if (nameFeature == null)
            return Optional.empty();

        Object nameValue = eObject.eGet(nameFeature);
        if (nameValue == null)
            return Optional.empty();

        if (nameValue instanceof EList<?>) {
            EList<?> nameValues = ((EList<?>) eObject.eGet(nameFeature));
            if (nameValues.isEmpty()) {
                return Optional.empty();
            }
            EObject firstNameLiteral = (EObject) nameValues.get(0);
            EStructuralFeature valueFeature =
                    firstNameLiteral.eClass().getEStructuralFeature("value");
            if (valueFeature == null)
                return Optional.empty();

            return Optional.ofNullable((String) firstNameLiteral.eGet(valueFeature));
        }

        return Optional.ofNullable((String) nameValue);
    }

    public static String getArchimateName(Referenceable referenceable)
    {
        if (referenceable != null && !referenceable.getName().isEmpty()) {
            return referenceable.getName().get(0).getValue();
        }
        return null;
    }

    public static void setName(EObject eObject, String name)
    {
        EStructuralFeature nameFeature = eObject.eClass().getEStructuralFeature("name");
        if (nameFeature != null)
            eObject.eSet(nameFeature, name);
    }

    public static <T> List<T> getAllElements(ResourceImpl modelResource, Class<T> targetedType)
    {
        if (modelResource != null)
            return GMMUtil.allContentOfType(modelResource.getContents().get(0), targetedType)
                          .collect(Collectors.toList());

        return Collections.emptyList();
    }
}