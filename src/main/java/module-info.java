/*-
 * #%L
 * Hangman Solver
 * %%
 * Copyright (C) 2016 - 2021 Frederik Kammel
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
module hangmanSolver {
    requires org.jetbrains.annotations;
    requires java.logging;
    requires common.core;
    requires common.view.core;
    requires org.apache.commons.io;
    requires org.mongodb.bson;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires common.internet;
    requires common.updater;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;

    opens view;
    opens view.noLanguageSelected;
    opens view.noSequenceEntered;
    opens view.SendReportQuestion;
}
