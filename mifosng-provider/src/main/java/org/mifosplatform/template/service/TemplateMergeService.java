/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.template.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.mifosplatform.template.domain.Template;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TemplateMergeService {

    //    private final FromJsonHelper fromApiJsonHelper;
    private Map<String, Object> scopes;

    private Map<String,Object> smsParams;



    public String compile(final Template template, final Map<String, Object> scopes) throws MalformedURLException, IOException {
        this.scopes = scopes;
        this.scopes.put("static", TemplateMergeService.now());

        final MustacheFactory mf = new DefaultMustacheFactory();
        final Mustache mustache = mf.compile(new StringReader(template.getText()), template.getName());

        final Map<String, Object> mappers = getCompiledMapFromMappers(template.getMappersAsMap());
        this.scopes.putAll(mappers);

        final StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, this.scopes);

        return stringWriter.toString();
    }

    public static String now() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        final Date date = new Date();

        return dateFormat.format(date);
    }

    private Map<String, Object> getCompiledMapFromMappers(final Map<String, String> data) {

        final MustacheFactory mf = new DefaultMustacheFactory();

        if (data != null) {
            for (final Map.Entry<String, String> entry : data.entrySet()) {

                final Mustache mappersMustache = mf.compile(new StringReader(entry.getValue()), "");
                final StringWriter stringWriter = new StringWriter();
                mappersMustache.execute(stringWriter, this.scopes);
                String url = stringWriter.toString();
                if (!url.startsWith("http")) {
                    url = this.scopes.get("BASE_URI") + url;
                }
                try {
                    this.scopes.put(entry.getKey(), getMapFromUrl(url));
                } catch (final MalformedURLException e) {
                    e.printStackTrace();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.scopes;
    }

    @SuppressWarnings("unchecked")
    private  Map<String, Object>  getMapFromUrl(final String url) throws MalformedURLException, IOException {

        final HttpURLConnection connection = getConnection(url);

        final String response = getStringFromInputStream(connection.getInputStream());

        HashMap<String, Object> result = new HashMap<>();
        if (connection.getContentType().equals("text/plain")) {
        	result.put("src", response);
        } else {
            result = new ObjectMapper().readValue(response, HashMap.class);
        }

        return result;
    }

    private HttpURLConnection getConnection(final String url) {

            final String name =SecurityContextHolder.getContext().getAuthentication().getName();
            final String password = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
            Authenticator.setDefault(new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(name, password.toCharArray());
                }
            });


        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            TrustModifier.relaxHostChecking(connection);

            connection.setDoInput(true);

        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final KeyManagementException e) {
            e.printStackTrace();
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final KeyStoreException e) {
            e.printStackTrace();
        }

        return connection;
    }

    private static String getStringFromInputStream(final InputStream is) {

        BufferedReader br = null;
        final StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (final IOException e) {
            // FIXME - HANDLE THIS CASE
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
    /*
      Gets the object from a runReport query
     */
    private List<HashMap<String,Object>> getRunReportObject(final String url) throws MalformedURLException, IOException{

        final HttpURLConnection connection = getConnection(url);

        final String response = getStringFromInputStream(connection.getInputStream());
        List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        result = new ObjectMapper().readValue(response, new TypeReference<List<HashMap<String,Object>>>(){});
        return result;
    }






     public Map<String, List<HashMap<String,Object>>> compileMappers(final Map<String, String> templateMappers,Map<String,Object> smsParams) {

        final MustacheFactory mf = new DefaultMustacheFactory();

        final Map<String,List<HashMap<String,Object>>> runReportObject = new HashMap<String, List<HashMap<String,Object>>>();

        if(templateMappers !=null){
            for(Map.Entry<String,String> entry : templateMappers.entrySet()){
                /*
                    "mapperkey": "runreports",
                    "mappervalue": "runreports/{{runreportId}}?associations=all&tenantIdentifier={{tenantIdentifier}}",
                    entry.getValue represents mapperValue
                 */
                final Mustache urlMustache = mf.compile(new StringReader(entry.getValue()),"");

                final StringWriter stringWriter = new StringWriter();
                //execute to replace params in the mapperValue above ex {{loanId}} = 4
                urlMustache.execute(stringWriter,smsParams);
                String url = stringWriter.toString(); //holds the url to query for object from runReport
                if (!url.startsWith("http")) {
                    url = smsParams.get("BASE_URI") + url;
                }
                try{
                    runReportObject.put(entry.getKey(), getRunReportObject(url));
                }catch(final MalformedURLException e){
                    //TODO throw something here
                }catch (final IOException e){
                   // TODO throw something here
                }
            }

        }
        return runReportObject; //contains list of runReport object runReport,{Object}
    }



}
