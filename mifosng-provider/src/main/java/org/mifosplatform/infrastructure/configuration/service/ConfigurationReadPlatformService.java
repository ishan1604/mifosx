/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.service;

import org.mifosplatform.infrastructure.configuration.data.GlobalConfigurationData;

import java.util.List;

public interface ConfigurationReadPlatformService {

    //GlobalConfigurationData retrieveGlobalConfiguration();
    GlobalConfigurationData retrieveGlobalConfiguration(boolean survey);

   // GlobalConfigurationData retrieveGlobalConfiguration(List<String> configurationName);

}