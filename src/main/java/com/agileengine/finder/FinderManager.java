package com.agileengine.finder;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.Optional;

/**
 * The class for managing searching for similar element in file process
 */
public class FinderManager {

    private static final Logger LOG = Logger.getLogger(FinderManager.class);
    private Finder finder = new Finder();

    public ImmutablePair<Element, Element> findSimilarElement(String elementId, File baseFile, File additionalFile) {

        Optional<Element> optionalElement = finder.findElementById(baseFile, elementId);

        String targetElementResult = finder.getAttributes(optionalElement)
                .map(attrs -> "Target element attributes: " + attrs)
                .orElse("Target element not found");
        LOG.info(targetElementResult);

        Optional<Element> theMostSimilarElement = optionalElement
                .flatMap(e -> finder.findSimilar(e, additionalFile));

        String similarElementResult = finder.getAttributes(theMostSimilarElement)
                .map(attrs -> "The most similar element attributes: " + attrs
                        + System.lineSeparator() +
                        "Path to the most similar element: " +
                        theMostSimilarElement
                                .map(el -> finder.getPathToElement(el))
                )
                .orElse("The most similar element not found");
        LOG.info(similarElementResult);

        return optionalElement.map(
                e -> theMostSimilarElement
                        .map(e1 -> ImmutablePair.of(e, e1))
                        .orElse(ImmutablePair.nullPair()))
                .orElse(ImmutablePair.nullPair());
    }

}
