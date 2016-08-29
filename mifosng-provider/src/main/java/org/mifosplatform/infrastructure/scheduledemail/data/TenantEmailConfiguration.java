/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.data;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;

/** 
 * immutable object representing a tenant's Email configuration
 **/
public class TenantEmailConfiguration {
    private final String apiAuthUsername;
    private final String apiAuthPassword;
    private final String apiBaseUrl;
    private final String sourceAddress;
    private final Integer emailCredits;
    private final String countryCallingCode;
    private final static String API_AUTH_USERNAME = "API_AUTH_USERNAME";
    private final static String API_AUTH_PASSWORD = "API_AUTH_PASSWORD";
    private final static String API_BASE_URL = "API_BASE_URL";
    private final static String EMAIL_SOURCE_ADDRESS = "EMAIL_SOURCE_ADDRESS";
    private final static String EMAIL_CREDITS = "EMAIL_CREDITS";
    private final static String COUNTRY_CALLING_CODE = "COUNTRY_CALLING_CODE";
    
    /**
     * @param apiAuthUsername
     * @param apiAuthPassword
     * @param apiBaseUrl
     * @param sourceAddress
     * @param emailCredits
     * @param countryCallingCode
     */
    private TenantEmailConfiguration(String apiAuthUsername, String apiAuthPassword, String apiBaseUrl,
            String sourceAddress, Integer emailCredits, String countryCallingCode) {
        this.apiAuthUsername = apiAuthUsername;
        this.apiAuthPassword = apiAuthPassword;
        this.apiBaseUrl = apiBaseUrl;
        this.sourceAddress = sourceAddress;
        this.emailCredits = emailCredits;
        this.countryCallingCode = countryCallingCode;
    }
    
    /**
     * @param emailConfigurationDataCollection
     * @return {@link TenantEmailConfiguration}
     */
    public static TenantEmailConfiguration instance(final Collection<EmailConfigurationData> emailConfigurationDataCollection) {
        final String apiAuthUsername = getConfigurationValue(emailConfigurationDataCollection, API_AUTH_USERNAME);
        final String apiAuthPassword = getConfigurationValue(emailConfigurationDataCollection, API_AUTH_PASSWORD);
        final String apiBaseUrl = getConfigurationValue(emailConfigurationDataCollection, API_BASE_URL);
        final String sourceAddress = getConfigurationValue(emailConfigurationDataCollection, EMAIL_SOURCE_ADDRESS);
        final String emailCreditsString = getConfigurationValue(emailConfigurationDataCollection, EMAIL_CREDITS);
        Integer emailCredits = null;

        if (emailCreditsString != null) {
            emailCredits = Integer.parseInt(emailCreditsString);
        }

        final String countryCallingCode = getConfigurationValue(emailConfigurationDataCollection, COUNTRY_CALLING_CODE);

        return new TenantEmailConfiguration(apiAuthUsername, apiAuthPassword, apiBaseUrl, sourceAddress, emailCredits,
                countryCallingCode);
    }

    /**
     * @param emailConfigurationDataCollection
     * @param configurationName
     * @return {@link EmailConfigurationData} value
     */
    private static String getConfigurationValue(final Collection<EmailConfigurationData> emailConfigurationDataCollection,
            final String configurationName) {
        String configurationData = null;

        for (EmailConfigurationData emailConfigurationData : emailConfigurationDataCollection) {
            if (StringUtils.equals(configurationName, emailConfigurationData.getName())) {
                
                configurationData = emailConfigurationData.getValue();
                break;
            }
        }
        
        return configurationData;
    }

    /**
     * @return the apiAuthUsername
     */
    public String getApiAuthUsername() {
        return apiAuthUsername;
    }

    /**
     * @return the apiAuthPassword
     */
    public String getApiAuthPassword() {
        return apiAuthPassword;
    }

    /**
     * @return the apiBaseUrl
     */
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    /**
     * @return the sourceAddress
     */
    public String getSourceAddress() {
        return sourceAddress;
    }

    /**
     * @return the emailCredits
     */
    public Integer getEmailCredits() {
        return emailCredits;
    }

    /**
     * @return the countryCallingCode
     */
    public String getCountryCallingCode() {
        return countryCallingCode;
    }
}
