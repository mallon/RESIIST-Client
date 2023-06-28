package com.axellience.resiist.client.analysis.evaluation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.axellience.resiist.client.acquisitiontointegration.integration.IntegrationPlatform;
import com.axellience.resiist.client.utils.ResiistConstants;
import com.genmymodel.ecoreonline.graphic.GMMUtil;

public class ResilienceRuleDefiner
{
    private static ResilienceRuleDefiner instance = null;

    private Map<String, Float>                 indicatorIdsAndValues = new HashMap<>();
    private Map<String, BinaryOperator<Float>> aggregationFunctions;

    private IntegrationPlatform integrationPlatform;

    private ResilienceRuleDefiner()
    {
        integrationPlatform = IntegrationPlatform.getInstance();
        initAggregationFonctions();
    }

    private void initAggregationFonctions()
    {
        aggregationFunctions = new HashMap<>();
        aggregationFunctions.put("SUM", (x, y) -> x + y);
        aggregationFunctions.put("SUB", (x, y) -> x - y);
        aggregationFunctions.put("PROD", (x, y) -> x * y);
        aggregationFunctions.put("DIV", (x, y) -> x / y);
        aggregationFunctions.put("MOD", (x, y) -> x % y);
        aggregationFunctions.put("MIN", Math::min);
        aggregationFunctions.put("MAX", Math::max);
        aggregationFunctions.put("POW", (x, y) -> Float.valueOf((float) Math.pow(x, y)));
    }

    public static ResilienceRuleDefiner getInstance()
    {
        if (instance == null)
            instance = new ResilienceRuleDefiner();

        return instance;
    }

    public Float evaluateElementResilience(String elementId, String value)
    {
        Resource projectResource = integrationPlatform.getModelResource();
        EObject eObject = projectResource.getEObject(elementId);
        Optional<EAnnotation> gmmAnnotation = GMMUtil.getGmmAnnotation(eObject);
        if (!gmmAnnotation.isPresent())
            throw new ResourceNotFoundException("Element not found: " + elementId);

        EMap<String, String> details = gmmAnnotation.get().getDetails();

        if (!details.containsKey(ResiistConstants.FNMIN))
            throw new ResourceNotFoundException("fnmin not found in: " + elementId);

        if (!details.containsKey(ResiistConstants.FNMAX))
            throw new ResourceNotFoundException("fnmax not found in: " + elementId);

        if (!details.containsKey(ResiistConstants.FMIN))
            throw new ResourceNotFoundException("fmin not found in: " + elementId);

        if (!details.containsKey(ResiistConstants.FMAX))
            throw new ResourceNotFoundException("fmax not found in: " + elementId);

        Float fn = Float.valueOf(value.replace(',', '.'));
        Float fnmin = Float.valueOf(details.get(ResiistConstants.FNMIN).replace(',', '.'));
        Float fnmax = Float.valueOf(details.get(ResiistConstants.FNMAX).replace(',', '.'));
        Float fmin = Float.valueOf(details.get(ResiistConstants.FMIN).replace(',', '.'));
        Float fmax = Float.valueOf(details.get(ResiistConstants.FMAX).replace(',', '.'));

        return evaluateResilience(fn, fnmax, fmax, fnmin, fmin);
    }

    private Float evaluateResilience(Float fn, Float fnmax, Float fmax, Float fnmin, Float fmin)
    {
        if (fn >= fmax || fn <= fmin)
            return Float.valueOf(0);

        if (fn > fnmax)
            return 1 - ((fn - fnmax) / (fmax - fnmax));

        if (fn < fnmin)
            return 1 - ((fnmin - fn) / (fnmin - fmin));

        return Float.valueOf(1);
    }

    public Float evaluateModelResilience(String indicatorId, Float indicatorResilience)
    {
        indicatorIdsAndValues.put(indicatorId, indicatorResilience);
        Float resilience = 1.0f;
        for (Float value : indicatorIdsAndValues.values()) {
            resilience *= value;
        }
        return resilience;
    }

    public Float evaluateModelResilience(String indicatorId, Float indicatorResilience,
                                         BinaryOperator<Float> aggregationFct)
    {
        indicatorIdsAndValues.put(indicatorId, indicatorResilience);
        Float resilience = 1.0f;
        for (Float value : indicatorIdsAndValues.values()) {
            resilience = aggregationFct.apply(resilience, value);
        }
        return resilience;
    }

    public String getResilienceAlertColor(String elementId, Float elementResilience)
    {
        Resource projectResource = integrationPlatform.getModelResource();
        EObject eObject = projectResource.getEObject(elementId);
        Optional<EAnnotation> gmmAnnotation = GMMUtil.getGmmAnnotation(eObject);
        if (!gmmAnnotation.isPresent())
            throw new ResourceNotFoundException("Element not found: " + elementId);

        EMap<String, String> details = gmmAnnotation.get().getDetails();

        if (elementResilience == 1.0)
            return details.get(ResiistConstants.COLOR_NORMAL);

        if (elementResilience == 0.0)
            return details.get(ResiistConstants.COLOR_UNACCEPTABLE);

        return details.get(ResiistConstants.COLOR_ACCEPTABLE);
    }

    public BinaryOperator<Float> getAggregationFunction(String elementId)
    {
        Resource projectResource = integrationPlatform.getModelResource();
        EObject eObject = projectResource.getEObject(elementId);
        Optional<EAnnotation> gmmAnnotation = GMMUtil.getGmmAnnotation(eObject);
        if (!gmmAnnotation.isPresent())
            throw new ResourceNotFoundException("Element not found: " + elementId);

        EMap<String, String> details = gmmAnnotation.get().getDetails();

        if (!details.containsKey(ResiistConstants.AGG_FCT))
            throw new ResourceNotFoundException("Aggregation function not found in: " + elementId);

        return aggregationFunctions.get(details.get(ResiistConstants.AGG_FCT));
    }
}