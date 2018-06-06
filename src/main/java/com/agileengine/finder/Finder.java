package com.agileengine.finder;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The class for finding similar elements in XML/HTML file.
 * Contains finding element by id and tag functionality.
 */
public class Finder {

    private static final Logger LOG = Logger.getLogger(Finder.class);
    private static final String CHARSET = "UTF-8";

    public Optional<Element> findElementById(File file, String targetElementId) {
        try {
            Document doc = Jsoup.parse(
                    file,
                    CHARSET,
                    file.getAbsolutePath());
            return Optional.of(doc.getElementById(targetElementId));
        } catch (Exception e) {
            LOG.error("Exception while parsing file " + file.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    public Optional<Element> findSimilar(Element baseElement, File additionalFile) {
        List<Element> elementsByTag = findElementsByTag(additionalFile, baseElement.tagName());
        return Optional.ofNullable(getTheMostSimilarElement(baseElement, elementsByTag));
    }

    public String getPathToElement(Element element) {
        if (Objects.isNull(element.parent())) {
            return "root";
        }
        return getPathToElement(element.parent()) + " > " + element.tagName();
    }

    public Optional<String> getAttributes(Optional<Element> element) {
        return element.map(button ->
                button.attributes().asList().stream()
                        .map(attr -> attr.getKey() + " = " + attr.getValue())
                        .collect(Collectors.joining(", "))
        );
    }

    private List<Element> findElementsByTag(File file, String tagName) {
        try {
            Document doc = Jsoup.parse(
                    file,
                    CHARSET,
                    file.getAbsolutePath());
            return doc.select(tagName);
        } catch (Exception e) {
            LOG.error("Exception while parsing file " + file.getAbsolutePath(), e);
            return Collections.emptyList();
        }
    }

    private Element getTheMostSimilarElement(Element baseElement, List<Element> elementsByTag) {
        double maxSimilarity = 0;
        Element theMostSimilarElement = null;
        for (Element candidate : elementsByTag) {
            double similarity = similarityDegree(baseElement, candidate);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                theMostSimilarElement = candidate;
            }
        }
        LOG.info("Maximum similarity found - " + new DecimalFormat("0.00").format(maxSimilarity));
        return theMostSimilarElement;
    }

    private double similarityDegree(Element baseElement, Element candidate) {
        List<Attribute> baseElementAttributes = baseElement.attributes().asList();
        List<Attribute> candidateAttributes = candidate.attributes().asList();
        double matched = baseElementAttributes
                .stream()
                .filter(candidateAttributes::contains)
                .count();
        return matched / baseElementAttributes.size();
    }

}
