package common;

/*-
 * #%L
 * Hangman Solver
 * %%
 * Copyright (C) 2016 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.github.vatbub.common.core.logging.FOKLogger;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import languages.Language;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

/**
 * A class to configure some parameters.
 *
 * @author frede
 */
public class AppConfig {
    public static final String artifactID = "hangmanSolver";
    public static final String groupID = "com.github.vatbub";
    public static final String updateFileClassifier = "jar-with-dependencies";
    /**
     * If the computer needs more guesses to guess the word than specified here,
     * the computer looses.
     */
    public static final int maxTurnCountToLoose = 11;
    /**
     * The path pattern to find the merged word dictionary. {langCode} will be
     * replaced by the language code
     */
    public static final String languageDictPattern = "/mergedLanguages/wn-merged-{langCode}.tab";

    // algorithm
    /**
     * The app saves an offline copy of the dictionary merged using
     * {@link Language#mergeWithOnlineVersion()} at the specified spot. The
     * specified file here is saved in a subfolder of the apps appData folder.
     * {langCode} will be replaced by the language code
     */
    public static final String languageDictEnhancedPattern = "dictionaries" + File.separator + "{langCode}.tab";
    /**
     * The path pattern to find the language code database.
     */
    public static final URL languageCodes = Language.class.getResource("/mergedLanguages/LanguageCodes.tab");
    /**
     * The best word must have a score bigger or equal to this to be shown as a
     * "thought" in the gui.
     *
     * @see #thresholdToSelectWord(int) thresholdToSelectWord
     */
    public static final double thresholdToShowWord = 0.2;

    // Language class
    /**
     * The api key for the <a href="https://ifttt.com/maker">IFTTT Maker
     * Channel</a>
     */
    public static final String iftttMakerApiKey = "dbjf67CBpZit4QOBthB0xW";
    /**
     * The name of the event that is sent to the
     * <a href="https://ifttt.com/maker">IFTTT Maker Channel</a> when the
     * computer wins a game.
     */
    public static final String iftttWinEvent = "hangmanSolverWon";
    /**
     * The name of the event that is sent to the
     * <a href="https://ifttt.com/maker">IFTTT Maker Channel</a> when the
     * computer looses a game.
     */
    public static final String iftttLooseEvent = "hangmanSolverLost";

    // View
    public static final ConnectionString mongoClientConnectionString =
            new ConnectionString("mongodb://user:ljkhfgsd98675@ds019634.mlab.com:19634/hangmanstats");

    /**
     * The {@link MongoClientSettings} to reach the database where all submitted
     * words are saved.
     */
    public static final MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(
            mongoClientConnectionString).build();

    // IFTTT
    /**
     * The name of the <a href="https://www.mongodb.com/">MongoDB</a> database
     * where all submitted words are saved.
     */
    public static final String mongoDBDatabaseName = "hangmanstats";
    /***
     * The name of the <a href="https://www.mongodb.com/">MongoDB</a> collection
     * where all submitted words are saved.
     */
    public static final String mongoDBWordsUsedCollectionName = "wordsused";
    private static int oldThreadCount = 0;

    // MongoDB

    // Project setup
    public static URL getUpdateRepoBaseURL() {
        URL res = null;
        try {
            res = new URL("http://dl.bintray.com/vatbub/fokprojectsReleases");
        } catch (MalformedURLException e) {
            FOKLogger.log(AppConfig.class.getName(), Level.SEVERE, "An error occurred", e);
        }

        return res;
    }

    /**
     * The maximum number of parallel threads that are used to compute the next
     * guess in the {@link algorithm.HangmanSolver} class. The number returned
     * depends on the number of cpu cores offered to the app.
     *
     * @return The maximum number of parallel threads that are used to compute
     * the next guess in the {@link algorithm.HangmanSolver} class.
     */
    public static int getParallelThreadCount() {
        int threadCount = Runtime.getRuntime().availableProcessors() + 1;

        if (threadCount != oldThreadCount) {
            oldThreadCount = threadCount;
            FOKLogger.info(AppConfig.class.getName(), "Now using " + threadCount + " threads");
        }

        return threadCount;
    }

    /**
     * The {@link algorithm.HangmanSolver}-algorithm will find the word in in
     * the dictionary that matches the current word sequence the best. This is
     * done by calculating a score using
     * {@link languages.TabFile#stringCorrelation}. If the computed score is
     * equal or bigger to the value returned by this method, the best word will
     * be accepted as the next guess.
     *
     * @param wordLength The length of the word that is evaluated.
     * @return The required correlation score to accept a word as the next
     * guess.
     * @see languages.TabFile#stringCorrelation
     */
    public static double thresholdToSelectWord(int wordLength) {
        if (wordLength <= 4) {
            return 0.7;
        } else {
            return 0.8;
        }
    }
}
