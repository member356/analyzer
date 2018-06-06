package com.agileengine.finder;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Application {

    private static final Logger LOG = Logger.getLogger(Application.class);

    /**
     * @param args requires the array of: id of element to find,
     *             file path where to find element by id and
     *             file path where to find similar element
     */
    public static void main(String[] args) {
        validateArguments(args);
        String elementId = args[0];
        String baseFilePath = args[1];
        String additionalFilePath = args[2];

        FinderManager finderManager = new FinderManager();

        ImmutablePair<Element, Element> resultPair = finderManager.findSimilarElement(
                elementId, new File(baseFilePath), new File(additionalFilePath));

        if (!Objects.equals(resultPair, ImmutablePair.nullPair())) {
            LOG.info("Result pair was found");
        }
    }

    private static void validateArguments(String[] args) {
        if (args.length != 3
                || !Files.exists(Paths.get(args[1]))
                || !Files.exists(Paths.get(args[2]))) {
            throw new IllegalArgumentException("Not valid program arguments");
        }
    }

}
