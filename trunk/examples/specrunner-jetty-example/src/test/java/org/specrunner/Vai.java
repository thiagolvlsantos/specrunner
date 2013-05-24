/*
 * Copyright 2012 Thiago.
 *
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
 */
package org.specrunner;

import java.io.File;
import org.specrunner.dumper.impl.AbstractSourceDumperFile;
import org.specrunner.features.IFeatureManager;
import org.specrunner.webdriver.PluginBrowser;

/**
 *
 * @author Thiago
 */
public class Vai {
    
    public static void main(String[] args) throws Exception {
        TestJetty tj = new TestJetty();
        tj.prepareTest();
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.put(PluginBrowser.FEATURE_RECORDING, false);
        fh.put(AbstractSourceDumperFile.FEATURE_OUTPUT_DIRECTORY, new File("src/test/resources/outcome/run"));
        for (int j = 0; j < 1; j++) {
            try {
                fh.put(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-run" + j + ".html");
                tj.runJettyWithRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
